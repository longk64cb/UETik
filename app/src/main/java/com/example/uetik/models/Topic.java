package com.example.uetik.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Topic implements Serializable {
    public int topicId;
    public String topicName;
    public String imgPath;
    public List<OnlineSong> songs;

    public Topic (int topicId, String topicName, String imgPath, List<OnlineSong> songs) {
        this.topicId = topicId;
        this.topicName = topicName;
        this.imgPath = imgPath;
        this.songs = songs;
    }
}
