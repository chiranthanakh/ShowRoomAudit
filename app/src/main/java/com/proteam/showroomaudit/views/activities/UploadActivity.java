package com.proteam.showroomaudit.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.proteam.showroomaudit.R;
import com.proteam.showroomaudit.databinding.ActivityUploadBinding;

public class UploadActivity extends AppCompatActivity {
    ActivityUploadBinding uploadBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uploadBinding=ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(uploadBinding.getRoot());


        uploadBinding.ivBack.setOnClickListener(view -> onBackPressed());
    }
}