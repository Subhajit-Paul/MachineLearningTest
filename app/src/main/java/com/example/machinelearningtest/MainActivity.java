package com.example.machinelearningtest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.machinelearningtest.helpers.ImageHelperActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onImageButtonClick(View view){
        Intent intent = new Intent(this, ImageHelperActivity.class);
        startActivity(intent);
    }

    public void onTextRecognizeButtonClick(View view){
        Intent intent = new Intent(this, ImageHelperActivity.class);
        startActivity(intent);
    }
}