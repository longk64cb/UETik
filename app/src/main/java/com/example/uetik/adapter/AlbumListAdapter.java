package com.example.uetik.adapter;

import static com.example.uetik.MainActivity.getAlbumArtFromUri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uetik.R;
import com.example.uetik.models.Album;
import com.example.uetik.ui.albums.AlbumsFragment;

import java.util.ArrayList;

public class AlbumListAdapter extends BaseAdapter {

    private AlbumsFragment albumsFragment;
    private ArrayList<Album> albumList;

    public AlbumListAdapter(AlbumsFragment albumsFragment, ArrayList<Album> albumList) {
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
        byte[] byteArray = getAlbumArtFromUri(albumList.get(i).getAlbumArt());
        if (byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            albumArt.setImageBitmap(bmp);
        } else {
            albumArt.setImageResource(R.drawable.ic_baseline_music_note_24);
        }
//        albumArt.setImageBitmap(albumList.get(i).getAlbumArt());
        return myView;
    }
}
