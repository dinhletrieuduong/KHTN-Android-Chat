package com.example.androidchatapp.Controller;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.Model.MessageModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;


public class ChatBoxAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private List<MessageModel> messageList;
    private final int TYPE_MESSAGE_SENT = 0;
    private final int TYPE_MESSAGE_RECEIVED = 1;
    private final int TYPE_IMAGE_SENT = 2;
    private final int TYPE_IMAGE_RECEIVED = 3;
    private LayoutInflater inflater;
    private Context context;
    private String removeMessageUrl = "", roomID, userID;
    private Socket socket;
    private static String POPUP_CONSTANT = "mPopup";
    private static String POPUP_FORCE_SHOW_ICON = "setForceShowIcon";

    // in this adaper constructor we add the list of messages as a parameter so that
    // we will passe  it when making an instance of the adapter object in our activity
    public ChatBoxAdapter(List<MessageModel> MessagesList) {
        this.messageList = MessagesList;
    }
    public ChatBoxAdapter(List<MessageModel> messageList, Context context, LayoutInflater inflater, String url, String roomID) {
        this.messageList = messageList;
        this.inflater = inflater;
        this.context = context;
        removeMessageUrl = url;
        this.roomID = roomID;
    }
    public ChatBoxAdapter(List<MessageModel> messageList, Context context, LayoutInflater inflater, String url, String roomID, String userID, Socket socket) {
        this.messageList = messageList;
        this.inflater = inflater;
        this.context = context;
        this.removeMessageUrl = url;
        this.roomID = roomID;
        this.userID = userID;
        this.socket = socket;
    }

    private static class SentMessageHolder extends RecyclerViewHolder{
        TextView messageTxt, seenTxt;
        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageTxt = itemView.findViewById(R.id.sentTxt);
            seenTxt = itemView.findViewById(R.id.seenTxt);
        }
    }
    private static class SentImageHolder extends RecyclerViewHolder {
        ImageView imageView;
        TextView seenTxt;

        public SentImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            seenTxt = itemView.findViewById(R.id.seenTxt);
        }
    }
    private static class ReceivedMessageHolder extends RecyclerViewHolder {
        TextView messageTxt, nameTxt;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            messageTxt = itemView.findViewById(R.id.receivedTxt);
            nameTxt = itemView.findViewById(R.id.nameTxt);
        }
    }
    private static class ReceivedImageHolder extends RecyclerViewHolder {
        TextView nameTxt;
        ImageView imageView;

        public ReceivedImageHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_MESSAGE_SENT:
                view = inflater.inflate(R.layout.item_sent_message, parent, false);
                return new SentMessageHolder(view);
            case TYPE_MESSAGE_RECEIVED:
                view = inflater.inflate(R.layout.item_received_message, parent, false);
                return new ReceivedMessageHolder(view);
            case TYPE_IMAGE_RECEIVED:
                view = inflater.inflate(R.layout.item_received_image, parent, false);
                return new ReceivedImageHolder(view);

            case TYPE_IMAGE_SENT:
                view = inflater.inflate(R.layout.item_sent_image, parent, false);
                return new SentImageHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {
        final MessageModel message = messageList.get(position);
        if (message.isSent()) {
            if (!message.isImage()) {
                SentMessageHolder messageHolder = (SentMessageHolder) holder;
                messageHolder.messageTxt.setText(message.getMessage());
                if (message.isSeen() && position == (messageList.size() - 1)) {
                    messageHolder.seenTxt.setVisibility(TextView.VISIBLE);
                    messageHolder.seenTxt.setText("Seen");
                }
                else {
                    messageHolder.seenTxt.setHeight(0);
                }
            }
            else {
                SentImageHolder imageHolder = (SentImageHolder) holder;
                Bitmap bitmap = Util.getBitmapFromString(message.getMessage());
                imageHolder.imageView.setImageBitmap(bitmap);
                if (message.isSeen() && position == (messageList.size() - 1)) {
                    imageHolder.seenTxt.setVisibility(TextView.VISIBLE);
                    imageHolder.seenTxt.setText("Seen");
                }
                else {
                    imageHolder.seenTxt.setHeight(0);
                }
            }
        } else {
            if (!message.isImage()) {
                ReceivedMessageHolder messageHolder = (ReceivedMessageHolder) holder;
                messageHolder.nameTxt.setText(message.getUserName());
                messageHolder.messageTxt.setText(message.getMessage());
            }
            else {
                ReceivedImageHolder imageHolder = (ReceivedImageHolder) holder;
                Bitmap bitmap = Util.getBitmapFromString(message.getMessage());
                imageHolder.nameTxt.setText(message.getUserName());
                imageHolder.imageView.setImageBitmap(bitmap);
            }
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, final int position, boolean isLongClick) {
                if (isLongClick) {
//                    view.get
                    showPopup(context, view, position);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageList.get(position);
        if (message.isSent()) {
            if (!message.isImage()) {
                return TYPE_MESSAGE_SENT;
            }
            return TYPE_IMAGE_SENT;
        } else {
            if (!message.isImage())
                return TYPE_MESSAGE_RECEIVED;
            return TYPE_IMAGE_RECEIVED;
        }
//        return -1;
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private void showPopup(final Context context, View view, final int position) {
        PopupMenu popup = new PopupMenu(context, view);
        if (messageList.get(position).getUserID().equals(userID))
            popup.setGravity(Gravity.END);
        else
            popup.setGravity(Gravity.START);

        try {
            // Reflection apis to enforce show icon
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals(POPUP_CONSTANT)) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(POPUP_FORCE_SHOW_ICON, boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.getMenuInflater().inflate(R.menu.popup_menu_message, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pmnuDelete:
                        final String messageID = messageList.get(position).get_ID();
                        socket.emit("message_delete", roomID, messageID);
                        Volley.newRequestQueue(context).add(removeMessageRequest(messageID));
                        return true;
                    case R.id.pmnuShare:
                        Toast.makeText(context, "Coming soon...", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
        popup.show();
    }

    private StringRequest removeMessageRequest(final String messageID) {
        return new StringRequest(Request.Method.POST, removeMessageUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": " + response);
//                Toast.makeText(context, "Removed!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
//                Toast.makeText(context, "Cann't Remove!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("roomID", roomID);
                postMap.put("messageID", messageID);
                return postMap;
            }
        };
    }
}