package com.example.uetik.ui.albums;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlbumsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlbumsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is albums fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}