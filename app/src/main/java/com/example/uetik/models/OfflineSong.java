package com.example.uetik.models;

import android.os.Parcel;
import android.os.Parcelable;

public class OfflineSong implements Parcelable {
    public String artist;
    public String title;
//    private String albumArt;
    public String songPath;
    public String duration;
    public String id;
    public String albumName;


    public OfflineSong(String title, String artist, String albumArt, String songPath, String duration, String id, String albumName) {
        this.title = title;
        this.artist = artist;
//        this.albumArt = albumArt;
        this.songPath = songPath;
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

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    public String getSongPath() {
        return songPath;
    }

    public String getTitle() {
        return title;
    }

    protected OfflineSong(Parcel in) {
//        id = in.readInt();
        artist = in.readString();
        title = in.readString();
//        albumArt = in.readString();
        songPath = in.readString();
        duration = in.readString();
        id = in.readString();
        albumName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
        dest.writeString(artist);
        dest.writeString(title);
//        dest.writeString(albumArt);
        dest.writeString(songPath);
        dest.writeString(duration);
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

