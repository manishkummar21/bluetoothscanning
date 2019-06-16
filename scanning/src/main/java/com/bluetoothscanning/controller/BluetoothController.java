package com.bluetoothscanning.controller;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bluetoothscanning.IBluetooth;

import java.util.Timer;
import java.util.TimerTask;

public class BluetoothController implements BluetoothInterface {

    private BluetoothAdapter bluetoothAdapter;
    private Activity activity;
    public static final int MY_PERMISSIONS_REQUEST_Location = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    private BroadcastReceiver receiver;
    private IBluetooth listener;


    public BluetoothController(Activity activity, IBluetooth listener, BroadcastReceiver receiver) {
        this.activity = activity;
        this.listener = listener;
        this.receiver = receiver;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }


    @Override
    public boolean checkIfDeviceSupports() {
        return bluetoothAdapter == null;
    }

    @Override
    public void enableAllPermission() {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_Location);
        } else
            enableBluetooth();


    }

    @Override
    public void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else
            startDiscoveryDelay();


    }

    @Override
    public void startDiscovery() {

        stopDiscovery();

        bluetoothAdapter.startDiscovery();

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(receiver, filter);

    }

    @Override
    public void startDiscoveryDelay() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startDiscovery();
            }
        }, 3000);

    }

    @Override
    public void stopDiscovery() {

        if (bluetoothAdapter.isDiscovering())
            bluetoothAdapter.cancelDiscovery();

    }

    @Override
    public void setName() {
        listener.setName(TextUtils.isEmpty(bluetoothAdapter.getName()) ? "UnKnown" : bluetoothAdapter.getName());
    }
}
