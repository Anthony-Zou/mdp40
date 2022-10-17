package com.example.mdp40.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mdp40.Constants;
import com.example.mdp40.R;
import com.example.mdp40.bluetooth40.BluetoothService;

import org.json.JSONException;
import org.json.JSONObject;


public class rightPanelFragment extends Fragment {
    Button btnConnect;
    BluetoothService bluetoothService;
    TextView msgReceived;

    String textReceived;

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
        msgReceived = view.findViewById(R.id.msgReceived);

        btnConnect.setBackgroundResource(R.drawable.disconnected);
        msgReceived.setBackgroundResource(R.drawable.received_bg);
    }
    public void setBluetoothService(BluetoothService bluetoothService) {
        this.bluetoothService = bluetoothService;
    }

    public Button getBtnConnect() {
        return btnConnect;
    }

    public TextView getMsgReceived() {return msgReceived; }

    public void setReceivedText(String text){
        this.textReceived = text;
    }

}