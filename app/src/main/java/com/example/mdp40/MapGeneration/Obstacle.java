package com.example.mdp40.MapGeneration;

public class Obstacle {

    String[][] obsIdentity= {{"11","12","13","14","15","16","17","18","19","20","21","22","23","24","25",
                                "26","27","28","29","30","31","32","33","34","35","36","37","38","39","40"},
                             {"1","2","3","4","5","6","7","8","9","A","B","C","D","E","F",
                                "G","H","S","T","U","V","W","X","Y","Z","NN","SS","WW","EE","OO"}};
    static int newId;

    private GridMap gridMap;

    public int[][] addMoreObs(int no_of_obs, int[][] obsLoc) {
        int newarray[][] = new int[3][no_of_obs];
        for(int i=0;i<3;i++){
            for(int j=0;j<no_of_obs-1;j++){
                newarray[i][j]=obsLoc[i][j];
            }
        }
        obsLoc = newarray;
        //add leftimage
        obsLoc[0][no_of_obs-1] = 0;
        //ThreadLocalRandom.current().nextInt(0, 17);
        //add topimage
        obsLoc[1][no_of_obs-1] = 0;
        //ThreadLocalRandom.current().nextInt(0, 17);
        //add angle
        obsLoc[2][no_of_obs-1] = 0;

        return obsLoc;
    }

    public boolean[] moveNewObs(int no_of_obs, boolean[] toMoveObs) {
        boolean newArray[] = new boolean[no_of_obs];
        for(int i =0;i<no_of_obs-1;i++){
            newArray[i] = toMoveObs[i];
        }
        return newArray;
    }

    public void setNewId(int newId){
        this.newId = newId;
        System.out.println("set newId:" + this.newId);
    }

    public void displayNewId (int newId){
        gridMap.setNewId(newId);
    }

    public String[][] getObsIdentity(){
        return obsIdentity;
    }

}
