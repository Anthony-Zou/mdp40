package com.example.mdp40.MapGeneration;

public class Robot {

    public Robot() {
    }

    //detect collision with boundary and obstacles when the robot is moving FORWARD/BACKWARD
    //if not, move robot according to FORWRAD/BACKWARD command
    public int[] moveRobot(int leftImage, int topImage, int direction, int faceDirection, int[][] obsLocation)
    {
        int[] moveValues = new int[2];

        if (direction == 1) {
            switch (faceDirection) {
                case 270:
                    if (!isBlocked(leftImage, topImage-1, obsLocation)) {
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

    //detect collision with boundary and obstacles when the robot is moving FORWARD LEFT/FORWARD RIGHT
    //if not, move robot according to FORWARD LEFT/FORWARD RIGHT command
    public int[] rotateRobot(int leftImage, int topImage, int direction, int faceDirection, int[][] obsLocation) {
        int[] rotateValues = new int[3];
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
                        topImage += 1;
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
        rotateValues[0] = faceDirection;
        rotateValues[1] = leftImage;
        rotateValues[2] = topImage;
        return rotateValues;
    }

    //detect collision with boundary and obstacles when the robot is moving BACKWARD LEFT/BACKWARD RIGHT
    //if not, move robot according to BACKWARD LEFT/BACKWARD RIGHT command
    public int[] rotateBackRobot(int leftImage, int topImage, int direction, int faceDirection,  int[][] obsLocation) {
        int[] rotateValues = new int[3];
        if (direction == 1) {
            switch (faceDirection) {
                case 270:
                        leftImage -= 1;
                        topImage += 1;
                        faceDirection += 90;
                    break;
                case 180:
                        leftImage += 1;
                        topImage += 1;
                        faceDirection += 90;
                    break;
                case 90:
                        leftImage += 1;
                        topImage -= 1;
                        faceDirection += 90;
                    break;
                default:
                        leftImage -= 1;
                        topImage -= 1;
                        faceDirection += 90;
            }
        }
        else {
            switch (faceDirection) {
                case 270:
                        leftImage += 1;
                        topImage += 1;
                        faceDirection -= 90;
                    break;
                case 180:
                        leftImage += 1;
                        topImage -= 1;
                        faceDirection -= 90;
                    break;
                case 90:
                        leftImage -= 1;
                        topImage -= 1;
                        faceDirection -= 90;
                    break;
                default:
                        leftImage -= 1;
                        topImage += 1;
                        faceDirection -= 90;
            }
        }
        rotateValues[0] = faceDirection;
        rotateValues[1] = leftImage;
        rotateValues[2] = topImage;
        return rotateValues;
    }

    public boolean isBlocked (int NextrobotLeft, int NextrobotTop, int[][] obsLoc){
        int i;
        if(NextrobotLeft>17 || NextrobotLeft<0 || NextrobotTop>17 || NextrobotTop<0){
            return true;
        }
        for (i = 0; i < obsLoc[0].length;i++){
            if (NextrobotLeft <= obsLoc[0][i] && NextrobotLeft+3 > obsLoc[0][i]){
                if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+3 > obsLoc[1][i]){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isLeftRotatable (int nextRobotLeft, int NextrobotTop, int faceDirection, int[][] obsLoc){
        int i;
        if(nextRobotLeft>17 || nextRobotLeft<0 || NextrobotTop>17 || NextrobotTop<0){
            return false;
        }

        switch(faceDirection){
            case 270:
                for (i = 0; i < obsLoc[0].length;i++){
                    if ((nextRobotLeft == obsLoc[0][i]  || nextRobotLeft+1==obsLoc[0][i])){
                        if(NextrobotTop == obsLoc[1][i]){
                            return false;
                        }
                    }
                    if((nextRobotLeft+2 == obsLoc[0][i])){
                        if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+3 >= obsLoc[1][i]){
                            return false;
                        }
                    }
                }
                break;
            case 180:
                for (i = 0; i < obsLoc[0].length;i++){
                    if ((nextRobotLeft+1 == obsLoc[0][i]  && nextRobotLeft+3>=obsLoc[0][i])){
                        if(NextrobotTop == obsLoc[1][i]){
                            return false;
                        }
                    }
                    if((nextRobotLeft == obsLoc[0][i])){
                        if(NextrobotTop <= obsLoc[1][i] &&NextrobotTop+2 >= obsLoc[1][i]){
                            return false;
                        }
                    }
                }
                break;
            case 90:
                for (i = 0; i < obsLoc[0].length;i++){
                    if ((nextRobotLeft+1 == obsLoc[0][i]  || nextRobotLeft+2==obsLoc[0][i])){
                        if(NextrobotTop+2 == obsLoc[1][i]){
                            return false;
                        }
                    }
                    if((nextRobotLeft == obsLoc[0][i])){
                        if(NextrobotTop-1 <= obsLoc[1][i] && NextrobotTop+2 >= obsLoc[1][i]){
                            return false;
                        }
                    }
                }
                break;
            default:
                for (i = 0; i < obsLoc[0].length;i++){
                    if ((nextRobotLeft-1 <= obsLoc[0][i] && nextRobotLeft+1 >= obsLoc[0][i])){
                        if(NextrobotTop+2 == obsLoc[1][i]){
                            return false;
                        }
                    }
                    if((nextRobotLeft+2 == obsLoc[0][i])){
                        if(NextrobotTop <= obsLoc[1][i] &&NextrobotTop+2 >= obsLoc[1][i]){
                            return false;
                        }
                    }
                }

        }
        return true;
    }

    public boolean isRightRotatable (int NextrobotLeft, int NextrobotTop, int faceDirection, int[][] obsLoc){
        int i;
        if(NextrobotLeft>17 || NextrobotLeft<0 || NextrobotTop>17 || NextrobotTop<0){
            return false;
        }
        switch(faceDirection){
            case 270:
                for (i = 0; i < obsLoc[0].length;i++){
                    if ((NextrobotLeft == obsLoc[0][i])){
                        if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+3 >= obsLoc[1][i]){
                            return false;
                        }
                    }
                    if(NextrobotLeft+1 <= obsLoc[0][i] && NextrobotLeft+2 >= obsLoc[0][i]){
                        if(NextrobotTop == obsLoc[1][i]){
                            return false;
                        }
                    }
                }
                break;
            case 180:
                for (i = 0; i < obsLoc[0].length;i++){
                    if (NextrobotLeft == obsLoc[0][i]){
                        if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+2 >= obsLoc[1][i]){
                            return false;
                        }
                    }
                    if(NextrobotLeft+1 <= obsLoc[0][i] && NextrobotLeft+3 >= obsLoc[0][i]){
                        if(NextrobotTop+2 == obsLoc[1][i]){
                            return false;
                        }
                    }
                }
                break;
            case 90:
                for (i = 0; i < obsLoc[0].length;i++){
                    if (NextrobotLeft+2 == obsLoc[0][i]){
                        if(NextrobotTop-1 <= obsLoc[1][i] && NextrobotTop+2 >= obsLoc[1][i]){
                            return false;
                        }
                    }
                    if(NextrobotLeft <= obsLoc[0][i] && NextrobotLeft+1 >= obsLoc[0][i]){
                        if(NextrobotTop+2 == obsLoc[1][i]){
                            return false;
                        }
                    }
                }
                break;
            default:
                for (i = 0; i < obsLoc[0].length;i++){
                    if (NextrobotLeft+2 == obsLoc[0][i]){
                        if(NextrobotTop <= obsLoc[1][i] && NextrobotTop+2 >= obsLoc[1][i]){
                            return false;
                        }
                    }
                    if(NextrobotLeft-1 <= obsLoc[0][i] && NextrobotLeft+1 >= obsLoc[0][i]){
                        if(NextrobotTop == obsLoc[1][i]){
                            return false;
                        }
                    }
                }

        }
        return true;
    }

}
