package com.example.mdp40;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    TextView mStatusBluetooth, mPariedBluetooth;
    ImageView mBluetooth;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn;
    BluetoothAdapter mBlueAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mStatusBluetooth = findViewById(R.id.statusBluetooth);
        mPariedBluetooth = findViewById(R.id.paired);
        mBluetooth = findViewById(R.id.bluetooth);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);
        mPairedBtn = findViewById(R.id.discoverableBtn);

        //adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bluetooth is available or not
        if(mBlueAdapter == null){
            mStatusBluetooth.setText("Bluetooth is not available");
        }
        else{
            mStatusBluetooth.setText("Bluetooth is available");
            // Set image according to bluetooth status(on/off)
            if(mBlueAdapter.isEnabled()){
                mBluetooth.setImageResource(R.drawable.bluetooth_on);
            }else{
                mBluetooth.setImageResource(R.drawable.bluetooth_off);
            }
        }



        //on btn click
        mOnBtn.setOnClickListener(new View.OnClickListener() {
            //@SuppressLint("MissingPermission")
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if(!mBlueAdapter.isEnabled()){
                    showToast("Turning On Bluetooth...");
                    //intent to on bluetooth
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                    mBluetooth.setImageResource(R.drawable.bluetooth_on);
                }
                else{
                    showToast("Bluetooth is already on");
                }

            }
        });



        //discover bluetooth btn
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if(!mBlueAdapter.isDiscovering()){
                    showToast("Making your device discoverable");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVER_BT);
                }
            }
        });

        // off btn click
        mOffBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if(mBlueAdapter.isEnabled()){
                    mBlueAdapter.disable();
                    showToast("Turning off Bluetooth");
                    mBluetooth.setImageResource(R.drawable.bluetooth_off);
                }
                else{
                    showToast("Bluetooth is already off");
                }
            }
        });

        //get Paired devices
        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mBlueAdapter.isEnabled()){
                    mPariedBluetooth.setText("Paried Devices");
                    @SuppressLint("MissingPermission")
                    Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                    for(BluetoothDevice device: devices){
                        mPariedBluetooth.append("\nDevices" + devices.getClass() +" , "+ device);
                    }
                }
                else{
                    //bluetooth is off so can't get paired devices
                    showToast("Turn on blurtooth to get paried devices");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch(requestCode){
            case REQUEST_DISCOVER_BT:
                if(resultCode == RESULT_OK){
                    //bluetooth is on
                    mBluetooth.setImageResource(R.drawable.bluetooth_on);
                    showToast("Bluetooth is on");
                }else{
                    //user denied to turn on bluetooth
                    showToast("Could not on bluetooth");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //toast message function
    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    /*Using BluetoothAdapter class will do the following operations
    1 - Check if the Bluetooth is available or not
    2 - Turn on/ off blue tooth
    3 - Make Bluetooth Discoverable
    4 - Display Paried/Bounded devices
    getBoundedDevices() method of BluetoothAdapter class
    provides a set containing list of paired or bounded bluetooth devices
     */

}