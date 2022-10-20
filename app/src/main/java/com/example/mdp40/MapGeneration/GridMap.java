package com.example.mdp40.MapGeneration;

import static java.lang.Math.abs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.mdp40.R;
import com.example.mdp40.fragments.mapPanelFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GridMap extends View{

    // grid map details
    private final int mapColour;
    private final int XColour;
    private final int OColour;
    private int cellSize = 500/20;

    private final Paint paint = new Paint();

    // classes used in GridMap.java
    private final GameLogic game;
    private final Obstacle obstacle;
    mapPanelFragment mapPanelFragment;

    // robot details
    public static Bitmap resizedRobot;
    public static Bitmap resizedRock;
    public static int faceDirection = 270;
    public static int robottopImage = 17, robotleftImage = 0;
    private float refX, refY;
    private float origX, origY;
    Bitmap robotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.curiosity_bg);

    //obstacle details
    //array contains obstacles' info: {{left},{top},{angle},{id},{font size}}
    public int[][] obsLocation = {{8,15,1,13},{7,15,12,5},{270,180,90,0},{11,12,13,14},{10,10,10,10}};
    int no_of_obs = 4;
    boolean[] isSelectedObs = {false,false,false,false};
    private String[][] obsIdentity;
    Bitmap whiteRock = BitmapFactory.decodeResource(getResources(), R.drawable.white_rock_white);
    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();


    public GridMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        game = new GameLogic();
        obstacle = new Obstacle();

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.GridMap, 0, 0);

        try {
            mapColour = a.getInteger(R.styleable.GridMap_mapColour,0);
            XColour = a.getInteger(R.styleable.GridMap_XColour,0);
            OColour = a.getInteger(R.styleable.GridMap_OColour,0);
        }finally {
            a.recycle();
        }

    }

    @Override
    protected void onMeasure(int width, int height){
        int dimension = Math.min(getMeasuredWidth(), getMeasuredHeight());
        cellSize = dimension/20;

        setMeasuredDimension(580, 580);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        // draw grid map first
        drawGridMap(canvas);

        // if not "clear canvas"
        if (game.getGripMap()[0][0] != -1) {
            //obstacle control
            drawObstacles(canvas);
            obsIdentity = obstacle.getObsIdentity();

            //robot control
            drawRobot(canvas);
            game.displayLoc(robotleftImage, robottopImage);

            //set latest robot location
            mapPanelFragment = new mapPanelFragment();
            mapPanelFragment.retrieveCurrentRobot(robotleftImage, robottopImage, faceDirection);
        }
        // clear map
        else {
            clearAll();
        }
    }

    public void clearAll() {
        obsLocation = new int[][]{{8, 15, 1, 13}, {7, 15, 12, 5}, {0, 0, 0, 0},
                                  {11, 12, 13, 14},{10, 10, 10, 10}};
        isSelectedObs = new boolean[]{false, false, false, false};
        robotleftImage = 0;
        robottopImage = 17;
        faceDirection = 270;
        no_of_obs = 4;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // finger touches the screen
                // if user chooses "generate more obstacles" or "move obstacles"
                // OR gate is to prevent frequent click needed to move obstacles around
                if (game.getGripMap()[0][0] == 7 || game.getGripMap()[0][0] == 8) {
                    refY = y;
                    refX = x;
                    origY = y;
                    origX = x;

                    for (int i = 0; i < isSelectedObs.length; i++) {
                        if(toDrag(refX, refY, obsLocation[0][i], obsLocation[1][i],bitmapArray.get(i))){
                            isSelectedObs[i]=true;
                        }
                    }
                    moveObstacles();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // finger moves on the screen
                if (game.getGripMap()[0][0] == 7 ||game.getGripMap()[0][0] == 8) {
                    for (int i = 0; i< isSelectedObs.length; i++){
                        if(isSelectedObs[i] == true){
                            float nX = event.getX();
                            float nY = event.getY();
                            obsLocation[0][i]= (int) Math.floor(nX / cellSize);
                            obsLocation[1][i] = (int) Math.floor(nY / cellSize);

                            refX = nX;
                            refY = nY;

                            moveObstacles();
                            invalidate();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                // finger lifts up from the screen
                // if user chooses "generate more obstacles" or "move obstacles"
                // OR gate is to prevent frequent click needed to move obstacles around
                if (game.getGripMap()[0][0] == 7 || game.getGripMap()[0][0] == 8) {
                    for (int i = 0; i< isSelectedObs.length; i++){
                        if(isSelectedObs[i]==true){
                            float nX = event.getX();
                            float nY = event.getY();
                            int newLeft = (int) Math.floor(nX / cellSize);
                            int newTop = (int) Math.floor(nY / cellSize);

                            // action considered as single click if the obstacle moved distance is too small
                            if (abs(nX - origX) < cellSize && abs(nY - origY) < cellSize) {
                                obsLocation[2][i] += 90;
                                if (obsLocation[2][i] == 360){
                                    obsLocation[2][i] -= 360;
                                }
                            }

                            // check if the current obstacle overlaps another obstacle
                            // if overlaps, current obstacle moves back to its original place
                            boolean changeable = true;
                            for (int j = 0; j < obsLocation[0].length; j++){
                                if (i != j && newLeft == obsLocation[0][j] && newTop == obsLocation[1][j]){
                                    changeable = false;
                                    obsLocation[0][i] = (int) Math.floor(origX / cellSize);
                                    obsLocation[1][i] = (int) Math.floor(origY / cellSize);
                                }
                            }
                            if (changeable){
                                refX = nX;
                                refY = nY;
                                obsLocation[0][i] = (int) Math.floor(nX / cellSize);
                                obsLocation[1][i] = (int) Math.floor(nY / cellSize);
                            }
                            isSelectedObs[i]=false;

                            moveObstacles();
                            invalidate();
                        }
                    }
                }
                break;
        }
        return true;
    }

    private void drawGridMap(Canvas canvas){
        paint.setColor(mapColour);
        paint.setStrokeWidth(1);

        int c, r;
        for(c = 1; c < 20; c++){
            canvas.drawLine(cellSize*c, 0, cellSize*c, canvas.getWidth(), paint);
        }
        for(r = 1; r < 20; r++){
            canvas.drawLine(0, cellSize*r, canvas.getWidth(), cellSize*r, paint);
        }

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, 0, 580, 580, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void drawObstacles(Canvas canvas) {
        if (game.getGripMap()[0][0] > 0) {
            //resize default bitmap
            resizedRock = getResizedBitmap(whiteRock, 1);

            bitmapArray.add(resizedRock);
            bitmapArray.add(resizedRock);
            bitmapArray.add(resizedRock);
            bitmapArray.add(resizedRock);

            //add more obstacles
            if (game.getGripMap()[0][0] == 7) {
                if (no_of_obs < 8) {
                    addMoreObs();
                }
            }

            //generate obstacles
            for (int i = 0; i < no_of_obs; i++) {
                if (obsLocation[0][i] >= 0 && obsLocation[0][i] < 20 && obsLocation[1][i] >= 0 && obsLocation[1][i] < 20) {
                    rotateBitmap(canvas, bitmapArray.get(i), obsLocation[0][i], obsLocation[1][i], obsLocation[2][i]);
                }
                else{
                    bitmapArray.remove(i);
                    obsLocation = obstacle.removeObs(no_of_obs, obsLocation, i);
                    isSelectedObs = obstacle.removeMoveObs(no_of_obs, isSelectedObs, i);
                    no_of_obs--;
                    i--;
                }
            }

            //add id to obstacles
            for (int i = 0; i < no_of_obs; i++) {
                addNumber(canvas, obsIdentity[1][obsLocation[3][i]-11],
                        (float) (obsLocation[0][i] + 0.4) * cellSize,
                        (float) (obsLocation[1][i] + 0.6) * cellSize, obsLocation[4][i]);
            }

        }
    }

    public void addNumber(Canvas canvas, String number, float leftCoord, float topCoord, int size) {
        obstacle.getObsIdentity();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.YELLOW);
        paint.setTextSize(17);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText(number, leftCoord, topCoord, paint);
    }

    private void drawRobot(Canvas canvas) {
        resizedRobot = getResizedBitmap(robotBitmap, 3);
        //place robot on the grid map
        if (game.getGripMap()[0][0] == 2) {
            rotateBitmap(canvas, resizedRobot, robotleftImage, robottopImage, faceDirection);
        }
        //move forward
        else if (game.getGripMap()[0][0] == 3) {
            moveRobot(canvas, resizedRobot, robotleftImage, robottopImage, 1);
        }
        //move backward
        else if (game.getGripMap()[0][0] == 4) {
            moveRobot(canvas, resizedRobot, robotleftImage, robottopImage, -1);
        }
        //turn forward left
        else if (game.getGripMap()[0][0] == 5) {
            if (faceDirection == 0){
                faceDirection = 360;
            }
            rotateRobot(canvas, resizedRobot, robotleftImage, robottopImage, 1);
        }
        //turn forward right
        else if (game.getGripMap()[0][0] == 6) {
            rotateRobot(canvas, resizedRobot, robotleftImage, robottopImage, -1);
            if (faceDirection == 360){
                faceDirection = 0;
            }
        }
        //turn back left
        else if (game.getGripMap()[0][0] == 10) {
            rotateBackRobot(canvas, resizedRobot, robotleftImage, robottopImage, 1);
            if (faceDirection == 360){
                faceDirection = 0;
            }
        }
        //turn back right
        else if (game.getGripMap()[0][0] == 11) {
            if (faceDirection == 0){
                faceDirection = 360;
            }
            rotateBackRobot(canvas, resizedRobot, robotleftImage, robottopImage, -1);
        }
    }


    public Bitmap getResizedBitmap(Bitmap bm, int cellNo) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) cellSize * cellNo) / width;
        float scaleHeight = ((float) cellSize * cellNo) / height;
        //create a matrix for the manipulation
        Matrix matrix = new Matrix();
        //resize the bitmap
        matrix.postScale(scaleWidth, scaleHeight);
        //recreate the new bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public boolean toDrag(float x, float y, int leftImage, int topImage, Bitmap bitmap){
        if((x >= leftImage*cellSize && x <= bitmap.getWidth() + leftImage*cellSize)
                && (y >= topImage*cellSize && y <= topImage*cellSize + bitmap.getHeight())) {
            return true;
        }
        else{
            return false;
        }
    }

    public void rotateBitmap(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int angle)
    {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle, bitmap.getWidth()/2, bitmap.getHeight()/2);
        matrix.postTranslate(leftImage*cellSize, topImage*cellSize);
        canvas.drawBitmap(bitmap, matrix, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addMoreObs(){
        //draw the new obstacle
        //call Obstacle class
        bitmapArray.add(resizedRock);
        no_of_obs++;

        obsLocation = obstacle.addMoreObs(no_of_obs, obsLocation);

        //update isSelectedObs boolean list, default false
        isSelectedObs = obstacle.moveNewObs(no_of_obs, isSelectedObs);
        isSelectedObs[no_of_obs-1] = false;
    }

    public void moveRobot(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int direction) {
        //call robot class
        Robot robot = new Robot();
        int[] movements = new int[2];
        movements = robot.moveRobot(leftImage, topImage, direction, faceDirection, obsLocation);
        leftImage = movements[0];
        topImage = movements[1];

        //move and draw robot
        Matrix matrix = new Matrix();
        matrix.setRotate(faceDirection, bitmap.getWidth()/2, bitmap.getHeight()/2);
        matrix.postTranslate(leftImage * cellSize, topImage * cellSize);
        canvas.drawBitmap(bitmap, matrix, null);
        robotleftImage = leftImage;
        robottopImage = topImage;
    }

    public void rotateRobot(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int direction){
        //call Robot class
        Robot robot = new Robot();
        int[] movements = new int[3];
        movements = robot.rotateRobot(leftImage, topImage, direction, faceDirection, obsLocation);
        faceDirection = movements[0];
        leftImage = movements[1];
        topImage = movements[2];

        //rotate and draw robot
        Matrix matrix = new Matrix();
        matrix.setRotate(faceDirection, bitmap.getWidth()/2, bitmap.getHeight()/2);
        matrix.postTranslate(leftImage * cellSize, topImage * cellSize);
        canvas.drawBitmap(bitmap, matrix, null);
        robotleftImage = leftImage;
        robottopImage = topImage;
    }

    public void rotateBackRobot(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int direction){
        //call Robot class
        Robot robot = new Robot();
        int[] movements = new int[3];
        movements = robot.rotateBackRobot(leftImage, topImage, direction, faceDirection, obsLocation);
        faceDirection = movements[0];
        leftImage = movements[1];
        topImage = movements[2];

        //rotate and draw robot
        Matrix matrix = new Matrix();
        matrix.setRotate(faceDirection, bitmap.getWidth()/2, bitmap.getHeight()/2);
        matrix.postTranslate(leftImage * cellSize, topImage * cellSize);
        canvas.drawBitmap(bitmap, matrix, null);
        robotleftImage = leftImage;
        robottopImage = topImage;
    }

    public void displayRobotPos (TextView robotX, TextView robotY){
        game.setRobotX(robotX);
        game.setRobotY(robotY);
    }

    //call game logic class
    public void genObstacles(){game.generateObstacles();}

    public void genMoreObs(){game.generateMoreObs();}

    public void moveObstacles(){game.moveObstacles();}

    public void genRobot(){game.generateRobot();}

    public void moveForward(){game.moveRobotForward();}

    public void moveBackward(){game.moveRobotBackward();}

    public void rotateLeft(){ game.rotateRobotLeft(); }

    public void rotateRight(){ game.rotateRobotRight(); }

    public void clearCanvas(){game.clearCanvas();}

    public void rotateBackLeft(){ game.rotateRobotBackLeft(); }

    public void rotateBackRight(){ game.rotateRobotBackRight(); }
}

