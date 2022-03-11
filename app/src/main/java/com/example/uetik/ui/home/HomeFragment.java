package com.example.uetik.ui.home;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uetik.R;
import com.example.uetik.databinding.FragmentHomeBinding;
import com.example.uetik.models.Song;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    ListView listView;
    String[] items;
    ArrayList<Song> songList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = (ListView) root.findViewById(R.id.listViewSong);
        if (listView != null) {
            Log.v("ListView", "Found listView home");
        }

//        System.out.println(listView);

        runtimePermission();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void runtimePermission() {
        Dexter.withContext(getContext()).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        displaySongs();
                        getSongList();
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

    public void displaySongs() {
        final ArrayList<File> mySongs = findSong(Environment.getExternalStorageDirectory());

        if (mySongs.size() > 0)
        {
            items = new String[mySongs.size()];
            for (int i = 0; i < mySongs.size(); i++) {
                items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".wav", "");
//                System.out.println(items[i]);
            }

//            ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, items);
//            listView.setAdapter(myAdapter);

            customAdapter customAdapter = new customAdapter();
            listView.setAdapter(customAdapter);

        }

    }

    class customAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return songList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textSong = myView.findViewById(R.id.txtSongName);
            TextView textArtist = myView.findViewById(R.id.txtArtistName);
            ImageView albumArt = myView.findViewById(R.id.imgSong);
            textSong.setSelected(true);
            textArtist.setSelected(true);
            albumArt.setSelected(true);
            textSong.setText(songList.get(i).title);
            textArtist.setText(songList.get(i).artist);
            if (songList.get(i).albumArt != Uri.EMPTY) {
                albumArt.setImageURI(songList.get(i).albumArt);
                Log.v("Test", songList.get(i).albumArt.toString());
            } else {
                albumArt.setImageResource(R.drawable.ic_baseline_music_note_24);
                Log.v("Test", "bruh");
            }
//            Log.v("Test", songList.get(i).albumArt.toString());
            return myView;
        }
    }

    public static Uri getArtUriFromMusicFile(Context context, File file) {
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
//            Log.v("Test", albumId.toString());
//            else Log.v("Test", "Khong");
            Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
            Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
            cursor.close();
            return albumArtUri;
        }
        return Uri.EMPTY;
    }

    public void getSongList() {
        ContentResolver trackResolver = getContext().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC;
        Cursor trackCursor = trackResolver.query(musicUri, null, selection, null, null);


        if(trackCursor!=null && trackCursor.moveToFirst()){
            //get columns
            int titleColumn = trackCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int artistColumn = trackCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumArtColumn = trackCursor.getColumnIndex
                    (MediaStore.Audio.Media.DATA);
            //add songs to list
            do {
                String thisTitle = trackCursor.getString(titleColumn);
                String thisArtist = trackCursor.getString(artistColumn);
                File thisFile = new File(trackCursor.getString(albumArtColumn));
                Uri thisArt = getArtUriFromMusicFile(getContext(), thisFile);
                songList.add(new Song(thisTitle, thisArtist, thisArt));
            }
            while (trackCursor.moveToNext());
            customAdapter customAdapter = new customAdapter();
            listView.setAdapter(customAdapter);
        }

    }


}