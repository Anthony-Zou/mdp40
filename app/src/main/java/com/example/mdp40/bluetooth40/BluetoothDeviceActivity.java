package com.example.mdp40.bluetooth40;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.mdp40.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView deviceListView;
    private ArrayAdapter devicesAdapter;

    public DeviceListAdapter mDeviceListAdapter;
    public ArrayList<BluetoothDevice> list1 = new ArrayList<>();
    public ArrayList<BluetoothDevice> list2 = new ArrayList<>();
    ListView lvScan;
    BluetoothAdapter bluetoothAdapter;
    boolean m1 = false, m3 = false;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lvScan = findViewById(R.id.lvScan);
        Button btnOnOff = findViewById(R.id.btnOnOff);
        Button btnDiscover = findViewById(R.id.btnDiscover);
        Button btnScan = findViewById(R.id.btnScan);

        btnOnOff.setOnClickListener(view -> toggleBT());

        btnDiscover.setOnClickListener(view ->{
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED){
                startActivityForResult(discoverableIntent,1);
            }
        });

        btnScan.setOnClickListener(v -> {
            Log.d("Bluetooth Activity","Button scan");
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if(permissionCheck != 0){
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},1001);
            }
            bluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3,discoverDevicesIntent);

            m3 = true;
        });
       refreshPairedDevices();
       IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
       registerReceiver(mBroadcastReceiver4,filter);
    }

    public void refreshPairedDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()) {
            list1.clear();
            list1.addAll(pairedDevices);
            deviceListView = findViewById(R.id.lv_devices);
            devicesAdapter = new DeviceAdapter(BluetoothDeviceActivity.this, list1);
            deviceListView.setAdapter(devicesAdapter);
        }
    }

    private void toggleBT() {
        if(bluetoothAdapter == null){
            Toast.makeText(BluetoothDeviceActivity.this,"Device does not have Bluetooth capabilities",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_CONNECT)!= PackageManager.PERMISSION_GRANTED){
                startActivity(enableBTIntent);
                return;
            }
        }
        if(bluetoothAdapter.isEnabled()){
            Toast.makeText(BluetoothDeviceActivity.this,"Turning Off Bluetooth",Toast.LENGTH_SHORT).show();
            bluetoothAdapter.disable();
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1,BTIntent);
            m1 = true;
        }
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("Bluetooth Activity", "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("Bluetooth Activity", "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("Bluetooth Activity", "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("Bluetooth Activity", "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) throws SecurityException{
            final String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
               BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
               if(device.getName()!= null && device.getName().length()>0 && !list1.contains(device)&&!list2.contains(device)){
                   list2.add(device);
               }
               Log.d("Bluetooth Activity","Broadcast Receiver 3");
               mDeviceListAdapter = new DeviceListAdapter(context,R.layout.item_scan_bluetooth_device,list2);
               lvScan.setAdapter(mDeviceListAdapter);
               lvScan.setSelection(mDeviceListAdapter.getCount()-1);
            }

        }
    };
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) throws SecurityException {
            String TAG = "BTBOND";
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                refreshPairedDevices();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG,"BOND_BONDED");
                    list2.remove(device);
                    mDeviceListAdapter = new DeviceListAdapter(context, R.layout.item_scan_bluetooth_device,list2);
                    lvScan.setAdapter(mDeviceListAdapter);
                    devicesAdapter = new DeviceAdapter(BluetoothDeviceActivity.this,list1);
                    deviceListView.setAdapter(devicesAdapter);
                }
                if(device.getBondState()== BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "BOND_BONDING.");
                }
                if(device.getBondState()== BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "BOND_NONE.");
                }
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (m1)
            unregisterReceiver(mBroadcastReceiver1);
        if (m3)
            unregisterReceiver(mBroadcastReceiver3);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.cancelDiscovery();
            list2.get(i).createBond();
        }
    }

    private class DeviceAdapter extends ArrayAdapter<BluetoothDevice> {

        public DeviceAdapter(@NonNull Context context, @NonNull List<BluetoothDevice> devices) {
            super(context, 0, devices);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            BluetoothDevice device = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
            }

            TextView tvName = convertView.findViewById(R.id.tv_device_name);
            TextView tvAddress = convertView.findViewById(R.id.tv_device_address);
            Button btnConnect = convertView.findViewById(R.id.btn_pair_device);

            if (ActivityCompat.checkSelfPermission(BluetoothDeviceActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                tvName.setText(device.getName());
                tvAddress.setText(device.getAddress());
                btnConnect.setOnClickListener(view -> onClickConnect(device));
                return convertView;
            }
            return convertView;
        }

        private void onClickConnect(BluetoothDevice device) {
            Activity ctx = (Activity) getContext();
            Intent data = new Intent();
            data.putExtra("bluetooth_address", device.getAddress());
            ctx.setResult(Activity.RESULT_OK, data);
            ctx.finish();
            //Toast.makeText(BluetoothDeviceActivity.this, "BT connect CLicked =  "+device.getAddress(), Toast.LENGTH_SHORT).show();

        }
    }
}
