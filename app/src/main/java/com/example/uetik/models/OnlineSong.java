package com.example.uetik.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class OnlineSong extends Song implements Parcelable {
    public int songId;
    public String topic;
    public String imgPath;

    protected OnlineSong(Parcel in) {
        songId = in.readInt();
        topic = in.readString();
        imgPath = in.readString();
        author = in.readString();
        songName = in.readString();
//        albumArt = in.readString();
        path = in.readString();
        duration = in.readInt();
    }

    public static final Creator<OnlineSong> CREATOR = new Creator<OnlineSong>() {
        @Override
        public OnlineSong createFromParcel(Parcel in) {
            return new OnlineSong(in);
        }

        @Override
        public OnlineSong[] newArray(int size) {
            return new OnlineSong[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(songId);
        parcel.writeString(topic);
        parcel.writeString(imgPath);
        parcel.writeString(author);
        parcel.writeString(songName);
        parcel.writeString(path);
        parcel.writeInt(duration);
    }
}

