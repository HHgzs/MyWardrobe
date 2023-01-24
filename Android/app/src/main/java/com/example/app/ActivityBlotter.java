package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ActivityBlotter extends AppCompatActivity implements View.OnClickListener {

    private ImageView wardrobe_button;
    private ImageView home_button;
    private ImageView blotter_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim_1,R.anim.activity_anim_2);
        setContentView(R.layout.activity_blotter);

        home_button = findViewById(R.id.home_button);
        wardrobe_button = findViewById(R.id.wardrobe_button);
        blotter_button = findViewById(R.id.blotter_button);

        home_button.setOnClickListener(this);
        wardrobe_button.setOnClickListener(this);
        blotter_button.setOnClickListener(this);
        changePageType();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_button:
                Intent home_intent = new Intent(this, ActivityHome.class);
                home_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home_intent);
                break;
            case R.id.wardrobe_button:
                Intent wr_intent = new Intent(this, ActivityWardrobe.class);
                wr_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(wr_intent);
                break;
        }
    }

    public void changePageType() {
        home_button.setActivated(false);
        wardrobe_button.setActivated(false);
        blotter_button.setActivated(true);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_anim_3,R.anim.activity_anim_4);
    }
}