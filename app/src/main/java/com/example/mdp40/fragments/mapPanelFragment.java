package com.example.mdp40.fragments;

import static com.example.mdp40.Constants.AlgActionDisconnect;
import static com.example.mdp40.Constants.AlgActionPlan_path;
import static com.example.mdp40.Constants.AlgActionSetObs;
import static com.example.mdp40.Constants.StmActionA;
import static com.example.mdp40.Constants.StmActionD;
import static com.example.mdp40.Constants.StmActionE;
import static com.example.mdp40.Constants.StmActionQ;
import static com.example.mdp40.Constants.StmActionS;
import static com.example.mdp40.Constants.StmActionW;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.mdp40.Constants;
import com.example.mdp40.MapGeneration.GameLogic;
import com.example.mdp40.MapGeneration.GridMap;
import com.example.mdp40.MapGeneration.Obstacle;
import com.example.mdp40.R;
import com.example.mdp40.bluetooth40.BluetoothService;

import java.util.ArrayList;
import java.util.Arrays;

public class mapPanelFragment extends Fragment {

    private Button forwardBtn;

    private ArrayAdapter<String> consoleArrayAdapter;
    private String connectedDeviceName;
    private BluetoothService bluetoothService;
    private final Handler handler = new Handler(Looper.myLooper(), message ->{
        switch(message.what){
            case Constants.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) message.obj;
                String writeMessage = new String(writeBuf);
                consoleArrayAdapter.add("me: " + writeMessage);
                break;
            case Constants.MESSAGE_READ:
                byte[] readBuf = (byte[]) message.obj;
                String readMessage = new String(readBuf,0,message.arg1);
                consoleArrayAdapter.add(connectedDeviceName +" : "+readMessage);
                break;
            case Constants.MESSAGE_DEVICE_NAME:
                forwardBtn.setEnabled(true);
                connectedDeviceName = message.getData().getString(Constants.DEVICE_NAME);
                if(connectedDeviceName != null){
                    Toast.makeText(getContext(),"Connected to "+ connectedDeviceName, Toast.LENGTH_SHORT).show();
                }
                break;

        }
        return false;
    });



    public mapPanelFragment() {
        // Required empty public constructor
    }

    private GridMap gridMap;
    private GameLogic game;
    private Obstacle obstacle;
    private consoleFragment console ;
    String textMessage= "";
    static int currentObs;
    static int currentRobotL;
    static int currentRobotR;
    TextView robotStatus;
    Button genRobotBtn;
    String direction = new String();
    static int faceDirection;
    boolean changeId = false;

    int[] currentId1;
    String[] currentId;
    String[] avaiId;
    String[] allId;




    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }






    public void setBluetoothService(BluetoothService bluetoothService){
        this.bluetoothService = bluetoothService;
    }
    Handler getHandler(){
        return handler;
    }

    @SuppressLint("NewApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridMap = view.findViewById(R.id.gridMap);
        game = new GameLogic();
        obstacle = new Obstacle();
        console = new consoleFragment();

        ///


        consoleArrayAdapter = new ArrayAdapter<>(getContext(),R.layout.item_message);


        ///

        //obstacle buttons
        Button genMapbtn = (Button)view.findViewById(R.id.genMap);
        Button moveObsBtn = (Button)view.findViewById(R.id.moveObs);
        Button addObsBtn = (Button)view.findViewById(R.id.addObs);
        Button changeIdBtn = (Button)view.findViewById(R.id.changeId);
        Button updateObsBtn = (Button)view.findViewById(R.id.updateObs);

        //obstacle number picker
        NumberPicker obsId = (NumberPicker)view.findViewById(R.id.numberPicker);
        currentId1 = gridMap.obsLocation[3];
        currentId = Arrays.stream(currentId1)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);

        System.out.println("currentId: "+Arrays.toString(currentId));
        allId = obstacle.obsIdentity[0];
        avaiId = new String[allId.length-currentId.length];
        //bluetoothService.write(data.getBytes());
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
                        Toast.makeText(getContext(), "This id has been used", Toast.LENGTH_LONG).show();
                    }
                    //System.out.println("i1"+i1);
                    //System.out.println("Gridmap obslocation:"+ Arrays.toString(gridMap.obsLocation[3]));
                }
            }
        });

        //robot buttons
        genRobotBtn = (Button)view.findViewById(R.id.genRobot);
        ImageView forwardView = (ImageView)view.findViewById(R.id.forwardView);
        ImageView backwardView = (ImageView)view.findViewById(R.id.backwardView);
        ImageView turnLView = (ImageView)view.findViewById(R.id.turnLView);
        ImageView turnRView = (ImageView)view.findViewById(R.id.turnRView);
        ImageView turnBLView = (ImageView)view.findViewById(R.id.turnBLView);
        ImageView turnBRView = (ImageView)view.findViewById(R.id.turnBRView);

        //robot coordinates
        TextView robotLeftImage = (TextView) view.findViewById(R.id.robotLeftImage);
        TextView robotTopImage = (TextView) view.findViewById(R.id.robotRightImage);
        robotStatus = (TextView) view.findViewById(R.id.robotStatus);
        gridMap.displayRobotPos(robotLeftImage, robotTopImage);

        //clear button
        Button clearBtn = (Button)view.findViewById(R.id.clearCanvas);

        //gridmap & algo buttons
        GridMap gridMap = (GridMap)view.findViewById(R.id.gridMap);
        Button planPathBtn = (Button)view.findViewById(R.id.planPath);
        Button disconnectAlgoBtn = (Button)view.findViewById(R.id.disconnect);

        //Click to generate obstacles
        genMapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Obstacles generated",
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
                Toast.makeText(getContext(), "Robot generated",
                        Toast.LENGTH_LONG).show();
                robotStatus.setText("Ready to Start");
                gridMap.genRobot();
                gridMap.invalidate();

