package com.example.demo_19.api;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.demo_19.model.MusicInfo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadMusic2 extends AsyncTask<String, Integer, Integer> {
    Context context;
    MusicInfo item;
    ProgressBar pbThumbnail;

//    MainActivity callClass;

    public DownloadMusic2(Context context, MusicInfo item, ProgressBar pbThumbnail) {
        this.context = context;
        this.item = item;
        this.pbThumbnail=pbThumbnail;
    }

    public interface OnDownloadMusicResult {
        void onDownloadMusicResult(int status);
    }
    OnDownloadMusicResult  onDownloadMusicResult;
    public void setOnDownloadMusicResult(OnDownloadMusicResult onDownloadMusicResult){
        this.onDownloadMusicResult=onDownloadMusicResult;
    }

    @Override
    protected void onPreExecute() {
//        callClass=new MainActivity();

        pbThumbnail.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        pbThumbnail.setVisibility(View.INVISIBLE);
        onDownloadMusicResult.onDownloadMusicResult(integer);
        super.onPostExecute(integer);
    }

    @Override
    protected Integer doInBackground(String... strings) {
        Log.d("_mp3_name", getNameFromUrl(item.getMp3_url()));

        String SDCardStatus = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = SDCardStatus + "/" + getNameFromUrl(item.getMp3_url()) + ".mp3";

        File file =new File(path);
        Log.d("_DownloadMusic", file.getAbsolutePath());

        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d("_DownloadMusic_Create", "File " + e + " " + file.getAbsolutePath());
                return -1;
            }
            int count;
            try {
                URL url=new URL(item.getMp3_url());
                URLConnection connection=url.openConnection();
                connection.connect();

                InputStream is=new BufferedInputStream(url.openStream());
                OutputStream os=new FileOutputStream(path);

                byte[] data=new byte[1024];

                while ((count=is.read(data)) != -1){
                    os.write(data, 0, count);
                }

                os.flush();
                os.close();
                is.close();
            } catch (MalformedURLException e) {
                Log.d("_DownloadMusic", "URL " + e);
                return -1;
            } catch (IOException e) {
                Log.d("_DownloadMusic", "OutputStream " + e);
                return -1;
            }

            item.setMp3_url(path);
            return 1;
        }else {
            return 0;
        }
    }
    private String getNameFromUrl(String url){
        String temp;
        int prePos=url.lastIndexOf("stream/") + 7;
        int postPos=url.lastIndexOf(".");

        temp=url.substring(prePos, postPos);

        return temp;
    }
}
