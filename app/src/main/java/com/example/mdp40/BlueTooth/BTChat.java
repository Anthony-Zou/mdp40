package com.example.mdp40.BlueTooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mdp40.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BTChat extends AppCompatActivity {
    Button listen, send, listDevices,mOnBtn, mOffBtn, upBtn,downBtn,leftBtn,rightBtn;
    ListView listView;
    TextView msg_box, status;
    EditText writeMsg;

    BluetoothAdapter bluetoothAdapter ;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLE_BLUETOOTH = 1;

    private static final String APP_NAME = "MDP40";
    private static final UUID MY_UUID = UUID.fromString("62a607c8-6ddd-450b-ba91-1af21b5e3954");

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btchat);

       findViewByIdes();
       bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //check if bluetooth is available or not
        if(bluetoothAdapter == null){
            status.setText("Bluetooth is not available");
        }
        else{
            status.setText("Bluetooth is available");
            // Set image according to bluetooth status(on/off)
            if(!bluetoothAdapter.isEnabled()){
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
            }
        }


       implementListeners();
    }

    @SuppressLint("MissingPermission")
    private void implementListeners() {

        //on btn click
        mOnBtn.setOnClickListener(new View.OnClickListener() {
            //@SuppressLint("MissingPermission")
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if(!bluetoothAdapter.isEnabled()){
                    showToast("Turning On Bluetooth...");
                    //intent to on bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                    status.setText("Bluetooth On");
                }
                else{
                    showToast("Bluetooth is already on");
                }

            }
        });

        // off btn click
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                    showToast("Turning off Bluetooth");
                    status.setText("Bluetooth Off");
                }
                else{
                    showToast("Bluetooth is already off");
                }
            }
        });

       listDevices.setOnClickListener(view ->  {

               @SuppressLint("MissingPermission")
               Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
               String[] strings = new String[bt.size()];
               btArray = new BluetoothDevice[bt.size()];
               int index = 0;
               if(bt.size()>0){
                   for(BluetoothDevice device:bt){
                       btArray[index] = device;
                       strings[index] = device.getName();
                       index++;
                   }
                   ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,strings);
                   listView.setAdapter(arrayAdapter);
               }

       });

       listen.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               ServerClass serverClass = new ServerClass();
               serverClass.start();
           }
       });

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               ClientClass clientClass = new ClientClass(btArray[i]);
               clientClass.start();

               status.setText("Connecting");
           }
       });

       send.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               String string = String.valueOf(writeMsg.getText());
               sendReceive.write(string.getBytes());
           }
       });
        upBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "up";
                sendReceive.write(string.getBytes());
            }
        });
        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "down";
                sendReceive.write(string.getBytes());
            }
        });
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "left";
                sendReceive.write(string.getBytes());
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = "right";
                sendReceive.write(string.getBytes());
            }
        });
    }



    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch(message.what){
                case STATE_LISTENING:
                    status.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    //status.setText("Code Developing");
                    //some codes for message receive here
                    byte[] readBuff = (byte[]) message.obj;
                    String tempMsg = new String(readBuff,0,message.arg1);
                    msg_box.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    private void findViewByIdes(){
        listen= findViewById(R.id.listen);
        send= findViewById(R.id.send);
        listDevices = findViewById(R.id.listDevices);
        listView = findViewById(R.id.listView_BTDevices);
        msg_box= findViewById(R.id.msg);
        status= findViewById(R.id.status);
        writeMsg= findViewById(R.id.writemsg);
        mOnBtn = findViewById(R.id.buttonOn);
        mOffBtn = findViewById(R.id.buttonOff);
        upBtn = findViewById(R.id.buttonUp);
        downBtn= findViewById(R.id.buttonDown);
        leftBtn= findViewById(R.id.buttonLeft);
        rightBtn= findViewById(R.id.buttonRight);

    }

    private class ServerClass extends Thread{
        private BluetoothServerSocket serverSocket;

        @SuppressLint("MissingPermission")
        public ServerClass(){
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        public void run(){
            BluetoothSocket socket = null;
            while(socket == null){
                try{
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);
                    socket = serverSocket.accept();
                }catch(IOException e){
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }
                if(socket != null){
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);
                    // Some code for send and receive;
                    sendReceive = new SendReceive(socket);
                    sendReceive.start();
                    break;
                }
            }
        }
    }

    //creating a class fore client

    private class ClientClass extends Thread{
        private  BluetoothDevice device;
        private BluetoothSocket socket;

        @SuppressLint("MissingPermission")
        public ClientClass(BluetoothDevice device1){
            device = device1;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("MissingPermission")
        public void run(){
            try {
                socket.connect();
                Message message = Message.obtain();
                message.what = STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread{
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive(BluetoothSocket socket){
            bluetoothSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = bluetoothSocket.getInputStream();
                tempOut = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }
        public   void run(){
            byte[] buffer = new byte[1024];
            int bytes = 100;
            while(true){
                try {
                    inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //toast message function
    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

}