package com.example.uetik.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uetik.R;
import com.example.uetik.models.Song;
import com.example.uetik.ui.home.HomeFragment;

import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    private final HomeFragment fragment;
    private ArrayList<Song> songList;
    private View adapterView;

    public SongAdapter(HomeFragment homeFragment, ArrayList<Song> songList) {
        this.fragment = homeFragment;
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
        View myView = fragment.getLayoutInflater().inflate(R.layout.list_item, null);
        TextView textSong = myView.findViewById(R.id.txtSongName);
        TextView textArtist = myView.findViewById(R.id.txtArtistName);
        ImageView albumArt = myView.findViewById(R.id.imgSong);
        ImageView btnMenu = myView.findViewById(R.id.songMenu);
        textSong.setText(songList.get(i).getTitle());
        textSong.setSelected(true);
        textArtist.setText(songList.get(i).getArtist());
        byte[] byteArray = getAlbumArtFromUri(songList.get(i).getSongPath());
        if (byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            albumArt.setImageBitmap(bmp);
        } else {
            albumArt.setImageResource(R.drawable.ic_baseline_music_note_24);
        }
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(fragment.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            Toast.makeText(fragment.getContext(), "Delete Clicked", Toast.LENGTH_SHORT).show();
                            fragment.deleteSong(i, v);
                            break;
                    }
                    return true;
                });
            }
        });
        return myView;
    }

    private byte[] getAlbumArtFromUri(String uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }

    public void updateList(ArrayList<Song> songs)
    {
        songList = new ArrayList<>();
        songList.addAll(songs);
        notifyDataSetChanged();
    }
}
