package com.example.uetik;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uetik.models.Album;
import com.example.uetik.models.Song;
import com.example.uetik.ui.albums.AlbumsFragment;

import java.util.ArrayList;

public class AlbumAdapter extends BaseAdapter {

    private AlbumsFragment albumsFragment;
    private ArrayList<Album> albumList;

    public AlbumAdapter(AlbumsFragment albumsFragment, ArrayList<Album> albumList) {
        this.albumsFragment = albumsFragment;
        this.albumList = albumList;
    }

    @Override
    public int getCount() {
        return albumList.size();
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
        View myView = albumsFragment.getLayoutInflater().inflate(R.layout.album_item, null);
        TextView albumName = myView.findViewById(R.id.album_name);
        ImageView albumArt = myView.findViewById(R.id.album_image);
        albumName.setText(albumList.get(i).getName());
        if (albumList.get(i).getAlbumArt() != Uri.EMPTY) {
            albumArt.setImageURI(albumList.get(i).getAlbumArt());
        } else {
            albumArt.setImageResource(R.drawable.albumart);
            Log.v("Test", "bruh");
        }
        return myView;
    }
}
