package com.example.onestop;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

public class first extends AppCompatActivity {
        private static int SPLASH_TIME_OUT = 2000;
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent first_login = new Intent(first.this, login.class);
                startActivity(first_login);
                finish();
            }
        },SPLASH_TIME_OUT);


        }



    }
