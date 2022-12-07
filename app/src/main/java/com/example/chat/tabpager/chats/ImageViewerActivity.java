package com.example.chat.tabpager.chats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.chat.R;
import com.example.chat.databinding.ActivityImageViewerBinding;

public class ImageViewerActivity extends AppCompatActivity {
    private ActivityImageViewerBinding binding;
    private String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_image_viewer);
        imageUri=getIntent().getStringExtra("url");
        Glide.with(this)
                .load(imageUri)
                .into(binding.imageViewer);



    }
}