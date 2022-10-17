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

    private final int mapColour;
    private final int XColour;
    private final int OColour;

    private final Paint paint = new Paint();

    private int cellSize = 500/20;

    private final GameLogic game;
    private final Obstacle obstacle;
    mapPanelFragment mapPanelFragment;

    public static Rect rect1;
    public static Bitmap resizedDown, resizedUp, resizedLeft, resizedRight, resizedRobot;
    public static Bitmap resizedRock;

    private float refX, refY;
    private float origX, origY;

    int no_of_obs = 4;
    public static int robottopImage = 17, robotleftImage = 0;
    private static HashMap<String, String> robotNewLoc = new HashMap<String, String>();
    //array contains obsticles' info: {left},{top},{angle}
    public int[][] obsLocation = {{8,15,1,13},{7,15,12,5},{270,180,90,0},{11,12,13,14},{10,10,10,10}};
    boolean[] isSelectedObs = {false,false,false,false};
    private int size = 10;
    private int enlargeSize = 18;
    private String[][] obsIdentity;
    private int newId;
    private boolean isOverlap = false;

    public static int robotAngle = 270;
    public static int faceDirection = 270;

    Bitmap downBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.down);
    Bitmap upBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.up);
    Bitmap leftBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left);
    Bitmap rightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.right);
    //Bitmap robotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
    Bitmap robotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.curiosity_bg);

    Bitmap gridBg = BitmapFactory.decodeResource(getResources(), R.drawable.gridmap_bg);
    Bitmap whiteRock = BitmapFactory.decodeResource(getResources(), R.drawable.white_rock_white);

    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    ArrayList<String> obsCurrentLoc = new ArrayList<String>();



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
        //super.onMeasure(width, height);
        int dimension = Math.min(getMeasuredWidth(), getMeasuredHeight());
        cellSize = dimension/20;

        setMeasuredDimension(580, 580);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onDraw(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        drawGridMap(canvas);
        if (game.getGripMap()[0][0] != -1) {
            //obstacle control
            drawObstacles(canvas);
            obsIdentity = obstacle.getObsIdentity();
            //Check obsLocation array
            for (int i = 0; i < obsLocation.length; i++) {
                for (int j = 0; j < obsLocation[i].length; j++) {
                    System.out.print(obsLocation[i][j] + " ");
                }
                System.out.print("; ");
            }
            System.out.println();

            //robot control
            drawRobot(canvas);
            game.displayLoc(robotleftImage, robottopImage);
            //set latest robot location
            mapPanelFragment = new mapPanelFragment();
            mapPanelFragment.retrieveCurrentRobot(robotleftImage, robottopImage, faceDirection);
        }
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
        robotAngle = 270;
        no_of_obs = 4;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // finger touches the screen
                if (game.getGripMap()[0][0] == 7 ||game.getGripMap()[0][0] == 8 ||game.getGripMap()[0][0] == 9) {
                    //System.out.println("touch action down: ");
                    refY = y;
                    refX = x;
                    origY = y;
                    origX = x;
                    //System.out.println("origX: " + origX);
                    //System.out.println("origY: " + origY);

                    for (int i = 0; i < isSelectedObs.length; i++) {
                        //System.out.println("i="+ i );
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
                    //System.out.println("touch action move: ");
                    for (int i = 0; i< isSelectedObs.length; i++){
                        if(isSelectedObs[i] == true){
                            float nX = event.getX();
                            float nY = event.getY();
                            obsLocation[0][i]= (int) Math.floor(nX / cellSize);
                            obsLocation[1][i] = (int) Math.floor(nY / cellSize);
                            /*System.out.println("nX Move: " + nX);
                            System.out.println("nY Move: " + nY);
                            System.out.println("origX Move: " + origX);
                            System.out.println("origY Move: " + origY);*/

                            //System.out.println("not overlap");
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
                if (game.getGripMap()[0][0] == 7 ||game.getGripMap()[0][0] == 8) {
                    for (int i = 0; i< isSelectedObs.length; i++){
                        if(isSelectedObs[i]==true){
                            float nX = event.getX();
                            float nY = event.getY();
                            int newLeft = (int) Math.floor(nX / cellSize);
                            int newTop = (int) Math.floor(nY / cellSize);

                            /*System.out.println("newLeft Up: " + newLeft);
                            System.out.println("newTop Up: " + newTop);*/
                            /*obsLocation[0][i] = (int) Math.floor(nX / cellSize);
                            obsLocation[1][i] = (int) Math.floor(nY / cellSize);*/
                            if (abs(nX - origX) < cellSize && abs(nY - origY) < cellSize) {
                                obsLocation[2][i] += 90;
                                if (obsLocation[2][i] == 360){
                                    obsLocation[2][i] -= 360;
                                }
                            }
                            /*System.out.println("nX Up: " + nX);
                            System.out.println("nY Up: " + nY);
                            System.out.println("origX Up: " + origX);
                            System.out.println("origY Up: " + origY);*/
                            boolean changeable = true;
                            for (int j = 0; j < obsLocation[0].length; j++){
                                if (i != j && newLeft == obsLocation[0][j] && newTop == obsLocation[1][j]){
                                    //System.out.println("overlap");
                                    changeable = false;
                                    obsLocation[0][i] = (int) Math.floor(origX / cellSize);
                                    obsLocation[1][i] = (int) Math.floor(origY / cellSize);
                                }
                            }
                            if (changeable){
                                //System.out.println("changeable");
                                refX = nX;
                                refY = nY;
                                obsLocation[0][i] = (int) Math.floor(nX / cellSize);
                                obsLocation[1][i] = (int) Math.floor(nY / cellSize);
                            }

                            isSelectedObs[i]=false;

                            //remove obstacles if out of boundary
                            moveObstacles();
                            invalidate();
                        }
                    }
                }
                else if (game.getGripMap()[0][0] == 9) {
                    for (int i=0; i<no_of_obs;i++){
                        if(isSelectedObs[i]==true) {
                            //System.out.println("gridmap isSelectedObs");
                            float nX = event.getX();
                            float nY = event.getY();
                            obsLocation[0][i] = (int) Math.floor(nX / cellSize);
                            obsLocation[1][i] = (int) Math.floor(nY / cellSize);
                            if (abs(nX - origX) < cellSize && abs(nY - origY) < cellSize) {
                                mapPanelFragment = new mapPanelFragment();
                                mapPanelFragment.retrieveCurrentObs(i);
                                //System.out.println("gridmap i to be pass= "+i);
                                //change grid map matrix to 10
                            }

                            //remove obstacles if out of boundary
                        }
                        isSelectedObs[i]=false;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    private void drawGridMap(Canvas canvas){
        paint.setColor(mapColour);
        paint.setStrokeWidth(1);

        //draw background
        Bitmap resizedBg = getResizedBitmap(gridBg, 20);
        //rotateBitmap(canvas, resizedBg, 0, 0, 0);

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
        //System.out.println("grid map: " + game.getGripMap()[0][0]);
        if (game.getGripMap()[0][0] > 0) {
            //rect1 = genRect(15, 15);
            //System.out.println("rect1: " + rect1);
            //canvas.drawRect(rect1, paint);

            //resize default bitmap
            resizedDown = getResizedBitmap(downBitmap, 1);
            resizedUp = getResizedBitmap(upBitmap, 1);
            resizedLeft = getResizedBitmap(leftBitmap, 1);
            resizedRight = getResizedBitmap(rightBitmap, 1);

            //resizedRock = getResizedBitmap(rockOBs, 1);
            resizedRock = getResizedBitmap(whiteRock, 1);

            //add default bitmaps to an array
            /*bitmapArray.add(resizedRight);
            bitmapArray.add(resizedRight);
            bitmapArray.add(resizedRight);
            bitmapArray.add(resizedRight);*/

            bitmapArray.add(resizedRock);
            bitmapArray.add(resizedRock);
            bitmapArray.add(resizedRock);
            bitmapArray.add(resizedRock);

            //add more obstacles
            if (game.getGripMap()[0][0] == 7) {
                if (no_of_obs < 8) {
                    addMoreObs();

//                Check obsLocation array
//                for (int i = 0; i < obsLocation.length; i++) {
//                    for (int j = 0; j < obsLocation[i].length; j++) {
//                        System.out.print(obsLocation[i][j] + " ");
//                    }
//                }
//                System.out.println();
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
                    //Check obsLocation array
                    //System.out.println("check obsLocation after remove: ");
                    for (int j = 0; j < obsLocation.length; j++) {
                        for (int k = 0; k < obsLocation[j].length; k++) {
                            System.out.print(obsLocation[j][k] + " ");
                        }
                    }
                    System.out.println();
//                    System.out.println("isSelected obslocation:"+ Arrays.toString(isSelectedObs));
//                    System.out.println("i: "+ i);
                }
            }
            //add numbers to obstacles
            for (int i = 0; i < no_of_obs; i++) {
                //System.out.println("obsLoc changed to: "+ (obsLocation[3][i]));
                //System.out.println("i: "+ i);
                addNumber(canvas, obsIdentity[1][obsLocation[3][i]-11],
                        (float) (obsLocation[0][i] + 0.4) * cellSize,
                        (float) (obsLocation[1][i] + 0.6) * cellSize, obsLocation[4][i]);
            }

            //Check obsLocation array
            /*for (int i = 0; i < obsLocation.length; i++){
                for (int j = 0; j < obsLocation[i].length; j++){
                    System.out.print(obsLocation[i][j] + " ");
                }
            }
            System.out.println();*/
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
        //turn left
        else if (game.getGripMap()[0][0] == 5) {
            if (faceDirection == 0){
                faceDirection = 360;
            }
            rotateRobot(canvas, resizedRobot, robotleftImage, robottopImage, 1);
        }
        //turn right
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

    public Rect genRect(int row, int col){
        //System.out.println("cellSize1: " + cellSize);
        //System.out.println("width: " + getWidth());
        Rect rect = new Rect();
        rect.left = (col+1)*cellSize;
        rect.top = row*cellSize;
        rect.right = row*cellSize;
        rect.bottom = (row+1)*cellSize;
        //System.out.println("genRect exe.: " + rect);
        return rect;
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
        //bm.recycle();
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
        Bitmap resizedObstacle = getResizedBitmap(downBitmap, 1);
        bitmapArray.add(resizedRock);
        no_of_obs++;

        obsLocation = obstacle.addMoreObs(no_of_obs, obsLocation);

        //update isSelectedObs boolean list, default false
        isSelectedObs = obstacle.moveNewObs(no_of_obs, isSelectedObs);
        isSelectedObs[no_of_obs-1] = false;
    }

    public void moveRobot(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int direction) {
        //System.out.println("move robot leftImage: " + leftImage);
        //System.out.println("move robot direction: " + direction);
        //Call robot class
        Robot robot = new Robot();
        int[] movements = new int[2];
        movements = robot.moveRobot(leftImage, topImage, direction, faceDirection, obsLocation);
        leftImage = movements[0];
        topImage = movements[1];

        //Move and draw robot
        Matrix matrix = new Matrix();
        matrix.setRotate(faceDirection, bitmap.getWidth()/2, bitmap.getHeight()/2);
        matrix.postTranslate(leftImage * cellSize, topImage * cellSize);
        canvas.drawBitmap(bitmap, matrix, null);
        robotleftImage = leftImage;
        robottopImage = topImage;
    }

    public void rotateRobot(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int direction){
        //System.out.println("rotate robot init left: " + leftImage);
        //System.out.println("rotate robot init top: " + topImage);
        //Call Robot class
        Robot robot = new Robot();
        int[] movements = new int[3];
        movements = robot.rotateRobot(leftImage, topImage, direction, faceDirection, obsLocation);
        faceDirection = movements[0];
        leftImage = movements[1];
        topImage = movements[2];

        //Rotate and draw robot
        Matrix matrix = new Matrix();
        matrix.setRotate(faceDirection, bitmap.getWidth()/2, bitmap.getHeight()/2);
        matrix.postTranslate(leftImage * cellSize, topImage * cellSize);
        canvas.drawBitmap(bitmap, matrix, null);
        robotleftImage = leftImage;
        robottopImage = topImage;
    }

    public void rotateBackRobot(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int direction){
        //Call Robot class
        Robot robot = new Robot();
        int[] movements = new int[3];
        movements = robot.rotateBackRobot(leftImage, topImage, direction, faceDirection, obsLocation);
        faceDirection = movements[0];
        leftImage = movements[1];
        topImage = movements[2];

        //Rotate and draw robot
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

    //Call game logic class
    public void genObstacles(){game.generateObstacles();}

    public void genMoreObs(){game.generateMoreObs();}

    public void moveObstacles(){game.moveObstacles();}

    public void changeObsId(){game.changeObsId();}

    public void genRobot(){game.generateRobot();}

    public void moveForward(){game.moveRobotForward();}

    public void moveBackward(){game.moveRobotBackward();}

    public void rotateLeft(){ game.rotateRobotLeft(); }

    public void rotateRight(){ game.rotateRobotRight(); }

    public void clearCanvas(){game.clearCanvas();}

    public void rotateBackLeft(){ game.rotateRobotBackLeft(); }

    public void rotateBackRight(){ game.rotateRobotBackRight(); }
}

