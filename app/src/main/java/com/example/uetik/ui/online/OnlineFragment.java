package com.example.uetik.ui.online;

import static com.example.uetik.MainActivity.onlineSongList;
import static com.example.uetik.MainActivity.topicList;
import static com.example.uetik.adapter.OnlineSongAdapter.PORT;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.uetik.MainActivity;

import com.example.uetik.R;
import com.example.uetik.adapter.OnlineSongAdapter;
import com.example.uetik.adapter.TopicAdapter;
import com.example.uetik.databinding.FragmentOnlineBinding;
import com.example.uetik.models.OnlineSong;
import com.example.uetik.models.Topic;
import com.example.uetik.ui.OnlinePlayerActivity;
import com.example.uetik.ui.TopicDetail;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class OnlineFragment extends Fragment implements OnlineSongAdapter.SongClickListener, TopicAdapter.TopicClickListener, Serializable{
    private OnlineViewModel onlineViewModel;
    private FragmentOnlineBinding binding;

    private OnlineSongAdapter songAdapter;
    private TopicAdapter topicAdapter;
    private RecyclerView onlineSongListView;
    private RecyclerView genreListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        onlineViewModel = new ViewModelProvider(this).get(OnlineViewModel.class);
        binding = FragmentOnlineBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setHasOptionsMenu(true);
        Toolbar toolbar = root.findViewById(R.id.online_toolbar);
        toolbar.inflateMenu(R.menu.search);
        toolbar.setOnCreateContextMenuListener(this);

//        SearchView searchView = root.findViewById(R.id.search_option);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setOnQueryTextListener(this);

        onlineSongListView = root.findViewById(R.id.onlineListViewSong);
        genreListView = root.findViewById(R.id.genreList);

        OkHttpClient client = new OkHttpClient();
        Moshi moshi = new Moshi.Builder().build();

        Type onlineSongsType = Types.newParameterizedType(List.class, OnlineSong.class);
        final JsonAdapter<List<OnlineSong>> jsonSongAdapter = moshi.adapter(onlineSongsType);
        Request song_request = new Request.Builder()
                .url(PORT + "/songs")
                .build();

        client.newCall(song_request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", String.valueOf(e));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                onlineSongList = jsonSongAdapter.fromJson(json);
            }
        });

        Type onlineGenresType = Types.newParameterizedType(List.class, Topic.class);
        final JsonAdapter<List<Topic>> jsonTopicAdapter = moshi.adapter(onlineGenresType);
        Request genre_request = new Request.Builder()
                .url(PORT + "/topics")
                .build();

        client.newCall(genre_request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", String.valueOf(e));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                topicList = jsonTopicAdapter.fromJson(json);
            }
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        genreListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        topicAdapter = new TopicAdapter(topicList, this, this);
        genreListView.setAdapter(topicAdapter);

        onlineSongListView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        songAdapter = new OnlineSongAdapter(onlineSongList, this, this);
        onlineSongListView.setAdapter(songAdapter);


        return root;
    }

    @Override
    public void onTopicClick(View view, int pos){
        TextView topicNameTxt = view.findViewById(R.id.topic_name);
        String topicName = (String) topicNameTxt.getText();
        Bundle bundle = new Bundle();
        bundle.putSerializable("topicList", (Serializable) topicList);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(getActivity().getApplicationContext(), TopicDetail.class)
                .putExtra("topicName", topicName)
                .putExtra("topicList", bundle)
                .putExtra("pos", pos));
    }

    @Override
    public void onSongClick(View view, int pos) {
        TextView songNameTxt = (TextView) view.findViewById(R.id.txtSongName);
        TextView artistNameTxt = (TextView) view.findViewById(R.id.txtArtistName);
        String songName = (String) songNameTxt.getText();
        String artistName = (String) artistNameTxt.getText();
        Bundle bundle = new Bundle();
        bundle.putSerializable("onlineSongList", (Serializable) onlineSongList);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(getActivity().getApplicationContext(), OnlinePlayerActivity.class)
                .putExtra("songName", songName)
                .putExtra("onlineSongList", bundle)
                .putExtra("pos", pos));
    }



}



