package com.example.androidchatapp.Controller;

import android.content.Context;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidchatapp.Model.NearbyModel;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;
import com.huxq17.swipecardsview.BaseCardAdapter;
import com.huxq17.swipecardsview.BuildConfig;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Constructor;
import java.util.List;

public class NearbyAdapter extends BaseCardAdapter {
//    private List<NearbyModel> modelList;
    private List<UserModel> modelList;
    private Context context;

//    public NearbyAdapter(List<NearbyModel> modelList, Context context){
//        this.modelList = modelList;
//        this.context = context;
//    }
    public NearbyAdapter(List<UserModel> modelList, Context context){
        this.modelList = modelList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public int getCardLayoutId() {
        return R.layout.item_card_nearby;
    }

    @Override
    public void onBindData(int position, View cardview) {
        if(modelList == null || modelList.size() == 0){
            return;
        }

        ImageView image = (ImageView)cardview.findViewById(R.id.imageView);
//        TextView textView = (TextView)cardview.findViewById(R.id.textView);
        UserModel model = modelList.get(position);
//        textView.setText(model.getUserName());
//        Picasso.get().load().into(image);
        if (!model.getAvatar().isEmpty()) {
            image.setImageBitmap(Util.getBitmapFromString(model.getAvatar()));
//            image.setBackgroundResource(R.drawable.layout3);
        }
    }

    public UserModel getItem(int index) {
        return modelList.get(index);
    }
}
