package com.example.app;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.app.adapter.ClothesAdapter;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.clothesInfo;
import com.example.app.util.ToastUtil;

import java.util.List;

public class ActivityWardrobe extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ImageView wardrobe_button;
    private ImageView home_button;
    private ImageView blotter_button;

    private List<clothesInfo> clothesInfoList;
    private ItemsDBHelper mDBHelper;

    private ListView lv_list_clothes;
    private PopupWindow mPopWindow;

    private long currentID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim_1,R.anim.activity_anim_2);
        setContentView(R.layout.activity_wardrobe);

        home_button = findViewById(R.id.home_button);
        wardrobe_button = findViewById(R.id.wardrobe_button);
        blotter_button = findViewById(R.id.blotter_button);
        lv_list_clothes = findViewById(R.id.lv_list_clothes);

        home_button.setOnClickListener(this);
        wardrobe_button.setOnClickListener(this);
        blotter_button.setOnClickListener(this);
        changePageType();

        mDBHelper = ItemsDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();

//        clothesInfoList = mDBHelper.queryAllClothesInfo();
//        ClothesAdapter adapter = new ClothesAdapter(this, clothesInfoList);
//        lv_list_clothes.setAdapter(adapter);
//        lv_list_clothes.setOnItemClickListener(this);
//        lv_list_clothes.setOnItemLongClickListener(this);



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

            case R.id.blotter_button:
                Intent bl_intent = new Intent(this, ActivityBlotter.class);
                bl_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(bl_intent);
                break;

            case R.id.pop_btn_delete:
                mDBHelper.deleteClothesInfo(currentID);
                mPopWindow.dismiss();
                refresh();


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        clothesInfoList = mDBHelper.queryAllClothesInfo();
        ClothesAdapter adapter = new ClothesAdapter(this, clothesInfoList);
        lv_list_clothes.setAdapter(adapter);
        lv_list_clothes.setOnItemClickListener(this);
        lv_list_clothes.setOnItemLongClickListener(this);




    }

    public void changePageType() {
        home_button.setActivated(false);
        wardrobe_button.setActivated(true);
        blotter_button.setActivated(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ToastUtil.show(this, "条目被点击了，" + clothesInfoList.get(position).name);

        Intent intent = new Intent(this,ActivityReviseItems.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id",clothesInfoList.get(position).id);
        bundle.putInt("mItems",0);
        intent.putExtras(bundle);
        startActivity(intent);

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//        ToastUtil.show(this, "条目被长按了，" + clothesInfoList.get(position).name);
        currentID = clothesInfoList.get(position).id;
        showPopupWindow();
        return true;
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_anim_3,R.anim.activity_anim_4);
    }


    private void showPopupWindow() {
        @SuppressLint("InflateParams") View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_delete_items,null);
        mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,200,true);
        mPopWindow.setContentView(contentView);

        TextView pop_btn_delete = contentView.findViewById(R.id.pop_btn_delete);
        pop_btn_delete.setOnClickListener(this);

        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_items,null);
        mPopWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);


    }

    public void refresh() {
        onCreate(null);
    }



}
