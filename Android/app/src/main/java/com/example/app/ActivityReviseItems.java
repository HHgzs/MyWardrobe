package com.example.app;

import static com.example.app.util.ToastUtil.show;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.itemsInfo;
import com.example.app.entity.staticData;
import com.example.app.util.DataService;
import com.example.app.util.FileUtil;

import java.util.Objects;


public class ActivityReviseItems extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

    private RadioButton rb_clothing;
    private RadioButton rb_bedding;
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
    private EditText et_items_num;
    private ImageView iv_img_show;
    private ImageView btn_add_img;
    private ImageView btn_crop_img;
    private TextView tv_revise_title;

    // 物品首选分类分成衣橱内物品和其他物品，衣橱内用 0 表示，其他用 1 表示
    // clothing代表衣物，bedding代表床单被褥，clothes是上述两者总称呼
    // clothing用 0 表示，bedding用 1 表示
    private int mItems;
    private int clothes = staticData.TYPE_CLOTHING;
    // 传入id，作为保存依据
    private int id;

    // 此处为spinner下拉菜单的适配器数组
    private static final String[] clothingTypeArray = staticData.clothingTypeArray;
    private static final String[] beddingTypeArray = staticData.beddingTypeArray;
    private static final String[] itemsTypeArray = staticData.itemsTypeArray;
    private static final String[] thicknessArray = staticData.thicknessArray;
    private static final String[] seasonArray = staticData.seasonArray;


    private final int ALBUM_REQUEST_CODE = 1;
    private final int CROP_REQUEST_CODE = 2;

    private static String namePath = staticData.EMPTY;

    private clothesInfo mClothesInfo;
    private itemsInfo mItemsInfo;
    private ItemsDBHelper mDBHelper;
    private Bitmap bitmap = null;
    private Bitmap bitmapCropped = null;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_items_2);

        RadioGroup rg_clothes = findViewById(R.id.rg_type);
        rg_clothes.setOnCheckedChangeListener(this);
        rb_clothing = findViewById(R.id.rb_clothing);
        rb_bedding = findViewById(R.id.rb_bedding);

        layout_clothes = findViewById(R.id.layout_clothes);
        layout_items = findViewById(R.id.layout_items);
        ll_clothingType = findViewById(R.id.ll_clothingType);
        ll_beddingType = findViewById(R.id.ll_beddingType);

        sp_clothing_type = findViewById(R.id.sp_clothing_type);
        sp_bedding_type = findViewById(R.id.sp_bedding_type);
        sp_clothes_thickness = findViewById(R.id.sp_clothes_thickness);
        sp_clothes_season = findViewById(R.id.sp_clothes_season);
        sp_items_type = findViewById(R.id.sp_items_type);

        et_name = findViewById(R.id.et_name);
        et_brief = findViewById(R.id.et_brief);
        et_items_brief = findViewById(R.id.et_items_brief);
        et_items_num = findViewById(R.id.et_items_num);
        iv_img_show = findViewById(R.id.iv_img_show);
        tv_revise_title = findViewById(R.id.tv_revise_title);

        // 为图片添加按钮设置点击事件监听，弹出拍照或从相册选择按钮
        btn_add_img = findViewById(R.id.btn_add_img);
        btn_add_img.setOnClickListener(v -> showPopupWindow());

        btn_crop_img = findViewById(R.id.btn_crop_img);
        btn_crop_img.setOnClickListener(this);
        iv_img_show.setOnClickListener(this);

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);

        mDBHelper = ItemsDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();

        // 接收数据，设置id，设置初始数据
        Bundle bundle = getIntent().getExtras();
        mItems = bundle.getInt("mItems");
        id = bundle.getInt("id");

        setType();
        // 初始化下拉列表
        initSpinner();
        // 初始化数据
        setData();

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DataService instance = DataService.getInstance();
        // 接收打开相册选择的照片，
        if (requestCode == ALBUM_REQUEST_CODE) {
            if (data != null) {

                Uri uri = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iv_img_show.setImageBitmap(bitmap);
                instance.setEditBitmap(bitmap);
                Intent intent_crop = new Intent(this,ActivityCropper.class);
                startActivityForResult(intent_crop,CROP_REQUEST_CODE);

            }
        }

        //  接收裁剪后的数据，存入全局变量 bitmap
        if (requestCode == CROP_REQUEST_CODE) {
            if (instance.getEditBitmap() != null){
                bitmapCropped = instance.getEditBitmap();
                iv_img_show.setImageBitmap(bitmapCropped);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (et_name.length() == 0) {
                    show(this,"请输入物品名称");
                    break;
                }

                // 保存已裁切图片
                if (bitmapCropped != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        namePath = FileUtil.saveImageAndGetName(this,bitmapCropped);
                    }
                } else if (bitmap != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        namePath = FileUtil.saveImageAndGetName(this,bitmap);
                    }
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
                    mClothesInfo.status = staticData.IN_STORE;
                    mClothesInfo.imgPath = namePath;

                    // 向clothes数据库插入项
                    mDBHelper.reviseClothesInfo(mClothesInfo);

                } else if (mItems == staticData.TYPE_ITEMS) {

                    mItemsInfo.name = et_name.getText().toString();
                    mItemsInfo.type = sp_items_type.getSelectedItemPosition();
                    mItemsInfo.brief = et_items_brief.getText().toString();
                    mItemsInfo.imgPath = namePath;

                    if (Integer.parseInt(et_items_num.getText().toString()) >= 0) {
                        mItemsInfo.status = Integer.parseInt(et_items_num.getText().toString());

                    } else {
                        mItemsInfo.status = staticData.IN_STORE;
                    }

                    // 向clothes数据库插入项
                    mDBHelper.reviseItemsInfo(mItemsInfo);
                }

                finish();
                break;

            case R.id.btn_exit:
                finish();
                break;


            case R.id.btn_crop_img:
                DataService instance = DataService.getInstance();
                instance.setEditBitmap(bitmap);
                Intent intent_crop = new Intent(this,ActivityCropper.class);
                startActivityForResult(intent_crop,CROP_REQUEST_CODE);
                break;


            case R.id.pop_btn_catch:
                Intent intent_catch = new Intent();
                intent_catch.setAction("android.media.action.STILL_IMAGE_CAMERA");
                startActivity(intent_catch);
                mPopWindow.dismiss();
                break;

            case R.id.pop_btn_album:

                Intent intent_album = new Intent(Intent.ACTION_PICK, null);
                intent_album.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent_album, ALBUM_REQUEST_CODE);
                mPopWindow.dismiss();
                break;

            case R.id.iv_img_show:
                DataService instance_2 = DataService.getInstance();
                Intent intent = new Intent(this,ActivityShowPicture.class);

                if (bitmapCropped != null) {
                    instance_2.setEditBitmap(bitmapCropped);
                    startActivity(intent);

                } else if (bitmap != null) {
                    instance_2.setEditBitmap(bitmap);
                    startActivity(intent);

                } else {
                    Intent intent_album_2 = new Intent(Intent.ACTION_PICK, null);
                    intent_album_2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent_album_2, ALBUM_REQUEST_CODE);
                }
                break;

        }
    }




    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

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


    // 显示下方弹出列表
    private void showPopupWindow() {
        @SuppressLint("InflateParams") View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_add_img,null);
        mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);
        mPopWindow.setContentView(contentView);

        TextView pop_btn_catch = (TextView) contentView.findViewById(R.id.pop_btn_catch);
        TextView pop_btn_album = (TextView) contentView.findViewById(R.id.pop_btn_album);
        pop_btn_catch.setOnClickListener(this);
        pop_btn_album.setOnClickListener(this);
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_items,null);
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


    // 根据上级Activity设置类型clothes或items,以及对象的基本数据，文字信息
    private void setType() {
        if (mItems == staticData.TYPE_CLOTHES) {

            tv_revise_title.setText("衣物信息");

            layout_clothes.setVisibility(View.VISIBLE);
            layout_items.setVisibility(View.GONE);

            mClothesInfo =  mDBHelper.queryClothesInfoByID(id);

            clothes = mClothesInfo.basicType;
            namePath = mClothesInfo.imgPath;

            et_name.setText(mClothesInfo.name);
            et_brief.setText(mClothesInfo.brief);

            if (!Objects.equals(mClothesInfo.imgPath, staticData.EMPTY)) {

                // 根据imgPath读取图像为uri并显示出来
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                    uri = FileUtil.findImageByName(MyApplication.getContext(),namePath);
                    iv_img_show.setImageURI(uri);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                iv_img_show.setImageResource(R.drawable.img_null);
            }



        } else if (mItems == staticData.TYPE_ITEMS) {

            tv_revise_title.setText("物品信息");

            layout_clothes.setVisibility(View.GONE);
            layout_items.setVisibility(View.VISIBLE);

            mItemsInfo = mDBHelper.queryItemsInfoByID(id);
            namePath = mItemsInfo.imgPath;

            et_name.setText(mItemsInfo.name);
            et_items_brief.setText(mItemsInfo.brief);
            et_items_num.setText(String.valueOf(mItemsInfo.status));

            if (!Objects.equals(mItemsInfo.imgPath, staticData.EMPTY)) {

                // 根据imgPath读取图像为uri并显示出来
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                    uri = FileUtil.findImageByName(MyApplication.getContext(),namePath);
                    iv_img_show.setImageURI(uri);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                iv_img_show.setImageResource(R.drawable.img_null);
            }
        }
    }

    // 根据setType中，由basicType设置的clothes，初始化下拉列表和单选按钮等信息
    private void setData() {
        if (mItems == staticData.TYPE_CLOTHES) {
            if (clothes == staticData.TYPE_CLOTHING) {

                rb_clothing.setChecked(true);
                rb_bedding.setChecked(false);
                sp_clothing_type.setSelection(mClothesInfo.secondType);

            } else if (clothes == staticData.TYPE_BEDDING) {

                rb_clothing.setChecked(false);
                rb_bedding.setChecked(true);
                sp_bedding_type.setSelection(mClothesInfo.secondType);
            }

            sp_clothes_thickness.setSelection(mClothesInfo.thickness);
            sp_clothes_season.setSelection(mClothesInfo.season);


        } else if (mItems == staticData.TYPE_ITEMS) {

            sp_items_type.setSelection(mItemsInfo.type);
        }
    }
}
