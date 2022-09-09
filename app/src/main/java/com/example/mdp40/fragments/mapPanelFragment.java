package com.example.mdp40.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mdp40.MapGeneration.GameLogic;
import com.example.mdp40.MapGeneration.GridMap;
import com.example.mdp40.MapGeneration.Obstacle;
import com.example.mdp40.R;

public class mapPanelFragment extends Fragment {

    private GridMap gridMap;
    private GameLogic game;
    private Obstacle obstacle;

    static int currentObs;
    boolean changeId = false;

    int[] currentId1;
    String[] currentId;
    String[] avaiId;
    String[] allId;

    public mapPanelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panel_center, container, false);
    }
}