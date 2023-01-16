package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ActivityHome extends AppCompatActivity implements View.OnClickListener {

    private ImageView wardrobe_button;
    private ImageView home_button;
    private ImageView blotter_button;
    private ImageView float_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        home_button = findViewById(R.id.home_button);
        wardrobe_button = findViewById(R.id.wardrobe_button);
        blotter_button = findViewById(R.id.blotter_button);
        float_button = findViewById(R.id.float_button);

        home_button.setOnClickListener(this);
        wardrobe_button.setOnClickListener(this);
        blotter_button.setOnClickListener(this);
        float_button.setOnClickListener(this);
        changePageType();



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wardrobe_button:
                Intent wr_intent = new Intent(this,ActivityWardrobe.class);
                wr_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(wr_intent);
                break;
            case R.id.blotter_button:
                Intent bl_intent = new Intent(this,ActivityBlotter.class);
                bl_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(bl_intent);
                break;
            case R.id.float_button:
                Intent items_intent = new Intent(this,ActivityAddItems.class);
                startActivity(items_intent);
                break;

        }











    }



    public void changePageType() {
        home_button.setActivated(true);
        wardrobe_button.setActivated(false);
        blotter_button.setActivated(false);
    }





}