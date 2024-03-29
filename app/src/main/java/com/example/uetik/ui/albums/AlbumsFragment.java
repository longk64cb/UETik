package com.example.uetik.ui.albums;

import static com.example.uetik.MainActivity.albumList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uetik.adapter.AlbumAdapter;
import com.example.uetik.R;
import com.example.uetik.adapter.AlbumListAdapter;
import com.example.uetik.databinding.FragmentAlbumsBinding;

import java.io.Serializable;

public class AlbumsFragment extends Fragment {

    private AlbumsViewModel albumsViewModel;
    private FragmentAlbumsBinding binding;

    AlbumListAdapter albumAdapter;
    GridView gridView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        albumsViewModel =
                new ViewModelProvider(this).get(AlbumsViewModel.class);

        binding = FragmentAlbumsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        gridView = root.findViewById(R.id.gridViewAlbums);
        albumAdapter = new AlbumListAdapter(this, albumList);
        gridView.setAdapter(albumAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("albumList", (Serializable) albumList);
                startActivity(new Intent(getActivity().getApplicationContext(), AlbumDetail.class)
                    .putExtra("albumList", bundle)
                    .putExtra("pos", i));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}