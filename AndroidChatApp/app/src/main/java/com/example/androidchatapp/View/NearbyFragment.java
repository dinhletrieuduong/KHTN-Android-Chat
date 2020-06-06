package com.example.androidchatapp.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.Controller.ContactAdapter;
import com.example.androidchatapp.Controller.ContactAvatarAdapter;
import com.example.androidchatapp.Controller.NearbyAdapter;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.NearbyModel;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.google.gson.Gson;
import com.huxq17.swipecardsview.SwipeCardsView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearbyFragment extends Fragment {

    private SwipeCardsView swipecardsview;
    private List<NearbyModel> modelList = new ArrayList<>();
    private List<UserModel> usersList = new ArrayList<>();
    private NearbyAdapter nearbyadapter;
    private ImageView matchBtn, cancelBtn;
//    private static final String ARG_PARAM1 = "param1";

    private ArrayList<String> al;
    private ArrayAdapter<String> arrayAdapter;
    private int i;

    ProgressDialog progressDialog;

    private TextView nameTxtView, ageDistanceTxtView;
    private String getNearbyUrl, newChatUrl;
    private String userID, roomID, userName, currentUserMatchID;


    public NearbyFragment() { }
    public NearbyFragment(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }

    // TODO: Rename and change types and number of parameters
    public static NearbyFragment newInstance(String param1, String param2) {
        NearbyFragment fragment = new NearbyFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nearby, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        getNearbyUrl = getString(R.string.URL_SERVER, "api/user/nearby/");
        newChatUrl = getResources().getString(R.string.URL_SERVER,  "api/chat/new/");

        swipecardsview = (SwipeCardsView) view.findViewById(R.id.swipeCardsView);
        swipecardsview.retainLastCard(false);
        swipecardsview.enableSwipe(true);

        matchBtn = (ImageView) view.findViewById(R.id.matchBtn);
        cancelBtn = (ImageView) view.findViewById(R.id.cancel_btn);
        nameTxtView = (TextView) view.findViewById(R.id.nameTxtView);
        ageDistanceTxtView = (TextView) view.findViewById(R.id.ageDistanceTxtView);

        Volley.newRequestQueue(getActivity()).add(getNearbyRequest());

        swipecardsview.setCardsSlideListener(new SwipeCardsView.CardsSlideListener() {
            @Override
            public void onShow(int index) {
                // LogUtils.i("test showing index = "+index);
                if (index <= nearbyadapter.getCount() - 1) {
                    currentUserMatchID = nearbyadapter.getItem(index).getUserID();
                    nameTxtView.setText(nearbyadapter.getItem(index).getUserName());
                    String ageDistance = nearbyadapter.getItem(index).getAge() + ", " + nearbyadapter.getItem(index).getLocationDistance();
                    ageDistanceTxtView.setText(ageDistance);
                }
            }

            @Override
            public void onCardVanish(int index, SwipeCardsView.SlideType type) {
                String orientation = "";
                switch (type){
                    case LEFT:
//                        Toast.makeText(getContext(),"Trai", Toast.LENGTH_SHORT).show();
                        break;
                    case RIGHT:
//                        Toast.makeText(getContext(),"Phai", Toast.LENGTH_SHORT).show();
                        break;
                    case NONE:
//                        Toast.makeText(getContext(),"NoneNoneNoneNone", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onItemClick(View cardImageView, int index) {
//                Toast.makeText(getContext(),"Hello", Toast.LENGTH_SHORT).show();
            }
        });

        matchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Volley.newRequestQueue(getActivity()).add(newChatRequest(currentUserMatchID));
                System.out.println(currentUserMatchID);
            }
        });

        return  view;
    }

    public void doLeftOut() {
        swipecardsview.slideCardOut(SwipeCardsView.SlideType.LEFT);
    }

    public void doRightOut() {
        swipecardsview.slideCardOut(SwipeCardsView.SlideType.RIGHT);
    }

    public void doTopOut() {
        swipecardsview.slideCardOut(SwipeCardsView.SlideType.NONE);
    }


    private StringRequest getNearbyRequest() {
        progressDialog.show();
        return new StringRequest(Request.Method.GET, getNearbyUrl + userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.hide();
                Log.e("Volley Result", ": "+ response);
                try {
                    JSONObject data = new JSONObject(response);
                    JSONArray jsonArray = data.getJSONArray("users");
                    for (int i1 = 0; i1 < jsonArray.length(); i1++) {
                        Gson gson = new Gson();
                        UserModel user = gson.fromJson(jsonArray.get(i1).toString(), UserModel.class);
                        usersList.add(user);
                    }
                    nearbyadapter = new NearbyAdapter(usersList, getActivity());
                    swipecardsview.setAdapter(nearbyadapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
            }
        });
    }


    private StringRequest newChatRequest(String currentUserMatchID) {
        final String users = userID + "," + currentUserMatchID;
        return new StringRequest(Request.Method.POST, newChatUrl, new Response.Listener<String>() {
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
    }
}
