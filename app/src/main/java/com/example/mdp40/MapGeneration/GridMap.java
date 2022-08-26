package com.example.mdp40.MapGeneration;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mdp40.R;

import java.util.ArrayList;

public class GridMap extends View{

    private final int mapColour;
    private final int XColour;
    private final int OColour;

    private final Paint paint = new Paint();

    private int cellSize = 500/20;

    private final GameLogic game;

    public static Rect rect1;
    public static Bitmap resizedDown, resizedUp, resizedLeft, resizedRight, resizedRobot;

    private float refX, refY;
    private float origX, origY;
    private boolean toMoveDown = false, toMoveUp = false, toMoveLeft = false, toMoveRight = false;
    private boolean toRotateUp = false, toRotateDown = false, toRotateLeft = false, toRotateRight = false;

    public static int downtopImage = 7, downleftImage = 8;
    private static int uptopImage = 15, upleftImage = 15;
    private static int lefttopImage = 10, leftleftImage = 11;
    private static int righttopImage = 5, rightleftImage = 13;
    private static int robottopImage = 17, robotleftImage = 0;

    int nearestLeftUnit, nearestTopUnit;

    private float xRotate, yRotate;
    private int upAngle = 0, downAngle = 0, leftAngle = 0, rightAngle = 0, robotAngle = 0;
    private static int faceDirection = 0;
    private boolean north = false, south = false, east = false, west = false;


