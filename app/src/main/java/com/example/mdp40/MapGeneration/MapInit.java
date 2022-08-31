package com.example.mdp40.MapGeneration;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mdp40.R;

public class MapInit extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_init);

        Button genMapbtn = (Button)findViewById(R.id.genMap);
        GridMap gridMap = (GridMap)findViewById(R.id.gridMap);

        View downObs = (View)findViewById(R.id.downObs);
        downObs.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                Toast.makeText(MapInit.this, "I have been long clicked",
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });

        genMapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapInit.this, "Obstacles generated",
                        Toast.LENGTH_LONG).show();
                gridMap.resetMap();
                gridMap.invalidate();
            }
        });
    }
}
