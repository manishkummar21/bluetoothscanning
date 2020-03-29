package com.bluetoothscanning;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bluetoothscanning.controller.BluetoothController;
import com.bluetoothscanning.databinding.ActivityMainBinding;


public class BluetoothDetection extends AppCompatActivity implements IBluetooth {

    private String TAG = BluetoothDetection.class.getCanonicalName();
    private ActivityMainBinding mainBinding;
    private BluetoothController controller;
    private Config config;
    private IDetected listener = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        controller = new BluetoothController(this, this, receiver);
        config = getIntent().getParcelableExtra(Config.EXTRA_CONFIG);
        if (getIntent().hasExtra(Config.EXTRA_Listener))
            listener = (IDetected) getIntent().getSerializableExtra(Config.EXTRA_Listener);

        //Initializing data from config
        mainBinding.title.setText(TextUtils.isEmpty(config.getTitle()) ? getString(R.string.title) : config.getTitle());
        mainBinding.backgroundcolor.setBackgroundColor(config.getBackgroundcolor() == 0 ? getResources().getColor(R.color.backgroundcolor) : config.getBackgroundcolor());
        mainBinding.pulsator.setColor(config.getPulsecolor() == 0 ? getResources().getColor(R.color.pulsecolor) : config.getPulsecolor());
        mainBinding.pulsator.setAvators(config.getAvatars());

        // let us check bluetoothSupports or not if not then finish the activity
        if (controller.checkIfDeviceSupports()) {
            Toast.makeText(getApplicationContext(), "Device not Support Bluetooth", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        controller.setName();

        //Check the bluetooth persmission
        controller.enableAllPermission();

        //Start Animation
        startPulse();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothController.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            controller.startDiscoveryDelay();
        } else
            finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == BluetoothController.MY_PERMISSIONS_REQUEST_Location) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                controller.enableBluetooth();
            } else {
                finish();
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mainBinding.pulsator.clearedDetectedDevices();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mainBinding.pulsator.addDetecteddevice(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                controller.startDiscovery();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.stopDiscovery();
        unregisterReceiver(receiver);
    }

    @Override
    public void startPulse() {

        mainBinding.pulsator.post(new Runnable() {
            @Override
            public void run() {
                mainBinding.pulsator.setListener(listener);
                mainBinding.pulsator.start();
            }
        });
    }

    @Override
    public void setName(final String displayname) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainBinding.name.setText(displayname);
            }
        });
    }

}
