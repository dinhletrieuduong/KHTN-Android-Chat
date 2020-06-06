package com.example.androidchatapp.Model;

import android.graphics.Bitmap;

public class NearbyModel {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String title;
    public int image;
    public NearbyModel(String title, int image){
        this.title = title;
        this.image = image;
    }
}