    Bitmap downBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.down);
    Bitmap upBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.up);
    Bitmap leftBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left);
    Bitmap rightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.right);
    Bitmap robotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.robot);

    ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

    public GridMap(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        game = new GameLogic();

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

        setMeasuredDimension(500, 500);
    }

    @Override
    protected void onDraw(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        drawGridMap(canvas);
        drawObstacles(canvas);
        drawRobot(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // finger touches the screen
                if (game.getGripMap()[0][0] == 1) {
                    System.out.println("touch action down: ");
                    refY = y;
                    refX = x;
                    origY = y;
                    origX = x;
                    System.out.println("origX: " + origX);
                    System.out.println("origY: " + origY);
                /*if((refX >= downleftImage*cellSize && refX <= resizedDown.getWidth() + downleftImage*cellSize)
                    && (refY >= downtopImage*cellSize && refY <= downtopImage*cellSize + resizedDown.getHeight())){
                    System.out.println("touch action down downImage if condition: ");
                    toMoveDown = true;
                }*/
                    for (int i = 0; i < bitmapArray.size(); i++) {
                        if (toDrag(refX, refY, downleftImage, downtopImage, bitmapArray.get(i))) {
                            toMoveDown = true;
                            toMoveUp = false;
                            toMoveLeft = false;
                            toMoveRight = false;
                        } else if (toDrag(refX, refY, upleftImage, uptopImage, bitmapArray.get(i))) {
                            System.out.println("touch action down upImage if condition: ");
                            toMoveUp = true;
                            toMoveDown = false;
                            toMoveLeft = false;
                            toMoveRight = false;
                        } else if (toDrag(refX, refY, leftleftImage, lefttopImage, bitmapArray.get(i))) {
                            toMoveLeft = true;
                            toMoveUp = false;
                            toMoveDown = false;
                            toMoveRight = false;
                        } else if (toDrag(refX, refY, rightleftImage, righttopImage, bitmapArray.get(i))) {
                            toMoveRight = true;
                            toMoveUp = false;
                            toMoveDown = false;
                            toMoveLeft = false;
                        } else {
                            System.out.println("touch action down else condition: ");
                            toMoveDown = false;
                            toMoveUp = false;
                            toMoveLeft = false;
                            toMoveRight = false;
                        }
                    }
                }

                /*if(toDrag(refX, refY, downleftImage, downtopImage, resizedDown)){
                    toMoveDown = true;
                }
                else if(toDrag(refX, refY, upleftImage, uptopImage, resizedUp)){
                    System.out.println("touch action down upImage if condition: ");
                    toMoveUp = true;
                }
                else{
                    System.out.println("touch action down else condition: ");
                    toMoveDown = false;
                    toMoveUp = false;
                    toMoveLeft = false;
                    toMoveRight = false;
                }*/
                break;

            case MotionEvent.ACTION_MOVE:
                // finger moves on the screen
                if (game.getGripMap()[0][0] == 1) {
                    System.out.println("touch action move: ");
                    if (toMoveDown) {
                        float nX = event.getX();
                        float nY = event.getY();

                        downleftImage = (int) Math.floor(nX / cellSize);
                        downtopImage = (int) Math.floor(nY / cellSize);

                        //downleftImage = downleftImage * cellSize;
                        //downtopImage = downtopImage * cellSize;
                   /* downleftImage += nX - refX;
                    downtopImage += nY - refY;*/
                        //downleftImage = downleftImage / cellSize;
                        //downtopImage = downtopImage / cellSize;

                        System.out.println("nX Move: " + nX);
                        System.out.println("nY Move: " + nY);
                        System.out.println("refX Move: " + refX);
                        System.out.println("refY Move: " + refY);


                        refX = nX;
                        refY = nY;

                        invalidate();
                    } else if (toMoveUp) {
                        float nX = event.getX();
                        float nY = event.getY();

                        upleftImage = (int) Math.floor(nX / cellSize);
                        uptopImage = (int) Math.floor(nY / cellSize);

                        System.out.println("origX: " + origX);
                        System.out.println("origY: " + origY);

                        refX = nX;
                        refY = nY;
                        invalidate();
                    } else if (toMoveLeft) {
                        float nX = event.getX();
                        float nY = event.getY();

                        leftleftImage = (int) Math.floor(nX / cellSize);
                        lefttopImage = (int) Math.floor(nY / cellSize);

                        refX = nX;
                        refY = nY;
                        invalidate();
                    } else if (toMoveRight) {
                        float nX = event.getX();
                        float nY = event.getY();

                        rightleftImage = (int) Math.floor(nX / cellSize);
                        righttopImage = (int) Math.floor(nY / cellSize);

                        refX = nX;
                        refY = nY;
                        invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                System.out.println("touch action up: ");
                if (game.getGripMap()[0][0] == 1) {
                    if (toMoveDown) {
                        float nX = event.getX();
                        float nY = event.getY();

                        downleftImage = (int) Math.floor(nX / cellSize);
                        //downleftImage = nearestLeftUnit * cellSize;
                        downtopImage = (int) Math.floor(nY / cellSize);
                        //downtopImage = nearestTopUnit * cellSize;
                        System.out.println("Left Up: " + downleftImage);
                        System.out.println("Top Up: " + downtopImage);

                        if (abs(nX - origX) < cellSize && abs(nY - origY) < cellSize) {
                            downAngle += 90;
                        }
                        refX = nX;
                        refY = nY;

                        invalidate();
                    } else if (toMoveUp) {
                        float nX = event.getX();
                        float nY = event.getY();

                        upleftImage = (int) Math.floor(nX / cellSize);
                        uptopImage = (int) Math.floor(nY / cellSize);

                        if (abs(nX - origX) < cellSize && abs(nY - origY) < cellSize) {
                            System.out.println("same location");
                            System.out.println("nx-refX: " + (nX - origX));
                            upAngle += 90;
                        }
                        refX = nX;
                        refY = nY;
                        invalidate();
                    } else if (toMoveLeft) {
                        float nX = event.getX();
                        float nY = event.getY();

                        leftleftImage = (int) Math.floor(nX / cellSize);
                        lefttopImage = (int) Math.floor(nY / cellSize);

                        if (abs(nX - origX) < cellSize && abs(nY - origY) < cellSize) {
                            leftAngle += 90;
                        }
                        refX = nX;
                        refY = nY;
                        invalidate();
                    } else if (toMoveRight) {
                        float nX = event.getX();
                        float nY = event.getY();

                        rightleftImage = (int) Math.floor(nX / cellSize);
                        righttopImage = (int) Math.floor(nY / cellSize);

                        if (abs(nX - origX) < cellSize && abs(nY - origY) < cellSize) {
                            rightAngle += 90;
                        }
                        refX = nX;
                        refY = nY;
                        invalidate();
                    }
                }
                break;
        }
        return true;
    }

    private void drawGridMap(Canvas canvas){
        paint.setColor(mapColour);
        paint.setStrokeWidth(5);

        int c, r;
        for(c = 1; c < 20; c++){
            canvas.drawLine(cellSize*c, 0, cellSize*c, canvas.getWidth(), paint);
        }
        for(r = 1; r < 20; r++){
            canvas.drawLine(0, cellSize*r, canvas.getWidth(), cellSize*r, paint);
        }
    }

    private void drawObstacles(Canvas canvas){
           if (game.getGripMap()[0][0] > 0) {
            rect1 = genRect(15, 15);
            System.out.println("rect1: " + rect1);
            //canvas.drawRect(rect1, paint);

            //resize bitmap
            resizedDown = getResizedBitmap(downBitmap, 1);
            resizedUp = getResizedBitmap(upBitmap, 1);
            resizedLeft = getResizedBitmap(leftBitmap, 1);
            resizedRight = getResizedBitmap(rightBitmap, 1);

            //add bitmaps to an array
            bitmapArray.add(resizedDown);
            bitmapArray.add(resizedUp);
            bitmapArray.add(resizedLeft);
            bitmapArray.add(resizedRight);

            System.out.println("leftImage drawMarkers before: " + downleftImage);
            System.out.println("topImage drawMarkers before: " + downtopImage);

            if (downleftImage >= 0 && downleftImage < 20 && downtopImage >= 0 && downtopImage < 20) {

                //canvas.drawBitmap(resizedDown, downleftImage * cellSize, downtopImage * cellSize, paint);
                //canvas.drawBitmap(resizedUp, upleftImage * cellSize, uptopImage * cellSize, paint);
                //canvas.drawBitmap(resizedLeft, leftleftImage * cellSize, lefttopImage * cellSize, paint);
                //canvas.drawBitmap(resizedRight, rightleftImage * cellSize, righttopImage * cellSize, paint);
                //draw obstacles
                rotateBitmap(canvas, resizedUp, upleftImage, uptopImage, upAngle);
                rotateBitmap(canvas, resizedDown, downleftImage, downtopImage, downAngle);
                rotateBitmap(canvas, resizedLeft, leftleftImage, lefttopImage, leftAngle);
                rotateBitmap(canvas, resizedRight, rightleftImage, righttopImage, rightAngle);

                System.out.println("leftImage drawMarkers after: " + downleftImage);
                System.out.println("topImage drawMarkers after: " + downtopImage);
            }
        }
    }

    private void drawRobot(Canvas canvas) {
        resizedRobot = getResizedBitmap(robotBitmap, 3);
        if (game.getGripMap()[0][0] == 2) {
            rotateBitmap(canvas, resizedRobot, robotleftImage, robottopImage, robotAngle);
        }
        else if (game.getGripMap()[0][0] == 3) {
            System.out.println("moveRobot called: ");
            moveRobot(canvas, resizedRobot, robotleftImage, robottopImage, 1);
        }
        else if (game.getGripMap()[0][0] == 4) {
            moveRobot(canvas, resizedRobot, robotleftImage, robottopImage, -1);
        }
        else if (game.getGripMap()[0][0] == 5) {
            faceDirection -= 90;
            rotateRobot(canvas, resizedRobot, robotleftImage, robottopImage, 1, 270);
        }
    }

    public Rect genRect(int row, int col){
        System.out.println("cellSize1: " + cellSize);
        System.out.println("width: " + getWidth());
        Rect rect = new Rect();
        rect.left = (col+1)*cellSize;
        rect.top = row*cellSize;
        rect.right = row*cellSize;
        rect.bottom = (row+1)*cellSize;
        System.out.println("genRect exe.: " + rect);
        return rect;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int cellNo) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) cellSize * cellNo) / width;
        float scaleHeight = ((float) cellSize * cellNo) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // "RECREATE" THE NEW BITMAP
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

    public void moveRobot(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int direction)
    {
        System.out.println("move robot: " + game.getGripMap()[0][0]);
        System.out.println("move robot leftImage: " + leftImage);
        System.out.println("move robot direction: " + direction);
        Matrix matrix = new Matrix();
        matrix.postTranslate(leftImage*cellSize+cellSize*direction, topImage*cellSize);
        System.out.println("move robotleftImage bef: " + robotleftImage);
        canvas.drawBitmap(bitmap, matrix, null);
        robotleftImage = robotleftImage + 1*direction;
        System.out.println("move robotleftImage aft: " + robotleftImage);
    }

    public void rotateRobot(Canvas canvas, Bitmap bitmap, int leftImage, int topImage, int direction, int angle){
        //TBC
        Matrix matrix = new Matrix();
        matrix.setRotate(angle, bitmap.getWidth()/2, bitmap.getHeight()/2);
        matrix.postTranslate((leftImage+1)*cellSize, (topImage-1)*cellSize);
        canvas.drawBitmap(bitmap, matrix, null);
    }

    public boolean robotDirection(){
        return true;
    }

    public void genObstacles(){
        game.generateObstacles();
    }

    public void genRobot(){
        game.generateRobot();
    }

    public void moveForward(){
        game.moveRobotForward();
    }

    public void moveBackward(){
        game.moveRobotBackward();
    }

    public void rotateLeft(){ game.rotateRobotLeft(); }
}

