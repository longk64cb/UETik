package com.example.uetik.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Album implements Parcelable {
    private String name;
    private String albumArt;
    private ArrayList<OfflineSong> offlineSongs;

    public Album (String name, String albumArt, ArrayList<OfflineSong> offlineSongs) {
        this.name = name;
        this.albumArt = albumArt;
        this.offlineSongs = offlineSongs;
    }

    protected Album(Parcel in) {
        name = in.readString();
        albumArt = in.readString();
        offlineSongs = in.createTypedArrayList(OfflineSong.CREATOR);
    }

    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    public ArrayList<OfflineSong> getSongs() {
        return offlineSongs;
    }

    public String getName() {
        return name;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(albumArt);
        parcel.writeTypedList(offlineSongs);
    }
}
