package com.example.uetik.ui;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.uetik.ApplicationClass.ACTION_NEXT;
import static com.example.uetik.ApplicationClass.ACTION_PLAY;
import static com.example.uetik.ApplicationClass.ACTION_PREVIOUS;
import static com.example.uetik.ApplicationClass.CHANNEL_ID_2;
import static com.example.uetik.MainActivity.ALBUM_TO_FRAG;
import static com.example.uetik.MainActivity.ARTIST_TO_FRAG;
import static com.example.uetik.MainActivity.LIST_TO_FRAG;
import static com.example.uetik.MainActivity.PATH_TO_FRAG;
import static com.example.uetik.MainActivity.POSITION_TO_FRAG;
import static com.example.uetik.MainActivity.SHOW_MINI_PLAYER;
import static com.example.uetik.MainActivity.TITLE_TO_FRAG;
import static com.example.uetik.MainActivity.musicService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uetik.MusicService;
import com.example.uetik.NotificationReceiver;
import com.example.uetik.R;
import com.example.uetik.models.OfflineSong;
import com.example.uetik.models.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MinimizePlayer extends Fragment implements ServiceConnection {

    ImageView nextBtn, albumArt;
    TextView artist, songTitle;
    FloatingActionButton playPauseBtn;
    View view;
//    MusicService musicService;

    private MediaSessionCompat mediaSessionCompat;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String SONG_TITLE = "SONG_TITLE";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String ALBUM_ART = "ALBUM_ART";
    public static final String SONG_LIST = "SONG_LIST";
    public static final String POSITION = "POSITION";

    public MinimizePlayer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_minimize_player, container, false);
        artist = view.findViewById(R.id.artist_minimize);
        songTitle = view.findViewById(R.id.title_minimize);
        nextBtn = view.findViewById(R.id.skip_next_minimize);
        albumArt = view.findViewById(R.id.minimize_player_album_art);
        playPauseBtn = view.findViewById(R.id.play_btn_minimize);
        songTitle.setSelected(true);

        mediaSessionCompat = new MediaSessionCompat(getContext(), "My Audio");

        updateLastPlayed();
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService != null) {
                    if (musicService.getMediaPlayer() == null) {
                        musicService.createMediaPlayer(POSITION_TO_FRAG, (ArrayList<Song>) LIST_TO_FRAG);
                    }
                    musicService.nextBtnClicked();
                    if (musicService.isPlaying()) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                    } else {
                        playPauseBtn.setImageResource(R.drawable.play);
                    }
                    if (getActivity() != null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                                .edit();
                        if (musicService.songs != null) {
                            Gson gson = new Gson();
                            String json = gson.toJson(musicService.songs);
                            editor.putString(SONG_LIST, json);
                        }
                        editor.putString(MUSIC_FILE, musicService.songs.get(musicService.position).getPath());
                        editor.putString(SONG_TITLE, musicService.songs.get(musicService.position).getSongName());
                        editor.putString(ARTIST_NAME, musicService.songs.get(musicService.position).getAuthor());
//                        editor.putString(ALBUM_ART, musicService.songs.get(musicService.position).getAlbumArt());
                        editor.apply();
                        updateLastPlayed();
                        if (SHOW_MINI_PLAYER) {
                            if (PATH_TO_FRAG != null) {
                                byte[] byteArray = getAlbumArtFromUri(PATH_TO_FRAG);
                                if (byteArray != null) {
                                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                    albumArt.setImageBitmap(Bitmap.createScaledBitmap(bmp, 60, 60, false));
                                } else {
                                    albumArt.setImageResource(R.drawable.album_art);
                                }
                                songTitle.setText(TITLE_TO_FRAG);
                                artist.setText(ARTIST_TO_FRAG);
                            }
                        }
                    }
                    showNotification(R.drawable.pause);
                }
            }
        });
        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (musicService != null) {
                    if (musicService.getMediaPlayer() == null) {
                        musicService.createMediaPlayer(POSITION_TO_FRAG, (ArrayList<Song>) LIST_TO_FRAG);
                        musicService.start();
                    } else {
                        musicService.playPauseBtnClicked();
                    }
                    if (musicService.isPlaying()) {
                        playPauseBtn.setImageResource(R.drawable.pause);
                        showNotification(R.drawable.pause);
                    } else {
                        playPauseBtn.setImageResource(R.drawable.play);
                        showNotification(R.drawable.play);
                    }
                }
            }
        });
        albumArt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String songName = (String) songTitle.getText();
                Bundle bundle = new Bundle();
                bundle.putSerializable("songList", (Serializable) LIST_TO_FRAG);
                startActivity(new Intent(getActivity().getApplicationContext(), PlayerActivity.class)
                        .putExtra("songName", songName)
                        .putExtra("songList", bundle)
                        .putExtra("pos", POSITION_TO_FRAG));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SHOW_MINI_PLAYER) {
            if (PATH_TO_FRAG != null) {
//                byte[] byteArray = getAlbumArtFromUri(PATH_TO_FRAG);
//                if (byteArray != null) {
//                    Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                    albumArt.setImageBitmap(Bitmap.createScaledBitmap(bmp, 60, 60, false));
//                } else {
//                    albumArt.setImageResource(R.drawable.album_art);
//                }
                songTitle.setText(TITLE_TO_FRAG);
                artist.setText(ARTIST_TO_FRAG);
                Intent intent = new Intent(getContext(), MusicService.class);
                if (getContext() != null) {
                    getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
            try {
                getContext().unbindService(this);
            } catch (IllegalArgumentException e) {

            }
        }
    }

    private byte[] getAlbumArtFromUri(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }

    private Bitmap uriToBitMap(Uri uri) {
        Bitmap bitmap = null;
        ContentResolver contentResolver = getContext().getContentResolver();
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


    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }

    public void updateLastPlayed() {
        SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
        String path = preferences.getString(MUSIC_FILE, null);
        String artistText = preferences.getString(ARTIST_NAME, null);
        String songTitleText = preferences.getString(SONG_TITLE, null);
        String albumArtText = preferences.getString(ALBUM_ART, null);
        String songArrayList = preferences.getString(SONG_LIST, null);
        Gson getGson = new Gson();
        Type type = new TypeToken<ArrayList<OfflineSong>>() {}.getType();
        int position = preferences.getInt(POSITION, -1);
        if (path != null) {
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAG = path;
            ARTIST_TO_FRAG = artistText;
            TITLE_TO_FRAG = songTitleText;
            ALBUM_TO_FRAG = albumArtText;
            LIST_TO_FRAG = getGson.fromJson(songArrayList, type);
            POSITION_TO_FRAG = position;
        } else {
            SHOW_MINI_PLAYER = false;
            PATH_TO_FRAG = null;
            ARTIST_TO_FRAG = null;
            TITLE_TO_FRAG = null;
            ALBUM_TO_FRAG = null;
            LIST_TO_FRAG = null;
            POSITION_TO_FRAG = -1;
        }
    }

    private void showNotification(int playPauseBtn) {
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);

        Intent prevIntent = new Intent(getContext(), NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending = PendingIntent.getBroadcast(getContext(), 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(getContext(), NotificationReceiver.class)
                .setAction(ACTION_PLAY);
        PendingIntent pausePending = PendingIntent.getBroadcast(getContext(), 0, pauseIntent, 0);

        Intent nextIntent = new Intent(getContext(), NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPending = PendingIntent.getBroadcast(getContext(), 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap picture;
        byte[] art = getAlbumArtFromUri(LIST_TO_FRAG.get(POSITION_TO_FRAG).getPath());
        if (art != null) {
            picture = BitmapFactory.decodeByteArray(art, 0, art.length);
        } else {
            picture = BitmapFactory.decodeResource(getResources(), R.drawable.album_art );
        }
        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(picture)
                .setContentTitle(LIST_TO_FRAG.get(POSITION_TO_FRAG).getSongName())
                .setContentText(LIST_TO_FRAG.get(POSITION_TO_FRAG).getAuthor())
                .addAction(R.drawable.prev, "Previous", prevPending)
                .addAction(playPauseBtn, "Pause", pausePending)
                .addAction(R.drawable.next, "Next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}