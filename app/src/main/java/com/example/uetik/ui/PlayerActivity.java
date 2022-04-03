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

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    View activityView;
    Button btnPlay, btnNext, btnPrev;
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

        int durationTotal = Integer.parseInt((String) songList.get(position).duration) / 1000;
        endSong.setText(formatTime(durationTotal));
        Log.v("Test", songList.get(position).duration);

        waveformSeekBar.setOnProgressChanged(new SeekBarOnProgressChanged() {
            @Override
            public void onProgressChanged(@NonNull WaveformSeekBar waveformSeekBar, float v, boolean b) {
                if (mediaPlayer != null && b) {
                    mediaPlayer.seekTo((int)v * 1000);
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
            btnPlay.setBackgroundResource(R.drawable.ic_baseline_play_circle_outline_24);
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
            btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
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
        position = ((position + 1) % songList.size());
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
            btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
        } else {
            btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
        }
        mediaPlayer.start();
    }

    private void prevBtnClicked() {
        mediaPlayer.stop();
        mediaPlayer.release();
        position = ((position - 1) < 0 ? (songList.size() - 1) : (position - 1));
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
            btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
        } else {
            btnPlay.setBackgroundResource(R.drawable.ic_baseline_pause_circle_outline_24);
        }
        mediaPlayer.start();
    }

    private void setPlayingSongView() {
        songUri = Uri.parse(songList.get(position).songPath);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
        txtSongName.setText(songList.get(position).title);
        txtArtistName.setText(songList.get(position).artist);
        albumArt.setImageURI(songList.get(position).albumArt);
        waveformSeekBar.setMaxProgress(mediaPlayer.getDuration() / 1000);
        int durationTotal = Integer.parseInt((String) songList.get(position).duration) / 1000;
        endSong.setText(formatTime(durationTotal));
        waveformSeekBar.setSampleFrom(songList.get(position).songPath);
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
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
}
