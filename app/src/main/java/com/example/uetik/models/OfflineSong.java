package com.example.uetik.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OfflineSong extends Song implements Parcelable {
    public String id;
    public String albumName;


    public OfflineSong(String songName, String author, String albumArt, String path, int duration, String id, String albumName) {
        this.songName = songName;
        this.author = author;
//        this.albumArt = albumArt;
        this.path = path;
        this.duration = duration;
        this.id = id;
        this.albumName = albumName;
    }

//    public String getAlbumArt() {
//        return albumArt;
//    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAuthor() {
        return author;
    }

    public int getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getSongName() {
        return songName;
    }

    protected OfflineSong(Parcel in) {
//        id = in.readInt();
        author = in.readString();
        songName = in.readString();
//        albumArt = in.readString();
        path = in.readString();
        duration = in.readInt();
        id = in.readString();
        albumName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
        dest.writeString(author);
        dest.writeString(songName);
//        dest.writeString(albumArt);
        dest.writeString(path);
        dest.writeInt(duration);
        dest.writeString(id);
        dest.writeString(albumName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OfflineSong> CREATOR = new Creator<OfflineSong>() {
        @Override
        public OfflineSong createFromParcel(Parcel in) {
            return new OfflineSong(in);
        }

        @Override
        public OfflineSong[] newArray(int size) {
            return new OfflineSong[size];
        }
    };


}

