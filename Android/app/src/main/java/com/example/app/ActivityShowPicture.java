package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.app.util.DataService;

public class ActivityShowPicture extends AppCompatActivity implements View.OnClickListener{

    private ImageView iv_show;
    private Bitmap bitmap;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_picture);
        iv_show = findViewById(R.id.iv_single_show);
        iv_show.setOnClickListener(this);

        DataService instance = DataService.getInstance();
        if (instance.getEditBitmap() != null) {
            bitmap = instance.getEditBitmap();
            iv_show.setImageBitmap(bitmap);
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_single_show) {
            finish();
        }

    }
}