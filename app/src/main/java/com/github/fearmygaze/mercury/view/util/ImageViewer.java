package com.github.fearmygaze.mercury.view.util;

import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.github.fearmygaze.mercury.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.ortiz.touchview.TouchImageView;

import java.util.Objects;

public class ImageViewer extends AppCompatActivity {

    MaterialToolbar toolbar;
    TouchImageView image;

    Bundle bundle;
    String imageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        toolbar = findViewById(R.id.imageViewerToolBar);
        image = findViewById(R.id.imageViewerImage);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        bundle = getIntent().getExtras();
        if (bundle == null) onBackPressed();
        imageData = bundle.getString("imageData");
        if (imageData == null) onBackPressed();

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.imageViewerOptionSave) {
                Toast.makeText(ImageViewer.this, "Not Implemented", Toast.LENGTH_SHORT).show();
                return true;
            } else return false;
        });


        Glide.with(ImageViewer.this).load(imageData).into(image);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getIntent().getBooleanExtra("downloadImage", false)) {
            getMenuInflater().inflate(R.menu.image_viewer_options, menu);
            return true;
        }
        return false;
    }
}
