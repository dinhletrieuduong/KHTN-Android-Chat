package com.example.androidchatapp.Controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidchatapp.Model.ChatBoxModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;

import java.util.ArrayList;

public class MessageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChatBoxModel> listMessages;

    public MessageAdapter(Context context, ArrayList<ChatBoxModel> listMessages) {
        this.context = context;
        this.listMessages = listMessages;
    }

    @Override
    public int getCount() {
        return listMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return listMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.message_item_row, null);

        ImageView avatar = (ImageView) row.findViewById(R.id.icon);
        TextView username = (TextView) row.findViewById(R.id.messageUserName);
        TextView time = (TextView) row.findViewById(R.id.messageTimeSend);
        TextView message = (TextView) row.findViewById(R.id.messageContent);
//
        ChatBoxModel model = listMessages.get(position);
        avatar.setImageBitmap(Util.getBitmapFromString(model.getChatAvatar()));
        username.setText(model.getChatBoxName());
        time.setText(model.getModifiedTime());
        message.setText(model.getLastMessage());

        return row;
    }

}