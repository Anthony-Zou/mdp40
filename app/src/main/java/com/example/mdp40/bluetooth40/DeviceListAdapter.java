package com.example.mdp40.bluetooth40;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mdp40.R;

import java.util.ArrayList;
import java.util.List;


public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
    private LayoutInflater layoutInflater;
    private ArrayList<BluetoothDevice> BTdevices;
    private int ViewResourceId;
    private Button btnPair;

    public DeviceListAdapter(@NonNull Context context, int textViewResourceID, @NonNull List<BluetoothDevice> devices){
        super(context,textViewResourceID,devices);
        this.BTdevices = (ArrayList<BluetoothDevice>) devices;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewResourceId = textViewResourceID;
    }

    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent)throws SecurityException{
        convertView = layoutInflater.inflate(ViewResourceId, null);
        BluetoothDevice device = BTdevices.get(position);

        if(device != null){
            TextView deviceName = convertView.findViewById(R.id.tv_device_name);
            TextView deviceAddress = convertView.findViewById(R.id.tv_device_address);

            if(deviceName != null){deviceName.setText(device.getName());}
            if(deviceAddress != null){deviceName.setText(device.getAddress());}

            btnPair = convertView.findViewById(R.id.btn_pair_device);
            btnPair.setOnClickListener(view -> onClickPair(device));
        }
        return convertView;
    }

    private void onClickPair(BluetoothDevice device) throws SecurityException{
        device.createBond();
    }

}
