package com.example.androidchatapp.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidchatapp.R;

public class PhoneActivity extends AppCompatActivity {
    private Button btnEditPhone;
    private EditText Edphonesetting;
    String edPhoneEdit ="";
    private Button btnConfirmEditPhone;
    private String oldPhone, newPhone;
    public static final String EXTRA_DATA_PHONE = "EXTRA_DATA";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        btnEditPhone = (Button) this.findViewById(R.id.bntEditPhone);
        btnConfirmEditPhone = (Button) this.findViewById(R.id.btnconfirmeditphone);
        Edphonesetting = (EditText) this.findViewById(R.id.EdPhoneSetting);
        Edphonesetting.setText(getIntent().getExtras().getString("PHONE"));

        btnEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPhone = Edphonesetting.getText().toString();
                Edphonesetting.setEnabled(true);
            }
        });

        btnConfirmEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPhone = Edphonesetting.getText().toString();
                if(oldPhone == newPhone){
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED,intent);
                    Edphonesetting.setEnabled(false);
                    finish();
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_DATA_PHONE, newPhone);
                    setResult(Activity.RESULT_OK,intent);
                    Edphonesetting.setEnabled(false);
                    finish();
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK) {
            newPhone = Edphonesetting.getText().toString();
            if(oldPhone == newPhone){
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED,intent);
                Edphonesetting.setEnabled(false);
                finish();
            }
            else {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DATA_PHONE, newPhone);
                setResult(Activity.RESULT_OK,intent);
                Edphonesetting.setEnabled(false);
                finish();
            }
            return true;
        }
        return false;
    }
}
