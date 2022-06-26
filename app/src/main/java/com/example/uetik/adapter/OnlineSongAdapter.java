package com.example.uetik.adapter;

import static com.example.uetik.MainActivity.getAlbumArtFromUri;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.uetik.MainActivity;
import com.example.uetik.R;
import com.example.uetik.models.OnlineSong;
import com.example.uetik.ui.online.OnlineFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OnlineSongAdapter extends RecyclerView.Adapter<OnlineSongAdapter.OnlineSongViewHolder>{
    private final OnlineFragment fragment;
    private List<OnlineSong> onlineSongList;
    private SongClickListener mSongClickListener;
    public static final String PORT = "http://192.168.1.4:10010";

    public OnlineSongAdapter(List<OnlineSong> onlineSongList, OnlineFragment fragment, SongClickListener songClickListener){
        this.onlineSongList = onlineSongList;
        this.fragment = fragment;
        this.mSongClickListener = songClickListener;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return onlineSongList.size();
    }

    @Override
    public OnlineSongAdapter.OnlineSongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.online_list_item, parent, false);

        return new OnlineSongAdapter.OnlineSongViewHolder(itemView, mSongClickListener);
    }

    public static class OnlineSongViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvSongName, tvSongAuthor;
        public ImageView ivSongArt, ivBtnMenu;
        SongClickListener songClickListener;
        public OnlineSongViewHolder(View itemView, SongClickListener songClickListener) {
            super(itemView);
            tvSongName = itemView.findViewById(R.id.txtSongName);
            tvSongAuthor =  itemView.findViewById(R.id.txtArtistName);
            ivSongArt = itemView.findViewById(R.id.imgSong);
            ivBtnMenu = itemView.findViewById(R.id.songMenu);
            tvSongName.setSelected(true);
            this.songClickListener = songClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            songClickListener.onSongClick(view, getAbsoluteAdapterPosition());
        }
    }
    @Override
    public void onBindViewHolder(OnlineSongAdapter.OnlineSongViewHolder holder, int position) {
        OnlineSong os = onlineSongList.get(position);
        Picasso.with(holder.ivSongArt.getContext())
                .load(PORT + os.imgPath)
                .into(holder.ivSongArt);
        holder.tvSongName.setText(os.songName);
        holder.tvSongAuthor.setText(os.author);
        holder.ivBtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(fragment.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.online_song_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()) {
                        case R.id.download:
                            Toast.makeText(fragment.getContext(), "Delete Clicked", Toast.LENGTH_SHORT).show();
                            ((MainActivity)fragment.getActivity()).downloadSong(PORT + os.path);
                            break;
                    }
                    return true;
                });
            }
        });
    }

    public void updateList(List<OnlineSong> songs)
    {
        onlineSongList.addAll(songs);
        notifyDataSetChanged();
    }

    public interface SongClickListener{
        void onSongClick(View view, int pos);
    }
}