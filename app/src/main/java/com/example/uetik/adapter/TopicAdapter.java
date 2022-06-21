package com.example.uetik.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.uetik.R;
import com.example.uetik.models.OnlineSong;
import com.example.uetik.models.Topic;
import com.example.uetik.ui.online.OnlineFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicViewHolder> {
    private final OnlineFragment fragment;
    private List<Topic> topicList;
    private Context context;
    private View adapterView;

    public TopicAdapter(List<Topic> topicList, OnlineFragment fragment){
        this.topicList = topicList;
        this.fragment = fragment;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    @Override
    public TopicAdapter.TopicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_item, parent, false);

        return new TopicAdapter.TopicViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(TopicAdapter.TopicViewHolder holder, int position) {
        Topic t = topicList.get(position);
        Picasso.with(holder.ivTopicArt.getContext())
                .load(t.imgPath)
                .into(holder.ivTopicArt);
        holder.tvTopicName.setText(t.topicName);
        Log.d("img", "value: " + t.imgPath);
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTopicName;
        public ImageView ivTopicArt;
        public TopicViewHolder(View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.album_name);
            ivTopicArt =  itemView.findViewById(R.id.album_image);
        }
    }
//    public void updateList(List<OnlineSong> songs)
//    {
//        onlineSongList.addAll(songs);
//        notifyDataSetChanged();
//    }
}
