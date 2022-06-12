package com.example.uetik.ui.online;

import static com.example.uetik.MainActivity.albumList;
import static com.example.uetik.MainActivity.songList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.uetik.adapter.AlbumAdapter;
import com.example.uetik.OnlineSongAdapter;
import com.example.uetik.R;
import com.example.uetik.databinding.FragmentOnlineBinding;
import com.example.uetik.ui.ExpandableHeightListView;

import org.lucasr.twowayview.TwoWayView;

public class OnlineFragment extends Fragment {

    private OnlineViewModel onlineViewModel;
    private FragmentOnlineBinding binding;

    private OnlineSongAdapter songAdapter;
    private AlbumAdapter albumAdapter;

    private ExpandableHeightListView onlineSongListView;
    private RecyclerView genreListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        onlineViewModel = new ViewModelProvider(this).get(OnlineViewModel.class);
        binding = FragmentOnlineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        onlineSongListView = root.findViewById(R.id.onlineListViewSong);
        genreListView = root.findViewById(R.id.genreList);

        songAdapter = new OnlineSongAdapter(this, songList);
        onlineSongListView.setAdapter(songAdapter);
        onlineSongListView.setExpanded(true);

        albumAdapter = new AlbumAdapter();
        albumAdapter.setData(albumList);
        genreListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        genreListView.setAdapter(albumAdapter);

        return root;
    }
}