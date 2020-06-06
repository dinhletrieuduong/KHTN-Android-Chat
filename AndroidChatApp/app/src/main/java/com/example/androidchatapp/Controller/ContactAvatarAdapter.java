package com.example.androidchatapp.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.androidchatapp.Model.AvatarModel;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAvatarAdapter extends RecyclerView.Adapter<ContactAvatarAdapter.ViewHolder> {

    private List<UserModel> list;
    private Context context;

    public ContactAvatarAdapter(Context context, int contact_item_row, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ContactAvatarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item_avatar_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAvatarAdapter.ViewHolder viewHolder, int i) {
        UserModel model = list.get(i);
        viewHolder.imageView.setImageBitmap(Util.getBitmapFromString(model.getAvatar()));
        viewHolder.name.setText(model.getUserName());

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
            name = view.findViewById(R.id.txtName);


            imageView = view.findViewById(R.id.contact_avatar);

        }
    }
}
