package com.example.uetik.ui.albums;

import static com.example.uetik.MainActivity.albumList;
import static com.example.uetik.MainActivity.songList;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uetik.R;
import com.example.uetik.SongAdapter;
import com.example.uetik.models.Album;
import com.example.uetik.models.Song;
import com.example.uetik.ui.PlayerActivity;
import com.example.uetik.ui.home.HomeFragment;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.Serializable;
import java.util.ArrayList;

public class AlbumDetail extends AppCompatActivity {

    ImageView albumArt;
    ListView listView;
    Album album;
    int position;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_album_detail);
        albumArt = findViewById(R.id.album_art_detail);
        listView = findViewById(R.id.listViewAlbumSong);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("albumList");
        position = intent.getIntExtra("pos", 0);
        album = albumList.get(position);
        albumArt.setImageURI(album.getAlbumArt());
        CollapsingToolbarLayout layout = findViewById(R.id.collapsing_toolbar_album_name);
        layout.setTitle(album.getName());

        SongAdapter songAdapter = new SongAdapter(album.getSongs());
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
                bundle.putSerializable("songList", (Serializable) album.getSongs());
                Drawable albumArt = (Drawable) albumImgView.getDrawable();
                Log.v("Test", album.getSongs().get(i).getAlbumArt().getPath());
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class)
                        .putExtra("songName", songName)
                        .putExtra("songList", bundle)
                        .putExtra("artistName", artistName)
                        .putExtra("albumArt", album.getSongs().get(i).getAlbumArt().toString())
                        .putExtra("pos", i));
            }
        });
    }

     class SongAdapter extends BaseAdapter {

        private final ArrayList<Song> songList;

        public SongAdapter(ArrayList<Song> songList) {
            this.songList = songList;
        }

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
            ImageView btnMenu = myView.findViewById(R.id.songMenu);
            textSong.setText(songList.get(i).getTitle());
            textArtist.setText(songList.get(i).getArtist());
            if (songList.get(i).getAlbumArt() != Uri.EMPTY) {
                albumArt.setImageURI(songList.get(i).getAlbumArt());
            } else {
                albumArt.setImageResource(R.drawable.ic_baseline_music_note_24);
                Log.v("Test", "bruh");
            }
            return myView;
        }
    }

}
