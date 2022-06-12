package com.example.uetik.adapter;

import static com.example.uetik.MainActivity.getAlbumArtFromUri;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uetik.R;
import com.example.uetik.models.Album;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Fragment fragment;
    private ArrayList<Album> albumList;

//    public AlbumAdapter(Fragment fragment, ArrayList<Album> albumList) {
//        this.fragment = fragment;
//        this.albumList = albumList;
//    }

    public void setData(ArrayList<Album> albumList) {
        this.albumList = albumList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        if (album == null) {
            return;
        }

//        holder.albumTitle.setText(album.getName());
        byte[] byteArray = getAlbumArtFromUri(album.getAlbumArt());
        if (byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            holder.albumArt.setImageBitmap(bmp);
        } else {
            holder.albumArt.setImageResource(R.drawable.ic_baseline_music_note_24);
        }
//        albumArt.setImageBitmap(albumList.get(i).getAlbumArt());
    }

    @Override
    public int getItemCount() {
        if (albumList != null) {
            return albumList.size();
        }
        return 0;
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {

        private ImageView albumArt;
        private TextView albumTitle;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            albumTitle = itemView.findViewById(R.id.album_name);
            albumArt = itemView.findViewById(R.id.album_image);
        }
    }
}
