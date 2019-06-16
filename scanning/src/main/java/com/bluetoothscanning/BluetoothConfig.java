package com.bluetoothscanning;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class BluetoothConfig {

    private static abstract class BaseBuilder {

        protected Config config;
        protected IDetected listener;

        private BaseBuilder(Context context) {
            this.config = new Config();
        }
    }


    public static abstract class Builder extends BaseBuilder {

        public Builder(Activity activity) {
            super(activity);
        }

        public Builder(Fragment fragment) {
            super(fragment.getActivity());
        }

        public Builder setTitle(String title) {
            config.setTitle(title);
            return this;
        }

        public Builder setBackgroundColor(int color) {
            config.setBackgroundcolor(color);
            return this;
        }

        public Builder setPulseColor(int color) {
            config.setPulsecolor(color);
            return this;
        }

        public Builder setAvators(ArrayList<Integer> images) {
            config.setAvatars(images);
            return this;
        }

        public Builder setListener(IDetected listener) {
            this.listener = listener;
            return this;
        }


        public abstract void start();

    }

    static class ActivityBuilder extends Builder {
        private Activity activity;

        private ActivityBuilder(Activity activity) {
            super(activity);
            this.activity = activity;
        }

        @Override
        public void start() {
            Intent intent = new Intent(activity, BluetoothDetection.class);
            intent.putExtra(Config.EXTRA_CONFIG, config);
            intent.putExtra(Config.EXTRA_Listener, listener);
            activity.startActivity(intent);
        }
    }

    public static Builder with(Activity activity) {
        return new ActivityBuilder(activity);
    }


}
