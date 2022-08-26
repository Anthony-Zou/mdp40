package com.example.mdp40.MapGeneration;

import static android.widget.Toast.*;

import android.widget.Toast;

public class GameLogic {
    private int[][] gripMap;

    private static int moveRobot = 0;

    GameLogic(){
        gripMap = new int[20][20];
        int r, c;
        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 0;
            }
        }
    }

    /*public boolean updateGameBoard(int row, int col){
        if (gripMap[row-1][col-1] == 1){
            gripMap[row-1][col-1] =
        }
        else{
            return false;
        }
    }*/

    public int[][] getGripMap() {
        return gripMap;
    }

    public void generateObstacles(){
        int r, c;
        System.out.println("123456789");

        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 1;
                moveRobot = 0;
            }
        }
        System.out.println("234567890");
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
}
