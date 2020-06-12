package com.example.androidchatapp.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileFragment extends Fragment {

    ImageView settings;
    private ImageView avatarImgView;

    private TextView userNameTxtView, ageTxtView, phoneTxtView;

    private LinearLayout logOutBtn;
    private FirebaseAuth mAuth;

    private String userID, getProfileUrl;
    private UserModel userProfile;

    public ProfileFragment() { }
    public ProfileFragment(String userID) { this.userID = userID; }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();

        getProfileUrl = getString(R.string.URL_SERVER, "api/user/profile/");

        Volley.newRequestQueue(getActivity()).add(getProfileRequest());

        logOutBtn = (LinearLayout) view.findViewById(R.id.logOutBtn);
        avatarImgView = (ImageView) view.findViewById(R.id.avatarImgView);
        userNameTxtView = (TextView) view.findViewById(R.id.userNameTxtView);
//        ageTxtView = (TextView) view.findViewById(R.id.ageTxtView);
        phoneTxtView = (TextView) view.findViewById(R.id.phoneTxtView);

        settings = view.findViewById(R.id.img_Setting);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra(MainActivity.USER_ID, userID);
                intent.putExtra("phone", userID);
                intent.putExtra("email", userID);
                intent.putExtra("place", userID);
                intent.putExtra("distance", userID);
                intent.putExtra("gender", userID);
                intent.putExtra("age", userID);
                startActivity(intent);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    mAuth.signOut();
                    LoginManager.getInstance().logOut();
                }
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return  view;
    }

    private StringRequest getProfileRequest() {
        return new StringRequest(Request.Method.GET, getProfileUrl + userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": "+ response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();
                    userProfile = gson.fromJson(response, UserModel.class);
                    userNameTxtView.setText(userProfile.getUserName());
                    phoneTxtView.setText(userProfile.getPhone());
                    if (jsonObject.getString("isLoginByFacebook").equals("true")) {
                        Picasso.get().load(userProfile.getAvatar()).into(avatarImgView);
                    }
                    else {
                        avatarImgView.setImageBitmap(Util.getBitmapFromString(userProfile.getAvatar()));
                    }
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
