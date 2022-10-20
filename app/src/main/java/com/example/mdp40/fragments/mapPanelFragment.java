package com.example.mdp40.fragments;

import static com.example.mdp40.Constants.AlgActionDisconnect;
import static com.example.mdp40.Constants.AlgActionPlan_path;
import static com.example.mdp40.Constants.AlgActionPlan_path2;
import static com.example.mdp40.Constants.AlgActionSetObs;
import static com.example.mdp40.Constants.StmActionA;
import static com.example.mdp40.Constants.StmActionD;
import static com.example.mdp40.Constants.StmActionE;
import static com.example.mdp40.Constants.StmActionQ;
import static com.example.mdp40.Constants.StmActionS;
import static com.example.mdp40.Constants.StmActionW;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private consoleFragment console;
    static int currentRobotL;
    static int currentRobotR;
    TextView robotStatus;
    Button genRobotBtn;
    String direction = new String();
    static int faceDirection;

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
        consoleArrayAdapter = new ArrayAdapter<>(getContext(),R.layout.item_message);

        // obstacle buttons
        Button genMapbtn = (Button)view.findViewById(R.id.genMap);
        Button moveObsBtn = (Button)view.findViewById(R.id.moveObs);
        Button addObsBtn = (Button)view.findViewById(R.id.addObs);
        Button updateObsBtn = (Button)view.findViewById(R.id.updateObs);

        // set button background image
        genMapbtn.setBackgroundResource(R.drawable.button_bg);
        moveObsBtn.setBackgroundResource(R.drawable.button_bg);
        addObsBtn.setBackgroundResource(R.drawable.button_bg);
        updateObsBtn.setBackgroundResource(R.drawable.button_bg);

        // robot buttons
        genRobotBtn = (Button)view.findViewById(R.id.genRobot);
        genRobotBtn.setBackgroundResource(R.drawable.button_bg);
        ImageView forwardView = (ImageView)view.findViewById(R.id.rover_F);
        ImageView backwardView = (ImageView)view.findViewById(R.id.rover_B);
        ImageView turnLView = (ImageView)view.findViewById(R.id.rover_LF);
        ImageView turnRView = (ImageView)view.findViewById(R.id.rover_RF);
        ImageView turnBLView = (ImageView)view.findViewById(R.id.rover_LB);
        ImageView turnBRView = (ImageView)view.findViewById(R.id.rover_RB);

        // robot coordinates
        TextView robotLeftImage = (TextView) view.findViewById(R.id.robotLeftImage);
        TextView robotTopImage = (TextView) view.findViewById(R.id.robotRightImage);
        robotStatus = (TextView) view.findViewById(R.id.robotStatus);
        gridMap.displayRobotPos(robotLeftImage, robotTopImage);

        // clear button
        Button clearBtn = (Button)view.findViewById(R.id.clearCanvas);
        clearBtn.setBackgroundResource(R.drawable.button_bg);

        // grid map & algo buttons
        GridMap gridMap = (GridMap)view.findViewById(R.id.gridMap);
        Button planPathBtn = (Button)view.findViewById(R.id.planPath);
        Button planPathBtn2 = (Button)view.findViewById(R.id.planPath2);
        Button disconnectAlgoBtn = (Button)view.findViewById(R.id.disconnect);

        // set button background image
        planPathBtn.setBackgroundResource(R.drawable.button_bg);
        planPathBtn2.setBackgroundResource(R.drawable.button_bg);
        disconnectAlgoBtn.setBackgroundResource(R.drawable.button_bg);

        // click to generate obstacles
        genMapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.genObstacles();
                gridMap.invalidate();
            }
        });

        // click to generate robot
        genRobotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robotStatus.setText("Ready to Start");
                gridMap.genRobot();
                gridMap.invalidate();
            }
        });

        // click to move obstacles
        moveObsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.moveObstacles();
                gridMap.invalidate();
            }
        });

        // click to generate more obstacles
        addObsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.genMoreObs();
                gridMap.invalidate();
            }
        });

        // click to move robot forward
        forwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speed = "010";
                sendBTMessage(bluetoothService, StmActionW+speed);

                gridMap.moveForward();
                gridMap.invalidate();

            }
        });

        // click to move robot backward
        backwardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String speed = "010";
                sendBTMessage(bluetoothService, StmActionS+speed);
                gridMap.moveBackward();
                gridMap.invalidate();
            }
        });

        // click to rotate robot forward left
        turnLView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(bluetoothService, StmActionQ);
                gridMap.rotateLeft();
                gridMap.invalidate();
            }
        });

        // click to rotate robot forward right
        turnRView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(bluetoothService, StmActionE);
                gridMap.rotateRight();
                gridMap.invalidate();
            }
        });

        // click to rotate robot back left
        turnBLView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(bluetoothService, StmActionA);
                gridMap.rotateBackLeft();
                gridMap.invalidate();
            }
        });

        // click to rotate robot back right
        turnBRView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBTMessage(bluetoothService, StmActionD);
                gridMap.rotateBackRight();
                gridMap.invalidate();
            }
        });

        // click to send obstacle location to algo
        updateObsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendObsInfo(bluetoothService);
            }
        });

        // click to send message algo and plan the shortest path
        planPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlgActionPlan_path();
            }
        });

        // click to send message algo and plan the shortest path
        planPathBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlgActionPlan_path2();
            }
        });

        // click to disconnect to algo
        disconnectAlgoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlgActionDisconnect();
            }
        });

        // click to clear all
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.clearCanvas();
                gridMap.invalidate();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panel_center, container, false);

    }

    public void retrieveCurrentRobot(int currentRobotL, int currentRobotR, int faceDirection){
        this.currentRobotL = currentRobotL;
        this.currentRobotR = currentRobotR;
        this.faceDirection = faceDirection;
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
        return direction;
    }

    int oldX=0;
    int oldY=0;
    public void sendBTMessage(BluetoothService bluetoothService, String action){
        direction = getFaceDirection(faceDirection);
        robotStatus.setText(action);

        if(oldX==currentRobotL && oldY==currentRobotR){
            robotStatus.setText("Encountered obstacle");
        }
        oldX=currentRobotL;
        oldY=currentRobotR;

        String data2 = "{'device':'ROBOT', 'X':"+currentRobotL+" " +
              ",'Y':"+currentRobotR+",'D':"+direction+",'A':"+action+"}";
        bluetoothService.write(data2.getBytes());
    }

    public void sendObsInfo(BluetoothService bluetoothService){
        String direction;
        int convY;
        int convX;
        int[][] obsLoc = gridMap.obsLocation;
        ArrayList<String> latestLoc = new ArrayList<String>();

        for (int i = 0; i < obsLoc[0].length; i++) {
            direction = getFaceDirection(obsLoc[2][i]);
            //convert (0,0) at axis top left to (0,0) at axis bottom left
            convY = 20 - obsLoc[1][i];
            convX = obsLoc[0][i] + 1;
            String obs = convX + "," + convY + "," + direction + " " + "|" + " ";
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

    public void AlgActionPlan_path(){
        sendBTMessage(bluetoothService,AlgActionPlan_path);
    }
    public void AlgActionPlan_path2(){
        bluetoothService.write(AlgActionPlan_path2.getBytes());
    }
    public void AlgActionDisconnect(){
        sendBTMessage(bluetoothService,AlgActionDisconnect);
    }
}