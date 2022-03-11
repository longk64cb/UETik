package com.example.uetik.models;

import android.graphics.Bitmap;
import android.net.Uri;

public class Song {
    public int id;
    public String artist;
    public String title;
    public Uri albumArt;

    public Song (String title, String artist, Uri albumArt) {
        this.title = title;
        this.artist = artist;
        this.albumArt = albumArt;
    }

}

