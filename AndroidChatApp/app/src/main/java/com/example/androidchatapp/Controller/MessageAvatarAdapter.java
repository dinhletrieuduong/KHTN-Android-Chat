package com.example.androidchatapp.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.androidchatapp.Model.AvatarModel;
import com.example.androidchatapp.Model.ChatBoxModel;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAvatarAdapter extends RecyclerView.Adapter<MessageAvatarAdapter.ViewHolder> {

    private List<ChatBoxModel> list;
    private Context context;

    public MessageAvatarAdapter(Context context, int contact_item_row, List<ChatBoxModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MessageAvatarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_item_avatar_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAvatarAdapter.ViewHolder viewHolder, int i) {
        ChatBoxModel model = list.get(i);
        viewHolder.imageView.setImageBitmap(Util.getBitmapFromString(model.getChatAvatar()));
        viewHolder.name.setText(model.getChatBoxName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView name;
        CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = view.findViewById(R.id.txtMessageName);


            imageView = view.findViewById(R.id.message_avatar);

        }
    }
}
