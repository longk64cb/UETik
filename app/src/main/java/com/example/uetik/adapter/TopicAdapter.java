package com.example.uetik.adapter;

import static com.example.uetik.adapter.OnlineSongAdapter.PORT;

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
    private TopicClickListener mTopicClickListener;

    public TopicAdapter(List<Topic> topicList, OnlineFragment fragment, TopicClickListener topicClickListener){
        this.topicList = topicList;
        this.fragment = fragment;
        this.mTopicClickListener = topicClickListener;
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
                .inflate(R.layout.topic_item, parent, false);

        return new TopicAdapter.TopicViewHolder(itemView, mTopicClickListener);
    }
    @Override
    public void onBindViewHolder(TopicAdapter.TopicViewHolder holder, int position) {
        Topic t = topicList.get(position);
        Picasso.with(holder.ivTopicArt.getContext())
                .load(PORT + t.imgPath)
                .into(holder.ivTopicArt);
        holder.tvTopicName.setText(t.topicName);
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvTopicName;
        public ImageView ivTopicArt;
        TopicClickListener topicClickListener;
        public TopicViewHolder(View itemView, TopicClickListener topicClickListener) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.topic_name);
            ivTopicArt =  itemView.findViewById(R.id.topic_image);
            tvTopicName.setSelected(true);
            this.topicClickListener = topicClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            topicClickListener.onTopicClick(view, getAbsoluteAdapterPosition());
        }
    }
    public void updateList(List<Topic> topic)
    {
        topicList.addAll(topic);
        notifyDataSetChanged();
    }
    public interface TopicClickListener{
        void onTopicClick(View view, int pos);

    }
}
