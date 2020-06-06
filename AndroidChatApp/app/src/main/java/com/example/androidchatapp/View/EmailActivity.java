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

public class EmailActivity extends AppCompatActivity {
    private Button btnEditEmail;
    private EditText Edemailsetting;
    String edEmailEdit ="";
    private Button btnConfirmEditEmail;
    private String oldEmail = "", newEmail = "";
    public static final String EXTRA_DATA_EMAIL = "EXTRA_DATA";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        btnEditEmail = (Button) this.findViewById(R.id.bntEditEmail);
        btnConfirmEditEmail = (Button) this.findViewById(R.id.btnconfirmeditemail);
        Edemailsetting = (EditText) this.findViewById(R.id.EdEmailSetting);
        Edemailsetting.setText(getIntent().getExtras().getString("EMAIL"));
        btnEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail = Edemailsetting.getText().toString();;
                Edemailsetting.setEnabled(true);
            }
        });

        btnConfirmEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEmail = Edemailsetting.getText().toString();
                if(oldEmail.equals(newEmail)){
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED,intent);
                    Edemailsetting.setEnabled(false);
                    finish();
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_DATA_EMAIL, newEmail);
                    setResult(Activity.RESULT_OK,intent);
                    Edemailsetting.setEnabled(false);
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK) {
            newEmail = Edemailsetting.getText().toString();
            if(oldEmail.equals(newEmail)){
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED,intent);
                Edemailsetting.setEnabled(false);
                finish();
            }
            else {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DATA_EMAIL, newEmail);
                setResult(Activity.RESULT_OK,intent);
                Edemailsetting.setEnabled(false);
                finish();
            }
            return true;
        }
        return false;
    }
}