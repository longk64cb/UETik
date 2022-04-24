package com.example.uetik.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cleveroad.audiovisualization.AudioVisualization;
import com.cleveroad.audiovisualization.DbmHandler;
import com.cleveroad.audiovisualization.VisualizerDbmHandler;
import com.example.uetik.R;
import com.example.uetik.databinding.ActivityPlayerBinding;
import com.example.uetik.models.Song;
import com.masoudss.lib.SeekBarOnProgressChanged;
import com.masoudss.lib.WaveformSeekBar;

import java.util.ArrayList;
import java.util.Random;

enum PlayMode {
    SHUFFLE,
    REPEAT,
    REPEAT_ONE
}

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    View activityView;
    Button btnPlay, btnNext, btnPrev, btnPlayMode;
    TextView txtSongName, txtArtistName, currentDuration, endSong;

    public static final String EXTRA_NAME = "songName";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<Song> songList;
    Uri songUri;

    private ActivityPlayerBinding binding;
    private AudioVisualization audioVisualization;
    private WaveformSeekBar waveformSeekBar;
    private ImageView albumArt;

    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread = new Thread();

    public PlayMode playMode = PlayMode.REPEAT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        activityView = binding.getRoot();
        setContentView(activityView);
        txtSongName = findViewById(R.id.txtPlayingSongName);
        txtArtistName = findViewById(R.id.txtPlayingArtistName);
        currentDuration = findViewById(R.id.currentDuration);
        endSong = findViewById(R.id.endSong);
        btnPlay = findViewById(R.id.playSong);
        btnPrev = findViewById(R.id.prevSong);
        btnNext = findViewById(R.id.nextSong);
        btnPlayMode = findViewById(R.id.playModeBtn);
        audioVisualization = (AudioVisualization) findViewById(R.id.visualizer_view);
        waveformSeekBar = (WaveformSeekBar) findViewById(R.id.waveformSeekBar);
        albumArt = findViewById(R.id.albumArtPlayer);
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i = getIntent();
        Bundle bundle = i.getBundleExtra("songList");

        songList = (ArrayList) bundle.getSerializable("songList");
        position = i.getIntExtra("pos", 0);
//        txtSongName.setSelected(true);
//        songUri = Uri.parse(songList.get(position).songPath);
//        txtSongName.setText(i.getStringExtra("songName"));
//        txtArtistName.setText(i.getStringExtra("artistName"));
//        Uri albumArtUri = Uri.parse(i.getStringExtra("albumArt"));
//        if (albumArtUri != Uri.EMPTY) {
//            albumArt.setImageURI(albumArtUri);
//        }
        setPlayingSongView();

        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);

        int durationTotal = Integer.parseInt((String) songList.get(position).getDuration()) / 1000;
        endSong.setText(formatTime(durationTotal));
        Log.v("Test", songList.get(position).getDuration());

        waveformSeekBar.setOnProgressChanged(new SeekBarOnProgressChanged() {
            @Override
            public void onProgressChanged(@NonNull WaveformSeekBar waveformSeekBar, float v, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo((int)v * 1000);
                }
            }
        });

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

        VisualizerDbmHandler vizualizerHandler = DbmHandler.Factory.newVisualizerHandler(getBaseContext(), mediaPlayer.getAudioSessionId());
        audioVisualization.linkTo(vizualizerHandler);
        audioVisualization.onResume();

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    waveformSeekBar.setProgress(mCurrentPosition);
                    currentDuration.setText(formatTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
        super.onResume();
        audioVisualization.onResume();
    }

    @Override
    protected void onPause() {
//        audioVisualization.release();
        audioVisualization.onPause();
        Log.v("Test", "paused");
        super.onPause();
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
                        Log.v("Test", "OOOO");
                    }
                });
            }
        };
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
                        switch (playMode) {
                            case SHUFFLE:
                                position = getRandom(songList.size() - 1);
                                break;
                            default:
                                position = ((position + 1) % songList.size());
                                break;
                        }
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

    private void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            btnPlay.setBackgroundResource(R.drawable.play);
            mediaPlayer.pause();
            audioVisualization.onPause();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        waveformSeekBar.setProgress(mCurrentPosition);
                        currentDuration.setText(formatTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            btnPlay.setBackgroundResource(R.drawable.pause);
            mediaPlayer.start();
            audioVisualization.onResume();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        waveformSeekBar.setProgress(mCurrentPosition);
                        currentDuration.setText(formatTime(mCurrentPosition));
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private void nextBtnClicked() {
        mediaPlayer.stop();
        mediaPlayer.release();
        setPlayingSongView();
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    waveformSeekBar.setProgress(mCurrentPosition);
                    currentDuration.setText(formatTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        if (mediaPlayer.isPlaying()) {
            btnPlay.setBackgroundResource(R.drawable.pause);
        } else {
            btnPlay.setBackgroundResource(R.drawable.pause);
        }
        mediaPlayer.start();
    }

    private void prevBtnClicked() {
        mediaPlayer.stop();
        mediaPlayer.release();
        switch (playMode) {
            case SHUFFLE:
                position = getRandom(songList.size() - 1);
                break;
            default:
                position = ((position - 1) < 0 ? (songList.size() - 1) : (position - 1));
                break;
        }
        setPlayingSongView();
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    waveformSeekBar.setProgress(mCurrentPosition);
                    currentDuration.setText(formatTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        if (mediaPlayer.isPlaying()) {
            btnPlay.setBackgroundResource(R.drawable.pause);
        } else {
            btnPlay.setBackgroundResource(R.drawable.pause);
        }
        mediaPlayer.start();
    }

    private void setPlayingSongView() {
        songUri = Uri.parse(songList.get(position).getSongPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
        txtSongName.setText(songList.get(position).getTitle());
        txtArtistName.setText(songList.get(position).getArtist());
        albumArt.setImageURI(songList.get(position).getAlbumArt());
        waveformSeekBar.setMaxProgress(mediaPlayer.getDuration() / 1000);
        int durationTotal = Integer.parseInt((String) songList.get(position).getDuration()) / 1000;
        endSong.setText(formatTime(durationTotal));
        waveformSeekBar.setSampleFrom(songList.get(position).getSongPath());
        mediaPlayer.setOnCompletionListener(this);
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private Spannable setSpan(String string) {
        Spannable span = new SpannableString(string);
        span.setSpan(new BackgroundColorSpan(0xFF000000), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
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

    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (playMode) {
            case SHUFFLE:
                position = getRandom(songList.size() - 1);
                break;
            case REPEAT:
                position = ((position + 1) % songList.size());
                break;
            case REPEAT_ONE:
                break;
        }
        nextBtnClicked();
//        if (mediaPlayer != null) {
//            mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
//            mediaPlayer.start();
//            mediaPlayer.setOnCompletionListener(this);
//        }
    }
}
