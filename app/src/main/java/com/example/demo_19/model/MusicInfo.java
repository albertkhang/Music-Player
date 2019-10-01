package com.example.demo_19.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class MusicInfo {
    private String song_name;
    private String singer;
    private String duration;

    private String thumbnail_url;
    private Bitmap thumbnail;

    private String mp3_url;
    private Uri mp3;

    public MusicInfo() {
        this.song_name=null;
        this.singer=null;
        this.duration=null;
        this.thumbnail=null;
        this.mp3=null;
        this.thumbnail_url=null;
        this.mp3_url=null;
    }

    public String getMp3_url() {
        return mp3_url;
    }

    public void setMp3_url(String mp3_url) {
        this.mp3_url = mp3_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public Uri getMp3() {
        return mp3;
    }

    public void setMp3(Uri mp3) {
        this.mp3 = mp3;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
