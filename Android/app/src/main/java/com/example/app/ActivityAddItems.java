package com.example.app;

import static com.example.app.R.id.et_name;
import static com.example.app.util.ToastUtil.show;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.itemsInfo;
import com.example.app.util.ToastUtil;

import java.io.File;
import java.util.Calendar;


public class ActivityAddItems extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private RadioGroup rg_clothes_or_items;
    private RadioGroup rg_clothes;
    private LinearLayout layout_items;
    private LinearLayout layout_clothes;
    private LinearLayout ll_beddingType;
    private LinearLayout ll_clothingType;
    private PopupWindow mPopWindow;
    private Spinner sp_items_type;
    private Spinner sp_clothes_season;
    private Spinner sp_clothes_thickness;
    private Spinner sp_clothing_type;
    private Spinner sp_bedding_type;
    private EditText et_name;
    private EditText et_brief;
    private EditText et_items_brief;

    // 物品首选分类分成衣橱内物品和其他物品，衣橱内用 0 表示，其他用 1 表示
    // clothing代表衣物，bedding代表床单被褥，clothes是上述两者总称呼
    // clothing用 0 表示，bedding用 1 表示
    private int mItems = 0;
    private int clothes = 0;

    // 此处为spinner下拉菜单的适配器数组
    private final static String[] clothingTypeArray = {"外套","外裤","衬衫","卫衣","内衣裤"};
    private final static String[] beddingTypeArray = {"床单","被褥","其他"};
    private final static String[] itemsTypeArray = {"摄影器材","工具","生活用品","电气设备","其他"};
    private final static String[] thicknessArray = {"薄","中","厚"};
    private final static String[] seasonArray = {"春秋","夏","冬"};

    private String directory;
    private ActivityResultLauncher<Intent> register;

    private clothesInfo mClothesInfo;
    private itemsInfo mItemsInfo;
    private ItemsDBHelper mDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        rg_clothes_or_items = findViewById(R.id.rg_clothes_or_items);
        rg_clothes_or_items.setOnCheckedChangeListener(this);

        rg_clothes = findViewById(R.id.rg_type);
        rg_clothes.setOnCheckedChangeListener(this);

        layout_clothes = (LinearLayout)findViewById(R.id.layout_clothes);
        layout_items = (LinearLayout)findViewById(R.id.layout_items);
        ll_clothingType = (LinearLayout)findViewById(R.id.ll_clothingType);
        ll_beddingType = (LinearLayout)findViewById(R.id.ll_beddingType);

        sp_clothing_type = findViewById(R.id.sp_clothing_type);
        sp_bedding_type = findViewById(R.id.sp_bedding_type);
        sp_clothes_thickness = findViewById(R.id.sp_clothes_thickness);
        sp_clothes_season = findViewById(R.id.sp_clothes_season);
        sp_items_type = findViewById(R.id.sp_items_type);

        et_name = findViewById(R.id.et_name);
        et_brief = findViewById(R.id.et_brief);
        et_items_brief = findViewById(R.id.et_items_brief);


        // 为图片添加按钮设置点击事件监听，弹出拍照或从相册选择按钮
        Button btn_add_img = (Button)findViewById(R.id.btn_add_img);
        btn_add_img.setOnClickListener(v -> showPopupWindow());

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);

        // 创建数据对象
        mClothesInfo = new clothesInfo();
        mItemsInfo = new itemsInfo();

        directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separatorChar;

        mDBHelper = ItemsDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();
        // 初始化下拉列表
        initSpinner();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (et_name.length() == 0) {
                    show(this,"请输入物品名称");
                    break;
                }

                if (mItems == 0) {
                    mClothesInfo.name = et_name.getText().toString();
                    mClothesInfo.basicType = clothes;
                    if (clothes == 0) {
                        mClothesInfo.secondType = sp_clothing_type.getSelectedItemPosition();
                    } else if (clothes == 1) {
                        mClothesInfo.secondType = sp_bedding_type.getSelectedItemPosition();
                    }
                    mClothesInfo.thickness = sp_clothes_thickness.getSelectedItemPosition();
                    mClothesInfo.season = sp_clothes_season.getSelectedItemPosition();
                    mClothesInfo.brief = et_brief.getText().toString();

                    // 向clothes数据库插入项
                    mDBHelper.insertClothesInfo(mClothesInfo);

                } else if (mItems == 1) {
                    mItemsInfo.name = et_name.getText().toString();
                    mItemsInfo.type = sp_items_type.getSelectedItemPosition();
                    mItemsInfo.brief = et_items_brief.getText().toString();

                    // 向clothes数据库插入项
                    mDBHelper.insertItemsInfo(mItemsInfo);

                }

