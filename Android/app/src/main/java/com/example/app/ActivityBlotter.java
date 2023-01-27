package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.app.adapter.BlotterAdapter;
import com.example.app.database.BlotterDBHelper;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.blotterInfo;
import com.example.app.entity.staticData;
import com.example.app.util.ListViewForScrollView;
import com.example.app.util.pixUtil;

import java.util.ArrayList;
import java.util.List;

public class ActivityBlotter extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ImageView wardrobe_button;
    private ImageView home_button;
    private ImageView blotter_button;
    private ListViewForScrollView lv_blotter_list;
    private ImageView iv_blotter_add;
    private PopupWindow mPopWindow;

    private List<blotterInfo> blotterInfoList;
    private BlotterDBHelper mDBHelper;

    private long currentID;
    private String currentName;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim_1,R.anim.activity_anim_2);
        setContentView(R.layout.activity_blotter_2);

        iv_blotter_add = findViewById(R.id.iv_blotter_add);
        home_button = findViewById(R.id.home_button);
        wardrobe_button = findViewById(R.id.wardrobe_button);
        blotter_button = findViewById(R.id.blotter_button);
        lv_blotter_list = findViewById(R.id.lv_blotter_list);

        home_button.setOnClickListener(this);
        wardrobe_button.setOnClickListener(this);
        blotter_button.setOnClickListener(this);

        iv_blotter_add.setOnClickListener(this);

        changePageType();

        mDBHelper = BlotterDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();
        MyApplication.noteInfoList = new ArrayList<>();


    }

    @Override
    protected void onResume() {
        super.onResume();

        blotterInfoList = mDBHelper.queryAllBlottersInfo();
        BlotterAdapter blotterAdapter = new BlotterAdapter(this,blotterInfoList);
        lv_blotter_list.setAdapter(blotterAdapter);
        lv_blotter_list.setOnItemClickListener(this);
        lv_blotter_list.setOnItemLongClickListener(this);




    }

    @SuppressLint("NonConstantResourceId")
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

            case R.id.iv_blotter_add:

                Intent intent_blotter = new Intent(this,ActivityAddBlotter.class);
                startActivity(intent_blotter);
                break;

            case R.id.pop_btn_delete:
                mDBHelper.deleteBlotterInfo(currentID);
                mDBHelper.deleteTableInfo(currentName);
                mPopWindow.dismiss();
                onResume();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ActivityShowBlotter.class);
        Bundle bundle = new Bundle();
        bundle.putString("tableName",blotterInfoList.get(position).table_name);
        bundle.putInt("blotterID",blotterInfoList.get(position).id);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        currentID = blotterInfoList.get(position).id;
        currentName = blotterInfoList.get(position).table_name;
        showPopupWindow();

        return true;
    }


    private void showPopupWindow() {

        // 动画初始化
        @SuppressLint("ResourceType") Animator animator = AnimatorInflater.loadAnimator(this, R.anim.pop_up);
        animator.setInterpolator(new OvershootInterpolator());

        int height = pixUtil.dip2px(this,80);

        @SuppressLint("InflateParams") View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_delete_items,null);
        mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,height,true);
        mPopWindow.setContentView(contentView);

        // 设置动画
        animator.setTarget(contentView);

        TextView pop_btn_delete = contentView.findViewById(R.id.pop_btn_delete);
        pop_btn_delete.setOnClickListener(this);

        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.activity_blotter,null);
        mPopWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);

        // 启用动画
        animator.start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();






    }
}