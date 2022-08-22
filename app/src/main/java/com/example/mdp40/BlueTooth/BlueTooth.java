package com.example.mdp40.BlueTooth;

        import android.annotation.SuppressLint;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.os.Handler;
        import android.os.Message;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.mdp40.MapGeneration.MapInit;
        import com.example.mdp40.R;

        import java.util.ArrayList;
        import java.util.Set;

public class BlueTooth extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int REQUEST_DISCOVER_BT = 1;

    TextView mStatusBluetooth, mPariedBluetooth, mScannedBluetooth, threadText;
    ImageView mBluetooth;
    Button mOnBtn, mOffBtn, mDiscoverBtn, mPairedBtn, mScanBtn;
    BluetoothAdapter mBlueAdapter;

    ListView pairedList, scanList;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        //bindTextview
        mStatusBluetooth = findViewById(R.id.statusBluetooth);
        mPariedBluetooth = findViewById(R.id.paired);
        mScannedBluetooth = findViewById(R.id.scanned);
        threadText= findViewById(R.id.ThreadText);
        //bindButtons
        mBluetooth = findViewById(R.id.bluetooth);
        mOnBtn = findViewById(R.id.onBtn);
        mOffBtn = findViewById(R.id.offBtn);
        mDiscoverBtn = findViewById(R.id.discoverableBtn);

        mPairedBtn = findViewById(R.id.pairedBtn);
        mScanBtn = findViewById(R.id.scanBtn);
        ///bindList
        pairedList = (ListView) findViewById(R.id.devistList);
        scanList =(ListView) findViewById(R.id.devistList);

        //adapter
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        //intentFilter
        IntentFilter scanIntentFilter = new IntentFilter(mBlueAdapter.ACTION_SCAN_MODE_CHANGED);
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

        //discover bluetooth btn
        mDiscoverBtn.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if(!mBlueAdapter.isDiscovering()){
                    showToast("Making your device discoverable");
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,10);

                    startActivity(intent);
                    mBluetooth.setImageResource(R.drawable.bluetooth_on);
                }


            }
        });

        //get Paired devices
        mPairedBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if(mBlueAdapter.isEnabled()){
                    mPariedBluetooth.setText("Paried Devices");

                    @SuppressLint("MissingPermission")
                    Set<BluetoothDevice> devices = mBlueAdapter.getBondedDevices();
                    String[] pairedDeviceStrings = new String[devices.size()];
                    int index = 0;
                    if(devices.size()>0){
                        for(BluetoothDevice device: devices){
                            //mPariedBluetooth.append("\nDevices" + device.getName() +" , "+ device);
                            pairedDeviceStrings[index]=device.getName();
                            index++;
                        }
                        ArrayAdapter<String> arrayAdapter =
                                new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,pairedDeviceStrings);
                        pairedList.setAdapter(arrayAdapter);
                    }

                }
                else{
                    //bluetooth is off so can't get paired devices
                    showToast("Turn on bluetooth to get paired devices");
                }
            }
        });


        //scan device button
        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
              Intent discoverableIntent = new Intent(mBlueAdapter.ACTION_REQUEST_DISCOVERABLE);
              discoverableIntent.putExtra(mBlueAdapter.EXTRA_DISCOVERABLE_DURATION,8);
              startActivity(discoverableIntent);

            }
        });
        registerReceiver(scanModeReceiver,scanIntentFilter);

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,stringArrayList);
        scanList.setAdapter(arrayAdapter);

        Thread2 t = new Thread2();
        t.start();


//Closing of OnCreate
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            threadText.setText(String.valueOf(message.arg1));
            return false;
        }
    });
    private class Thread2 extends Thread{
        public void run(){
            for(int i =0;i<50;i++){
                Message message = Message.obtain();
                message.arg1=i;
                handler.sendMessage(message);
                //threadText.setText(String.valueOf(i));
                try{
                    sleep(500);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

    }


BroadcastReceiver scanModeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(mBlueAdapter.ACTION_SCAN_MODE_CHANGED))
        {
            int modeValue = intent.getIntExtra(mBlueAdapter.EXTRA_SCAN_MODE,mBlueAdapter.ERROR);
            if(modeValue==mBlueAdapter.SCAN_MODE_CONNECTABLE){
                mScannedBluetooth.setText("The device is not in discoverable mode but can still receive connection");
            }else if (modeValue ==mBlueAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE){
                mScannedBluetooth.setText("The device is in discoverable mode");
            }
            else if(modeValue ==mBlueAdapter.SCAN_MODE_NONE){
                mScannedBluetooth.setText("The device is not in discoverable mode and cannot receive connection");
            }
            else{
                mScannedBluetooth.setText("Error");
            }

    }
}
    };

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