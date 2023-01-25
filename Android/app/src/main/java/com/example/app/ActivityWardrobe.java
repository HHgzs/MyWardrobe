package com.example.app;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.app.adapter.ClothesAdapter;
import com.example.app.adapter.ItemsAdapter;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.itemsInfo;
import com.example.app.entity.staticData;
import com.example.app.util.ToastUtil;
import com.example.app.util.pixUtil;

import java.util.List;

public class ActivityWardrobe extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ImageView wardrobe_button;
    private ImageView home_button;
    private ImageView blotter_button;
    private ImageView iv_head_image;

    private List<clothesInfo> clothesInfoList;
    private List<itemsInfo> itemsInfoList;
    private ItemsDBHelper mDBHelper;

    private ListView lv_list;
    private PopupWindow mPopWindow;

    private long currentID;
    private int startX;
    private int startY;
    private int endY;
    private int endX;
    private int moveX;
    private int page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.activity_anim_1,R.anim.activity_anim_2);
        setContentView(R.layout.activity_wardrobe);

        home_button = findViewById(R.id.home_button);
        wardrobe_button = findViewById(R.id.wardrobe_button);
        blotter_button = findViewById(R.id.blotter_button);
        iv_head_image = findViewById(R.id.iv_head_image);

        home_button.setOnClickListener(this);
        wardrobe_button.setOnClickListener(this);
        blotter_button.setOnClickListener(this);
        changePageType();

        page = staticData.PAGE_CLOTHES;
        mDBHelper = ItemsDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();


    }


    @Override
    protected void onResume() {
        super.onResume();

        lv_list = findViewById(R.id.lv_list);

        if (page == staticData.PAGE_CLOTHES) {

            iv_head_image.setImageResource(R.drawable.wardrobe_head_background);
            clothesInfoList = mDBHelper.queryAllClothesInfo();

            ClothesAdapter ClothesAdapter = new ClothesAdapter(this, clothesInfoList);
            lv_list.setAdapter(ClothesAdapter);
            lv_list.setOnItemClickListener(this);
            lv_list.setOnItemLongClickListener(this);


        } else if (page == staticData.PAGE_ITEMS) {

            iv_head_image.setImageResource(R.drawable.chest_head_background);
            itemsInfoList = mDBHelper.queryAllItemsInfo();

            ItemsAdapter ItemsAdapter = new ItemsAdapter(this, itemsInfoList);
            lv_list.setAdapter(ItemsAdapter);
            lv_list.setOnItemClickListener(this);
            lv_list.setOnItemLongClickListener(this);




        }


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
                if (page == staticData.PAGE_CLOTHES) {
                    mDBHelper.deleteClothesInfo(currentID);

                } else if (page == staticData.PAGE_ITEMS) {
                    mDBHelper.deleteItemsInfo(currentID);
                }

                mPopWindow.dismiss();
                onResume();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getX();
                startY = (int) event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                endX = (int) event.getX();
                endY = (int) event.getY();
                moveX = endX - startX;
                Log.d("HH" , String.valueOf(moveX));

                int height = pixUtil.dip2px(this,120);
                if (endY < height) {
                    if (moveX > 50) {
                        if (page != staticData.PAGE_CLOTHES) {
                            page = staticData.PAGE_CLOTHES;
                        }
                        onResume();

                    } else if (moveX < -50) {
                        if (page != staticData.PAGE_ITEMS) {
                            page = staticData.PAGE_ITEMS;
                        }
                        onResume();
                    }
                }
                break;
        }


        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent = new Intent(this,ActivityReviseItems.class);
        Bundle bundle = new Bundle();

        if (page == staticData.PAGE_CLOTHES) {
            bundle.putInt("id",clothesInfoList.get(position).id);
            bundle.putInt("mItems",staticData.TYPE_CLOTHES);

        } else if (page == staticData.PAGE_ITEMS) {
            bundle.putInt("id",itemsInfoList.get(position).id);
            bundle.putInt("mItems",staticData.TYPE_ITEMS);
        }

        intent.putExtras(bundle);
        startActivity(intent);

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        if (page == staticData.PAGE_CLOTHES) {
            currentID = clothesInfoList.get(position).id;

        } else if (page == staticData.PAGE_ITEMS) {
            currentID = itemsInfoList.get(position).id;
        }

        showPopupWindow();
        return true;
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_anim_3,R.anim.activity_anim_4);
    }




    public void changePageType() {
        home_button.setActivated(false);
        wardrobe_button.setActivated(true);
        blotter_button.setActivated(false);
    }


    private void showPopupWindow() {

        int height = pixUtil.dip2px(this,80);

        @SuppressLint("InflateParams") View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_delete_items,null);
        mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT,height,true);
        mPopWindow.setContentView(contentView);

        TextView pop_btn_delete = contentView.findViewById(R.id.pop_btn_delete);
        pop_btn_delete.setOnClickListener(this);

        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.activity_wardrobe,null);
        mPopWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);


    }


}
