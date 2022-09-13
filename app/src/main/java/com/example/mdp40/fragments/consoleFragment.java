package com.example.mdp40.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mdp40.Constants;
import com.example.mdp40.R;
import com.example.mdp40.bluetooth40.BluetoothService;

public class consoleFragment extends Fragment {
    private ListView listViewConsole;
    private EditText editTextPrompt;
    private Button btnSend;

    private ArrayAdapter<String> consoleArrayAdapter;
    private String connectedDeviceName;
    private BluetoothService bluetoothService;
    private final Handler handler = new Handler(Looper.myLooper(),message ->{
        switch(message.what){
            case Constants.MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) message.obj;
                String writeMessage = new String(writeBuf);
                consoleArrayAdapter.add("me: " + writeMessage);
                break;
            case Constants.MESSAGE_READ:
                byte[] readBuf = (byte[]) message.obj;
                String readMessage = new String(readBuf,0,message.arg1);
                consoleArrayAdapter.add(connectedDeviceName +" : "+readMessage);
                break;
            case Constants.MESSAGE_DEVICE_NAME:
                btnSend.setEnabled(true);
                connectedDeviceName = message.getData().getString(Constants.DEVICE_NAME);
                if(connectedDeviceName != null){
                    Toast.makeText(getContext(),"Connected to "+ connectedDeviceName, Toast.LENGTH_SHORT).show();
                }
                break;

        }
        return false;
    });

    public consoleFragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_console,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @NonNull Bundle savedIstanceState){
        listViewConsole = view.findViewById(R.id.lv_console);
        editTextPrompt = view.findViewById(R.id.et_command);
        btnSend = view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(V->onClickSend());
        consoleArrayAdapter = new ArrayAdapter<>(getContext(),R.layout.item_message);
        listViewConsole.setAdapter(consoleArrayAdapter);
    }

    private void onClickSend() {
        String data = editTextPrompt.getText().toString();
        editTextPrompt.setText("");
        data ="Foward Test";
        bluetoothService.write(data.getBytes());
    }



    public void setBluetoothService(BluetoothService bluetoothService){
        this.bluetoothService = bluetoothService;
    }
    Handler getHandler(){
        return handler;
    }
}