package com.example.mdp40.MapGeneration;

import java.util.Arrays;

public class Robot {

    public Robot() {
    }

    public int[] moveRobot(int leftImage, int topImage, int direction, int faceDirection, int[][] obsLocation)
    {
        int[] moveValues = new int[2];
        //System.out.println("move robot leftImage: " + leftImage);
        //System.out.println("move robot direction: " + direction);
        //robotleftImage = robotleftImage + 1*direction;
        if (direction == 1) {
            switch (faceDirection) {
                case 270:
                    if (!isBlocked(leftImage, topImage-1, obsLocation)) {
                        //System.out.println("robot top image="+ topImage);
                        System.out.println("obstacles' leftimages="+ Arrays.toString(obsLocation[0]));
                        System.out.println("obstacles' topimages="+ Arrays.toString(obsLocation[1]));
                        topImage -= 1;
                    }
                    break;
                case 180:
                    if (!isBlocked(leftImage-1, topImage, obsLocation)) {
                        leftImage -= 1;
                    }
                    break;
                case 90:
                    if (!isBlocked(leftImage, topImage+1, obsLocation)) {
                        topImage += 1;
                    }
                    break;
                default:
                    if (!isBlocked(leftImage+1, topImage, obsLocation)) {
                        leftImage += 1;
                    }
            }
        }
        if (direction == -1) {
            switch (faceDirection) {
                case 270:
                    if (!isBlocked(leftImage, topImage+1, obsLocation)) {
                        topImage += 1;
                    }
                    break;
                case 180:
                    if (!isBlocked(leftImage+1, topImage, obsLocation)) {
                        leftImage += 1;
                    }
                    break;
                case 90:
                    if (!isBlocked(leftImage, topImage-1, obsLocation)) {
                        topImage -= 1;
                    }
                    break;
                default:
                    if (!isBlocked(leftImage-1, topImage, obsLocation)) {
                        leftImage -= 1;
                    }
            }
        }
        moveValues[0] = leftImage;
        moveValues[1] = topImage;
        return moveValues;
    }

    public int[] rotateRobot(int leftImage, int topImage, int direction, int faceDirection, int rotation, int[][]obsLocation) {
        int[] rotateValues = new int[3];
        if (rotation == 1) {
            if (direction == 1) {
                switch (faceDirection - 90) {
                    case 270:
                        if (isLeftRotatable(leftImage + 1, topImage - 1, 270, obsLocation)) {
                            leftImage += 1;
                            topImage -= 1;
                            faceDirection -= 90;
                        }
                        break;
                    case 180:
                        if (isLeftRotatable(leftImage - 1, topImage - 1, 180, obsLocation)) {
                            leftImage -= 1;
                            topImage -= 1;
                            faceDirection -= 90;
                        }
                        break;
                    case 90:
                        if (isLeftRotatable(leftImage - 1, topImage + 1, 90, obsLocation)) {
                            leftImage -= 1;
                            topImage += 1 ;
                            faceDirection -= 90;
                        }
                        break;
                    default:
                        if (isLeftRotatable(leftImage + 1, topImage + 1, 360, obsLocation)) {
                            leftImage += 1;
                            topImage += 1;
                            faceDirection -= 90;
                        }
                }
            } else {
                switch (faceDirection + 90) {
                    case 270:
                        if (isRightRotatable(leftImage - 1, topImage - 1, 270, obsLocation)) {
                            leftImage -= 1;
                            topImage -= 1;
                            faceDirection += 90;
                        }
                        break;
                    case 180:
                        if (isRightRotatable(leftImage - 1, topImage + 1, 180, obsLocation)) {
                            leftImage -= 1;
                            topImage += 1;
                            faceDirection += 90;
                        }
                        break;
                    case 90:
                        if (isRightRotatable(leftImage + 1, topImage + 1, 90, obsLocation)) {
                            leftImage += 1;
                            topImage += 1;
                            faceDirection += 90;
                        }
                        break;
                    default:
                        if (isRightRotatable(leftImage + 1, topImage - 1, 0, obsLocation)) {
                            leftImage += 1;
                            topImage -= 1;
                            faceDirection += 90;
                        }
                }
            }
        }

        if (rotation == -1) {
            if (direction == 1) {
                switch (faceDirection) {
                    case 270:
                        if (isLeftRotatable(leftImage, topImage, faceDirection, obsLocation)) {
                            leftImage -= 1;
                            topImage += 1;
                            faceDirection += 90;
                        }
                        break;
                    case 180:
                        if (isLeftRotatable(leftImage, topImage, faceDirection, obsLocation)) {
                            leftImage += 1;
                            topImage += 1;
                            faceDirection += 90;
                        }
                        break;
                    case 90:
                        if (isLeftRotatable(leftImage, topImage, faceDirection, obsLocation)) {
                            leftImage += 1;
                            topImage -= 1;
                            faceDirection += 90;
                        }
                        break;
                    default:
                        if (isLeftRotatable(leftImage, topImage, faceDirection, obsLocation)) {
                            leftImage -= 1;
                            topImage -= 1;
                            faceDirection += 90;
                        }
                }
            } else {
                switch (faceDirection + 90) {
                    case 270:
                        if (isRightRotatable(leftImage - 1, topImage - 1, 270, obsLocation)) {
                            leftImage -= 1;
                            topImage -= 1;
                            faceDirection += 90;
                        }
                        break;
                    case 180:
                        if (isRightRotatable(leftImage - 1, topImage + 1, 180, obsLocation)) {
                            leftImage -= 1;
                            topImage += 1;
                            faceDirection += 90;
                        }
                        break;
                    case 90:
                        if (isRightRotatable(leftImage + 1, topImage + 1, 90, obsLocation)) {
                            leftImage += 1;
                            topImage += 1;
                            faceDirection += 90;
                        }
                        break;
                    default:
                        if (isRightRotatable(leftImage + 1, topImage - 1, 0, obsLocation)) {
                            leftImage += 1;
                            topImage -= 1;
                            faceDirection += 90;
                        }
                }
            }
        }
        rotateValues[0] = faceDirection;
        rotateValues[1] = leftImage;
        rotateValues[2] = topImage;
        return rotateValues;
    }

