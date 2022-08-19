package com.example.mdp40.MapGeneration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.mdp40.R;

public class GridMap extends View implements View.OnTouchListener{

    private final int mapColour;
    private final int XColour;
    private final int OColour;

    private final Paint paint = new Paint();

    private int cellSize = getWidth()/20;

    private final GameLogic game;

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
        super.onMeasure(width, height);

        int dimension = Math.min(getMeasuredWidth(), getMeasuredHeight());
        cellSize = dimension/20;

        setMeasuredDimension(dimension, dimension);
    }

    @Override
    protected void onDraw(Canvas canvas){
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        drawGridMap(canvas);
        drawMarkers(canvas);
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

    public static Rect rect1;

    private void drawMarkers(Canvas canvas){
        int r, c;
        for (r = 0; r < 20; r++) {
            for (c = 0; c < 20; c++) {
                if (game.getGripMap()[r][c] == 0) {
                    rect1 = genRect(1, 1);
                    System.out.println("rect1: " + rect1);
                    canvas.drawRect(rect1, paint);
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

    public void resetMap(){
        game.resetMap();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        rect1.offsetTo(0,0);
        invalidate();
        return true;
    }
}

