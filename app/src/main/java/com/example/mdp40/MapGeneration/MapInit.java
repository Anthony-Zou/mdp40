package com.example.mdp40.MapGeneration;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mdp40.R;

import java.util.Arrays;

public class MapInit extends AppCompatActivity {

    private GridMap gridMap;
    private GameLogic game;
    private Obstacle obstacle;

    static int currentObs;
    boolean changeId = false;

    int[] currentId1;
    String[] currentId;
    String[] avaiId;
    String[] allId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_init);

        gridMap = findViewById(R.id.gridMap);
        game = new GameLogic();
        obstacle = new Obstacle();

        //obstacle buttons
        Button genMapbtn = (Button)findViewById(R.id.genMap);
        Button genRobotBtn = (Button)findViewById(R.id.genRobot);
        Button moveObsBtn = (Button)findViewById(R.id.moveObs);
        Button addObsBtn = (Button)findViewById(R.id.addObs);
        Button changeIdBtn = (Button)findViewById(R.id.changeId);

        //obstacle number picker
        NumberPicker obsId = (NumberPicker)findViewById(R.id.numberPicker);
        currentId1 = gridMap.obsLocation[3];
        currentId = Arrays.stream(currentId1)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);

        System.out.println("currentId: "+Arrays.toString(currentId));
        allId = obstacle.obsIdentity[0];
        avaiId = new String[allId.length-currentId.length];


        avaiId = obstacle.checkAvaiId(currentId);
        obsId.setDisplayedValues(allId);
        obsId.setMaxValue(allId.length-1);
        obsId.setMinValue(0);
        obsId.setWrapSelectorWheel(false);


        obsId.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if (changeId) {
                    boolean idUsed = true;
                    System.out.println("avaiId now: "+Arrays.toString(avaiId));
                    for (int j=0; j<avaiId.length; j++) {
                        if (allId[i1].equals(avaiId[j])) {
                            idUsed = false;
                        }
                    }
                    if (!idUsed){
                        //remove new id from avaiId, add back the old id
                        System.out.println("avaiId old: "+Arrays.toString(avaiId));
                        for (int k=0; k<avaiId.length; k++){
                            if (avaiId[k].equals(allId[i1])){
                                System.out.println("prev id: "+gridMap.obsLocation[3][currentObs]);
                                avaiId[k] = String.valueOf(gridMap.obsLocation[3][currentObs]);
                            }
                        }
                        gridMap.obsLocation[3][currentObs] = Integer.valueOf(allId[i1]);
                        System.out.println("avaiId new: "+Arrays.toString(avaiId));
                        gridMap.obsLocation[4][currentObs] = 18;
                    }
                    else{
                        //gridMap.obsLocation[3][currentObs] = Integer.valueOf(allId[i]);
                        Toast.makeText(MapInit.this, "This id has been used", Toast.LENGTH_LONG).show();
                    }
                    //System.out.println("i1"+i1);
                    //System.out.println("Gridmap obslocation:"+ Arrays.toString(gridMap.obsLocation[3]));
                }
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

        //Click to change obstacle id
        changeIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.changeObsId();
                gridMap.invalidate();
                changeId = true;
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

    public void retrieveCurrentObs(int currentObs){
        this.currentObs = currentObs;
    }
}
