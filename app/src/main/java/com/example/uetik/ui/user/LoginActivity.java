package com.example.uetik.ui.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.uetik.R;

public class LoginActivity extends AppCompatActivity {
    private String url = "http://192.168.1.4:10010/login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
    }
}