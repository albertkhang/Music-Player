package com.example.demo_19.api;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.example.demo_19.R;
import com.example.demo_19.model.MusicInfo;
import com.example.demo_19.model.RoundImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.os.Looper.getMainLooper;

public class DownloadMusic extends AsyncTask<String, Integer, String> {
    MusicInfo item;
    MediaPlayer mediaPlayer;
    ProgressBar pbLoading;
    View view;

    SeekBar sbLoading;
    TextView txtDuration;
    ImageView imgPlayPause;
    RoundImageView imgThumbnail;
    Handler handler;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public DownloadMusic(View view, MusicInfo item, MediaPlayer mediaPlayer, ProgressBar pbLoading, SeekBar sbLoading, TextView txtDuration, ImageView imgPlayPause, RoundImageView imgThumbnail) {
        this.view=view;
        this.item = item;
        this.mediaPlayer = mediaPlayer;
        this.pbLoading = pbLoading;
        this.sbLoading=sbLoading;
        this.txtDuration=txtDuration;
        this.imgPlayPause=imgPlayPause;
        this.imgThumbnail=imgThumbnail;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pbLoading.setVisibility(View.VISIBLE);
        handler=new Handler(getMainLooper());
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pbLoading.setVisibility(View.INVISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d("_mp3_name", getNameFromUrl(item.getMp3_url()));

        String SDCardStatus = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = SDCardStatus + "/" + getNameFromUrl(item.getMp3_url()) + ".mp3";

        File file =new File(path);
        Log.d("_DownloadMusic", file.getAbsolutePath());

        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d("_DownloadMusic", "File " + e + " " + file.getAbsolutePath());
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
            } catch (IOException e) {
                Log.d("_DownloadMusic", "OutputStream " + e);
            }

            item.setMp3_url(path);

            Uri uri=Uri.parse(path);
            mediaPlayer=MediaPlayer.create(view.getContext(), uri);
        }else {
            Uri uri=Uri.parse(path);
            mediaPlayer=MediaPlayer.create(view.getContext(), uri);
        }

        mediaPlayer.start();
        loadCurrentTime(getCurrentTimeFormatted(mediaPlayer.getDuration()));

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                imgPlayPause.setImageResource(R.drawable.ic_play);
                imgThumbnail.setAnimation(null);
                handlerCondition=false;
            }
        });

        return null;
    }

    private String getNameFromUrl(String url){
        String temp;
        int prePos=url.lastIndexOf("stream/") + 7;
        int postPos=url.lastIndexOf(".");

        temp=url.substring(prePos, postPos);

        return temp;
    }

    public void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private static boolean handlerCondition=true;
    int temp;
    private void loadCurrentTime(final String length){
        sbLoading.setMax(mediaPlayer.getDuration()/1000);
        sbLoading.setProgress(0);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (handlerCondition){
                    Log.d("_handler", "new handler");

                    temp=mediaPlayer.getCurrentPosition();

                    //update seekBar
//                    sbLoading.setProgress(temp/1000);
                    sbLoading.post(new Runnable() {
                        @Override
                        public void run() {
                            sbLoading.setProgress(temp/1000);
                        }
                    });

                    //update time
                    String current=getCurrentTimeFormatted(temp);
                    txtDuration.setText(current + " / " + length);

                    txtDuration.postDelayed(this, 1000);
                } else {
                    Log.d("_handler", "handler stopped");
                }
            }
        });
    }

    private String getCurrentTimeFormatted(int milliseconds){
        int temp=milliseconds/1000;
        int m=temp/60;
        int s=temp%60;

        String mm;
        String ss;

        if (m<10)
            mm="0"+m;
        else
            mm=""+m;

        if (s<10)
            ss="0"+s;
        else
            ss=""+s;

        return mm+":"+ss;
    }
}