//                finish();
                break;

            case R.id.btn_exit:
                finish();
                break;

            case R.id.btn_delete:
                mDBHelper.dataInit();
                break;



            case R.id.pop_btn_catch:
                mPopWindow.dismiss();

                String path = directory + getTime() +".jpg";
                register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result != null) {
                        Intent intent = result.getData();
                        if (intent != null && result.getResultCode() == Activity.RESULT_OK) {








                        }
                    }






                });



//                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,);






                break;

            case R.id.pop_btn_album:
//                Intent intent_album = new Intent(Intent.ACTION_PICK);

                show(this,getTime());

                mPopWindow.dismiss();
                break;

        }
    }




    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_clothes:
                layout_clothes.setVisibility(View.VISIBLE);
                layout_items.setVisibility(View.GONE);
                mItems = 0;
                break;
            case R.id.rb_items:
                layout_clothes.setVisibility(View.GONE);
                layout_items.setVisibility(View.VISIBLE);
                mItems = 1;
                break;
            case R.id.rb_clothing:
                ll_clothingType.setVisibility(View.VISIBLE);
                ll_beddingType.setVisibility(View.GONE);
                clothes = 0;
                break;
            case R.id.rb_bedding:
                ll_clothingType.setVisibility(View.GONE);
                ll_beddingType.setVisibility(View.VISIBLE);
                clothes = 1;
                break;

        }
    }

    private void showPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_add_img,null);
        mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
        mPopWindow.setContentView(contentView);

        TextView pop_btn_catch = (TextView) contentView.findViewById(R.id.pop_btn_catch);
        TextView pop_btn_album = (TextView) contentView.findViewById(R.id.pop_btn_album);
        pop_btn_catch.setOnClickListener(this);
        pop_btn_album.setOnClickListener(this);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_items,null);
        mPopWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);


    }

    // 为多个下拉列表设置适配器
    private void initSpinner() {

        ArrayAdapter<String> thicknessAdapter = new ArrayAdapter<>(this,R.layout.item_select,thicknessArray);
        sp_clothes_thickness.setAdapter(thicknessAdapter);
        sp_clothes_thickness.setSelection(0);
        sp_clothes_thickness.setOnItemSelectedListener(this);

        ArrayAdapter<String> seasonAdapter = new ArrayAdapter<>(this,R.layout.item_select,seasonArray);
        sp_clothes_season.setAdapter(seasonAdapter);
        sp_clothes_season.setSelection(0);
        sp_clothes_season.setOnItemSelectedListener(this);

        ArrayAdapter<String> clothingTypeAdapter = new ArrayAdapter<>(this,R.layout.item_select, clothingTypeArray);
        sp_clothing_type.setAdapter(clothingTypeAdapter);
        sp_clothing_type.setSelection(0);
        sp_clothing_type.setOnItemSelectedListener(this);

        ArrayAdapter<String> beddingTypeAdapter = new ArrayAdapter<>(this,R.layout.item_select, beddingTypeArray);
        sp_bedding_type.setAdapter(beddingTypeAdapter);
        sp_bedding_type.setSelection(0);
        sp_bedding_type.setOnItemSelectedListener(this);

        ArrayAdapter<String> itemsTypeAdapter = new ArrayAdapter<>(this,R.layout.item_select,itemsTypeArray);
        sp_items_type.setAdapter(itemsTypeAdapter);
        sp_items_type.setSelection(0);
        sp_items_type.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDBHelper.closeLink();
    }

    private String getTime() {
        String str;
        Calendar selectedDate = Calendar.getInstance();

        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH) + 1;
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);
        int hour = selectedDate.get(Calendar.HOUR);
        int minute = selectedDate.get(Calendar.MINUTE);
        int second = selectedDate.get(Calendar.SECOND);

        str = String.valueOf(year);
        if(month < 10) { str += "0"; }
        str += String.valueOf(month);
        if(day < 10) { str += "0"; }
        str += String.valueOf(day);
        str += "_";
        if(hour < 10) { str += "0"; }
        str += String.valueOf(hour);
        if(minute < 10) { str += "0"; }
        str += String.valueOf(minute);
        if(second < 10) { str += "0"; }
        str += String.valueOf(second);
        return str;
    }











}
