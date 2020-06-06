package com.example.androidchatapp.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FillInfoActivity extends AppCompatActivity {
    EditText edAge, edPhone;
    boolean isImageDefault = true;
    RadioGroup rdgGender;
    RadioButton checkGender;
    private Bitmap bmpAttach;
    private ImageView imgAttach;
    String base64ImageString;
    final int REQUEST_PHOTO = 1;

    String updateProfileUrl, userID;
    Button updateBtn;
    public static final String USER_ID = "userID", USER_NAME = "userName", AVATAR = "avatar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_fill_info);

        updateProfileUrl = getString(R.string.URL_SERVER, "api/user/updateProfile");
        userID = getIntent().getExtras().getString(MainActivity.USER_ID);

        updateBtn = (Button) findViewById(R.id.updateBtn);
        rdgGender = (RadioGroup) findViewById(R.id.rdgGender);
        edAge = (EditText) findViewById(R.id.edAge);
        edPhone = (EditText) findViewById(R.id.edPhone);
        imgAttach = (ImageView) findViewById(R.id.img_attach);

        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id){
                    case R.id.img_attach:
                        pickImage();
                        break;
                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String error = "";
                String phone = edPhone.getText().toString();
                String age = edAge.getText().toString();
                checkGender = (RadioButton) findViewById(rdgGender.getCheckedRadioButtonId());
                String gender = checkGender.getText().toString();
                if(phone.trim().length() < 10 || phone.trim().length() > 11){
                    error += "Phone number not valid";
                    Toast.makeText(FillInfoActivity.this, error, Toast.LENGTH_SHORT).show();
                }
                else if(age.trim().length() == 0 || age.trim().length() > 2) {
                    error += "Age not valid";
                    Toast.makeText(FillInfoActivity.this, error, Toast.LENGTH_SHORT).show();
                }
                else {
                    if (isImageDefault) {
                        BitmapDrawable drawable = (BitmapDrawable) imgAttach.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        base64ImageString = Util.convertBitmapToString(bitmap);
                    }
                    Volley.newRequestQueue(FillInfoActivity.this).add(updateProfileRequest(gender));
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "There is no selected photo", Toast.LENGTH_LONG).show();
                return;
            }
            isImageDefault = false;
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                bmpAttach = BitmapFactory.decodeStream(bufferedInputStream);

                imgAttach.setImageBitmap(bmpAttach);
                imgAttach.setVisibility(View.VISIBLE);
                base64ImageString = Util.convertBitmapToString(bmpAttach);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private StringRequest updateProfileRequest(final String gender) {
        return new StringRequest(Request.Method.POST, updateProfileUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": "+ response);
                Gson gson = new Gson();
                UserModel user = gson.fromJson(response, UserModel.class);
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
                postMap.put("userID", userID);
                postMap.put("phone", edPhone.getText().toString());
                postMap.put("age", edAge.getText().toString());
                postMap.put("gender", gender);
                postMap.put("avatar", base64ImageString);
                return postMap;
            }
        };
    }
    private void pickImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PHOTO);
    }
}