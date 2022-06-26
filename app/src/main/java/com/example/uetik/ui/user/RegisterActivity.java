package com.example.uetik.ui.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Intent;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.uetik.R;

public class RegisterActivity extends AppCompatActivity {
    private String url = "http://192.168.1.4:10010/signin";
    EditText registerEmail, registerUsername, registerPassword;
    String email, username, password;
    Button registerButton;
    ProgressDialog loadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail = (EditText) findViewById(R.id.register_email);
        registerUsername = (EditText) findViewById(R.id.register_username);
        registerPassword = (EditText) findViewById(R.id.register_password);

        loadingBar.setTitle("Đang tạo tài khoản");
        loadingBar.setMessage("Kiểm tra thông tin cá nhân");
        loadingBar.setCanceledOnTouchOutside(false);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = registerEmail.getText().toString();
                username = registerUsername.getText().toString();
                password = registerPassword.getText().toString();

//                register(email, username, password);
            }
        });
    }
    Intent intent = new Intent(this, LoginActivity.class);
    private void register(String email, String username, String password) {
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Vui lòng điền tên đăng nhập", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Vui lòng điền email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.show();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if(response.equals("Create user successful!")){
                        Intent intent = new Intent(this, LoginActivity.class);
                    }
                }
            });


        }
    }
}