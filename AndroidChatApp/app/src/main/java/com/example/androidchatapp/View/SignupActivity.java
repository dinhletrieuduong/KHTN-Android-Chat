package com.example.androidchatapp.View;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnSignUp;
    TextView txt_backLogin;
    EditText edUsername, edPassword, edDisplayName, edDateOfBirth, edPhone;
    boolean isImageDefault = true;
    RadioGroup rdgGender;
    RadioButton checkGender;

    private Bitmap bmpAttach;
    private ImageView imgAttach;
    String base64ImageString;
    final int REQUEST_PHOTO = 1;

    public static final String USER_ID = "userID";
    public static final String USERNAME = "userName";
    public static final String AVATAR = "avatar";

    String requestUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        requestUrl = getResources().getString(R.string.URL_SERVER,  "api/user/signup/");
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_signup);

        btnSignUp = (Button) findViewById(R.id.btnCreate);
        edUsername = (EditText) findViewById(R.id.edUsername);
        edPassword = (EditText) findViewById(R.id.edPassword);
        edDisplayName = (EditText) findViewById(R.id.edDisplayName);
        edDateOfBirth = (EditText) findViewById(R.id.edDateOfBirth);
        edPhone = (EditText) findViewById(R.id.edPhone);
        txt_backLogin = (TextView) findViewById((R.id.txt_backLogin));

        //ibtnPhoto = (ImageButton) findViewById(R.id.ibtnPhoto);
        rdgGender = (RadioGroup) findViewById(R.id.rdgGender);
//        rdMale =(RadioButton) this.findViewById(R.id.rdMale);
//        rdFemale = (RadioButton) this.findViewById(R.id.rdFemaler);
        imgAttach = (ImageView) findViewById(R.id.img_attach);

        btnSignUp.setOnClickListener(this);
        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id){
                    case R.id.img_attach:
                        pickImage();
                        break;
                }
            }
        });
        // chon ngay sinh
        edDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseDate();
            }
        });

        // Tro ve login
        txt_backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    //chup anh
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_PHOTO);
        }
    }
    // chon anh
    private void pickImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PHOTO);
    }

    // chon ngay sinh
    private void chooseDate(){
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(calendar.DATE);
        int month = calendar.get(calendar.MONTH);
        int year = calendar.get(calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int i1, int i2, int i3) {
                calendar.set(i1, i2, i3);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                edDateOfBirth.setText(simpleDateFormat.format(calendar.getTime()));
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    // thong bao loi
    @Override
    public void onClick(View v) {
        String username = edUsername.getText().toString();
        String password = edPassword.getText().toString();
        String displayName = edDisplayName.getText().toString();
        String phone = edPhone.getText().toString();
        String dateofbirth = edDateOfBirth.getText().toString();
        String error = "";

        int gender = this.rdgGender.getCheckedRadioButtonId();
        checkGender = (RadioButton) this.findViewById(gender);
        String gen = checkGender.getText().toString();

        if(username.trim().length() < 6){
            error += "Username not valid";
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
        }
        else if(password.trim().length() < 6) {
            error += "Password not valid";
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
        else if(displayName.trim().length() < 6) {
            error += "Display Name not valid";
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
        else if(!gen.equals("Male") && !gen.equals("Female")) {
            error += " Gender not valid";
            Toast.makeText(this, error,Toast.LENGTH_SHORT).show();
        }
        else if(phone.trim().length() < 10 || phone.trim().length() > 11){
            error += "Phone number not valid";
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
        }
        else if(dateofbirth.trim().length() < 6){
            error += "DateOfBirth not valid";
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show();
        }
        else {
            if (isImageDefault) {
                BitmapDrawable drawable = (BitmapDrawable) imgAttach.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                base64ImageString = Util.convertBitmapToString(bitmap);
            }
            StringRequest stringRequest = signUpRequest();
            Volley.newRequestQueue(SignupActivity.this).add(stringRequest);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "There is no selected photo", Toast.LENGTH_LONG).show();
                return;
            }
            isImageDefault = false;
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                bmpAttach = BitmapFactory.decodeStream(bufferedInputStream);

                imgAttach.setImageBitmap(bmpAttach);
                imgAttach.setVisibility(View.VISIBLE);
                base64ImageString = Util.convertBitmapToString(bmpAttach);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private StringRequest signUpRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Volley Result: ", response);
                Gson g = new Gson();
                UserModel user = g.fromJson(response, UserModel.class);
                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                i.putExtra(USERNAME, user.getUserName());
                i.putExtra(USER_ID, user.getUserID());
                i.putExtra(AVATAR, base64ImageString);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace(); //log the error resulting from the request for diagnosis/debugging
                Toast.makeText(SignupActivity.this, "Username exists", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> postMap = new HashMap<>();
                postMap.put("userName", edUsername.getText().toString());
                postMap.put("password", edPassword.getText().toString());
                postMap.put("name", edDisplayName.getText().toString());
                postMap.put("dob", edDateOfBirth.getText().toString());
                postMap.put("phone", edPhone.getText().toString());
                postMap.put("gender", checkGender.getText().toString());
                postMap.put("avatar", base64ImageString);
                return postMap;
            }
        };
        return stringRequest;
    }
}