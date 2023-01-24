package com.example.app;

import static com.example.app.util.ToastUtil.show;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.app.util.PermissionUtil;

public class ActivityHome extends AppCompatActivity implements View.OnClickListener {

    private ImageView wardrobe_button;
    private ImageView home_button;
    private ImageView blotter_button;
    private Button float_button;

    private static final int REQUEST_CODE_STORAGE = 1;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim_1,R.anim.activity_anim_2);
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
                Intent wr_intent = new Intent(this, ActivityWardrobe.class);
                wr_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(wr_intent);
                break;

            case R.id.blotter_button:
                Intent bl_intent = new Intent(this, ActivityBlotter.class);
                bl_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(bl_intent);
                break;

            case R.id.float_button:
                if (PermissionUtil.checkPermission(this, PERMISSIONS, REQUEST_CODE_STORAGE)) {
                    Intent intent = new Intent(this, ActivityAddItems.class);
                    startActivity(intent);
                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (PermissionUtil.checkGrant(grantResults)) {
                    Log.d("HH", "所有权限获取成功");

                    Intent intent = new Intent(this, ActivityAddItems.class);
                    startActivity(intent);

                } else {
                    show(this,"缺少权限");
                }
        }
    }

    public void changePageType() {
        home_button.setActivated(true);
        wardrobe_button.setActivated(false);
        blotter_button.setActivated(false);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_anim_3,R.anim.activity_anim_4);
    }
}