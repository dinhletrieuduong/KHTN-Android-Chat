package com.example.androidchatapp.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidchatapp.R;

public class GenderActivity extends AppCompatActivity {
    public static final String EXTRA_DATA_GENDER = "EXTRA_DATA";
    RadioGroup rdgGender;
    RadioButton rdbMale, rdbFemale, rdbAll;
    Button confirmGenderSetting;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        rdgGender = (RadioGroup) findViewById(R.id.rdgGenderSetting);
        rdbMale  = (RadioButton) findViewById(R.id.rdbMaleSetting);
        rdbFemale  = (RadioButton) findViewById(R.id.rdbFemaleSetting);
        rdbAll  = (RadioButton) findViewById(R.id.rdbAllSetting);
        confirmGenderSetting = (Button) findViewById(R.id.bntconfirmGender);

        String gender = getIntent().getExtras().getString("GENDER");
        if (gender.equals("Male")) rdbMale.setChecked(true);
        else if (gender.equals("Female")) rdbFemale.setChecked(true);
        else rdbAll.setChecked(true);

        confirmGenderSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gender = GenderActivity.this.rdgGender.getCheckedRadioButtonId();
                RadioButton checkGender  = (RadioButton) findViewById(gender);
                String gen = checkGender.getText().toString();

                Intent intent = new Intent();
                intent.putExtra(EXTRA_DATA_GENDER, gen);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK) {
            super.onKeyDown(keyCode, event);
            int gender = GenderActivity.this.rdgGender.getCheckedRadioButtonId();
            RadioButton checkGender  = (RadioButton) findViewById(gender);
            String gen = checkGender.getText().toString();

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATA_GENDER, gen);
            setResult(Activity.RESULT_OK,intent);
            finish();
            return true;
        }
        return false;
    }
}