package com.example.mdp40.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
        Button genRobotBtn = (Button)view.findViewById(R.id.genRobot);
        Button moveObsBtn = (Button)view.findViewById(R.id.moveObs);
        Button addObsBtn = (Button)view.findViewById(R.id.addObs);
        Button changeIdBtn = (Button)view.findViewById(R.id.changeId);
        forwardBtn = (Button)view.findViewById(R.id.fowardBtn);

        //obstacle number picker
        NumberPicker obsId = (NumberPicker)view.findViewById(R.id.numberPicker);
        currentId1 = gridMap.obsLocation[3];
        currentId = Arrays.stream(currentId1)
                .mapToObj(String::valueOf)
                .toArray(String[]::new);

        System.out.println("currentId: "+Arrays.toString(currentId));
        allId = obstacle.obsIdentity[0];
        avaiId = new String[allId.length-currentId.length];


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
        ImageView forwardView = (ImageView)view.findViewById(R.id.forwardView);
        ImageView backwardView = (ImageView)view.findViewById(R.id.backwardView);
        ImageView turnLView = (ImageView)view.findViewById(R.id.turnLView);
        ImageView turnRView = (ImageView)view.findViewById(R.id.turnRView);

        //robot coordinates
        TextView robotLeftImage = (TextView) view.findViewById(R.id.robotLeftImage);
        TextView robotRightImage = (TextView) view.findViewById(R.id.robotRightImage);
        gridMap.displayRobotPos(robotLeftImage, robotRightImage);

        //clear button
        Button clearBtn = (Button)view.findViewById(R.id.clearCanvas);

        GridMap gridMap = (GridMap)view.findViewById(R.id.gridMap);

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
                gridMap.genRobot();
                gridMap.invalidate();
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

        //Click to rotate robot right
        turnRView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gridMap.rotateRight();
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

        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data ="Foward KGBKJBKLJH";
  bluetoothService.write(data.getBytes());
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


    private void onClickSend2() {
//        String data ="Foward KGBKJBKLJH";
//        bluetoothService.write(data.getBytes());
        Toast.makeText(getContext(), "iygasjdgasiouhdhaosu", Toast.LENGTH_LONG).show();

    }

}