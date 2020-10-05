package com.example.tictacttoe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Button start;
    ImageView exit, replay;
    int PERMISSION_CODE = 100;
    EditText firstPlayerEdit, firstPlayerNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        listeners();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.INTERNET, Manifest.permission.RECEIVE_SMS,}, PERMISSION_CODE);
        }
    }


    @Override
    public void onBackPressed() {
        exitAppCode();
    }

    public void exitAppCode() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void listeners() {

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firstPlayerEdit.getText().toString().length() == 0) {
                    firstPlayerEdit.setError("Empty Field!!");
                } else if (firstPlayerNum.getText().toString().length() == 0) {
                    firstPlayerNum.setError("Empty Field!!");
                } else {

                    PreferencesService.instance().setFirstPlayer(firstPlayerEdit.getText().toString());
                    PreferencesService.instance().setFirstNum(firstPlayerNum.getText().toString());

                    startActivity(new Intent(MainActivity.this, SecondPlayer.class));
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitAppCode();
            }
        });

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
    }
    public void init() {

        firstPlayerEdit = findViewById(R.id.firstPlayerEdit);
        firstPlayerNum = findViewById(R.id.firstPlayerNum);
        exit = findViewById(R.id.exit);
        replay = findViewById(R.id.replay);
        start = findViewById(R.id.start);
        PreferencesService.init(MainActivity.this);
    }


}