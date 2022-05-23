package com.example.uetik.models;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.Serializable;

public class Song implements Parcelable {
    private String artist;
    private String title;
//    private String albumArt;
    private String songPath;
    private String duration;
    private String id;
    private String albumName;

    public Song (String title, String artist, String albumArt, String songPath, String duration, String id, String albumName) {
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

    protected Song(Parcel in) {
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

