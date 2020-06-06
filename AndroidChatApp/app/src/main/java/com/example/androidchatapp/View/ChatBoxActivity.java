package com.example.androidchatapp.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
//import androidx.emoji.text.EmojiCompat;
//import androidx.emoji.text.FontRequestEmojiCompatConfig;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.Controller.ChatBoxAdapter;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.MessageModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import br.com.instachat.emojilibrary.model.layout.EmojiCompatActivity;
import br.com.instachat.emojilibrary.model.layout.EmojiEditText;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class ChatBoxActivity extends EmojiCompatActivity {
    private Socket socket;
    private String userName, userID, roomID;

    public RecyclerView myRecylerView;
    public List<MessageModel> messageList;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messageEditTxt;
    public Button sendBtn;
    public View pickImgBtn;
    public TextView seenTxt;

    private ImageView img_attack;

    private Toolbar mToolbar;

    private int IMAGE_REQUEST_ID = 1;
    private boolean IS_SENT = true, IS_IMAGE = true;
    String message, socketUrl, get20MessagesUrl, removeMessageUrl;

    enum EVENTS_SOCKET {
        join_room,
        user_joined_room,
        user_disconnect,
        message_detection,
        message,
        message_deleted,
    };

    private int numberClients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_chat_box);

//        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        socketUrl = getString(R.string.URL_SERVER, "");
        get20MessagesUrl = getString(R.string.URL_SERVER, "api/chat/get20LastMessages/");
        removeMessageUrl = getString(R.string.URL_SERVER, "api/chat/removeMessage/");

        messageEditTxt = (EditText) findViewById(R.id.message);
        sendBtn = (Button) findViewById(R.id.send);
        pickImgBtn = (ImageView) findViewById(R.id.pickImgBtn);
        seenTxt = (TextView) findViewById(R.id.seenTxt);
        img_attack = (ImageView) findViewById(R.id.img_attach);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        userName = (String) getIntent().getExtras().getString(MainActivity.USER_NAME);
        roomID = (String) getIntent().getExtras().getString(MainActivity.ROOM_ID);
        userID = (String) getIntent().getExtras().getString(MainActivity.USER_ID);

        img_attack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatBoxActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        //connect you socket client to the server
        try {
            socket = IO.socket(socketUrl);
            socket.connect();
            // emit the event join along side with the roomID, userID
            socket.emit(EVENTS_SOCKET.join_room.toString(), roomID, userID);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this); //new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());

        messageList = new ArrayList<>();
        chatBoxAdapter = new ChatBoxAdapter(messageList, ChatBoxActivity.this, getLayoutInflater(), removeMessageUrl, roomID, userID, socket);
        // notify the adapter to update the recycler view
        chatBoxAdapter.notifyDataSetChanged();
        //set the adapter for the recycler view
        myRecylerView.setAdapter(chatBoxAdapter);

//        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//                messageEditTxt.clearFocus();
//                getWindow().getDecorView().clearFocus();
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(messageEditTxt.getWindowToken(), 0);
//                imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
//                Intent intent = new Intent(ChatBoxActivity.this, MainActivity.class);
//                intent.putExtra(MainActivity.USER_ID, userID);
//                intent.putExtra(MainActivity.USER_NAME, userName);
////                intent.putExtra(MainActivity.AVATAR, avatar);
//                startActivity(intent);
//                finish();
//            }
//        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = messageEditTxt.getText().toString();
                if(!message.isEmpty()){
                    socket.emit(EVENTS_SOCKET.message_detection.toString(), roomID, userID, userName, message, IS_SENT, !IS_IMAGE);
                    messageEditTxt.setText("");
                }
            }
        });
        messageEditTxt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sendBtn.performClick();
                    handled = true;
                }
                return handled;
            }
        });

        pickImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Pick image"), IMAGE_REQUEST_ID);
            }
        });

        socket.on(EVENTS_SOCKET.user_joined_room.toString(), new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            numberClients = data.getInt("numberClients");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Volley.newRequestQueue(ChatBoxActivity.this).add(getMessageRequest());
                    }
                });
            }
        });
        socket.on(EVENTS_SOCKET.user_disconnect.toString(), new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(ChatBoxActivity.this, "User offline", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        socket.on(EVENTS_SOCKET.message.toString(), new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        Gson gson = new Gson();
                        MessageModel message = gson.fromJson(data.toString(), MessageModel.class);
                        message.setSent(userID.equals(message.getUserID()));
                        messageList.add(message);
                        chatBoxAdapter = new ChatBoxAdapter(messageList, ChatBoxActivity.this, getLayoutInflater(), removeMessageUrl, roomID, userID, socket);
                        chatBoxAdapter.notifyDataSetChanged();
                        myRecylerView.setAdapter(chatBoxAdapter);
                        myRecylerView.smoothScrollToPosition(chatBoxAdapter.getItemCount() - 1);
                    }
                });
            }
        });

        socket.on(EVENTS_SOCKET.message_deleted.toString(), new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        String messageID;
                        try {
                            messageID = data.getString("messageID");
                            for (int i = 0; i < messageList.size() - 1; i++) {
                                if (messageList.get(i).get_ID().equals(messageID))
                                    messageList.remove(i);
                            }
                            chatBoxAdapter = new ChatBoxAdapter(messageList, ChatBoxActivity.this, getLayoutInflater(), removeMessageUrl, roomID, userID, socket);
                            chatBoxAdapter.notifyDataSetChanged();
                            myRecylerView.setAdapter(chatBoxAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_ID && resultCode == RESULT_OK) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                sendImage(image);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        String base64String = Util.convertBitmapToString(image);
        socket.emit(EVENTS_SOCKET.message_detection.toString(), roomID, userID, userName, base64String, IS_SENT, IS_IMAGE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }

    private StringRequest getMessageRequest() {
        return new StringRequest(Request.Method.GET, get20MessagesUrl + roomID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": "+ response);
                try {
                    JSONObject data = new JSONObject(response);
                    JSONArray jsonArray = data.getJSONArray("messages");
                    if (messageList.size() == 0)
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Gson gson = new Gson();
                            MessageModel message1 = gson.fromJson(jsonArray.get(i).toString(), MessageModel.class);
                            message1.setSent(userID.equals(message1.getUserID()));
                            messageList.add(message1);
                        }
                    if (numberClients == 2)
                        messageList.get(messageList.size() - 1).setSeen(true);
                    chatBoxAdapter = new ChatBoxAdapter(messageList, ChatBoxActivity.this, getLayoutInflater(), removeMessageUrl, roomID, userID, socket);
                    chatBoxAdapter.notifyDataSetChanged();
                    myRecylerView.setAdapter(chatBoxAdapter);
                    myRecylerView.scrollToPosition(chatBoxAdapter.getItemCount() - 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
            }
        });
    }
}
