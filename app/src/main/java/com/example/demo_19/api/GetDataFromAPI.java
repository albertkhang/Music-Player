package com.example.demo_19.api;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.demo_19.adapter.MusicAdapter;
import com.example.demo_19.model.MusicInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetDataFromAPI extends AsyncTask<String, Integer, String> {
    public static final String API="https://api.deezer.com/artist/1562681/top?limit=30";

    ArrayList<MusicInfo> musicList;
    ProgressBar pbLoading;
    MusicAdapter adapter;

    public GetDataFromAPI(ArrayList<MusicInfo> musicList, ProgressBar pbLoading, MusicAdapter adapter) {
        this.musicList = musicList;
        this.pbLoading = pbLoading;
        this.adapter = adapter;
    }

    @Override
    protected String doInBackground(String... strings) {
        String data="";
        URL url;
        HttpURLConnection connection=null;

        //get Object
        try {
            url=new URL(strings[0]);
            connection= (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStreamReader isr=new InputStreamReader(connection.getInputStream());
            BufferedReader br=new BufferedReader(isr);

            String line=br.readLine();

            while (line!=null){
                data+=line;
                line=br.readLine();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.disconnect();

        return data;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject object=new JSONObject(s);
            JSONArray jsonArray=object.getJSONArray("data");
            int amount=jsonArray.length();
            Log.d("_json", ""+amount);

            for (int i=0; i<amount; i++){
                Log.d("_infos", "id: "+i);
                JSONObject currentObject=jsonArray.getJSONObject(i);
                //title
                String title=currentObject.getString("title");
                Log.d("_infos", "title: "+title);

                //mp3
                String url_mp3=currentObject.getString("preview");
                Log.d("_infos", "mp3_url: "+url_mp3);

                //duration
                String duration_string=currentObject.getString("duration");
                int duration_int=Integer.parseInt(duration_string);
                String duration=convertTime(duration_int);
                Log.d("_infos", "duration: "+duration);

                //get contributors array object
                JSONArray contributorsArrayObject=currentObject.getJSONArray("contributors");
                JSONObject firstContributorsObject=contributorsArrayObject.getJSONObject(0);

                //singer
                String singer=firstContributorsObject.getString("name");
                Log.d("_infos", "singer: "+singer);

                //thumbnail
                String thumbail_url=firstContributorsObject.getString("picture_medium");
                Log.d("_infos", "thumbnail: "+thumbail_url);

                //add item
                final MusicInfo item=new MusicInfo();
                //song_name
                item.setSong_name(title);
                //singer
                item.setSinger(singer);
                //duration
                item.setDuration(duration);
                //thumbnail
                item.setThumbnail_url(thumbail_url);
                //mp3
                item.setMp3_url(url_mp3);

                musicList.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pbLoading.setVisibility(View.INVISIBLE);

        adapter.notifyDataSetChanged();
    }

    private String convertTime(int seconds){
        int m=seconds/60;
        int s=seconds%60;
        String mm=""+m;
        String ss=""+s;

        if (m<10 || s<10)
        {
            if (m<10)
                mm="0"+mm;
            if(s<10)
                ss="0"+ss;
        }

        return mm+":"+ss;
    }
}
