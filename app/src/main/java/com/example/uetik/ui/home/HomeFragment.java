package com.example.uetik.ui.home;

import static com.example.uetik.MainActivity.songList;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uetik.R;
import com.example.uetik.SongAdapter;
import com.example.uetik.databinding.FragmentHomeBinding;
import com.example.uetik.models.Song;
import com.example.uetik.ui.PlayerActivity;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements Serializable{

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    ListView listView;
    String[] items;
    SongAdapter songAdapter;
//    public ArrayList<Song> songList = new ArrayList<>();

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

        songAdapter = new SongAdapter(this, songList);
        listView.setAdapter(songAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView songNameTxt = (TextView) view.findViewById(R.id.txtSongName);
                TextView artistNameTxt = (TextView) view.findViewById(R.id.txtArtistName);
                ImageView albumImgView = (ImageView) view.findViewById(R.id.imgSong);
                String songName = (String) songNameTxt.getText();
                String artistName = (String) artistNameTxt.getText();
                Bundle bundle = new Bundle();
                bundle.putSerializable("songList", (Serializable) songList);
                Drawable albumArt = (Drawable) albumImgView.getDrawable();
                Log.v("Test", songList.get(i).getAlbumArt().getPath());
                startActivity(new Intent(getActivity().getApplicationContext(), PlayerActivity.class)
                        .putExtra("songName", songName)
                        .putExtra("songList", bundle)
                        .putExtra("artistName", artistName)
                        .putExtra("albumArt", songList.get(i).getAlbumArt().toString())
                        .putExtra("pos", i));
            }
        });

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

    public void deleteSong(int position, View view) {
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(songList.get(position).getId()));
        File file = new File(songList.get(position).getSongPath());
        Log.v("Test", file.getAbsolutePath());
        if (file.exists()) {
            Log.v("Test", "Exists");
        } else {
            Log.v("Test", "Non Exist");
        }
        boolean deleted = delete(getContext(), file);
        if (deleted) {
            getContext().getContentResolver().delete(contentUri, null, null);
            songList.remove(position);
            Snackbar.make(view, "Song Deleted: ", Snackbar.LENGTH_LONG)
                    .show();
            Log.v("Test", "deleted");
        } else {
            Snackbar.make(view, "Can't be deleted: ", Snackbar.LENGTH_LONG).show();
            Log.v("Test", "can't delete");
        }
    }

    public boolean delete(final Context context, final File file) {
        final String where = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectionArgs = new String[] {
                file.getAbsolutePath()
        };
        final ContentResolver contentResolver = context.getContentResolver();
        final Uri filesUri = MediaStore.Files.getContentUri("external");

        contentResolver.delete(filesUri, where, selectionArgs);

        if (file.exists()) {

            contentResolver.delete(filesUri, where, selectionArgs);
        }
        return !file.exists();
    }

}