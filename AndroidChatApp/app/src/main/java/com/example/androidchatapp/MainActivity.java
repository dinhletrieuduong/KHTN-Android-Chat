package com.example.androidchatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.View.MessageFragment;
import com.example.androidchatapp.View.NearbyFragment;
import com.example.androidchatapp.View.ProfileFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public String userName, userID, base64String;
    public static final String USER_NAME = "userName", USER_ID = "userID", ROOM_ID = "roomID", AVATAR = "avatar";
    int fragmentOpening = 0;
    String lastLocationUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 11);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        }

        lastLocationUrl = getString(R.string.URL_SERVER, "api/user/lastLocation");
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.out.println("Longitude: " + location.getLongitude());
                            System.out.println("Latitude: " + location.getLatitude());
                            final String lastLocation = location.getLongitude() + "," + location.getLatitude();
                            Volley.newRequestQueue(MainActivity.this).add(getLastLocationRequest(lastLocation));
                        }
                    }
                });

        userName = (String) getIntent().getExtras().getString(MainActivity.USER_NAME);
        userID = (String) getIntent().getExtras().getString(MainActivity.USER_ID);
//        base64String = (String) getIntent().getExtras().getString(AVATAR);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.nav_message:
                        if (fragmentOpening == R.id.nav_message)
                            return true;
                        fragmentOpening = R.id.nav_message;
                        fragment = new MessageFragment(userID, userName);
                        loadFragment(fragment);
                        return true;
//                    case R.id.nav_contact:
//                        if (fragmentOpening == R.id.nav_contact)
//                            return true;
//                        fragmentOpening = R.id.nav_contact;
//                        fragment = new ContactFragment(userID, userName);
//                        loadFragment(fragment);
//                        return true;
                    case R.id.action_nearby:
                        if (fragmentOpening == R.id.action_nearby)
                            return true;
                        fragmentOpening = R.id.action_nearby;
                        fragment = new NearbyFragment(userID, userName);
                        loadFragment(fragment);
                        return true;
                    case R.id.nav_setting:
                        if (fragmentOpening == R.id.nav_setting)
                            return true;
                        fragmentOpening = R.id.nav_setting;
                        fragment = new ProfileFragment(userID);
                        loadFragment(fragment);
                        return true;
                }
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_message);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        return;
    }

    private StringRequest getLastLocationRequest(final String lastLocation) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, lastLocationUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": "+ response);
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
                postMap.put("userID", userID);
                postMap.put("lastLocation", lastLocation);
                return postMap;
            }
        };
        return stringRequest;
    }
}
