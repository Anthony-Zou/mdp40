package com.example.mdp40;
import static com.example.mdp40.bluetooth40.BluetoothService.STATE_CONNECTED;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.mdp40.MapGeneration.GridMap;
import com.example.mdp40.bluetooth40.BluetoothDeviceActivity;
import com.example.mdp40.bluetooth40.BluetoothListener;
import com.example.mdp40.bluetooth40.BluetoothService;
import com.example.mdp40.fragments.consoleFragment;
import com.example.mdp40.fragments.mapPanelFragment;
import com.example.mdp40.fragments.rightPanelFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements BluetoothListener{
    private ActivityResultLauncher<Intent> activityResultLauncher;
    BluetoothAdapter bluetoothAdapter;
    BluetoothService bluetoothService;
    BluetoothDevice bluetoothDevice;

    String deviceMACAddr = "";
    public static HashMap<String, String> receivedText = new HashMap<String, String>();
    ArrayList<String> textHistory = new ArrayList<>();
    int textCount = 0;

    consoleFragment fragmentConsole;
    rightPanelFragment rightPanelFragment;
    mapPanelFragment mapPanelFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initBluetooth();
    }

    private void initViews() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentConsole = (consoleFragment) fragmentManager.findFragmentById(R.id.fragmentConsolePanel);
        rightPanelFragment = (rightPanelFragment) fragmentManager.findFragmentById(R.id.fragmentRightPanel);
        mapPanelFragment = (mapPanelFragment) fragmentManager.findFragmentById(R.id.fragmentMapPanel);
        LinearLayout layout = findViewById(R.id.main_layout);
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService = new BluetoothService(btMsgHandler);
        fragmentConsole.setBluetoothService(bluetoothService);
        rightPanelFragment.setBluetoothService(bluetoothService);
        mapPanelFragment.setBluetoothService(bluetoothService);

        // passes onBluetoothStatusChange defined here into BluetoothService so it can manipulate views
        bluetoothService.setBluetoothStatusChange(this);

        promptBTPermissions();
        listenBTFragment();
    }
    private void promptBTPermissions() {
        // permissions to handle bluetooth
        if (checkSelfPermission(Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            rightPanelFragment.btEnabled = true;
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder
                    .setMessage("This app requires Bluetooth to connect to the robot")
                    .setTitle("Alert");

            AlertDialog dialog = builder.create();
            dialog.show();

            rightPanelFragment.btEnabled = false;
        }
    }


    private void listenBTFragment() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // retrieves data sent from closed BT intent
                Intent intent = result.getData();
                Bundle intentBundle = intent.getExtras();

                // connects with the selected device from BT intent
                deviceMACAddr = intentBundle.getString("bluetooth_address");
                bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceMACAddr);
                bluetoothService.connect(bluetoothDevice);
            }
        });
    }

    private final Handler btMsgHandler = new Handler(Looper.myLooper(), message -> {
        if (message.what == Constants.MESSAGE_READ) {
            byte[] readBuf = (byte[]) message.obj;
            String strMessage = new String(readBuf, 0, message.arg1);
            GridMap gridMap = (GridMap) findViewById(R.id.gridMap);

            try {
//                '{"mode":"updateRobot", "x":value, "y":value, "direction":"value"}'
//
//                '{"mode":"moveRobot", "action":"value"}'
//
//                '{"mode":"updateId", "oldId":value, "newId":value}'
                JSONObject json = new JSONObject(strMessage);
                if(json.getString("mode").equals("updateRobot")) {
                    /*System.out.println("btMsgHandler if");
                    System.out.println("strMessage: " + json.getInt("x") + " " + json.getInt("y")
                            + " " + json.getString("direction"));*/
                    onReceivedMsgChanged(json.getInt("x"), json.getInt("y"), json.getString("direction"),
                                  "updateRobot");
                    // rpiMessageHandler(json);
                    GridMap.robotleftImage = json.getInt("x");
                    GridMap.robottopImage = json.getInt("y");
                    System.out.println("y: " + json.getInt("y"));
                    if (json.getString("direction").equals("N")) {
                        GridMap.robotAngle = 270;
                    } else if (json.getString("direction").equals("W")) {
                        GridMap.robotAngle = 180;
                    } else if (json.getString("direction").equals("S")) {
                        GridMap.robotAngle = 90;
                    } else {
                        GridMap.robotAngle = 0;
                    }
                }
                else if(json.getString("mode").equals("moveRobot")) {
                    onReceivedMsgChanged(0, 0, json.getString("action"), "moveRobot");
                    String action =json.getString("action");
                    switch (action.charAt(0)){
                        case 'Q':
                            gridMap.rotateLeft();
                            gridMap.invalidate();
                            break;
                        case 'W':
                            gridMap.moveForward();
                            gridMap.invalidate();
                            break;
                        case 'E':
                            gridMap.rotateRight();
                            gridMap.invalidate();
                            break;
                        case 'A':
                            gridMap.rotateBackLeft();
                            gridMap.invalidate();
                            break;
                        case 'S':
                            gridMap.moveBackward();
                            gridMap.invalidate();
                            break;
                        case 'D':
                            gridMap.rotateBackRight();
                            gridMap.invalidate();
                            break;
                        default:
                            break;

                    }
                }
                //update obstacle id
                else if(json.getString("mode").equals("updateId")) {
                    onReceivedMsgChanged(json.getInt("oldId"), json.getInt("newId"),
                            json.getString("mode"), "updateId");
                    String oldId = json.getString("oldId");
                    String newId = json.getString("newId");
                    for (int i = 0; i < gridMap.obsLocation[3].length; i++) {
                       // Toast(String.valueOf(gridMap.obsLocation[3][i]));
                        if (String.valueOf(gridMap.obsLocation[3][i]).equals(oldId)) {
                          //  Toast("old obstacle found");
                            gridMap.obsLocation[3][i] = Integer.parseInt(newId);
                            //Toast("new int passed in");
                            gridMap.obsLocation[4][i] = 18;
                            //set the matrix in GameLogic to 2
                            gridMap.genRobot();
                            gridMap.invalidate();
                        }
                    }
                }
                else if(json.getString("mode").equals("updateId_coord")) {
                    onReceivedMsgChanged(json.getInt("x"), json.getInt("y"),
                            String.valueOf(json.getInt("newId")), "updateId_coord");
                    int x = json.getInt("x");
                    int y = json.getInt("y");
                    int newId = json.getInt("newId");
                    int convX = x - 1;
                    int convY = 19 - y + 1;
                    for (int i = 0; i < gridMap.obsLocation[0].length; i++) {
                        if (gridMap.obsLocation[0][i] == convX && gridMap.obsLocation[1][i] == convY)  {
                            gridMap.obsLocation[3][i] = newId;
                            gridMap.obsLocation[4][i] = 18;

                            gridMap.genRobot();
                            gridMap.invalidate();
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    });




    // handles the runnable for reconnecting to bluetooth device
    Handler reconnectHandler = new Handler();


    // runnable that runs code to reconnect to bluetooth device
    Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (bluetoothService.state == STATE_CONNECTED)
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();

                reconnectHandler.removeCallbacks(reconnectRunnable);
                bluetoothService.connect(bluetoothDevice);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Failed to reconnect, trying in 5 second", Toast.LENGTH_SHORT).show();
            }
        }
    };


    // prompt user whether to reconnect to bluetooth device
    private void promptReconnect() {
        new AlertDialog.Builder(this).setTitle("Reconnect to Bluetooth Device")
                .setPositiveButton("Yes", (dialogInterface, i) -> reconnectHandler.postDelayed(reconnectRunnable, 2000))
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bluetoothService.stop();
    }

    // adopted from BluetoothListener interface, used in BluetoothService class
    public void onBluetoothStatusChange(int status) {
        ArrayList<String> text = new ArrayList<>(Arrays.asList("Not Connected", "", "Connecting", "Connected"));
        //ArrayList<String> col = new ArrayList<>(Arrays.asList("#FFFF0000", "", "#FFFFFF00", "#FF00FF00"));
        ArrayList<String> col = new ArrayList<>(Arrays.asList("#FFFF0000", "", "#FFFFFF00", "#FF000000"));

        runOnUiThread(() -> {
            rightPanelFragment.getBtnConnect().setText(text.get(status));
            if (rightPanelFragment.getBtnConnect().getText().equals("Connected")){
                runOnUiThread(() -> {
                    mapPanelFragment.getRobotStatus().setText("Ready to start");
                    rightPanelFragment.getBtnConnect().setBackgroundResource(R.drawable.connected);
                });
            }
            else{
                rightPanelFragment.getBtnConnect().setBackgroundResource(R.drawable.disconnected);
            }
            rightPanelFragment.getBtnConnect().setTextColor(Color.parseColor(col.get(status)));

            if (bluetoothService.state == BluetoothService.STATE_NONE) {
                promptReconnect();
                Toast.makeText(MainActivity.this, "BT state =  "+bluetoothService.state, Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void onBtnConnectClick(View view) {
        if (rightPanelFragment.btEnabled) {
            Intent intent = new Intent(MainActivity.this, BluetoothDeviceActivity.class);
            bluetoothService.start();
            activityResultLauncher.launch(intent);
        }
    }
//    public void onMapClicked(View view) {
//     startActivity(new Intent(MainActivity.this, MapInit.class));
//    }

    public void onReceivedMsgChanged(int x, int y, String direction, String mode) {
        System.out.println("check mode: "+mode);
        if (mode.equals("updateRobot")) {
            receivedText.put("x", String.valueOf(x));
            receivedText.put("y", String.valueOf(y));
            receivedText.put("direction", direction);
            receivedText.remove("newId");
            receivedText.remove("oldId");
            receivedText.remove("action");
            mapPanelFragment.getBtnClicked().performClick();
        }
        else if (mode.equals("moveRobot")) {
            receivedText.put("action", direction);
            receivedText.remove("x");
            receivedText.remove("y");
            receivedText.remove("direction");
            receivedText.remove("newId");
            receivedText.remove("oldId");
        }
        else if (mode.equals("updateId")){
            receivedText.put("oldId", String.valueOf(x));
            receivedText.put("newId", String.valueOf(y));
            receivedText.remove("x");
            receivedText.remove("y");
            receivedText.remove("action");
            receivedText.remove("direction");
        }
        else {
            receivedText.put("x", String.valueOf(x));
            receivedText.put("y", String.valueOf(y));
            receivedText.put("newId", direction);
            receivedText.remove("oldId");
            receivedText.remove("action");
            receivedText.remove("direction");
        }
        textHistory.add(receivedText.toString());
        textCount+=1;
        if (textCount>10){
            textHistory.remove(0);
        }
        System.out.println("receivedText: " + String.valueOf(receivedText));
        //convert all lines into a single line
        StringBuilder builder = new StringBuilder();
        for (String details : textHistory) {
            builder.append(details + "\n");
        }

        runOnUiThread(() -> {
            rightPanelFragment.getMsgReceived().setText(builder.toString());
        });
    }

    public void Toast(String msg){
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

    }
}