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
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;

public class ImageHelperActivity extends AppCompatActivity {
    private ImageView imageViewInput;
    private TextView  textViewOutput;
    private final int r_CODE = 1000;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageLabeler imageLabeler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_helper);
        textViewOutput = findViewById(R.id.textViewOutput);
        imageViewInput = findViewById(R.id.imageViewInput);

        imageLabeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder().setConfidenceThreshold(.6f).build());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
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
                IMGClassifier(bitmap);
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                assert data != null;
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageViewInput.setImageBitmap(bitmap);
                IMGClassifier(bitmap);
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
    private void IMGClassifier(Bitmap bitmap){
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        imageLabeler.process(inputImage).addOnSuccessListener(imageLabels -> {
            if(imageLabels.size() > 0){
                StringBuilder sb = new StringBuilder();
                for (ImageLabel label: imageLabels){
                    sb.append(label.getText()).append(" : ").append(label.getConfidence()).append("\n");
                }
                textViewOutput.setText(sb.toString());
            }
            else{
                textViewOutput.setText("Couldn't Classify");
            }
        }).addOnFailureListener(Throwable::printStackTrace);
    }
}