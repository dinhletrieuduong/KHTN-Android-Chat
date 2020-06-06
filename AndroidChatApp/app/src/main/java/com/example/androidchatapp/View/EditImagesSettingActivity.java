package com.example.androidchatapp.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidchatapp.R;

import java.util.ArrayList;

public class EditImagesSettingActivity extends AppCompatActivity {
    private Button mBack;
    private ImageView mImage1, mImage2, mImage3;
    private String userId, phone, image1Url, image2Url, image3Url, userSex;
    private ArrayList<ImageView> imageViewsList = new ArrayList<ImageView>();
    final int MaxTags = 5;
    final int MaxImages = 3;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_images_settings);

        mImage1 = findViewById(R.id.image1);
        mImage2 = findViewById(R.id.image2);
        mImage3 = findViewById(R.id.image3);
        mBack = findViewById(R.id.back);


        mImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        mImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);
            }
        });
        mImage3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 3);
            }
        });




    }
}