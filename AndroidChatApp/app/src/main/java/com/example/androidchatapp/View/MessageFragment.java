package com.example.androidchatapp.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.Controller.ContactAdapter;
import com.example.androidchatapp.Controller.ContactAvatarAdapter;
import com.example.androidchatapp.Controller.MessageAdapter;
import com.example.androidchatapp.Controller.MessageAvatarAdapter;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.ChatBoxModel;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {


    private RecyclerView avatarList;
    private LinearLayoutManager avatarManager;
    private List<ChatBoxModel> list;
    private List<ChatBoxModel> listSearch = new ArrayList<>();
    private MessageAvatarAdapter avatarAdapter;
    private final int NORMAL_MODE = 0;
    private final int SEARCH_MODE = 1;
    private int currentMode  = NORMAL_MODE;
    private EditText edSearch;


    private static final String TAG = MessageFragment.class.getSimpleName();
    String browseChatUrl, browseAllChatUrl;

    String roomID, userName, userID;

    ListView listChatBox;
    MessageAdapter messageAdapter;
    ArrayList<ChatBoxModel> listMessage;
    public MessageFragment() { }
    public MessageFragment(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        avatarList =(RecyclerView) view.findViewById(R.id.rclNewMatchMessage);
        edSearch = (EditText) view.findViewById(R.id.edSearchMessage);
        avatarManager = new LinearLayoutManager(container.getContext());
        avatarManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        avatarManager.setSmoothScrollbarEnabled(true);
        avatarList.setLayoutManager(avatarManager);
        list = new ArrayList<>();
        avatarAdapter = new MessageAvatarAdapter(container.getContext(), R.layout.message_item_avatar_row, list);
        avatarList.setAdapter(avatarAdapter);

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchMatch(edSearch.getText().toString());
            }
        });



        listChatBox = (ListView) view.findViewById(R.id.listChatBox);
        browseAllChatUrl = getString(R.string.URL_SERVER, "api/chat/browseAllChat/");
        browseChatUrl = getString(R.string.URL_SERVER, "api/chat/browse/");
        listChatBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ChatBoxActivity.class);
                intent.putExtra(MainActivity.ROOM_ID, listMessage.get(position).getRoomID());
                intent.putExtra(MainActivity.USER_ID, userID);
                intent.putExtra(MainActivity.USER_NAME, userName);
                startActivity(intent);
            }
        });
        listMessage  = new ArrayList<>();

        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(getActivity()).add(browseAllChatRequest());
        return view;
    }

    private void searchMatch(String contact){
        if(contact.trim().length() > 0 )
        {
            listSearch.clear();
            for (int i = 0; i<list.size();i++){
                if(list.get(i).getChatBoxName().trim().toLowerCase().contains(contact.trim().toLowerCase())){
                    listSearch.add(list.get(i));
                }
            }
            currentMode = SEARCH_MODE;
            avatarAdapter = new MessageAvatarAdapter(getActivity(), R.layout.contact_item_row, listSearch);
            avatarList.setAdapter(avatarAdapter);
        }
        else
        {
            currentMode = NORMAL_MODE;
            avatarAdapter = new MessageAvatarAdapter(getActivity(), R.layout.contact_item_row, list);
            avatarList.setAdapter(avatarAdapter);
        }
    }

    private StringRequest browseAllChatRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, browseAllChatUrl + userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": "+ response);
                try {
                    JSONObject data = new JSONObject(response);
                    JSONArray jsonArray = data.getJSONArray("listChatBox");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Gson gson = new Gson();
                        ChatBoxModel chatBoxModel = gson.fromJson(jsonArray.get(i).toString(), ChatBoxModel.class);
                        listMessage.add(chatBoxModel);
                        list.add(chatBoxModel);
                    }
                    messageAdapter = new MessageAdapter(getActivity(), listMessage);
                    listChatBox.setAdapter(messageAdapter);

                    avatarAdapter = new MessageAvatarAdapter(getContext(),R.layout.message_item_avatar_row, list);
                    avatarList.setAdapter(avatarAdapter);

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
        return stringRequest;
    }
}
