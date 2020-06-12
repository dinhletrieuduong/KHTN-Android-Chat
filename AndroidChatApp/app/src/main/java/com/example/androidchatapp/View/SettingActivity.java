package com.example.androidchatapp.View;

import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.example.androidchatapp.utils.Util;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SettingActivity extends AppCompatActivity {
    TextView txtPhone_setting, txt_gender_setting, txtEmail_setting, txt_place_setting;
    TextView ageSetting, distanceSetting;
    SeekBar ageSeekBar, distanceSeekBar;

    public static final int PLACE_REQUEST_CODE_PLACE = 1;
    public static final int PLACE_REQUEST_CODE_EMAIL = 2;
    public static final int PLACE_REQUEST_CODE_PHONE = 3;
    public static final int PLACE_REQUEST_CODE_GENDER = 4;

    // Gui ve fragment SETTING
   /* public static final String EXTRA_DATA_FRAGMENT_PHONE = "EXTRA_DATA";
    public static final String EXTRA_DATA_FRAGMENT_EMAIL = "EXTRA_DATA";
    public static final String EXTRA_DATA_FRAGMENT_PLACE = "EXTRA_DATA";
    public static final String EXTRA_DATA_FRAGMENT_DISTANCE = "EXTRA_DATA";
    public static final String EXTRA_DATA_FRAGMENT_GENDER = "EXTRA_DATA";
    public static final String EXTRA_DATA_FRAGMENT_AGE = "EXTRA_DATA";*/

    private String userID, getProfileUrl, updateProfileUrl;
    String newPlace, newPhone, newEmail, newGender;
    int newAge, newDistance;

    Button btnConfirmSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        updateProfileUrl = getString(R.string.URL_SERVER, "api/user/updateProfile");
        getProfileUrl = getString(R.string.URL_SERVER, "api/user/profile/");

        userID = getIntent().getExtras().getString(MainActivity.USER_ID);
        btnConfirmSetting = (Button) this.findViewById(R.id.bnt_ConfirmSetting);
        btnConfirmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newPhone = txtPhone_setting.getText().toString();
                newEmail = txtEmail_setting.getText().toString();
                newPlace = txt_place_setting.getText().toString();
                newGender = txt_gender_setting.getText().toString();
                newAge = ageSeekBar.getProgress();
                newDistance = distanceSeekBar.getProgress();


                Intent intent = new Intent();
                intent.putExtra("PHONE", newPhone);
                intent.putExtra("EMAIL", newEmail);
                intent.putExtra("PLACE", newPlace);
                intent.putExtra("GENDER", newGender);
                intent.putExtra("AGE", newAge);
                intent.putExtra("DISTANCE", newDistance);
                setResult(Activity.RESULT_OK, intent);
                finish();
                Volley.newRequestQueue(SettingActivity.this).add(updateProfile());
            }
        });

        txtPhone_setting = (TextView) this.findViewById(R.id.txtPhoneSetting);
        txtPhone_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, PhoneActivity.class);
                intent.putExtra("PHONE", txtPhone_setting.getText().toString());
                startActivityForResult(intent, PLACE_REQUEST_CODE_PHONE);
            }
        });

        txtEmail_setting = (TextView) this.findViewById(R.id.txtEmailSetting);
        txtEmail_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, EmailActivity.class);
                intent.putExtra("EMAIL", txtEmail_setting.getText().toString());
                startActivityForResult(intent, PLACE_REQUEST_CODE_EMAIL);
            }
        });

        txt_place_setting = (TextView) this.findViewById(R.id.txtPlaceSetting);
        txt_place_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, PlaceActivity.class);
                intent.putExtra("PLACE", txt_place_setting.getText().toString());
                startActivityForResult(intent, PLACE_REQUEST_CODE_PLACE);
            }
        });

        txt_gender_setting = (TextView) this.findViewById(R.id.txtGenderSetting);
        txt_gender_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, GenderActivity.class);
                intent.putExtra("GENDER", txt_gender_setting.getText().toString());
                startActivityForResult(intent, PLACE_REQUEST_CODE_GENDER);
            }
        });

        ageSeekBar = (SeekBar) findViewById(R.id.ageSeekbarSetting);
        ageSetting = (TextView) findViewById(R.id.txtageSetting);
        ageSeekBar.setProgress(0);
        ageSeekBar.setMax(50);
        ageSeekBar.setProgress(18);

        // perform seek bar change listener event used for getting the progress value
        ageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ageSetting.setText("Age :" + progressChangedValue);
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                progressChangedValue = 18;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(SettingActivity.this, "Seek bar progress is :" + progressChangedValue,
                //Toast.LENGTH_SHORT).show();
                ageSetting.setText("Age :" + progressChangedValue);
                newAge = progressChangedValue;
            }
        });


        distanceSeekBar = (SeekBar) findViewById(R.id.distanceSeekBarSetting);
        distanceSetting = (TextView) findViewById(R.id.txtDistanceSetting);
        // perform seek bar change listener event used for getting the progress value
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValueDistance = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceSetting.setText("Distance :" + progressChangedValueDistance);
                progressChangedValueDistance = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                progressChangedValueDistance = 18;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(SettingActivity.this, "Seek bar progress is :" + progressChangedValue,
                //Toast.LENGTH_SHORT).show();
                distanceSetting.setText("Distance :" + progressChangedValueDistance);
                newDistance = progressChangedValueDistance;
            }
        });

        Volley.newRequestQueue(SettingActivity.this).add(getProfileRequest());

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            newPhone = txtPhone_setting.getText().toString();
            newEmail = txt_place_setting.getText().toString();
            newPlace = txtPhone_setting.getText().toString();
            newGender = txt_gender_setting.getText().toString();
            newAge = ageSeekBar.getProgress();
            newDistance = distanceSeekBar.getProgress();


            Intent intent = new Intent();
            intent.putExtra("PHONE", newPhone);
            intent.putExtra("EMAIL", newEmail);
            intent.putExtra("PLACE", newPlace);
            intent.putExtra("GENDER", newGender);
            intent.putExtra("AGE", newAge);
            intent.putExtra("DISTANCE", newDistance);
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_REQUEST_CODE_PLACE) {
            Intent intent = getIntent();
            newPlace = data.getStringExtra(PlaceActivity.EXTRA_DATA_PLACE);
            txt_place_setting.setText(newPlace);
        }

        if (requestCode == PLACE_REQUEST_CODE_EMAIL) {
            Intent intent = getIntent();
            newEmail = data.getStringExtra(EmailActivity.EXTRA_DATA_EMAIL);
            txtEmail_setting.setText(newEmail);
        }

        if (requestCode == PLACE_REQUEST_CODE_PHONE) {
            Intent intent = getIntent();
            newPhone = data.getStringExtra(PhoneActivity.EXTRA_DATA_PHONE);
            txtPhone_setting.setText(newPhone);
        }

        if (requestCode == PLACE_REQUEST_CODE_GENDER) {
            Intent intent = getIntent();
            newGender = data.getStringExtra(GenderActivity.EXTRA_DATA_GENDER);
            txt_gender_setting.setText(newGender);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(SettingActivity.this, "Hello", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private StringRequest updateProfile() {
        return new StringRequest(Request.Method.POST, updateProfileUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("userID", userID);
                postMap.put("phone", newPhone);
//                postMap.put("age", users);
                postMap.put("email", newEmail);
                postMap.put("place", newPlace);
                postMap.put("distance", newDistance + "");
                postMap.put("genderSelection", newGender);
                postMap.put("ageSelection", newAge + "");
                return postMap;
            }
        };
    }

    private StringRequest getProfileRequest() {
        return new StringRequest(Request.Method.GET, getProfileUrl + userID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": " + response);
                Gson gson = new Gson();
                UserModel userProfile = gson.fromJson(response, UserModel.class);
                txtPhone_setting.setText(userProfile.getPhone());
                txt_place_setting.setText(userProfile.getPlace());
                txtEmail_setting.setText(userProfile.getEmail());
                txt_gender_setting.setText(userProfile.getGenderSelection());
                ageSeekBar.setProgress(Integer.parseInt(userProfile.getAgeSelection()));
                ageSetting.setText(userProfile.getAgeSelection());
                distanceSeekBar.setProgress(Integer.parseInt(userProfile.getLocationDistance()));
                distanceSetting.setText(userProfile.getLocationDistance());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
            }
        });
    }
}

