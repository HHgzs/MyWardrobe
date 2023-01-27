package com.example.harmoid;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class ActivityTestAnim extends AppCompatActivity implements View.OnClickListener{


    private ImageView iv_add;
    private Button btn_test;
    private ImageView iv_bk;
    private ImageView iv_cover_image;
    private ImageView iv_list_clothes_img;
    private TextView tv_list_clothes_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_left,R.anim.slide_right);
        setContentView(R.layout.activity_test_anim);
        btn_test = findViewById(R.id.btn_test);
        btn_test.setOnClickListener(this);
        iv_add = findViewById(R.id.iv_add);
        iv_bk = findViewById(R.id.iv_bk);
        iv_cover_image = findViewById(R.id.iv_cover_image);
        iv_list_clothes_img = findViewById(R.id.iv_list_clothes_img);
        tv_list_clothes_name = findViewById(R.id.tv_list_clothes_name);


    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:

                // 设置动画
                @SuppressLint("ResourceType") Animator animator = AnimatorInflater.loadAnimator(this, R.anim.bounce_up);
                animator.setInterpolator(new OvershootInterpolator());
                animator.setTarget(iv_add);

                // 开始动画
                animator.start();
                break;

//
//            case 2:
//                @SuppressLint("ResourceType") Animator animator5 = AnimatorInflater.loadAnimator(this, R.anim.slide_left);
//                animator5.setInterpolator(new OvershootInterpolator());
//                animator5.setTarget(iv_bk);
//
//                iv_bk.setImageResource(R.drawable.wardrobe_head_background);
//
//                animator5.start();
//
//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        iv_bk.setImageResource(R.drawable.chest_head_background);
//                        timer.cancel(); //执行完毕停止定时器
//                    }
//                }, 250);
//
//
//
            case 3:
                @SuppressLint("ResourceType") Animator animator4 = AnimatorInflater.loadAnimator(this, R.anim.pop_in);
                animator4.setInterpolator(new OvershootInterpolator());
                animator4.setTarget(iv_cover_image);
                Animator animator2 = AnimatorInflater.loadAnimator(this, R.anim.pop_in);
                animator2.setInterpolator(new OvershootInterpolator());
                animator2.setTarget(iv_list_clothes_img);
                Animator animator3 = AnimatorInflater.loadAnimator(this, R.anim.show_in);
                animator3.setInterpolator(new OvershootInterpolator());
                animator3.setTarget(tv_list_clothes_name);


                animator4.start();
                animator2.start();
                animator3.start();
                break;



        }
    }
}