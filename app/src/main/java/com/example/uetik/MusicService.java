package com.example.uetik;

import static com.example.uetik.ApplicationClass.ACTION_NEXT;
import static com.example.uetik.ApplicationClass.ACTION_PLAY;
import static com.example.uetik.ApplicationClass.ACTION_PREVIOUS;
import static com.example.uetik.ApplicationClass.CHANNEL_ID_2;
import static com.example.uetik.MainActivity.getAlbumArtFromUri;
import static com.example.uetik.MainActivity.musicService;
import static com.example.uetik.MainActivity.songList;
import static com.example.uetik.ui.PlayerActivity.playMode;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.uetik.models.Song;
import com.example.uetik.ui.PlayerActivity;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    private IBinder mBinder = new MyBinder();
    private MediaPlayer mediaPlayer;
    private MediaSessionCompat mediaSessionCompat;
    public ArrayList<Song> songs = new ArrayList<>();
    private Uri uri;
    public int position = 0;
    ActionPlaying actionPlaying;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String SONG_TITLE = "SONG_TITLE";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String ALBUM_ART = "ALBUM_ART";
    public static final String SONG_LIST = "SONG_LIST";
    public static final String POSITION = "POSITION";

    @Override
    public void onCreate() {
        super.onCreate();
        songs = songList;
        mediaSessionCompat = new MediaSessionCompat(this, "My Audio");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mediaPlayer.stop();
//        mediaPlayer.release();
//    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition", -1);
        String actionName = intent.getStringExtra("ActionName");
        Bundle bundle1 = intent.getBundleExtra("songList");
        Log.d("check3a", "3a: " + bundle1);
//        songs = (ArrayList) bundle1.getSerializable("songList");
        Log.v("Test", "Start service");
//        if (mediaPlayer != null) {
//            Log.v("Test", "Mediaplayer is not null");
//            if (mediaPlayer.isPlaying()) {
//                Log.v("Test", "Mediaplayer is playing");
//            }
//        }
        if (myPosition != -1) {
            playMedia(myPosition);
        }
        if (actionName != null) {
            switch (actionName) {
                case "playPause":
                    Toast.makeText(this, "PlayPause", Toast.LENGTH_SHORT).show();
//                    if (actionPlaying != null) {
//                        actionPlaying.playPauseBtnClicked();
//
//                    }
                    playPauseBtnClicked();
                    break;
                case "next":
                    Toast.makeText(this, "Next", Toast.LENGTH_SHORT).show();
                    nextBtnClicked();
                    break;
                case "previous":
                    Toast.makeText(this, "Previous", Toast.LENGTH_SHORT).show();
                    previousBtnClicked();
                    break;
            }
        }
        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        Log.v("Test", "Start");
        position = startPosition;
        if (mediaPlayer != null)
        {
            Log.v("Test", "MediaPlayer is not null");
            try {
                mediaPlayer.stop(); //error
                mediaPlayer.reset();
//                mediaPlayer.release();
            } catch(Exception e){
                Log.d("Nitif Activity", e.toString());
            }
            if (songs != null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else
        {
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    public void start() {
        mediaPlayer.start();
    }

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public int getCurrentPosition() {
        int curPos = 0;
        try {
            curPos = mediaPlayer.getCurrentPosition();
        } catch(Exception e){
            Log.d("Nitif Activity", e.toString());
        }
        return curPos;
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void release() {
        mediaPlayer.release();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    public void createMediaPlayer(int positionInner) {
        position = positionInner;
        uri = Uri.parse(songs.get(position).getSongPath());
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        Gson gson = new Gson();
        String json = gson.toJson(songs);
        editor.putInt(POSITION, position);
        editor.putString(SONG_LIST, json);
        editor.putString(MUSIC_FILE, uri.getPath());
        editor.putString(SONG_TITLE, songs.get(position).getTitle());
        editor.putString(ARTIST_NAME, songs.get(position).getArtist());
//        editor.putString(ALBUM_ART, songs.get(position).getAlbumArt());
//        editor.put
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public void createMediaPlayer(int positionInner, ArrayList<Song> songsToPlay) {
        position = positionInner;
        songs = songsToPlay;
        uri = Uri.parse(songs.get(position).getSongPath());
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        Gson gson = new Gson();
        String json = gson.toJson(songs);
        editor.putString(SONG_LIST, json);
        editor.putInt(POSITION, position);
        editor.putString(MUSIC_FILE, uri.getPath());
        editor.putString(SONG_TITLE, songs.get(position).getTitle());
        editor.putString(ARTIST_NAME, songs.get(position).getArtist());
//        editor.putString(ALBUM_ART, songs.get(position).getAlbumArt());
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void onCompleted() {
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (playMode != null) {
            switch (playMode) {
                case SHUFFLE:
                    position = getRandom(songs.size() - 1);
                    break;
                case REPEAT:
                    position = ((position + 1) % songs.size());
                    break;
                case REPEAT_ONE:
                    break;
            }
        }
        if (actionPlaying != null) {
            actionPlaying.nextBtnClicked();
        }
        onCompleted();
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    public void setCallBack(ActionPlaying actionPlaying) {
        this.actionPlaying = actionPlaying;
    }

    public void playPauseBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.playPauseBtnClicked();
        } else {
            if (mediaPlayer.isPlaying()) {
                pause();
//                showNotification(R.drawable.play);
            } else {
                start();
//                showNotification(R.drawable.pause);
            }
        }
    }

    public void previousBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.prevBtnClicked();
        }
    }

    public void nextBtnClicked() {
        if (actionPlaying != null) {
            actionPlaying.nextBtnClicked();
            Log.v("Test", "ActionPlaying next");
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position + 1) % songs.size());
            createMediaPlayer(position);
            onCompleted();
            start();
//            showNotification(R.drawable.pause);
        }
        Log.v("Test", "Non-ActionPlaying next");

    }

    private void showNotification(int playPauseBtn) {
        Intent intent = new Intent(this, MusicService.class);
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
        byte[] art = getAlbumArtFromUri(songs.get(position).getSongPath());
        if (art != null) {
            picture = BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            picture = BitmapFactory.decodeResource(getResources(), R.drawable.album_art );
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(picture)
                .setContentTitle(songs.get(position).getTitle())
                .setContentText(songs.get(position).getArtist())
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
}
