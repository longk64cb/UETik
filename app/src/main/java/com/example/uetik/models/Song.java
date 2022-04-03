package com.example.uetik.models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;

public class Song implements Parcelable {
    public int id;
    public String artist;
    public String title;
    public Uri albumArt;
    public String songPath;
    public String duration;

    public Song (String title, String artist, Uri albumArt, String songPath, String duration) {
        this.title = title;
        this.artist = artist;
        this.albumArt = albumArt;
        this.songPath = songPath;
        this.duration = duration;
    }

    protected Song(Parcel in) {
        id = in.readInt();
        artist = in.readString();
        title = in.readString();
        albumArt = in.readParcelable(Uri.class.getClassLoader());
        songPath = in.readString();
        duration = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeParcelable(albumArt, flags);
        dest.writeString(songPath);
        dest.writeString(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}

