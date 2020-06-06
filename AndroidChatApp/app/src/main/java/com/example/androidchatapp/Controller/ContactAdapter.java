package com.example.androidchatapp.Controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;


import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;

import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {
    Context context;
    private ArrayList<UserModel> arrContact;

    public ContactAdapter( Context context, int contact_item_row, ArrayList<UserModel> arr) {
        this.context = context;
        this.arrContact = arr;
    }

    @Override
    public int getCount() {
        return arrContact.size();
    }

    @Override
    public Object getItem(int position) {
        return arrContact.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.contact_item_row, null);

        TextView name = (TextView) row.findViewById(R.id.contactUserName);
        ImageView icon = (ImageView) row.findViewById(R.id.icon);

        UserModel model = arrContact.get(position);
        icon.setImageBitmap(Util.getBitmapFromString(model.getAvatar()));
        name.setText(model.getUserName());
        return row;
    }
}
