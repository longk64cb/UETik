package com.example.uetik.ui;

import static com.example.uetik.MainActivity.onlineSongList;
import static com.example.uetik.MainActivity.topicList;
import static com.example.uetik.MainActivity.getAlbumArtFromUri;
import static com.example.uetik.MainActivity.user;
import static com.example.uetik.adapter.OnlineSongAdapter.PORT;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uetik.R;
import com.example.uetik.adapter.OnlineSongAdapter;
import com.example.uetik.models.OnlineSong;
import com.example.uetik.models.Topic;
import com.example.uetik.models.User;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PlaylistDetail extends AppCompatActivity {
    List<OnlineSong> playlistSongList;
    ImageView topicArt;
    ListView listView;
    Topic topic;
    int position;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        topicArt = findViewById(R.id.topic_art_detail);
        listView = findViewById(R.id.listViewAlbumSong);
//        Intent intent = getIntent();
//        Bundle bundle = intent.getBundleExtra("topicList");
//        position = intent.getIntExtra("pos", 0);
        Log.d("checkuserplaylistdetail", user.username);
        OkHttpClient client = new OkHttpClient();
        Moshi moshi = new Moshi.Builder().build();
        Type onlineSongsType = Types.newParameterizedType(List.class, OnlineSong.class);
        final JsonAdapter<List<OnlineSong>> jsonSongAdapter = moshi.adapter(onlineSongsType);
        Request song_request = new Request.Builder()
                .url(PORT + "/song-playlist")
                .addHeader("user-name", user.username)
                .addHeader("token", user.userToken)
                .build();

        client.newCall(song_request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", String.valueOf(e));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                playlistSongList = jsonSongAdapter.fromJson(json);
                Log.d("CHECKPLAYLISTSONGS", json);
            }
        });
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OnlineSongAdapter playlistSongAdapter = new OnlineSongAdapter(playlistSongList);
        listView.setAdapter(playlistSongAdapter);
        Log.d("check", "val:" + playlistSongList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView songNameTxt = (TextView) view.findViewById(R.id.txtSongName);
                TextView artistNameTxt = (TextView) view.findViewById(R.id.txtArtistName);
                ImageView albumImgView = (ImageView) view.findViewById(R.id.imgSong);
                String songName = (String) songNameTxt.getText();
                String artistName = (String) artistNameTxt.getText();
                Bundle bundle = new Bundle();
                bundle.putSerializable("onlineSongList", (Serializable) playlistSongList);
                Log.d("check2", "val: " + playlistSongList);
                Drawable albumArt = (Drawable) albumImgView.getDrawable();
                startActivity(new Intent(getApplicationContext(), OnlinePlayerActivity.class)
                        .putExtra("songName", songName)
                        .putExtra("onlineSongList", bundle)
                        .putExtra("artistName", artistName)
                        .putExtra("pos", i));
            }
        });
    }

    class OnlineSongAdapter extends BaseAdapter {

        private final List<OnlineSong> onlineSongList;

        public OnlineSongAdapter(List<OnlineSong> onlineSongList) {
            this.onlineSongList = onlineSongList;
        }

        @Override
        public int getCount() {
            return onlineSongList.size();
//            return 0;
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
        public View getView(int position, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.online_list_item, null);
            TextView tvSongName = myView.findViewById(R.id.txtSongName);
            TextView tvSongAuthor = myView.findViewById(R.id.txtArtistName);
            ImageView ivSongArt = myView.findViewById(R.id.imgSong);
            ImageView btnMenu = myView.findViewById(R.id.songMenu);
            OnlineSong os = onlineSongList.get(position);
            tvSongName.setText(os.songName);
            tvSongAuthor.setText(os.author);
            tvSongName.setSelected(true);

            if (os.imgPath != null) {
                Picasso.with(ivSongArt.getContext()).load(PORT + os.imgPath).into(ivSongArt);
            } else {
                ivSongArt.setImageResource(R.drawable.ic_baseline_music_note_24);
            }
            return myView;
        }

//        private byte[] getAlbumArtFromUri(String uri) {
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(uri);
//            byte[] art = retriever.getEmbeddedPicture();
//            return art;
//        }
    }

}