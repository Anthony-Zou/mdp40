package com.example.mdp40.MapGeneration;

public class GameLogic {
    private int[][] gripMap;

    GameLogic(){
        gripMap = new int[20][20];
        int r, c;
        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 1;
            }
        }
    }

    public int[][] getGripMap() {
        return gripMap;
    }

    public void resetMap(){
        int r, c;
        System.out.println("here");
        for (r = 0; r < 20; r++){
            for (c = 0; c < 20; c++){
                gripMap[r][c] = 0;
            }
        }
        System.out.println("reset completed");
    }
}