//                for(int i = 0; i <= 3; i++) {
//                    SystemClock.sleep(100);
//                    gridMap.moveForward();
//                    gridMap.invalidate();
//                    Toast.makeText(getContext(),
//                i+"(s) passed",
//                    Toast.LENGTH_LONG).show();
//                }

            }
        });

        //Click to move obstacles
        moveObsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Moving obstacles...",
                        Toast.LENGTH_LONG).show();
                gridMap.moveObstacles();
                gridMap.invalidate();
            }
        });

        //Click to move robot forward
        forwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speed = "010";
                sendBTMessage(bluetoothService, StmActionW+speed);

                gridMap.moveForward();
                gridMap.invalidate();
            }
        });

        //Click to move robot backward
        backwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speed = "010";
                sendBTMessage(bluetoothService, StmActionS+speed);
               // robotStatus.setText("Moving Backward");
                gridMap.moveBackward();
                gridMap.invalidate();
            }
        });

        //Click to rotate robot left
        turnLView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(bluetoothService, StmActionQ);
                gridMap.rotateLeft();
                gridMap.invalidate();
            }
        });

        //Click to rotate robot right
        turnRView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(bluetoothService, StmActionE);
                //robotStatus.setText("Turning Right");
                gridMap.rotateRight();
                gridMap.invalidate();
            }
        });

        //Click to rotate robot back left
        turnBLView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(bluetoothService, StmActionA);
                gridMap.rotateBackLeft();
                gridMap.invalidate();
            }
        });

        //Click to rotate robot back right
        turnBRView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(bluetoothService, StmActionD);
                gridMap.rotateBackRight();
                gridMap.invalidate();
            }
        });

        //Click to generate more obstacles
        addObsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Moving obstacles...",
                        Toast.LENGTH_LONG).show();
                gridMap.genMoreObs();
                gridMap.invalidate();
            }
        });

        //Click to send obstacle location to algo
        updateObsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendObsInfo(bluetoothService);
            }
        });

        //Click to send message algo and plan the shortest path
        planPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlgActionPlan_path();
            }
        });

        //Click to disconnect algo
        disconnectAlgoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlgActionDisconnect();
            }
        });

        //Click to clear all
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Moving obstacles...",
                        Toast.LENGTH_LONG).show();
                gridMap.clearCanvas();
                gridMap.invalidate();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panel_center, container, false);

    }

    public void retrieveCurrentObs(int currentObs){
        this.currentObs = currentObs;
    }

    public void retrieveCurrentRobot(int currentRobotL, int currentRobotR, int faceDirection){
        this.currentRobotL = currentRobotL;
        this.currentRobotR = currentRobotR;
        this.faceDirection = faceDirection;
        System.out.println("CurrentRobotL retrieve method: " + currentRobotL+ " "+ currentRobotR + " " + faceDirection);
    }

    public String getFaceDirection(int faceDirection){
        String direction = new String();
        if (faceDirection == 90){
            direction ="S";
        }
        else if (faceDirection == 180){
            direction = "W";
        }
        else if (faceDirection == 270){
            direction = "N";
        }
        else{
            direction = "E";
        }
        System.out.println("direction getFaceDirection: " + direction);
        return direction;
    }

    int oldX=0;
    int oldY=0;
    public void sendBTMessage(BluetoothService bluetoothService, String action){
        direction = getFaceDirection(faceDirection);
        /*String data ="ROBOT, <" + currentRobotL +">, <" + currentRobotR
                + ">, <" + direction + ">";*/
        robotStatus.setText(action);
        if(oldX==currentRobotL && oldY==currentRobotR){
            robotStatus.setText("Encountered obstacle");
            Log.d("test","encountered  obstacle");
        }
        oldX=currentRobotL;
        oldY=currentRobotR;

        //Log.d("test",String.valueOf(oldX)+":"+String.valueOf(currentRobotL));
        String data2 = "{'device':'ROBOT', 'X':"+currentRobotL+" " +
              ",'Y':"+currentRobotR+",'D':"+direction+",'A':"+action+"}";
        bluetoothService.write(data2.getBytes());
    }

    public void sendObsInfo(BluetoothService bluetoothService){
        String direction;
        int convY;
        int[][] obsLoc = gridMap.obsLocation;
        ArrayList<String> latestLoc = new ArrayList<String>();

        for (int i = 0; i < obsLoc[0].length; i++) {
            direction = getFaceDirection(obsLoc[2][i]);
            //convert (0,0) at axis top left to (0,0) at axis bottom left
            convY = 20 - obsLoc[1][i];
            String obs = obsLoc[0][i] + "," + convY + "," + direction + " " + "|" + " ";
            latestLoc.add(obs);
        }
        System.out.println(AlgActionSetObs + latestLoc.toString());

        //convert all lines into a single line
        StringBuilder builder = new StringBuilder();
        builder.append(AlgActionSetObs);
        for (String details : latestLoc) {
            builder.append(details);
        }
        bluetoothService.write(builder.toString().getBytes());
    }

    public TextView getRobotStatus() {return robotStatus; }

    public Button getBtnClicked() {
        return genRobotBtn;
    }


    public void AlgActionPlan_path(){
        sendBTMessage(bluetoothService,AlgActionPlan_path);
    }
    public void AlgActionDisconnect(){
        sendBTMessage(bluetoothService,AlgActionDisconnect);
    }

//    public ImageView forward() {
//\        return forwardView ;}
//    public View backward() {
//            return backwardView;}
//    public View frontLeft() {
//    return turnLView;}
//    public View frontRight() {
//            return turnRView;}

    private void onClickSend2() {
//        String data ="Foward KGBKJBKLJH";
//        bluetoothService.write(data.getBytes());
        Toast.makeText(getContext(), "iygasjdgasiouhdhaosu", Toast.LENGTH_LONG).show();
    }
}