package com.example.uetik.ui.user;

import static com.example.uetik.MainActivity.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.uetik.R;
import com.example.uetik.models.User;
import com.example.uetik.ui.PlaylistDetail;
import com.example.uetik.ui.TopicDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private String url = "http://192.168.1.4:10010/login";
    EditText loginUsername, loginPassword;
    String username, password;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        loginUsername = (EditText) findViewById(R.id.login_username);
        loginPassword = (EditText) findViewById(R.id.login_password);

        btnLogin = findViewById(R.id.login_button);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v);
            }
        });
    }
    private void login(View view){
        username = loginUsername.getText().toString().trim();
        password = loginPassword.getText().toString().trim();
        Log.d("checkinfo", username + password);
        if(!password.equals("")&&!username.equals("")){
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    url + "?user-name=" + username+ "&password=" + password,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("checklogin", "val: "+  response);
                    if (response.charAt(0) == '[') { // doan nay la sao
                        user.userToken = response;
                        user.username = username;
                        user.password = password;
                        Log.d("CHECKUSERARR", username+password+response);
                        Intent intent = new Intent(view.getContext(), PlaylistDetail.class);
                        startActivity(intent);
                        finish();
                    } else if (response.equals("user is active or wrong password!")) {
                        Toast.makeText(getApplicationContext(), "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
        else{
            Toast.makeText(this, "Phải điền đầy đủ các trường", Toast.LENGTH_SHORT).show();
        }
    }
}