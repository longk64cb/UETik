package com.example.uetik.ui.user;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.uetik.R;
import com.example.uetik.models.User;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private String url = "http://192.168.1.4:10010/signin";
    EditText registerEmail, registerUsername, registerPassword;
    String email, username, password;
    public User user;
    private Button btnRegister;
//    ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail = (EditText) findViewById(R.id.register_email);
        registerUsername = (EditText) findViewById(R.id.register_username);
        registerPassword = (EditText) findViewById(R.id.register_password);

//        loadingBar.setTitle("Đang tạo tài khoản");
//        loadingBar.setMessage("Kiểm tra thông tin cá nhân");
//        loadingBar.setCanceledOnTouchOutside(false);
// chet m t xoa nham
        btnRegister = findViewById(R.id.register_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(v);
            }
        });
    }

    private void register(View view) {
        email = registerEmail.getText().toString().trim();
        username = registerUsername.getText().toString().trim();
        password = registerPassword.getText().toString().trim();
        if(!email.equals("")&&!password.equals("")&&!username.equals("")){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("checksignup", response);
                    if (response.equals("Create user successful!")) {
                        Intent intent = new Intent(view.getContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (response.equals("Username or email already existed!")) {
                        Toast.makeText(getApplicationContext(), "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> data = new HashMap<>();
                    data.put("user-name", username);
                    data.put("password", password);
                    data.put("email", email);
                    return data;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
            Log.d("check2", stringRequest.toString());
        }
        else{
            Toast.makeText(this, "Phải điền đầy đủ các trường", Toast.LENGTH_SHORT).show();
        }
    }
}