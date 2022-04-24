package com.example.uetik.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Album implements Parcelable {
    private String name;
    private Uri albumArt;
    private ArrayList<Song> songs;

    public Album (String name, Uri albumArt, ArrayList<Song> songs) {
        this.name = name;
        this.albumArt = albumArt;
        this.songs = songs;
    }

    protected Album(Parcel in) {
        name = in.readString();
        albumArt = in.readParcelable(Uri.class.getClassLoader());
        songs = in.createTypedArrayList(Song.CREATOR);
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

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public String getName() {
        return name;
    }

    public Uri getAlbumArt() {
        return albumArt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeParcelable(albumArt, i);
        parcel.writeTypedList(songs);
    }
}