    public boolean isBlocked (int NextrobotLeft, int NextrobotTop, int[][] obsLoc){
        int i;
        //System.out.println("checking robot next image: "+NextrobotLeft+","+NextrobotTop);
        if(NextrobotLeft>17 || NextrobotLeft<0 || NextrobotTop>17 || NextrobotTop<0){
            return true;
        }
        for (i = 0; i < obsLoc[0].length;i++){
            //System.out.println("checking "+ i+"th obstacle");
            if (NextrobotLeft <= obsLoc[0][i] && NextrobotLeft+3 > obsLoc[0][i]){
                //System.out.println("left matches for "+i+"th obstacle");
                if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+3 > obsLoc[1][i]){
                    //System.out.println("top matches");
                    return true;
                }
            }
        }
        //System.out.println("not blocked!");
        return false;
    }

    public boolean isLeftRotatable (int nextRobotLeft, int NextrobotTop, int faceDirection, int[][] obsLoc){
        int i;
        System.out.println("checking robot next image: "+nextRobotLeft+","+NextrobotTop);
        if(nextRobotLeft>17 || nextRobotLeft<0 || NextrobotTop>17 || NextrobotTop<0){
            return false;
        }
        //need to edit to match rotation

        switch(faceDirection){
            case 270:
                for (i = 0; i < obsLoc[0].length;i++){
                    //System.out.println("checking "+ i+"th obstacle");

                    if ((nextRobotLeft == obsLoc[0][i]  || nextRobotLeft+1==obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop == obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                    if((nextRobotLeft+2 == obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+3 >= obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                }
                break;
            case 180:
                for (i = 0; i < obsLoc[0].length;i++){
                    //System.out.println("checking "+ i+"th obstacle");

                    if ((nextRobotLeft+1 == obsLoc[0][i]  && nextRobotLeft+3>=obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop == obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                    if((nextRobotLeft == obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop <= obsLoc[1][i] &&NextrobotTop+2 >= obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                }
                break;
            case 90:
                for (i = 0; i < obsLoc[0].length;i++){
                    //System.out.println("checking "+ i+"th obstacle");

                    if ((nextRobotLeft+1 == obsLoc[0][i]  || nextRobotLeft+2==obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop+2 == obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                    if((nextRobotLeft == obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop-1 <= obsLoc[1][i] && NextrobotTop+2 >= obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                }
                break;
            default:
                for (i = 0; i < obsLoc[0].length;i++){
                    //System.out.println("checking "+ i+"th obstacle");

                    if ((nextRobotLeft-1 <= obsLoc[0][i] && nextRobotLeft+1 >= obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop+2 == obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                    if((nextRobotLeft+2 == obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop <= obsLoc[1][i] &&NextrobotTop+2 >= obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                }

        }

        //System.out.println("not blocked!");
        return true;
    }

    public boolean isRightRotatable (int NextrobotLeft, int NextrobotTop, int faceDirection, int[][] obsLoc){
        int i;
        System.out.println("checking robot next image: "+NextrobotLeft+","+NextrobotTop);
        if(NextrobotLeft>17 || NextrobotLeft<0 || NextrobotTop>17 || NextrobotTop<0){
            return false;
        }
        switch(faceDirection){
            case 270:
                for (i = 0; i < obsLoc[0].length;i++){
                    //System.out.println("checking "+ i+"th obstacle");

                    if ((NextrobotLeft == obsLoc[0][i])){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+3 >= obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                    if(NextrobotLeft+1 <= obsLoc[0][i] && NextrobotLeft+2 >= obsLoc[0][i]){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop == obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                }
                break;
            case 180:
                for (i = 0; i < obsLoc[0].length;i++){
                    //System.out.println("checking "+ i+"th obstacle");

                    if (NextrobotLeft == obsLoc[0][i]){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+2 >= obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                    if(NextrobotLeft+1 <= obsLoc[0][i] && NextrobotLeft+3 >= obsLoc[0][i]){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop+2 == obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                }
                break;
            case 90:
                for (i = 0; i < obsLoc[0].length;i++){
                    //System.out.println("checking "+ i+"th obstacle");

                    if (NextrobotLeft+2 == obsLoc[0][i]){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop-1 <= obsLoc[1][i] && NextrobotTop+2 >= obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                    if(NextrobotLeft <= obsLoc[0][i] && NextrobotLeft+1 >= obsLoc[0][i]){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop+2 == obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                }
                break;
            default:
                for (i = 0; i < obsLoc[0].length;i++){
                    //System.out.println("checking "+ i+"th obstacle");

                    if (NextrobotLeft+2 == obsLoc[0][i]){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+2 >= obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                    if(NextrobotLeft-1 <= obsLoc[0][i] && NextrobotLeft+1 >= obsLoc[0][i]){
                        //System.out.println("left matches for "+i+"th obstacle");
                        if(NextrobotTop == obsLoc[1][i]){
                            //System.out.println("top matches");
                            return false;
                        }
                    }
                }

        }

        //System.out.println("not blocked!");
        return true;
    }

}
