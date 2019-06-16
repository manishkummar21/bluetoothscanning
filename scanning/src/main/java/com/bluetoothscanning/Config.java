package com.bluetoothscanning;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

public class Config implements Parcelable {

    public static final String EXTRA_CONFIG = "config";
    public static final String EXTRA_Listener = "listener";

    private String title;
    private int backgroundcolor;
    private int pulsecolor;
    private ArrayList avatars;

    public Config() {

    }

    private Config(Parcel in) {
        this.title = in.readString();
        this.backgroundcolor = in.readInt();
        this.pulsecolor = in.readInt();
        this.avatars = in.readArrayList(Integer.class.getClassLoader());
    }

    public static final Creator<Config> CREATOR = new Creator<Config>() {
        @Override
        public Config createFromParcel(Parcel in) {
            return new Config(in);
        }

        @Override
        public Config[] newArray(int size) {
            return new Config[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBackgroundcolor() {
        return backgroundcolor;
    }

    public void setBackgroundcolor(int backgroundcolor) {
        this.backgroundcolor = backgroundcolor;
    }

    public int getPulsecolor() {
        return pulsecolor;
    }

    public void setPulsecolor(int pulsecolor) {
        this.pulsecolor = pulsecolor;
    }

    public ArrayList getAvatars() {
        if (avatars == null || avatars.isEmpty())
            return new ArrayList<>(Arrays.asList(R.drawable.icon1, R.drawable.icon2, R.drawable.icon3, R.drawable.icon4, R.drawable.icon5, R.drawable.icon6, R.drawable.icon7, R.drawable.icon8, R.drawable.icon9, R.drawable.icon10, R.drawable.icon11, R.drawable.icon12));
        return avatars;
    }

    public void setAvatars(ArrayList avatars) {
        this.avatars = avatars;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeInt(backgroundcolor);
        parcel.writeInt(pulsecolor);
        parcel.writeList(avatars);
    }
}
