package com.github.fearmygaze.mercury.view.util;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.ortiz.touchview.TouchImageView;

public class ImageViewer extends AppCompatActivity {

    ShapeableImageView goBack;
    TouchImageView image;
    String imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        goBack = findViewById(R.id.imageViewerGoBack);
        image = findViewById(R.id.imageViewerImage);

        imageData = getIntent().getStringExtra("imageData");

        goBack.setOnClickListener(v -> onBackPressed());

        Glide.with(ImageViewer.this).load(imageData).into(image);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}