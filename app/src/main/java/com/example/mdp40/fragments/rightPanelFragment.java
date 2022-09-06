package com.example.mdp40.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.mdp40.R;
import com.example.mdp40.bluetooth40.BluetoothService;


public class rightPanelFragment extends Fragment {
    Button btnConnect;
    BluetoothService bluetoothService;
    public boolean btEnabled = false;
    public rightPanelFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panel_right, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        btnConnect = view.findViewById(R.id.btnConnect);

    }
    public void setBluetoothService(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    public Button getBtnConnect() {
        return btnConnect;
    }

}