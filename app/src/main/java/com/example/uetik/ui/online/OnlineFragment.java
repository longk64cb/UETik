package com.example.uetik.ui.online;

import static com.example.uetik.MainActivity.albumList;
import static com.example.uetik.MainActivity.onlineSongList;
import static com.example.uetik.MainActivity.songList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.example.uetik.MainActivity;
import com.example.uetik.adapter.AlbumAdapter;
import com.example.uetik.OnlineSongAdapter;
import com.example.uetik.R;
import com.example.uetik.databinding.FragmentOnlineBinding;
import com.example.uetik.models.OnlineSong;
import com.example.uetik.ui.ExpandableHeightListView;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.lucasr.twowayview.TwoWayView;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class OnlineFragment extends Fragment {

    private OnlineViewModel onlineViewModel;
    private FragmentOnlineBinding binding;

    private OnlineSongAdapter songAdapter;
    private AlbumAdapter albumAdapter;

    private RecyclerView onlineSongListView;
    private RecyclerView genreListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        onlineViewModel = new ViewModelProvider(this).get(OnlineViewModel.class);
        binding = FragmentOnlineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        onlineSongListView = root.findViewById(R.id.onlineListViewSong);
        onlineSongListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        genreListView = root.findViewById(R.id.genreList);

        OkHttpClient client = new OkHttpClient();
        Moshi moshi = new Moshi.Builder().build();
        Type onlineSongsType = Types.newParameterizedType(List.class, OnlineSong.class);
        final JsonAdapter<List<OnlineSong>> jsonAdapter = moshi.adapter(onlineSongsType);

        Request request = new Request.Builder()
                .url("http://192.168.1.10:10050/songs")
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", String.valueOf(e));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String json = response.body().string();
                onlineSongList = jsonAdapter.fromJson(json);
                songAdapter = new OnlineSongAdapter(onlineSongList, OnlineFragment.this);
                onlineSongListView.setAdapter(songAdapter);
                Log.d("abc1", "value " + songAdapter);
            }
        });

        albumAdapter = new AlbumAdapter();
        albumAdapter.setData(albumList);
        genreListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        genreListView.setAdapter(albumAdapter);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("abc2", "value: " + albumAdapter);
        return root;
    }
}