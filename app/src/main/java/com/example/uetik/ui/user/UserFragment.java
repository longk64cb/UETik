package com.example.uetik.ui.user;

import static com.example.uetik.MainActivity.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.uetik.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.uetik.databinding.FragmentUserBinding;

import java.util.HashMap;
import java.util.Map;


public class UserFragment extends Fragment {
    private UserViewModel userViewModel;
    private FragmentUserBinding binding;
    Button registerBtn, loginBtn, logoutBtn, playlistBtn;
    String username, password;
    public LinearLayout loggedLayout, unloggedLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        userViewModel =
                new ViewModelProvider(this).get(UserViewModel.class);

        binding = FragmentUserBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LinearLayout loggedLayout = root.findViewById(R.id.logged_layout);
        LinearLayout unloggedLayout = root.findViewById(R.id.unlogged_layout);
        registerBtn = root.findViewById(R.id.user_register_button);
        loginBtn = root.findViewById(R.id.user_login_button);
        playlistBtn = root.findViewById(R.id.user_playlist_button);
        logoutBtn = root.findViewById(R.id.user_logout_button);
        if(user.userToken == null){
            loggedLayout.setVisibility(View.GONE);
            unloggedLayout.setVisibility(View.VISIBLE);
        }
        else{
            loggedLayout.setVisibility(View.VISIBLE);
            unloggedLayout.setVisibility(View.GONE);
        }
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), RegisterActivity.class);
                startActivity(i);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                if(user.userToken == null){
                    loggedLayout.setVisibility(View.GONE);
                    unloggedLayout.setVisibility(View.VISIBLE);
                }
                else{
                    loggedLayout.setVisibility(View.VISIBLE);
                    unloggedLayout.setVisibility(View.GONE);
                }
            }
        });

        playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PlaylistDetail.class);
                startActivity(i);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            private String url = "http://192.168.1.4:10010/logout";
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("checksignup", response);
                        if (response.equals("Logout successful!")) {
                            loggedLayout.setVisibility(View.GONE);
                            unloggedLayout.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getContext(), "Lỗi hệ thống, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Nullable
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("user-name", user.username);
                        data.put("token", user.userToken);
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(stringRequest);
                Log.d("check2", stringRequest.toString());
            }
        });

        return root;
    }
}