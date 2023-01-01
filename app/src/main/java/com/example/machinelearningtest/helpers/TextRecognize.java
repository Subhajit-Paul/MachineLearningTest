package com.example.machinelearningtest.helpers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.machinelearningtest.R;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class TextRecognize extends AppCompatActivity {

    private ImageView imageViewInput;
    private TextView textViewOutput;
    private final int r_CODE = 1000;

    private TextRecognizer recognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognize);

        textViewOutput = findViewById(R.id.textViewOutput);
        imageViewInput = findViewById(R.id.imageViewInput);

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    public void onPickImage(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, r_CODE);
    }

    public void onStartCamera(View view){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == r_CODE){
                assert data != null;
                Uri uri = data.getData();
                Bitmap bitmap = loadFromURI(uri);
                imageViewInput.setImageBitmap(bitmap);
                recognizer(bitmap);
            }
        }
    }

    private Bitmap loadFromURI(Uri uri){
        Bitmap bitmap = null;
        try {
            ImageDecoder.Source source;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                source = ImageDecoder.createSource(getContentResolver(), uri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
            else{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return bitmap;
    }

    @SuppressLint("SetTextI18n")
    private void recognizer(Bitmap bitmap) {
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        recognizer.process(inputImage)
                .addOnSuccessListener(visionText -> {
                        textViewOutput.setText(visionText.getText());
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
}