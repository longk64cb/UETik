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
import com.example.uetik.models.Topic;
import com.example.uetik.ui.albums.AlbumsFragment;
import com.example.uetik.ui.online.OnlineFragment;

import java.util.ArrayList;
import java.util.List;

public class TopicListAdapter extends BaseAdapter {

    private OnlineFragment onlineFragment;
    private List<Topic> topicList;

    public TopicListAdapter(OnlineFragment onlineFragment, List<Topic> topicList) {
        this.onlineFragment = onlineFragment;
        this.topicList = topicList;
    }

    @Override
    public int getCount() {
        return topicList.size();
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
        View myView = onlineFragment.getLayoutInflater().inflate(R.layout.topic_item, null);
        TextView topicName = myView.findViewById(R.id.topic_name);
        ImageView topicArt = myView.findViewById(R.id.topic_image);
        topicName.setText(topicList.get(i).topicName);
        byte[] byteArray = getAlbumArtFromUri(topicList.get(i).imgPath);
        if (byteArray != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            topicArt.setImageBitmap(bmp);
        } else {
            topicArt.setImageResource(R.drawable.ic_baseline_music_note_24);
        }
//        albumArt.setImageBitmap(albumList.get(i).getAlbumArt());
        return myView;
    }
}