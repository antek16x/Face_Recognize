package com.example.facerecognize;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.facerecognize.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), MenuActivity.class));
        MainActivity.this.finish();
    }
}