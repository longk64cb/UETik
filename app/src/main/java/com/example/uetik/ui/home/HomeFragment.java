package com.example.uetik.ui.home;

import static com.example.uetik.MainActivity.songList;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uetik.R;
import com.example.uetik.adapter.SongAdapter;
import com.example.uetik.databinding.FragmentHomeBinding;
import com.example.uetik.models.Song;
import com.example.uetik.ui.ExpandableHeightListView;
import com.example.uetik.ui.PlayerActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements Serializable, SearchView.OnQueryTextListener {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    ExpandableHeightListView listView;
    String[] items;
    SongAdapter songAdapter;
//    public ArrayList<Song> songList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);
        Toolbar toolbar = root.findViewById(R.id.home_toolbar);
        toolbar.inflateMenu(R.menu.search);
        toolbar.setOnCreateContextMenuListener(this);

        SearchView searchView = root.findViewById(R.id.search_option);
//        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

//        MenuItem menuItem = menu.findItem(R.id.search_option);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setOnQueryTextListener(this);

        listView = (ExpandableHeightListView) root.findViewById(R.id.listViewSong);

        if (listView != null) {
            Log.v("ListView", "Found listView home");
        }

        songAdapter = new SongAdapter(this, songList);
        listView.setAdapter(songAdapter);
        listView.setScrollContainer(false);
        listView.setExpanded(true);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView songNameTxt = (TextView) view.findViewById(R.id.txtSongName);
                    TextView artistNameTxt = (TextView) view.findViewById(R.id.txtArtistName);
                    String songName = (String) songNameTxt.getText();
                    String artistName = (String) artistNameTxt.getText();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("songList", (Serializable) songList);
                    startActivity(new Intent(getActivity().getApplicationContext(), PlayerActivity.class)
                            .putExtra("songName", songName)
                            .putExtra("songList", bundle)
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<Song> searchFiles = new ArrayList<>();
        for (Song song : songList)
        {
            if(song.getTitle().toLowerCase().contains(userInput))
            {
                searchFiles.add(song);
            }
        }
        songAdapter.updateList(searchFiles);
        return true;
    }
}