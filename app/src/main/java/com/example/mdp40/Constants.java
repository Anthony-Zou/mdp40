package com.example.mdp40;

public interface Constants {
    // Message types sent from the BluetoothChatService Handler
    int MESSAGE_STATE_CHANGE = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_WRITE = 3;
    int MESSAGE_DEVICE_NAME = 4;
    int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    String DEVICE_NAME = "device_name";
    String TOAST = "toast";

    // Android to Algo
    String AlgActionSetObs = "ALG | set_obstacles |";
    String AlgActionPlan_path ="ALG | send_path";
    String AlgActionDisconnect = "ALG | disconnect";

    // Android to STM
    String StmActionQ = "STM | Q000";
    String StmActionW = "STM | W";
    String StmActionE = "STM | E000";
    String StmActionA = "STM | A000";
    String StmActionS = "STM | S";
    String StmActionD = "STM | D000";
    // Android message types



}
