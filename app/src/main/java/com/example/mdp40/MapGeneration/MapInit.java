package com.example.mdp40.MapGeneration;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mdp40.R;

public class MapInit extends AppCompatActivity {

    private GridMap gridMap;
    private Obstacle obstacle;

    static String newId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_init);

        gridMap = findViewById(R.id.gridMap);

        //obstacle buttons
        Button genMapbtn = (Button)findViewById(R.id.genMap);
        Button genRobotBtn = (Button)findViewById(R.id.genRobot);
        Button moveObsBtn = (Button)findViewById(R.id.moveObs);
        Button addObsBtn = (Button)findViewById(R.id.addObs);

        //obstacle number picker
        NumberPicker obsId = (NumberPicker)findViewById(R.id.numberPicker);
        obsId.setMinValue(11);
        obsId.setMaxValue(40);

        obsId.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                gridMap.setNewId(i1);
            }
        });

        //robot buttons
        ImageView forwardView = (ImageView)findViewById(R.id.forwardView);
        ImageView backwardView = (ImageView)findViewById(R.id.backwardView);
        ImageView turnLView = (ImageView)findViewById(R.id.turnLView);
        ImageView turnRView = (ImageView)findViewById(R.id.turnRView);

        //robot coordinates
        TextView robotLeftImage = (TextView) findViewById(R.id.robotLeftImage);
        TextView robotRightImage = (TextView) findViewById(R.id.robotRightImage);
        gridMap.displayRobotPos(robotLeftImage, robotRightImage);

        //clear button
        Button clearBtn = (Button)findViewById(R.id.clearCanvas);

        GridMap gridMap = (GridMap)findViewById(R.id.gridMap);

        View downObs = (View)findViewById(R.id.downObs);
        downObs.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                Toast.makeText(MapInit.this, "I have been long clicked",
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });

        //Click to generate obstacles
        genMapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapInit.this, "Obstacles generated",
                        Toast.LENGTH_LONG).show();
                gridMap.genObstacles();
                gridMap.invalidate();
            }
        });

        //Click to generate robot
        genRobotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapInit.this, "Robot generated",
                        Toast.LENGTH_LONG).show();
                gridMap.genRobot();
                gridMap.invalidate();
            }
        });

        //Click to move obstacles
        moveObsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapInit.this, "Moving obstacles...",
                        Toast.LENGTH_LONG).show();
                gridMap.moveObstacles();
                gridMap.invalidate();
            }
        });

        //Click to move robot forward
        forwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.moveForward();
                gridMap.invalidate();
            }
        });

        //Click to move robot backward
        backwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.moveBackward();
                gridMap.invalidate();
            }
        });

        //Click to rotate robot left
        turnLView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.rotateLeft();
                gridMap.invalidate();
            }
        });

        //Click to rotate robot right
        turnRView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.rotateRight();
                gridMap.invalidate();
            }
        });

        //Click to generate more obstacles
        addObsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapInit.this, "Moving obstacles...",
                        Toast.LENGTH_LONG).show();
                gridMap.genMoreObs();
                gridMap.invalidate();
            }
        });

        //Click to clear all
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapInit.this, "Moving obstacles...",
                        Toast.LENGTH_LONG).show();
                gridMap.clearCanvas();
                gridMap.invalidate();
            }
        });
    }

    public void changeId(int newId){
        obstacle.setNewId(newId);
    }
}
