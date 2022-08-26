package com.example.mdp40.BlueTooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mdp40.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
public class BTImage extends AppCompatActivity {

    Button listen, send, listDevices;
    ListView listView;
    TextView  status;
    ImageView imageView;


    BluetoothAdapter bluetoothAdapter ;
    BluetoothDevice[] btArray;

    BTImage.SendReceive sendReceive;

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
        setContentView(R.layout.activity_btimage);

        findViewByIdes();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BLUETOOTH);
        }

        implementListeners();
    }

    @SuppressLint("MissingPermission")
    private void implementListeners() {
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
                BTImage.ServerClass serverClass = new BTImage.ServerClass();
                serverClass.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BTImage.ClientClass clientClass = new BTImage.ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.happy);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
                byte[] imageBytes = stream.toByteArray();
                int subArraySize = 400;
                sendReceive.write(String.valueOf(imageBytes.length).getBytes());
                for(int i = 0; i<imageBytes.length;i+=subArraySize){
                    byte[] tempArray;
                    tempArray = Arrays.copyOfRange(imageBytes,i,Math.min(imageBytes.length,i+subArraySize));
                    sendReceive.write(tempArray);
                }
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
//                    byte[] readBuff = (byte[]) message.obj;
//                    String tempMsg = new String(readBuff,0,message.arg1);
//                    msg_box.setText(tempMsg);
                     byte[] readBuff = (byte[]) message.obj;
                     Bitmap bitmap = BitmapFactory.decodeByteArray(readBuff,0,message.arg1);
                     imageView.setImageBitmap(bitmap);
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
        imageView= findViewById(R.id.imageView);
        status= findViewById(R.id.status);

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
                    sendReceive = new BTImage.SendReceive(socket);
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

                sendReceive = new BTImage.SendReceive(socket);
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
            byte[] buffer = null;
            int numberOfBytes = 0;
            int index = 0;
            boolean flag = true;
            while(true){
                if(flag){
                    try {
                        byte[] temp = new byte[inputStream.available()];
                        if(inputStream.read(temp)>0){
                            numberOfBytes = Integer.parseInt(new String(temp,"UTF8"));
                            buffer = new byte[numberOfBytes];
                            flag = false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    try {
                        byte[] data = new byte[inputStream.available()];
                        int numbers = inputStream.read(data);
                        System.arraycopy(data, 0,buffer,index,numbers);
                        index = index+numbers;
                        if(index == numberOfBytes){
                            handler.obtainMessage(STATE_MESSAGE_RECEIVED, numberOfBytes,-1, buffer).sendToTarget();
                            flag = true;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }

        public void write(byte[] bytes){
            try {
                outputStream.write(bytes);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}