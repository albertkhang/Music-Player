package com.example.demo_19.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.demo_19.model.MusicInfo;
import com.example.demo_19.model.RoundCornerImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadThumbnail extends AsyncTask<String, Integer, Boolean> {
    RoundCornerImageView thumbnail;
    MusicInfo musicInfo;

    public DownloadThumbnail(RoundCornerImageView thumbnail, MusicInfo musicInfo) {
        this.thumbnail = thumbnail;
        this.musicInfo = musicInfo;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        URL url=null;
        HttpURLConnection connection=null;

        try {
            url=new URL(strings[0]);
            connection= (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream is = connection.getInputStream();
            final Bitmap bitmap= BitmapFactory.decodeStream(is);
            connection.disconnect();

            thumbnail.post(new Runnable() {
                @Override
                public void run() {
                    thumbnail.setImageBitmap(bitmap);
                    musicInfo.setThumbnail(bitmap);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
