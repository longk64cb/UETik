package com.example.uetik;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.uetik.models.Album;
import com.example.uetik.models.Song;
import com.example.uetik.ui.PlayerActivity;
import com.example.uetik.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.uetik.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Song> songList = new ArrayList<>();
    public static ArrayList<Album> albumList = new ArrayList<>();

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

        runtimePermission();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_albums)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
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
                songList.add(new Song(thisTitle, thisArtist, thisArt, thisFile.getPath(), thisDuration, thisId, thisAlbum));
            }
            while (trackCursor.moveToNext());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getAlbumList() {
        Hashtable<String, ArrayList<Song>> albums = new Hashtable<String, ArrayList<Song>>();
        for (Song song :
             songList) {
            String albumName = song.getAlbumName();
            if (!albums.containsKey(albumName)) {
                albums.put(albumName, new ArrayList<Song>());
            }
            albums.get(albumName).add(song);
        }
        albums.forEach((name, songs) -> {
            Album album = new Album(name, songs.get(0).getAlbumArt(), songs);
            albumList.add(album);
        });
    }

}