package com.example.harmoid;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{

    private TextView tv_1;
    private TextView tv_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.btn_test).setOnClickListener(this);
        tv_1 = findViewById(R.id.tv_show_1);
        tv_2 = findViewById(R.id.tv_show_2);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:
//                String versionName = APKVersionCodeUtils.getVerName(this);
//                String versionCode = APKVersionCodeUtils.getVersionCode(this) + "";
//                Log.i("HH","versionName--" + versionName);
//                Log.i("HH","versionName--" + versionName);


                tv_1.setText("Product Model: " + android.os.Build.MODEL + ","
                        + android.os.Build.VERSION.SDK + ","
                        + android.os.Build.VERSION.RELEASE);
//                tv_2.setText(versionName);




                break;
        }

    }
}



