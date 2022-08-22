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

import com.example.mdp40.BlueTooth.BlueTooth;
import com.example.mdp40.MapGeneration.MapInit;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button mapGenBtn;
    mapGenBtn = findViewById(R.id.mapGen);
    setContentView(R.layout.activity_main);


    mapGenBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(BlueTooth.this, MapInit.class));
        }
    });
}