package com.example.uetik;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.uetik.models.OnlineSong;
import com.example.uetik.models.Song;
import com.example.uetik.ui.home.HomeFragment;
import com.example.uetik.ui.online.OnlineFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class OnlineSongAdapter extends RecyclerView.Adapter<OnlineSongAdapter.OnlineItemViewHolder> {

    private final OnlineFragment fragment;
    private List<OnlineSong> onlineSongList;
//    private Context context;

    private View adapterView;

    public OnlineSongAdapter(List<OnlineSong> onlineSongList, OnlineFragment fragment) {
        this.onlineSongList = onlineSongList;
        this.fragment = fragment;
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public OnlineItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.online_list_item, parent, false);

        return new OnlineItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(OnlineItemViewHolder holder, int position) {
        OnlineSong os = onlineSongList.get(position);
        Picasso.with(holder.ivAlbumArt.getContext())
                .load(os.imgPath)
                .into(holder.ivAlbumArt);
        holder.tvSongName.setText(os.songName);
        holder.tvArtistName.setText(String.valueOf(os.author));
    }

    public static class OnlineItemViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSongName;
        public TextView tvArtistName;
        public ImageView ivAlbumArt;

        public OnlineItemViewHolder(View itemView) {
            super(itemView);
            tvSongName = (TextView) itemView.findViewById(R.id.txtSongName);
            tvArtistName = itemView.findViewById(R.id.txtArtistName);
            ivAlbumArt =  itemView.findViewById(R.id.imgSong);
        }
    }


    public void updateList(List<OnlineSong> songs)
    {
        onlineSongList.addAll(songs);
        notifyDataSetChanged();
    }
}
