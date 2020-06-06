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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.Controller.ContactAdapter;
import com.example.androidchatapp.Controller.ContactAvatarAdapter;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactFragment extends Fragment {
    private ListView listContact;
    private ArrayList<UserModel> arrContact = new ArrayList<>();
    private ArrayList<UserModel> searchArrContact = new ArrayList<>();
    private ContactAdapter adapter;
    private EditText edSearch;
    private final int NORMAL_MODE = 0;
    private final int SEARCH_MODE = 1;
    private int currentMode  = NORMAL_MODE;


    private RecyclerView avatarList;
    private LinearLayoutManager avatarManager;
    private List<UserModel> list;
    private ContactAvatarAdapter avatarAdapter;

//    private static final String TAG = ContactFragment.class.getSimpleName();
    private String friendsUrl, newChatUrl, browseChatUrl;
    private String userID, roomID, userName;

    public ContactFragment() {}
    public ContactFragment(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        friendsUrl = getResources().getString(R.string.URL_SERVER,  "api/user/friends/");
        newChatUrl = getResources().getString(R.string.URL_SERVER,  "api/chat/new/");
        browseChatUrl = getResources().getString(R.string.URL_SERVER,  "api/chat/browse/");

        avatarList = view.findViewById(R.id.avatarList);
        avatarManager = new LinearLayoutManager(container.getContext());
        avatarManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        avatarManager.setSmoothScrollbarEnabled(true);
        avatarList.setLayoutManager(avatarManager);
        list = new ArrayList<>();
        avatarAdapter = new ContactAvatarAdapter(container.getContext(), R.layout.contact_item_avatar_row, list);
        avatarList.setAdapter(avatarAdapter);

        edSearch = (EditText) view.findViewById(R.id.edSearch);
        listContact = (ListView) view.findViewById(R.id.list_contact);
        adapter = new ContactAdapter(getActivity(), R.layout.contact_item_row, arrContact);
        listContact.setAdapter(adapter);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                searchContact(edSearch.getText().toString());
            }
        });

        listContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final String users = userID + "," + arrContact.get(position).getUserID();
                StringRequest browseChatRequest = new StringRequest(Request.Method.GET, browseChatUrl + users, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals("{}")) {
                            Log.e("Volley Result", ": " + response);
                            try {
                                JSONObject data = new JSONObject(response);
                                roomID = data.getString("roomID");
                                Intent intent = new Intent(getActivity(), ChatBoxActivity.class);
                                intent.putExtra(MainActivity.ROOM_ID, roomID);
                                intent.putExtra(MainActivity.USER_ID, userID);
                                intent.putExtra(MainActivity.USER_NAME, userName);
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            StringRequest newChatRequest = new StringRequest(Request.Method.POST, newChatUrl, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.e("Volley Result", ": "+ response);
                                    try {
                                        JSONObject data = new JSONObject(response);
                                        roomID = data.getString("roomID");
                                        Intent intent = new Intent(getActivity(), ChatBoxActivity.class);
                                        intent.putExtra(MainActivity.ROOM_ID, roomID);
                                        intent.putExtra(MainActivity.USER_ID, userID);
                                        intent.putExtra(MainActivity.USER_NAME, userName);
                                        startActivity(intent);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                                }
                            })
                            {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> postMap = new HashMap<>();
                                    postMap.put("users", users);
                                    return postMap;
                                }
                            };
                            //make the request to your server as indicated in your request url
                            Volley.newRequestQueue(getActivity()).add(newChatRequest);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                    }
                });
                Volley.newRequestQueue(getActivity()).add(browseChatRequest);
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.GET, friendsUrl + userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": "+ response);
                try {
                    JSONObject data = new JSONObject(response);
                    JSONArray jsonArray = data.getJSONArray("friends");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Gson gson = new Gson();
                        UserModel user = gson.fromJson(jsonArray.get(i).toString(), UserModel.class);
                        arrContact.add(user);
                        list.add(user);
                    }
                    ContactAdapter adapter = new ContactAdapter(getActivity(), R.layout.contact_item_row, arrContact);
                    listContact.setAdapter(adapter);

                    avatarAdapter = new ContactAvatarAdapter(container.getContext(),R.layout.contact_item_avatar_row, list);
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
        //make the request to your server as indicated in your request url
        Volley.newRequestQueue(getActivity()).add(stringRequest);

        // Inflate the layout for this fragment
        return view;
    }

    // ham search
    private void searchContact(String contact){
        if(contact.trim().length() > 0 )
        {
            searchArrContact.clear();
            for (int i = 0; i<arrContact.size();i++){
                if(arrContact.get(i).getUserName().trim().toLowerCase().contains(contact.trim().toLowerCase())){
                    searchArrContact.add(arrContact.get(i));
                }
            }
            currentMode = SEARCH_MODE;
            adapter = new ContactAdapter(getActivity(), R.layout.contact_item_row, searchArrContact);
            listContact.setAdapter(adapter);
        }
        else
        {
            currentMode = NORMAL_MODE;
            adapter = new ContactAdapter(getActivity(), R.layout.contact_item_row, arrContact);
            listContact.setAdapter(adapter);
        }
    }

}
