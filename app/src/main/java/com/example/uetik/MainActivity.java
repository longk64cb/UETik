package com.example.uetik;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;

import com.example.uetik.models.Album;
import com.example.uetik.models.OnlineSong;
import com.example.uetik.models.OfflineSong;
import com.example.uetik.models.Topic;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.uetik.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<OfflineSong> offlineSongList = new ArrayList<>();
    public static ArrayList<Album> albumList = new ArrayList<>();
    public static List<Topic> topicList;
    public static List<OnlineSong> onlineSongList;
    public static MusicService musicService;
    public static OnlineMusicService onlineMusicService;

    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String SONG_TITLE = "SONG_TITLE";
    public static final String ARTIST_NAME = "ARTIST_NAME";
    public static final String ALBUM_ART = "ALBUM_ART";
    public static final String SONG_LIST = "SONG_LIST";
    public static final String POSITION = "POSITION";

    public static boolean SHOW_MINI_PLAYER = false;
    public static String PATH_TO_FRAG = null;
    public static String TITLE_TO_FRAG = null;
    public static String ARTIST_TO_FRAG = null;
    public static String ALBUM_TO_FRAG = null;
    public static ArrayList<OfflineSong> LIST_TO_FRAG = null;
    public static int POSITION_TO_FRAG = -1;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS}, 0);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        runtimePermission();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_albums)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    public void runtimePermission() {
        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        displaySongs();
                        getSongList();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            getAlbumList();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file) {
        ArrayList arrayList = new ArrayList();

        File [] files = file.listFiles();

        if(files != null) {
//            System.out.println(file.length());
            for (File singlefile : files) {
                if (singlefile.isDirectory() && !singlefile.isHidden()) {
                    arrayList.addAll(findSong(singlefile));
                } else {
                    if (singlefile.getName().endsWith(".wav")) {
                        arrayList.add(singlefile);
                    } else if (singlefile.getName().endsWith(".mp3")) {
                        arrayList.add(singlefile);
                    }
                }

            }
        } else {
            System.out.println("ko co j");
        }
        return arrayList;
    }

//    public void displaySongs() {
//        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());
//
//        if (mySongs.size() > 0)
//        {
//            items = new String[mySongs.size()];
//            for (int i = 0; i < mySongs.size(); i++) {
//                items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
////                System.out.println(items[i]);
//            }
//
////            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, items);
////            listView.setAdapter(myAdapter);
//
//            SongAdapter customAdapter = new SongAdapter(this);
//            listView.setAdapter(customAdapter);
//
//        }
//
//    }

    public Uri getArtUriFromMusicFile(Context context, File file) {
        final Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        final String[] cursor_cols = { MediaStore.Audio.Media.ALBUM_ID };

        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1 AND " + MediaStore.Audio.Media.DATA + " = '"
                + file.getAbsolutePath() + "'";
        final Cursor cursor = context.getApplicationContext().getContentResolver().query(uri, cursor_cols, where, null, null);
        /*
         * If the cursor count is greater than 0 then parse the data and get the art id.
         */
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
//            if (albumId != null)
//            Log.v("Test", String.format("%d", cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
//            else Log.v("Test", "Khong");
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            cursor.close();
            return albumArtUri;
        }
        return Uri.EMPTY;
    }

    public void getSongList() {
        ContentResolver trackResolver = getApplicationContext().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC;
        Cursor trackCursor = trackResolver.query(musicUri, null, selection, null, null);


        if(trackCursor!=null && trackCursor.moveToFirst()){
            //get columns
            int titleColumn = trackCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int artistColumn = trackCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int path = trackCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            int duration = trackCursor.getColumnIndex
                    (MediaStore.Audio.Media.DURATION);
            int id = trackCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int albumColumn = trackCursor.getColumnIndex
                    (MediaStore.Audio.Media.ALBUM);
            //add songs to list
            do {
                String thisTitle = trackCursor.getString(titleColumn);
                String thisArtist = trackCursor.getString(artistColumn);
                File thisFile = new File(trackCursor.getString(path));
                Uri thisArt = getArtUriFromMusicFile(getApplicationContext(), thisFile);
                String thisDuration = trackCursor.getString(duration);
                String thisId = trackCursor.getString(id);
                String thisAlbum = trackCursor.getString(albumColumn);
//                Log.v("Test", thisDuration);
                offlineSongList.add(new OfflineSong(thisTitle, thisArtist, thisArt.getPath(), thisFile.getPath(), thisDuration, thisId, thisAlbum));
            }
            while (trackCursor.moveToNext());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getAlbumList() {
        Hashtable<String, ArrayList<OfflineSong>> albums = new Hashtable<String, ArrayList<OfflineSong>>();
        for (OfflineSong offlineSong :
                offlineSongList) {
            String albumName = offlineSong.getAlbumName();
            if (!albums.containsKey(albumName)) {
                albums.put(albumName, new ArrayList<OfflineSong>());
            }
            albums.get(albumName).add(offlineSong);
        }
        albums.forEach((name, songs) -> {
//            Bitmap picture;
//            byte[] art = getAlbumArtFromUri(songs.get(0).getSongPath());
//            if (art != null) {
//                picture = BitmapFactory.decodeByteArray(art, 0, art.length);
//            } else {
//                picture = BitmapFactory.decodeResource(getResources(), R.drawable.album_art );
//            }
            Album album = new Album(name, songs.get(0).getSongPath(), songs);
            albumList.add(album);
        });
    }

    public static byte[] getAlbumArtFromUri(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
        String path = preferences.getString(MUSIC_FILE, null);
        String artist = preferences.getString(ARTIST_NAME, null);
        String songTitle = preferences.getString(SONG_TITLE, null);
        String albumArt = preferences.getString(ALBUM_ART, null);
        String songList = preferences.getString(SONG_LIST, null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<OfflineSong>>() {}.getType();
        int position = preferences.getInt(POSITION, -1);
        if (path != null) {
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAG = path;
            ARTIST_TO_FRAG = artist;
            TITLE_TO_FRAG = songTitle;
            ALBUM_TO_FRAG = albumArt;
            LIST_TO_FRAG = gson.fromJson(songList, type);
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
}