package com.example.mdp40.MapGeneration;

import android.widget.TextView;

public class GameLogic {
    private int[][] gripMap;

    private static int moveRobot = 0;
    private TextView robotX;
    private TextView robotY;


    public GameLogic(){
        gripMap = new int[20][20];
        int r, c;
        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 0;
            }
        }
    }

    public int[][] getGripMap() {
        return gripMap;
    }

    public void generateObstacles(){
        int r, c;

        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 1;
                moveRobot = 0;
            }
        }
    }

    public void generateMoreObs(){
        int r, c;

        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 7;
                moveRobot = 0;
            }
        }
    }

    public void moveObstacles(){
        int r, c;

        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 8;
                moveRobot = 0;
            }
        }
    }

    public void generateRobot(){
        int r, c;

        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 2;
                moveRobot += 1;
            }
        }
    }

    public void changeObsId(){
        int r, c;

        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 9;
                moveRobot += 1;
            }
        }
    }

    public void moveRobotForward(){
        int r, c;
        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                if(moveRobot > 0) {
                    gripMap[r][c] = 3;
                }
            }
        }
    }

    public void moveRobotBackward(){
        int r, c;
        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                if(moveRobot > 0) {
                    gripMap[r][c] = 4;
                }
            }
        }
    }

    public void rotateRobotLeft(){
        int r, c;
        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                if(moveRobot > 0) {
                    gripMap[r][c] = 5;
                }
            }
        }
    }

    public void rotateRobotRight(){
        int r, c;
        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                if(moveRobot > 0) {
                    gripMap[r][c] = 6;
                }
            }
        }
    }

    //map init
    public void setRobotX (TextView robotX){
        this.robotX = robotX;
    }

    public void setRobotY (TextView robotY){
        this.robotY = robotY;
    }

    //grid map
    public void displayLoc (int robotLeft, int robotTop){
       robotX.setText(String.valueOf(robotLeft));
        robotY.setText(String.valueOf(robotTop));
    }

    public void clearCanvas(){
        int r, c;

        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = -1;
                moveRobot = 0;
            }
        }
    }
}
