package com.example.mdp40.MapGeneration;

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

import com.example.mdp40.R;

public class GridMap extends View{

    private final int mapColour;
    private final int XColour;
    private final int OColour;

    private final Paint paint = new Paint();

    private int cellSize = 500/20;

    private final GameLogic game;

    public static Rect rect1;
    public static Bitmap resizedDown, resizedUp, resizedLeft, resizedRight;

    private float refX, refY;
    private boolean toMove = false;

    public static int downtopImage = 7, downleftImage = 8;
    private int uptopImage = 15, upleftImage = 15;
    private int lefttopImage = 10, leftleftImage = 11;
    private int righttopImage = 18, rightleftImage = 20;

    int nearestLeftUnit, nearestTopUnit;


    Bitmap downBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.down);
    Bitmap upBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.up);
    Bitmap leftBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left);
    Bitmap rightBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.right);

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
        drawMarkers(canvas);
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // finger touches the screen
                System.out.println("touch action down: ");
                refY = y;
                refX = x;
                if((refX >= downleftImage*cellSize && refX <= resizedDown.getWidth() + downleftImage*cellSize)
                    && (refY >= downtopImage*cellSize && refY <= downtopImage*cellSize + resizedDown.getHeight())){
                    System.out.println("touch action down if condition: ");
                    System.out.println("touch action down if condition x: "+x);
                    System.out.println("touch action down if condition y: "+y);
                    System.out.println("touch action down if condition refX: "+refX);
                    System.out.println("touch action down if condition refY: "+refY);
                    toMove = true;
                }
                else{
                    System.out.println("touch action down else condition: ");
                    toMove = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // finger moves on the screen
                System.out.println("touch action move: ");
                if (toMove) {
                    float nX = event.getX();
                    float nY = event.getY();


                    System.out.println("leftImage Move before: "+downleftImage);

                    downleftImage = (int) Math.floor(nX / cellSize);
                    //downleftImage = nearestLeftUnit * cellSize;
                    downtopImage = (int) Math.floor(nY / cellSize);

                    //System.out.println("topImage Move before: "+downtopImage);
                    //downleftImage = downleftImage * cellSize;
                    //downtopImage = downtopImage * cellSize;
                   /* downleftImage += nX - refX;
                    downtopImage += nY - refY;*/
                    //downleftImage = downleftImage / cellSize;
                    //downtopImage = downtopImage / cellSize;
                    System.out.println("leftImage Move after: "+downleftImage);
                    System.out.println("topImage Move after: "+downtopImage);

                    System.out.println("nX Move: "+nX);
                    System.out.println("nY Move: "+nY);
                    System.out.println("refX Move: "+refX);
                    System.out.println("refY Move: "+refY);

                    refX = nX;
                    refY = nY;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                if(toMove) {
                    System.out.println("touch action up: ");
                    float nX = event.getX();
                    float nY = event.getY();

                    downleftImage = (int) Math.floor(nX / cellSize);
                    //downleftImage = nearestLeftUnit * cellSize;
                    downtopImage = (int) Math.floor(nY / cellSize);
                    //downtopImage = nearestTopUnit * cellSize;
                    System.out.println("Left Up: " + downleftImage);
                    System.out.println("Top Up: " + downtopImage);

                    refX = nX;
                    refY = nY;
                    invalidate();
                }
                break;
        }
        // tell the system that we handled the event and no further processing is required
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

    private void drawMarkers(Canvas canvas){
        int r, c;
        for (r = 0; r < 20; r++) {
            for (c = 0; c < 20; c++) {
                if (game.getGripMap()[r][c] == 0) {
                    rect1 = genRect(15, 15);
                    System.out.println("rect1: " + rect1);
                    canvas.drawRect(rect1, paint);

                    //resize bitmap
                    resizedDown = getResizedBitmap(downBitmap);
                    resizedUp = getResizedBitmap(upBitmap);
                    resizedLeft = getResizedBitmap(leftBitmap);
                    resizedRight = getResizedBitmap(rightBitmap);

                    //
                    /*nearestLeftUnit = (int)Math.floor(leftImage/cellSize);
                    leftImage = nearestLeftUnit*cellSize;

                    nearestTopUnit = (int)Math.floor(topImage/cellSize);
                    topImage = nearestTopUnit*cellSize;*/

                    //draw bitmap
                    System.out.println("leftImage drawMarkers before: "+downleftImage);
                    System.out.println("topImage drawMarkers before: "+downtopImage);

                    //downleftImage *= cellSize;
                    //downtopImage *= cellSize;
                    canvas.drawBitmap(resizedDown, downleftImage*cellSize, downtopImage*cellSize, paint);
                    System.out.println("leftImage drawMarkers after: "+downleftImage);
                    System.out.println("topImage drawMarkers after: "+downtopImage);
                    /*drawObstacle(canvas, 7, 8);
                    drawObstacle(canvas, 5, 12);
                    drawObstacle(canvas, 18, 17);*/
                }
            }
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

    public Bitmap getResizedBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) cellSize) / width;
        float scaleHeight = ((float) cellSize) / height;
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

    public void resetMap(){
        game.resetMap();
    }
}

