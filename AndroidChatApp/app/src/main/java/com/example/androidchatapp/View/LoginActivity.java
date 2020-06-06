package com.example.androidchatapp.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidchatapp.MainActivity;
import com.example.androidchatapp.Model.UserModel;
import com.example.androidchatapp.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button loginBtn;
    ImageView loginGoogleBtn;
    private LoginButton loginFaceBookBtn;
    private TextView signupBtn;
    private EditText userNameTxt, passwordTxt;
    public static final String USER_ID = "userID", USER_NAME = "userName", AVATAR = "avatar";

    String loginUrl, loginByFacebookUrl;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        loginUrl = getResources().getString(R.string.URL_SERVER, "api/user/login/");
        loginByFacebookUrl = getResources().getString(R.string.URL_SERVER, "api/user/loginByFacebook/");

        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.loginBtn);
//        loginGoogleBtn = (ImageView) findViewById(R.id.loginGoogle);
        loginFaceBookBtn = (LoginButton) findViewById(R.id.loginFacebook);
        loginFaceBookBtn.setReadPermissions("email", "public_profile");

        // Callback registration
        loginFaceBookBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                System.out.println("facebook:onCancel:");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("facebook:onError:");
            }
        });

        signupBtn = (TextView) findViewById(R.id.registerBtn);
        userNameTxt = (EditText) findViewById(R.id.edDangNhap);
        passwordTxt = (EditText) findViewById(R.id.edMatKhau);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
                finish();
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = userNameTxt.getText().toString();
                final String password = passwordTxt.getText().toString();
                if(!username.isEmpty() && !password.isEmpty()){
                    progressDialog.show();
                    StringRequest stringRequest = loginRequest(username, password);
                    Volley.newRequestQueue(LoginActivity.this).add(stringRequest);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            StringRequest stringRequest = loginByFacebookRequest(currentUser.getEmail(), currentUser.getPhotoUrl().toString());
            Volley.newRequestQueue(LoginActivity.this).add(stringRequest);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
//                            Picasso.get().load(user.getPhotoUrl()).into(loginGoogleBtn);
                            StringRequest stringRequest = loginByFacebookRequest(user.getEmail(), user.getPhotoUrl().toString());
                            Volley.newRequestQueue(LoginActivity.this).add(stringRequest);
                        } else {
                            // If sign in fails, display a message to the user.
                            System.out.println("signInWithCredential:failure" + task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();
    }

    private StringRequest loginByFacebookRequest(final String userName, final String photoUrl) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginByFacebookUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                        the response contains the result from the server, a json string or any other object returned by your server
                Log.e("Volley Result", ": "+ response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Gson g = new Gson();
                    UserModel user = g.fromJson(response, UserModel.class);
                    if (jsonObject.getString("firstLogin").equals("true")) {
                        Intent i = new Intent(LoginActivity.this, FillInfoActivity.class);
                        i.putExtra(USER_ID, user.getUserID());
                        startActivity(i);
                        finish();
                    }
                    else {
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.putExtra(USER_NAME, user.getUserName());
                        i.putExtra(USER_ID, user.getUserID());
//                        i.putExtra(AVATAR, user.getAvatar());
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                progressDialog.hide();
                if (error.getMessage() != null) {
                    if (error.getMessage().contains("Failed to connect") || error.getMessage().contains("Errors"))
                        Toast.makeText(LoginActivity.this, "Connect to server failed.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(LoginActivity.this, "Username or password is invalid.", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(LoginActivity.this, "Connect to server failed.", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("userName", userName);
//                postMap.put("avatar", photoUrl);
                return postMap;
            }
        };
        return stringRequest;
    }

    private StringRequest loginRequest(final String userName, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result", ": "+ response);
                progressDialog.hide();
                Gson g = new Gson();
                UserModel user = g.fromJson(response, UserModel.class);
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                i.putExtra(USER_NAME, user.getUserName());
                i.putExtra(USER_ID, user.getUserID());
                i.putExtra(AVATAR, user.getAvatar());
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                progressDialog.hide();
                if (error.getMessage() != null) {
                    if (error.getMessage().contains("Failed to connect") || error.getMessage().contains("Errors"))
                        Toast.makeText(LoginActivity.this, "Connect to server failed.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(LoginActivity.this, "Username or password is invalid.", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(LoginActivity.this, "Connect to server failed.", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("userName", userName);
                postMap.put("password", password);
                return postMap;
            }
        };
        return stringRequest;
    }

}
