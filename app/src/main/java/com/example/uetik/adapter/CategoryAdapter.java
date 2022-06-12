package com.example.uetik.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryAdapter {

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView txtNameCategory;
        private RecyclerView rcvAlbum;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
