package com.example.androidchatapp.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class FillInfoActivity extends AppCompatActivity {

    String updateProfileUrl;
    Button updateBtn;
    public static final String USER_ID = "userID", USER_NAME = "userName", AVATAR = "avatar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_info);

        updateProfileUrl = getString(R.string.URL_SERVER, "api/user/updateProfile");

        updateBtn = (Button) findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Volley.newRequestQueue(FillInfoActivity.this).add(updateProfileRequest());
            }
        });
    }

    private StringRequest updateProfileRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateProfileUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": "+ response);
                Gson gson = new Gson();
                UserModel user = gson.fromJson(response, UserModel.class);
                System.out.println(user.getUserName());

                Intent i = new Intent(FillInfoActivity.this, MainActivity.class);
                i.putExtra(USER_NAME, user.getUserName());
                i.putExtra(USER_ID, user.getUserID());
                i.putExtra(AVATAR, user.getAvatar());
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postMap = new HashMap<>();
//                postMap.put("phone", userName);
//                postMap.put("dob", password);
//                postMap.put("gender", password);
//                postMap.put("avatar", password);
                return postMap;
            }
        };
        return stringRequest;
    }
}
