package com.example.mdp40.bluetooth40;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.mdp40.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

@SuppressLint("MissingPermission")
public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private static final String APP_NAME = "bluetooth40";
    private static final UUID UUID = java.util.UUID.fromString("00001101-0000-1000-8000-0085F9B34FB");
    private ConnectThread connectThread = null;
    private ConnectedThread connectedThread = null;
    private AcceptThread acceptThread = null;
    private final Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    public int state;
    private BluetoothListener btListener;


    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public BluetoothService(Handler handler) {
        this.handler = handler;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.state = STATE_NONE;
    }

    public void setBluetoothStatusChange(BluetoothListener btListener){
        this.btListener = btListener;
    }

    public synchronized void start(){
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if(connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
        if(acceptThread == null){
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
        if(state == STATE_CONNECTED){
            stop();
        }
    }

    public synchronized void connect(BluetoothDevice device){


        if(state == STATE_CONNECTING){
            if(connectThread != null){
                connectThread.cancel();
                connectThread = null;
            }
        }
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if(connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device);
        connectThread.start();
        state = STATE_CONNECTING;

        updateBTConnected(STATE_CONNECTING);

    }

    public synchronized void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice){
        if(connectedThread != null){
            connectedThread.cancel();
            connectedThread = null;
        }
        if(acceptThread != null){
            acceptThread.cancel();
            acceptThread = null;
        }

        connectedThread = new ConnectedThread(bluetoothSocket);
        connectedThread.start();

        Message message = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.DEVICE_NAME, bluetoothDevice.getName());
        message.setData(bundle);
        handler.sendMessage(message);
        state = STATE_CONNECTED;
        updateBTConnected(STATE_CONNECTED);


    }

    public synchronized void stop(){
        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        if(connectedThread != null){
            connectedThread.cancel();
            connectedThread= null;
        }
        if(acceptThread != null){
            acceptThread.cancel();
            acceptThread = null;
        }

        state = STATE_NONE;

        updateBTConnected(STATE_NONE);
    }

    private void connectionFailed(){
        Message message = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST,"Failed to connect device");
        message.setData(bundle);
        handler.sendMessage(message);
        state = STATE_NONE;
        updateBTConnected(STATE_NONE);
    }

    private void connectionLost(){
        Message message = handler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Lost BT Connection");
        message.setData(bundle);
        state = STATE_NONE;
        updateBTConnected(STATE_NONE);
    }

    private void updateBTConnected(int status){
        btListener.onBluetoothStatusChange(status);
    }

    public void write(byte[] out){
        ConnectedThread r;
        synchronized (this){
            if(state != STATE_CONNECTED){
                return;
            }
            r = connectedThread;
        }
        r.write(out);
    }



    private class ConnectThread extends Thread
    {
        private BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice bluetoothDevice){

            this.bluetoothDevice = bluetoothDevice;
            BluetoothSocket tmpBluetoothSocket = null;
            try{
                tmpBluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID);
            }catch(IOException e){
                Log.e(TAG,"Bluetooth socket's create method have failed",e);
            }
            bluetoothSocket = tmpBluetoothSocket;
            state = STATE_CONNECTING;
        }
        public void run() {
            BluetoothSocket socket = null;

            try {
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }

            bluetoothSocket = socket;
            bluetoothAdapter.cancelDiscovery();

            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                try {
                    bluetoothSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "Unable to close() socket during connection failure", e2);
                }

                connectionFailed();
                return;
            }

            connected(bluetoothSocket, bluetoothDevice);
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Socket's close() failed", e);
            }
        }
    }

    private class ConnectedThread extends Thread
    {
        private final BluetoothSocket socket;
        private final InputStream is;
        private final OutputStream os;
        private byte[] buffer;
        public ConnectedThread(BluetoothSocket socket){
            this.socket = socket;
            InputStream _is = null;
            OutputStream _os = null;

            try {
                _is = socket.getInputStream();
            } catch (IOException e) {
               Log.e(TAG, "Failed to create input stream");

            }
            try {
                _os = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Failed to create output stream");

            }
            this.is = _is;
            this.os = _os;
            state = STATE_CONNECTED;
        }

        public void run(){
            buffer = new byte[1024];
            int bytes;
            while(state == STATE_CONNECTED){
                try{
                    bytes = is.read(buffer);
                    handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1,buffer).sendToTarget();
                }catch(IOException e){
                    Log.d(TAG,"Input Stream was disconnected",e);
                    connectionLost();
                    break;
                }
            }
        }
        public void write(byte[] bytes){
            try{
                os.write(bytes);
                handler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1,buffer).sendToTarget();
            }catch(IOException e){
                Log.e(TAG, "Error occurred while attempting to send data");
            }
        }
        public void cancel(){
            try{
                socket.close();
            }catch(IOException e){
                Log.e(TAG,"Could not close the socket",e);
            }
        }
    }

    private class AcceptThread extends Thread
    {
    private final BluetoothServerSocket bluetoothServerSocket;
    public AcceptThread(){
        BluetoothServerSocket tmpBluetoothServerSocket = null;
        try{
            tmpBluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(APP_NAME,UUID);
        } catch (IOException e) {
            Log.e(TAG,"Socket's listen method failed",e);
        }
        bluetoothServerSocket = tmpBluetoothServerSocket;
        state = STATE_LISTEN;
    }
    public void run(){
        BluetoothSocket socket;
        while(state != STATE_CONNECTED){
            try{
            socket = bluetoothServerSocket.accept();}
            catch(IOException e){
                Log.e(TAG,"Socket accept method failed",e);
                break;
            }

            if(socket != null){
                synchronized(BluetoothService.this){
                    switch(state){
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            connected(socket,socket.getRemoteDevice());
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            try{
                                socket.close();
                            }catch(IOException e){
                                Log.e(TAG,"Could not close unwanted socket",e);
                            }
                            break;
                    }
                }
            }
        }
    }
    public void cancel(){
        try{
            bluetoothServerSocket.close();
        }catch(IOException e){
            Log.e(TAG, "Close method have failed",e);
        }
    }
    }

}
