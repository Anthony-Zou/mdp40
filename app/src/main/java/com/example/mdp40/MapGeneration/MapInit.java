package com.example.mdp40.MapGeneration;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mdp40.R;

public class MapInit extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_init);

        Button genMapbtn = (Button)findViewById(R.id.genMap);
        Button genRobotBtn = (Button)findViewById(R.id.genRobot);
        Button moveObsBtn = (Button)findViewById(R.id.moveObs);

        Button moveRobBtn = (Button)findViewById(R.id.moveRobot);
        ImageView forwardView = (ImageView)findViewById(R.id.forwardView);
        ImageView backwardView = (ImageView)findViewById(R.id.backwardView);
        ImageView turnLView = (ImageView)findViewById(R.id.turnLView);
        ImageView turnRView = (ImageView)findViewById(R.id.turnRView);

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
                gridMap.genObstacles();
                gridMap.invalidate();
            }
        });

        //Click to move robot
        moveRobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapInit.this, "Moving obstacles...",
                        Toast.LENGTH_LONG).show();
                gridMap.genRobot();
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
    }
}
