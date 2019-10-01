package com.example.demo_19;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_19.adapter.MusicAdapter;
import com.example.demo_19.api.DownloadMusic2;
import com.example.demo_19.api.GetDataFromAPI;
import com.example.demo_19.model.MusicInfo;
import com.example.demo_19.model.RoundImageView;

import java.util.ArrayList;

import static com.example.demo_19.R.drawable.ic_play;

public class MainActivity extends AppCompatActivity {
    ImageView imgNext;
    ImageView imgPlayPause;
    ImageView imgPrevious;

    RoundImageView imgThumbnail;
    TextView txtSong;
    TextView txtSinger;
    ProgressBar pbThumbnail;

    SeekBar sbLoading;
    TextView txtCurrentTime;
    private MediaPlayer mediaPlayer;

    private static boolean isPlaying = false;
    Handler handler;
    Runnable updateTime;
    private static int pos;
    Animation rotateAnimation;

    RecyclerView rvList;
    ProgressBar pbLoading;
    GetDataFromAPI getDataFromAPI;
    ArrayList<MusicInfo> musicList;
    MusicAdapter adapter;

    private static boolean checkedChooseMusicPhone = false;
    private static boolean checkedChooseMusicInternet = false;
    private static boolean selectedASong = false;
    String tempLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        addControl();
        addEvent();
    }

    private void addControl() {
        imgNext = findViewById(R.id.imgNext);
        imgPlayPause = findViewById(R.id.imgPlayPause);
        imgPrevious = findViewById(R.id.imgPrevious);

        imgThumbnail = findViewById(R.id.imgThumbnail);
        txtSong = findViewById(R.id.txtSong);
        txtSinger = findViewById(R.id.txtSinger);

        sbLoading = findViewById(R.id.sbLoading);
        txtCurrentTime = findViewById(R.id.txtCurrentTime);

        rvList = findViewById(R.id.rvList);
        pbLoading = findViewById(R.id.pbLoading);
        pbThumbnail = findViewById(R.id.pbThumbnail);

        handler = new Handler(getMainLooper());

        updateTime = new Runnable() {
            @Override
            public void run() {
                if (handlerCondition) {
                    Log.d("_handler", "new handler");

                    int temp = mediaPlayer.getCurrentPosition();

                    //update seekBar
                    sbLoading.setProgress(temp / 1000);

                    //update time
                    String current = getTimeFormatted(temp);
                    txtCurrentTime.setText(current + " / " + tempLength);

                    txtCurrentTime.postDelayed(this, 1000);
                } else {
                    Log.d("_handler", "handler stopped");
                }
            }
        };

        sbLoading.setProgress(0);
        rotateAnimation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        musicList = new ArrayList<>();
        adapter = new MusicAdapter(this, musicList, pbThumbnail);
        rvList.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        rvList.setLayoutManager(manager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkIsPlaying();
    }

    private void checkIsPlaying() {
        if (isPlaying) {
            imgPlayPause.setImageResource(R.drawable.ic_pause);
            sbLoading.setVisibility(View.VISIBLE);
            txtCurrentTime.setVisibility(View.VISIBLE);

            //change layout
            ConstraintLayout constraintLayout = findViewById(R.id.parent_layout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.rvList, ConstraintSet.BOTTOM, R.id.sbLoading, ConstraintSet.TOP);
            constraintSet.applyTo(constraintLayout);
        } else {
            imgPlayPause.setImageResource(ic_play);
            sbLoading.setVisibility(View.INVISIBLE);
            txtCurrentTime.setVisibility(View.INVISIBLE);

            //change layout
            ConstraintLayout constraintLayout = findViewById(R.id.parent_layout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.rvList, ConstraintSet.BOTTOM, R.id.musicPlayerLayout, ConstraintSet.TOP);
            constraintSet.applyTo(constraintLayout);
        }
    }

    private String getTimeFormatted(int milliseconds) {
        int temp = milliseconds / 1000;
        int m = temp / 60;
        int s = temp % 60;

        String mm;
        String ss;

        if (m < 10)
            mm = "0" + m;
        else
            mm = "" + m;

        if (s < 10)
            ss = "0" + s;
        else
            ss = "" + s;

        return mm + ":" + ss;
    }

    private static boolean currentItem = true;

    private void addEvent() {
        imgPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedChooseMusicPhone) {
                    if (mediaPlayer.isPlaying()) {
                        //turn off music
                        stopMusic();
                        currentItem = true;
                    } else {
                        //turn on music
                        if (currentItem = true) {
                            restartMusic();
                        }
                    }
                } else {
                    if (checkedChooseMusicInternet) {
                        if (selectedASong) {
                            //play music =====
                            if (mediaPlayer.isPlaying()) {
                                //turn off music
                                stopMusic();
                                currentItem = true;
                            } else {
                                if (currentItem = true) {
                                    restartMusic();
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Choose a song!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Please choose music resource first!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        sbLoading.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    Log.d("_seekBar", "" + i);
                    String length = getTimeFormatted(mediaPlayer.getDuration());
                    String current = getTimeFormatted(i * 1000);
                    txtCurrentTime.setText(current + " / " + length);
                    pos = i;
                } catch (Exception e) {

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("_seekBar", "onStartTrackingTouch");
                imgPlayPause.setImageResource(ic_play);
                handlerCondition = false;
                mediaPlayer.pause();
                isPlaying = false;

                rotateAnimation.cancel();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("_seekBar", "onStopTrackingTouch");
                imgPlayPause.setImageResource(R.drawable.ic_pause);
                handlerCondition = true;
                loadCurrentTime(getTimeFormatted(mediaPlayer.getDuration()));
                mediaPlayer.seekTo(pos * 1000);
                mediaPlayer.start();
                isPlaying = true;

                rotateAnimation.startNow();
            }
        });

        imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View v = inflater.inflate(R.layout.song_resource_layout, null, false);

                TextView close = v.findViewById(R.id.txtAlertDialogClose);
                ConstraintLayout internet = v.findViewById(R.id.fromInternetLayoutAlertDialog);
                ConstraintLayout phone = v.findViewById(R.id.fromPhoneLayoutAlertDialog);

                builder.setView(v);
                final AlertDialog dialog = builder.create();
                dialog.show();

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                internet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkedChooseMusicInternet = true;
                        loadAPI();
                        dialog.dismiss();
                    }
                });

                phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                Toast.makeText(MainActivity.this, "From Phone", Toast.LENGTH_LONG).show();
                        checkedChooseMusicPhone = true;
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            currentPosMusic = 0;
                            mediaPlayer.seekTo(currentPosMusic);
                            stopMusic();
                        }
                        playPhoneMusic();
                        dialog.dismiss();
                    }
                });
            }
        });

        adapter.setOnItemClickListener(new MusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
//                tempPosition=position;
//                ConstraintLayout layout=view.findViewById(R.id.clItemLayout);
//                layout.setBackgroundColor(getResources().getColor(R.color.background_bright));
                downloadMusic(position);

//                pbThumbnail.setVisibility(View.INVISIBLE);
            }
        });
    }

    private static boolean handlerCondition = false;

    private void loadCurrentTime(final String length) {
        tempLength = length;
        handler.post(updateTime);
    }

    private void rotateImage() {
        rotateAnimation.setDuration(10000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        imgThumbnail.startAnimation(rotateAnimation);
    }

    protected void stopMusic() {
        if (mediaPlayer != null) {
            currentPosMusic = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();

            rotateAnimation.cancel();
            imgPlayPause.setImageResource(ic_play);
            handlerCondition = false;
            handler.removeCallbacks(updateTime);
        }
    }

    protected void playMusic(Uri uri, int position) {
        try {
//            mediaPlayer=MediaPlayer.create(MainActivity.this, uri);
//            mediaPlayer.start();
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    currentPosMusic=0;
//                    mediaPlayer.seekTo(currentPosMusic);
//                    stopMusic();
//                }
//            });
//            handlerCondition=true;
//            loadCurrentTime(getTimeFormatted(mediaPlayer.getDuration()));
//            sbLoading.setMax(mediaPlayer.getDuration()/1000);

            txtSong.setText(musicList.get(position).getSong_name());
            Log.d("_Check_item_song", musicList.get(position).getSong_name());
            txtSinger.setText(musicList.get(position).getSinger());

            imgThumbnail.setImageBitmap(musicList.get(position).getThumbnail());
            rotateImage();

            imgPlayPause.setImageResource(R.drawable.ic_pause);

            sbLoading.setProgress(0);
            sbLoading.setVisibility(View.VISIBLE);
            txtCurrentTime.setVisibility(View.VISIBLE);

            ConstraintLayout constraintLayout = findViewById(R.id.parent_layout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.rvList, ConstraintSet.BOTTOM, R.id.sbLoading, ConstraintSet.TOP);
            constraintSet.applyTo(constraintLayout);
        } catch (Exception e) {
//            Toast.makeText(MainActivity.this, "Downloading...", Toast.LENGTH_LONG).show();
//            playMusic(uri, position);
        }

    }

    private void playPhoneMusic() {
        mediaPlayer = null;
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.neungayay);
            sbLoading.setMax(mediaPlayer.getDuration() / 1000);
            txtCurrentTime.setText("00:00 / " + getTimeFormatted(mediaPlayer.getDuration()));
            Uri uri = Uri.parse("android.resource://com.example.demo_19/" + R.raw.neungayay);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(MainActivity.this, uri);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    currentPosMusic = 0;
                    mediaPlayer.seekTo(currentPosMusic);
                    stopMusic();
                }
            });

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST);
            byte[] rawArt = retriever.getEmbeddedPicture();
            if (rawArt != null) {
                BitmapFactory.Options bfo = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.length, bfo);
                imgThumbnail.setImageBitmap(bitmap);
                rotateImage();
            }

            txtSong.setText(title);
            txtSinger.setText(artist);

            mediaPlayer.start();

            handlerCondition = true;
            loadCurrentTime(getTimeFormatted(mediaPlayer.getDuration()));
            sbLoading.setMax(mediaPlayer.getDuration() / 1000);

            imgPlayPause.setImageResource(R.drawable.ic_pause);

            sbLoading.setProgress(0);
            sbLoading.setVisibility(View.VISIBLE);
            txtCurrentTime.setVisibility(View.VISIBLE);

            ConstraintLayout constraintLayout = findViewById(R.id.parent_layout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.rvList, ConstraintSet.BOTTOM, R.id.sbLoading, ConstraintSet.TOP);
            constraintSet.applyTo(constraintLayout);
        } else {
            mediaPlayer.start();
            handlerCondition = true;
            rotateImage();
            loadCurrentTime(getTimeFormatted(mediaPlayer.getDuration()));
        }
    }

    private static int currentPosMusic = 0;

    private void restartMusic() {
        if (currentPosMusic != mediaPlayer.getDuration()) {
            mediaPlayer.seekTo(currentPosMusic);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    currentPosMusic = 0;
                    mediaPlayer.seekTo(currentPosMusic);
                    stopMusic();
                }
            });
            handlerCondition = true;
            loadCurrentTime(getTimeFormatted(mediaPlayer.getDuration()));
            sbLoading.setMax(mediaPlayer.getDuration() / 1000);
            imgPlayPause.setImageResource(R.drawable.ic_pause);
            rotateImage();
        } else {
            mediaPlayer.seekTo(0);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopMusic();
                }
            });
            handlerCondition = true;
            loadCurrentTime(getTimeFormatted(mediaPlayer.getDuration()));
            sbLoading.setMax(mediaPlayer.getDuration() / 1000);
            imgPlayPause.setImageResource(R.drawable.ic_pause);
            rotateImage();
        }
    }

    private void loadAPI() {
        musicList.clear();
        adapter.notifyDataSetChanged();

        getDataFromAPI = new GetDataFromAPI(musicList, pbLoading, adapter);
        getDataFromAPI.execute(GetDataFromAPI.API);
        rvList.setVisibility(View.VISIBLE);
    }

    private void downloadMusic(final int position) {
        selectedASong = true;
        DownloadMusic2 downloadMusic2 = new DownloadMusic2(MainActivity.this, musicList.get(position), pbThumbnail);
        downloadMusic2.execute(musicList.get(position).getMp3_url());
        downloadMusic2.setOnDownloadMusicResult(new DownloadMusic2.OnDownloadMusicResult() {
            @Override
            public void onDownloadMusicResult(int status) {
                switch (status) {
                    case -1:
                        Toast.makeText(MainActivity.this, "Download Music Faile!", Toast.LENGTH_LONG).show();
                        break;
                    default:
//                        pbThumbnail.setVisibility(View.VISIBLE);

//                        Toast.makeText(MainActivity.this, "Loading", Toast.LENGTH_SHORT).show();
//                        Log.d("_Thread_MainThread", Thread.currentThread().getName());

//                        stopMusic();
//                        Uri uri=Uri.parse(musicList.get(position).getMp3_url());
//                        playMusic(uri, position);
//                        currentItem=false;
//                        new UpdateMusicPlayer(position).execute();
//                        final Handler handler=new Handler();
//                        final Runnable runnableSet=new Runnable() {
//                            @Override
//                            public void run() {
//                                pbThumbnail.setVisibility(View.VISIBLE);
//                            }
//                        };
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                pbThumbnail.post();
////
////                                stopMusic();
////                                Uri uri=Uri.parse(musicList.get(position).getMp3_url());
////                                playMusic(uri, position);
////                                currentItem=false;
////
////                            }
////                        });
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                pbThumbnail.post(runnableSet);
//
//                                stopMusic();
//                                Uri uri=Uri.parse(musicList.get(position).getMp3_url());
//                                playMusic(uri, position);
//                                currentItem=false;
//
//                                pbThumbnail.removeCallbacks(runnableSet);
//                            }
//                        });

//                        new UpdateMusicPlayer().execute(position);

//                        pbThumbnail.setVisibility(View.INVISIBLE);

//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                pbThumbnail.setVisibility(View.VISIBLE);
//                                stopMusic();
//                                Uri uri=Uri.parse(musicList.get(position).getMp3_url());
//                                playMusic(uri, position);
//                                currentItem=false;
//                                pbThumbnail.setVisibility(View.INVISIBLE);
//                            }
//                        });

//                        stopMusic();
//                        Uri uri=Uri.parse(musicList.get(position).getMp3_url());
//                        playMusic(uri, position);
//                        currentItem=false;
//                        break;

                        new UpdateMusicPlayer(position).execute();
                }
            }
        });
    }

    protected class UpdateMusicPlayer extends AsyncTask<Integer, Integer, String> {
        int position;

        public UpdateMusicPlayer(int position) {
            this.position = position;
        }

        @Override
        protected String doInBackground(Integer... integers) {
//            Log.d("_Thread_MainThread", Thread.currentThread().getName());
            stopMusic();

            Uri uri = Uri.parse(musicList.get(position).getMp3_url());
            mediaPlayer = MediaPlayer.create(MainActivity.this, uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    currentPosMusic = 0;
                    mediaPlayer.seekTo(currentPosMusic);
                    stopMusic();

                }
            });
            handlerCondition = true;
            loadCurrentTime(getTimeFormatted(mediaPlayer.getDuration()));
            sbLoading.setMax(mediaPlayer.getDuration() / 1000);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbThumbnail.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Uri uri = Uri.parse(musicList.get(position).getMp3_url());
            playMusic(uri, position);
            currentItem = false;
            pbThumbnail.setVisibility(View.INVISIBLE);
        }
    }
}
