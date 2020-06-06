package com.example.androidchatapp.View;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidchatapp.R;

public class PlaceActivity extends AppCompatActivity {

    private Button btnEditPlace;
    private EditText Edpalcesetting;
    String edPlaceEdit ="";
    private Button btnConfirmEditPlace;
    private String oldPlace, newPlace;
    public static final String EXTRA_DATA_PLACE = "EXTRA_DATA";
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        btnEditPlace = (Button) this.findViewById(R.id.btneditplace);
        btnConfirmEditPlace = (Button) this.findViewById(R.id.btnconfirmedit);
        Edpalcesetting = (EditText) this.findViewById(R.id.EdPlaceSetting);
        Edpalcesetting.setText(getIntent().getExtras().getString("PLACE"));
        oldPlace = newPlace = Edpalcesetting.getText().toString();

        btnEditPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldPlace = Edpalcesetting.getText().toString();
                Edpalcesetting.setEnabled(true);
            }
        });

        btnConfirmEditPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPlace = Edpalcesetting.getText().toString();
                if (oldPlace == newPlace) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED, intent);
//                    Toast.makeText(PlaceActivity.this, "AAAA",Toast.LENGTH_SHORT ).show();
                    Edpalcesetting.setEnabled(false);
                    finish();
                } else {
                    Intent intent = new Intent(PlaceActivity.this, SettingActivity.class);
                    intent.putExtra(EXTRA_DATA_PLACE, newPlace);
                    setResult(Activity.RESULT_OK, intent);
                    Edpalcesetting.setEnabled(false);
                    finish();
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK) {
            newPlace = Edpalcesetting.getText().toString();
            if (oldPlace == newPlace) {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                Toast.makeText(PlaceActivity.this, "AAAA",Toast.LENGTH_SHORT ).show();
                Edpalcesetting.setEnabled(false);
                finish();
            } else {
                Intent intent = new Intent(PlaceActivity.this, SettingActivity.class);
                intent.putExtra(EXTRA_DATA_PLACE, newPlace);
                setResult(Activity.RESULT_OK, intent);
                Edpalcesetting.setEnabled(false);
                finish();
            }
            return true;
        }
        return false;
    }
}
