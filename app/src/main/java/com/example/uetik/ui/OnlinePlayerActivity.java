package com.example.uetik.ui;

//import static com.example.uetik.MainActivity.musicService;

import static com.example.uetik.ApplicationClass.ACTION_NEXT;
import static com.example.uetik.ApplicationClass.ACTION_PLAY;
import static com.example.uetik.ApplicationClass.ACTION_PREVIOUS;
import static com.example.uetik.ApplicationClass.CHANNEL_ID_2;
//import static com.example.uetik.MainActivity.musicService;
import static com.example.uetik.MainActivity.offlineSongList;
import static com.example.uetik.adapter.OnlineSongAdapter.PORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;
import com.example.uetik.ActionPlaying;
import com.example.uetik.NotificationReceiver;
import com.example.uetik.OnlineMusicService;
import com.example.uetik.PlayMode;
import com.example.uetik.R;
import com.example.uetik.databinding.OnlineActivityPlayerBinding;
import com.example.uetik.models.OnlineSong;
import com.masoudss.lib.SeekBarOnProgressChanged;
import com.masoudss.lib.WaveformSeekBar;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

public class OnlinePlayerActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    View onlineActivityView;
    Button btnPlay, btnNext, btnPrev, btnPlayMode;
    TextView txtSongName, txtArtistName, currentDuration, endSong;

    public static final String EXTRA_NAME = "songName";
    int position;
    List<OnlineSong> onlineSongList;
    Uri songUri, imgUri;

    private OnlineActivityPlayerBinding binding;
    private AudioVisualization audioVisualization;
    private WaveformSeekBar waveformSeekBar;
    private ImageView topicArt;

    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    //    private MusicService musicService;
    private OnlineMusicService onlineMusicService;
    private MediaSessionCompat mediaSessionCompat;

    public static PlayMode playMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = OnlineActivityPlayerBinding.inflate(getLayoutInflater());
        onlineActivityView = binding.getRoot();
        setContentView(onlineActivityView);
        txtSongName = findViewById(R.id.txtPlayingSongName);
        txtArtistName = findViewById(R.id.txtPlayingArtistName);
        txtSongName.setSelected(true);
        currentDuration = findViewById(R.id.currentDuration);
        endSong = findViewById(R.id.endSong);
        btnPlay = findViewById(R.id.playSong);
        btnPrev = findViewById(R.id.prevSong);
        btnNext = findViewById(R.id.nextSong);
        btnPlayMode = findViewById(R.id.playModeBtn);
        audioVisualization = (AudioVisualization) findViewById(R.id.visualizer_view);
        waveformSeekBar = (WaveformSeekBar) findViewById(R.id.waveformSeekBar);
        topicArt = findViewById(R.id.albumArtPlayer);

        playMode = PlayMode.REPEAT;

        Intent i = this.getIntent();
        Bundle bundle = i.getBundleExtra("onlineSongList");
        onlineSongList = (List<OnlineSong>) bundle.getSerializable("onlineSongList");
        position = i.getIntExtra("pos", 0);
        Log.d("OPA", "val: " + onlineSongList);
        mediaSessionCompat = new MediaSessionCompat(getBaseContext(), "My Audio");

        showNotification(R.drawable.pause);
        if (onlineMusicService != null) {
            if (onlineMusicService.getMediaPlayer() != null && onlineMusicService.isPlaying()) {
                onlineMusicService.stop();
                onlineMusicService.release();
            }
        }
        Intent intent = new Intent(this, OnlineMusicService.class);
        Bundle bundleService = new Bundle();
        bundleService.putSerializable("onlineSongList", (Serializable) onlineSongList);
        intent.putExtra("servicePosition", position)
                .putExtra("onlineSongList", bundle);
        startService(intent);

        setPlayingSongView();

        int durationTotal = onlineSongList.get(position).duration;
        endSong.setText(formatTime(durationTotal));

        btnPlayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (playMode) {
                    case REPEAT:
                        btnPlayMode.setBackgroundResource(R.drawable.repeat_one);
                        playMode = PlayMode.REPEAT_ONE;
                        break;
                    case REPEAT_ONE:
                        btnPlayMode.setBackgroundResource(R.drawable.shuffle);
                        playMode = PlayMode.SHUFFLE;
                        break;
                    case SHUFFLE:
                        btnPlayMode.setBackgroundResource(R.drawable.repeat);
                        playMode = PlayMode.REPEAT;
                        break;
                }
            }
        });
        waveformSeekBar.setOnProgressChanged(new SeekBarOnProgressChanged() {
            @Override
            public void onProgressChanged(@NonNull WaveformSeekBar waveformSeekBar, float v, boolean b) {
                if (onlineMusicService != null && b) {
                    onlineMusicService.seekTo((int)v * 1000);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, OnlineMusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
//        audioVisualization.release();
        audioVisualization.onPause();
        Log.v("Test", "paused");
    }

    @Override
    protected void onStop() {
        audioVisualization.onPause();
        Log.v("Test", "stoped");
        super.onStop();
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        audioVisualization.onResume();
        playThread.start();
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        switch (playMode) {
//                            case SHUFFLE:
//                                position = getRandom(songList.size() - 1);
//                                break;
//                            default:
//                                position = ((position + 1) % songList.size());
//                                break;
//                        }
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                btnPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void playPauseBtnClicked() {
        if (onlineMusicService.isPlaying()) {
            btnPlay.setBackgroundResource(R.drawable.play);
            showNotification(R.drawable.play);
            onlineMusicService.pause();
            audioVisualization.onPause();
            OnlinePlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onlineMusicService != null) {
                        int mCurrentPosition = onlineMusicService.getCurrentPosition() / 1000;
                        waveformSeekBar.setProgress(mCurrentPosition);
                        currentDuration.setText(formatTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            btnPlay.setBackgroundResource(R.drawable.pause);
            showNotification(R.drawable.pause);
            onlineMusicService.start();
            audioVisualization.onResume();
            OnlinePlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onlineMusicService != null) {
                        int mCurrentPosition = onlineMusicService.getCurrentPosition() / 1000;
                        waveformSeekBar.setProgress(mCurrentPosition);
                        currentDuration.setText(formatTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    public void nextBtnClicked() {
//        Log.v("Test", "Next Click");
        onlineMusicService.stop();
        onlineMusicService.release();
        switch (playMode) {
            case SHUFFLE:
                position = getRandom(onlineSongList.size() - 1);
                break;
            case REPEAT_ONE:
                break;
            default:
                position = ((position + 1) % onlineSongList.size());
                break;
        }
        setPlayingSongView();
        onlineMusicService.createMediaPlayer(position);
        onlineMusicService.onCompleted();
        waveformSeekBar.setMaxProgress(onlineMusicService.getDuration() / 1000);
        showNotification(R.drawable.pause);
        onlineMusicService.start();
        OnlinePlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onlineMusicService != null) {
                    int mCurrentPosition = onlineMusicService.getCurrentPosition() / 1000;
                    waveformSeekBar.setProgress(mCurrentPosition);
                    currentDuration.setText(formatTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        if (onlineMusicService.isPlaying()) {
            btnPlay.setBackgroundResource(R.drawable.pause);
        } else {
            btnPlay.setBackgroundResource(R.drawable.play);
        }
        VisualizerDbmHandler visualizerHandler = DbmHandler.Factory.newVisualizerHandler(getBaseContext(), onlineMusicService.getAudioSessionId());
        audioVisualization.linkTo(visualizerHandler);
        audioVisualization.onResume();
    }

    public void prevBtnClicked() {
        onlineMusicService.stop();
        onlineMusicService.release();
        switch (playMode) {
            case SHUFFLE:
                position = getRandom(onlineSongList.size() - 1);
                Log.d("checkpos", "" + position);
                break;
            default:
                position = ((position - 1) < 0 ? (onlineSongList.size() - 1) : (position - 1));
                break;
        }
        setPlayingSongView();
        onlineMusicService.createMediaPlayer(position);
        waveformSeekBar.setMaxProgress(onlineMusicService.getDuration() / 1000);
        onlineMusicService.onCompleted();
        showNotification(R.drawable.pause);
        Log.d("checkcheck", "va" + onlineMusicService);
        onlineMusicService.start();
        OnlinePlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onlineMusicService != null) {
                    int mCurrentPosition = onlineMusicService.getCurrentPosition() / 1000;
                    waveformSeekBar.setProgress(mCurrentPosition);
                    currentDuration.setText(formatTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        if (onlineMusicService.isPlaying()) {
            btnPlay.setBackgroundResource(R.drawable.pause);
        } else {
            btnPlay.setBackgroundResource(R.drawable.play);
        }
        onlineMusicService.start();
        VisualizerDbmHandler visualizerHandler = DbmHandler.Factory.newVisualizerHandler(getBaseContext(), onlineMusicService.getAudioSessionId());
        audioVisualization.linkTo(visualizerHandler);
        audioVisualization.onResume();
    }

    private void setPlayingSongView() {
        OnlineSong os = onlineSongList.get(position);
        songUri = Uri.parse(PORT + os.path);
        imgUri = Uri.parse(PORT + os.imgPath);
        if (onlineMusicService == null) {
            Log.v("Test", "No music service.");
        }

        txtSongName.setText(os.songName);
        txtArtistName.setText(os.author);
//
        if (os.imgPath != null) {
            Picasso.with(topicArt.getContext()).load(PORT + os.imgPath).into(topicArt);
        } else {
            topicArt.setImageResource(R.drawable.ic_baseline_music_note_24);
        }
        int durationTotal = os.duration;
        endSong.setText(formatTime(durationTotal));
        waveformSeekBar.setSampleFrom(offlineSongList.get(position).getSongPath());
    }

    public int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private String formatTime(int time) {
        String seconds = String.valueOf(time % 60);
        String minutes = String.valueOf(time / 60);

        if (seconds.length() == 1) {
            return minutes + ":0" + seconds;
        } else {
            return minutes + ":" + seconds;
        }
    }

//    @Override
//    public void onCompletion(MediaPlayer mp) {
//
////        if (mediaPlayer != null) {
////            mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
////            mediaPlayer.start();
////            mediaPlayer.setOnCompletionListener(this);
////        }
//    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        OnlineMusicService.MyBinder myBinder = (OnlineMusicService.MyBinder) service;
        onlineMusicService = myBinder.getService();
        onlineMusicService.setCallBack(this);
        Toast.makeText(this, "Connected" + onlineMusicService, Toast.LENGTH_SHORT).show();
//        if (musicService.isPlaying()) {
//            musicService.stop();
//            musicService.release();
//        }
//        musicService.createMediaPlayer(position);
//        musicService.start();

        onlineMusicService.onCompleted();
        showNotification(R.drawable.pause);

        waveformSeekBar.setMaxProgress(onlineMusicService.getDuration() / 1000);

        Log.v("Test", "id:" + String.valueOf(onlineMusicService.getAudioSessionId()));
        VisualizerDbmHandler visualizerHandler = DbmHandler.Factory.newVisualizerHandler(getBaseContext(), onlineMusicService.getAudioSessionId());
        audioVisualization.linkTo(visualizerHandler);
        audioVisualization.onResume();

//        waveformSeekBar.setOnProgressChanged(new SeekBarOnProgressChanged() {
//            @Override
//            public void onProgressChanged(@NonNull WaveformSeekBar waveformSeekBar, float v, boolean b) {
//                if (musicService != null && b) {
//                    musicService.seekTo((int)v * 1000);
//                }
//            }
//        });
//
        OnlinePlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onlineMusicService != null) {
                    int mCurrentPosition = onlineMusicService.getCurrentPosition() / 1000;
                    waveformSeekBar.setProgress(mCurrentPosition);
                    currentDuration.setText(formatTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        onlineMusicService = null;
    }

    private void showNotification(int playPauseBtn) {
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent prevIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);

        Intent nextIntent = new Intent(this, NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap picture;
        byte[] art = getAlbumArtFromUri(PORT + onlineSongList.get(position).path);
        if (art != null) {
            picture = BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            picture = BitmapFactory.decodeResource(getResources(), R.drawable.album_art );
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(picture)
                .setContentTitle(onlineSongList.get(position).songName)
                .setContentText(onlineSongList.get(position).author)
                .addAction(R.drawable.prev, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.next, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private byte[] getAlbumArtFromUri(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }

    private Bitmap uriToBitMap(Uri uri) {
        Bitmap bitmap = null;
        ContentResolver contentResolver = getContentResolver();
        try {
            if(Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}