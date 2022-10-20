package com.example.mdp40.MapGeneration;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.mdp40.R;

import java.util.Arrays;

public class Obstacle {

    public String[][] obsIdentity= {{"11","12","13","14","15","16","17","18","19","20","21","22","23","24","25",
                                "26","27","28","29","30","31","32","33","34","35","36","37","38","39","40"},
                             {"1","2","3","4","5","6","7","8","9","A","B","C","D","E","F",
                                "G","H","S","T","U","V","W","X","Y","Z","NN","SS","WW","EE","OO"}};
    private int[] currentId1;
    private String[] currentId;

    private String[] avaiId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int[][] addMoreObs(int no_of_obs, int[][] obsLoc) {
        int newarray[][] = new int[5][no_of_obs];
        for(int i=0;i<5;i++){
            for(int j=0;j<no_of_obs-1;j++){
                newarray[i][j]=obsLoc[i][j];
            }
        }
        obsLoc = newarray;

        //add leftimage
        obsLoc[0][no_of_obs-1] = 0;

        //add topimage
        obsLoc[1][no_of_obs-1] = 0;

        //add angle
        obsLoc[2][no_of_obs-1] = 0;

        //add id
        currentId1 = new int[obsLoc[3].length-1];
        for (int j=0;j<obsLoc[3].length-1;j++){
            currentId1[j] = obsLoc[3][j];
        }
        currentId = Arrays.stream(currentId1)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);
        avaiId = new String[obsIdentity[0].length-currentId.length];
        avaiId = checkAvaiId(currentId);
        obsLoc[3][no_of_obs-1] = Integer.parseInt(avaiId[0]);

        //add size
        obsLoc[4][no_of_obs-1] = 10;

        return obsLoc;
    }

    public boolean[] moveNewObs(int no_of_obs, boolean[] toMoveObs) {
        boolean newArray[] = new boolean[no_of_obs];
        for(int i =0;i<no_of_obs-1;i++){
            newArray[i] = toMoveObs[i];
        }
        return newArray;
    }

    public boolean[] removeMoveObs(int no_of_obs, boolean[] toMoveObs, int obsNum) {
        boolean newArray[] = new boolean[no_of_obs-1];
        int j=0;
        for(int i=0;i<no_of_obs;i++) {
            if (i == obsNum) {
                continue;
            }
            else {
                newArray[j++] = toMoveObs[i];
            }
        }
        return newArray;
    }

    public int[][] removeObs(int no_of_obs, int[][] obsLoc, int obsNum) {
        int newArray[][] = new int[5][no_of_obs-1];
        for(int i=0;i<5;i++){
            int k=0;
            for(int j=0;j<no_of_obs;j++) {
                if (j == obsNum) {
                    continue;
                }
                else {
                    newArray[i][k++] = obsLoc[i][j];
                }
            }
        }
        return newArray;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String[] checkAvaiId(String[] currentId) {
        String[] allId = obsIdentity[0];
        String[] avaiId = new String[allId.length-currentId.length];
        int avaiIdcount=0;
        for(int i=0;i<allId.length;i++){
            if(!Arrays.stream(currentId).anyMatch(allId[i]::equals)){
                avaiId[avaiIdcount] = allId[i];
                avaiIdcount++;
            }
        }
        return avaiId;
    }

    public String[][] getObsIdentity(){
        return obsIdentity;
    }

}
