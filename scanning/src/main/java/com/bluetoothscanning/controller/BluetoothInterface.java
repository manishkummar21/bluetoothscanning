package com.bluetoothscanning.controller;

public interface BluetoothInterface {

    public boolean checkIfDeviceSupports();

    public void enableAllPermission();

    public void enableBluetooth();

    public void startDiscovery();

    public void startDiscoveryDelay();

    public void stopDiscovery();

    public void setName();


}
