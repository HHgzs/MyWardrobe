package com.example.app;

import static com.example.app.util.ToastUtil.show;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.adapter.BlotterShowClothesAdapter;
import com.example.app.adapter.BlotterShowItemsAdapter;
import com.example.app.adapter.ClothesAdapter;
import com.example.app.adapter.ItemsAdapter;
import com.example.app.database.BlotterDBHelper;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.blotterInfo;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.dateInfo;
import com.example.app.entity.itemsInfo;
import com.example.app.entity.noteInfo;
import com.example.app.entity.staticData;
import com.example.app.util.FileUtil;
import com.example.app.util.dateUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ActivityAddBlotter extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, RadioGroup.OnCheckedChangeListener{


    private EditText et_blotter_name;
    private EditText et_blotter_brief;
    private RadioGroup rg_blotter_show_type;
    private RadioButton rb_blotter_clothes;
    private RadioButton rb_blotter_items;
    private TextView tv_blotter_show_time;
    private ListView lv_list_blotter_add_items;
    private Button btn_blotter_set_time;
    private Button btn_save;
    private Button btn_exit;

    private BlotterDBHelper mBlotterDBHelper;
    private ItemsDBHelper mItemsDBHelper;
    private blotterInfo mBlotterInfo;
    private noteInfo mNoteInfo;

    private List<clothesInfo> clothesInfoList;
    private List<itemsInfo> itemsInfoList;
    private BlotterShowClothesAdapter clothesAdapter;
    private BlotterShowItemsAdapter itemsAdapter;

    private int mYear;
    private int mMonth;
    private int mDay;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private String createdData;
    private String applyDate = staticData.EMPTY;
    private String tableName = staticData.EMPTY;

    private int page;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blotter);

        et_blotter_name = findViewById(R.id.et_blotter_name);
        et_blotter_brief = findViewById(R.id.et_blotter_brief);
        tv_blotter_show_time = findViewById(R.id.tv_blotter_show_time);
        lv_list_blotter_add_items = findViewById(R.id.lv_list_blotter_add_items);
        btn_blotter_set_time = findViewById(R.id.btn_blotter_set_time);
        btn_blotter_set_time.setOnClickListener(this);
        btn_save = findViewById(R.id.btn_save);
        btn_exit = findViewById(R.id.btn_exit);
        btn_save.setOnClickListener(this);
        btn_exit.setOnClickListener(this);

        rg_blotter_show_type = findViewById(R.id.rg_blotter_show_type);
        rg_blotter_show_type.setOnCheckedChangeListener(this);
        rb_blotter_clothes = findViewById(R.id.rb_blotter_clothes);
        rb_blotter_items = findViewById(R.id.rb_blotter_items);

        mBlotterDBHelper = BlotterDBHelper.getInstance(this);
        mBlotterDBHelper.openReadLink();
        mBlotterDBHelper.openWriteLink();

        mItemsDBHelper = ItemsDBHelper.getInstance(this);
        mItemsDBHelper.openReadLink();
        mItemsDBHelper.openWriteLink();

        clothesInfoList = mItemsDBHelper.queryAllClothesInfo();
        itemsInfoList = mItemsDBHelper.queryAllItemsInfo();

        clothesAdapter = new BlotterShowClothesAdapter(this, clothesInfoList);
        itemsAdapter = new BlotterShowItemsAdapter(this, itemsInfoList);

        lv_list_blotter_add_items.setAdapter(clothesAdapter);
        lv_list_blotter_add_items.setOnItemClickListener(this);
        rb_blotter_clothes.setChecked(true);

        tableName = "table" + FileUtil.getTime();
        MyApplication.noteInfoList = new ArrayList<>();

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateInfo dateInfo = new dateInfo(year,monthOfYear,dayOfMonth);
                applyDate = dateInfo.dateStr;
                tv_blotter_show_time.setText(dateUtil.publishDate(dateInfo));
            }
        };



    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (Objects.equals(applyDate, staticData.EMPTY) || Objects.equals(createdData, staticData.EMPTY)) {
                    show(this,"请选择时间");
                    break;
                }

                blotterInfo blotterInfo = new blotterInfo();
                blotterInfo.table_name = tableName;
                blotterInfo.publish_name = et_blotter_name.getText().toString();
                blotterInfo.brief = et_blotter_brief.getText().toString();
                blotterInfo.createdTime = createdData;
                blotterInfo.applyTime = applyDate;

                mBlotterDBHelper.createNoteTable(tableName);
                mBlotterDBHelper.insertNoteInfo(MyApplication.noteInfoList,tableName);
                mBlotterDBHelper.insertBlotterInfo(blotterInfo);


                break;

            case R.id.btn_exit:
                finish();
                break;



            case R.id.btn_blotter_set_time:
                dateInfo dateInfo = new dateInfo();
                mYear = dateInfo.year;
                mMonth = dateInfo.month;
                mDay = dateInfo.day;
                createdData = dateInfo.dateStr;
                new DatePickerDialog(this,onDateSetListener,mYear, mMonth, mDay).show();
                break;


        }






    }





    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

            case R.id.rb_blotter_clothes:
                page = staticData.PAGE_CLOTHES;
                lv_list_blotter_add_items.setAdapter(clothesAdapter);
                lv_list_blotter_add_items.setOnItemClickListener(this);
                break;

            case R.id.rb_blotter_items:
                page = staticData.PAGE_ITEMS;
                lv_list_blotter_add_items.setAdapter(itemsAdapter);
                lv_list_blotter_add_items.setOnItemClickListener(this);
                break;
        }






    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (page == staticData.PAGE_CLOTHES) {
//            clothesInfoList.get(position).id;


        } else if (page == staticData.PAGE_ITEMS) {
//            itemsInfoList.get(position).id

        }


    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlotterDBHelper.closeLink();
        mItemsDBHelper.closeLink();
    }



}