package com.example.mdp40.bluetooth40;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
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
    private static final UUID UUID = java.util.UUID.fromString("62a607c8-6ddd-450b-ba91-1af21b5e3954");
    private ConnectThread connectThread = null;
    private ConnectedThread connectedThread = null;
    private AcceptedThread acceptedThread = null;
    private final Handler handler;
    private BluetoothAdapter bluetoothAdapter;
    public int state;
    private BluetoothListener btListener;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device


    public BluetoothService(Handler handler) {
        this.handler = handler;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.state = STATE_NONE;
    }

    public void setBluetoothStatusChange(BluetoothListener btListener){
        this.btListener = btListener;
    }

    public synchronized void start(){

    }

    public synchronized void connect(BluetoothDevice device){}

    public synchronized void connected(BluetoothSocket bluetoothSocket, BluetoothDevice bluetoothDevice){}

    public synchronized void stop(){}

    private void connectionFailed(){}

    private void connectionLost(){}

    private void updateBTConnected(){}

    public void write(byte[] out){}



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

    private class AcceptedThread extends Thread
    {
    private final BluetoothServerSocket bluetoothServerSocket;
    public AcceptedThread(){
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
