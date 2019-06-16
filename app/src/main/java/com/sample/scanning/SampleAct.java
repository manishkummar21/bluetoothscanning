package com.sample.scanning;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bluetoothscanning.BluetoothConfig;
import com.bluetoothscanning.IDetected;

public class SampleAct extends AppCompatActivity implements IDetected {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BluetoothConfig.with(this)
                .setBackgroundColor(Color.parseColor("#1E90FF"))
                .setPulseColor(Color.parseColor("#ffffff"))
                .setListener(this)
                .start();

    }

    @Override
    public void onSelectedDevice(BluetoothDevice device) {

    }
}
