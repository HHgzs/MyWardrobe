package com.example.app;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static androidx.core.content.PackageManagerCompat.LOG_TAG;
import static com.example.app.util.ToastUtil.show;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.text.format.DateUtils;
import android.util.Log;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.itemsInfo;
import com.example.app.entity.staticData;
import com.example.app.util.DataService;
import com.example.app.util.FileUtil;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import kotlin.jvm.internal.PropertyReference0Impl;


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
    private ImageView iv_img_show;
    private ImageView iv_img_show_2;

    // 物品首选分类分成衣橱内物品和其他物品，衣橱内用 0 表示，其他用 1 表示
    // clothing代表衣物，bedding代表床单被褥，clothes是上述两者总称呼
    // clothing用 0 表示，bedding用 1 表示
    private int mItems = 0;
    private int clothes = 0;

    // 此处为spinner下拉菜单的适配器数组
    private static String[] clothingTypeArray = staticData.clothingTypeArray;
    private static String[] beddingTypeArray = staticData.beddingTypeArray;
    private static String[] itemsTypeArray = staticData.itemsTypeArray;
    private static String[] thicknessArray = staticData.thicknessArray;
    private static String[] seasonArray = staticData.seasonArray;


    private final int ALBUM_REQUEST_CODE = 1;
    private final int CROP_REQUEST_CODE = 2;
    private final int OUT_STORE = 0;
    private final int IN_STORE = 1;

    private static String path = null;
    private static Uri mUri;
    private ActivityResultLauncher<Intent> registerC;

    private clothesInfo mClothesInfo;
    private itemsInfo mItemsInfo;
    private ItemsDBHelper mDBHelper;
    private Bitmap bitmap = null;
    private Bitmap bitmapCropped = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items);

        rg_clothes_or_items = findViewById(R.id.rg_clothes_or_items);
        rg_clothes_or_items.setOnCheckedChangeListener(this);

        rg_clothes = findViewById(R.id.rg_type);
        rg_clothes.setOnCheckedChangeListener(this);

        layout_clothes = (LinearLayout) findViewById(R.id.layout_clothes);
        layout_items = (LinearLayout) findViewById(R.id.layout_items);
        ll_clothingType = (LinearLayout) findViewById(R.id.ll_clothingType);
        ll_beddingType = (LinearLayout) findViewById(R.id.ll_beddingType);

        sp_clothing_type = findViewById(R.id.sp_clothing_type);
        sp_bedding_type = findViewById(R.id.sp_bedding_type);
        sp_clothes_thickness = findViewById(R.id.sp_clothes_thickness);
        sp_clothes_season = findViewById(R.id.sp_clothes_season);
        sp_items_type = findViewById(R.id.sp_items_type);

        et_name = findViewById(R.id.et_name);
        et_brief = findViewById(R.id.et_brief);
        et_items_brief = findViewById(R.id.et_items_brief);
        iv_img_show = findViewById(R.id.iv_img_show);
        iv_img_show_2 = findViewById(R.id.iv_img_show_2);

        // 为图片添加按钮设置点击事件监听，弹出拍照或从相册选择按钮
        Button btn_add_img = (Button) findViewById(R.id.btn_add_img);
        btn_add_img.setOnClickListener(v -> showPopupWindow());

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_crop_img).setOnClickListener(this);

        // 创建数据对象
        mClothesInfo = new clothesInfo();
        mItemsInfo = new itemsInfo();

        mDBHelper = ItemsDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();
        // 初始化下拉列表
        initSpinner();
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
                iv_img_show_2.setImageBitmap(bitmapCropped);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                if (et_name.length() == 0) {
                    show(this,"请输入物品名称");
                    break;
                }

                // 保存已裁切图片
                saveImageToGallery(this, bitmapCropped);

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
                    mClothesInfo.imgPath = path;
                    mClothesInfo.status = IN_STORE;

                    // 向clothes数据库插入项
                    mDBHelper.insertClothesInfo(mClothesInfo);

                } else if (mItems == 1) {
                    mItemsInfo.name = et_name.getText().toString();
                    mItemsInfo.type = sp_items_type.getSelectedItemPosition();
                    mItemsInfo.brief = et_items_brief.getText().toString();
                    mItemsInfo.imgPath = path;
                    mItemsInfo.status = IN_STORE;

                    // 向clothes数据库插入项
                    mDBHelper.insertItemsInfo(mItemsInfo);
                }

                finish();
                break;

            case R.id.btn_exit:
                finish();
                break;

            case R.id.btn_delete:
                mDBHelper.dataInit();
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HH","Activity_Add_Item onResume");
//        DataService instance = DataService.getInstance();
//        if (instance.getEditBitmap() != null) {
//            iv_img_show_2.setImageBitmap(instance.getEditBitmap());
//        }
    }

    // 显示下方弹出列表
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

    private static String getTime() {
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
        /* str += "_"; */
        if(hour < 10) { str += "0"; }
        str += String.valueOf(hour);
        if(minute < 10) { str += "0"; }
        str += String.valueOf(minute);
        if(second < 10) { str += "0"; }
        str += String.valueOf(second);
        return str;
    }


    public String getCachePath(Context context) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            // 外部存储可用
            cachePath = context.getExternalCacheDir().getPath() ;
        } else {
            //外部存储不可用
            cachePath = context.getCacheDir().getPath() ;
        }
        return cachePath ;
    }



    @Nullable
    File getAppSpecificAlbumStorageDir(Context context, String albumName) {
        // Get the pictures directory that's inside the app-specific directory on
        // external storage.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (file == null || !file.mkdirs()) {
            Log.e("HH", "Directory not created");
        }

        return file;
    }





    public static void saveImageToGallery(Context context, Bitmap image){


        long mImageTime = System.currentTimeMillis();
        String imageDate = getTime();
        String PICTURE_SAVE_NAME_TEMPLATE = "img_%s.png";
        String mImageFileName = String.format(PICTURE_SAVE_NAME_TEMPLATE, imageDate);

        final ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, DIRECTORY_PICTURES + File.separator + "MyWardrobe");
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, mImageFileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        values.put(MediaStore.MediaColumns.DATE_ADDED, mImageTime / 1000);
        values.put(MediaStore.MediaColumns.DATE_MODIFIED, mImageTime / 1000);
        values.put(MediaStore.MediaColumns.DATE_EXPIRES, (mImageTime + DateUtils.DAY_IN_MILLIS) / 1000);
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);
        ContentResolver resolver = context.getContentResolver();

        final Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // uri提取，保存到静态
        Log.d("HH", String.valueOf(uri));
        mUri = uri;

        try {
            // First, write the actual data for our screenshot
            try (OutputStream out = resolver.openOutputStream(uri)) {
                if (!image.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                    throw new IOException("Failed to compress");
                }
            }
            // Everything went well above, publish it!
            values.clear();
            values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            values.putNull(MediaStore.MediaColumns.DATE_EXPIRES);
            resolver.update(uri, values, null, null);

        } catch (IOException e){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                resolver.delete(uri, null);
            }
            /* G.look("Exception:"+e.toString()); */
            e.printStackTrace();
        } finally {
            //将保存的uri转存为String，保存为path
            path = String.valueOf(mUri);
        }

    }




}
