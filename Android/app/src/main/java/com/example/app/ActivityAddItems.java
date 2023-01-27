package com.example.app;

import static android.os.Environment.DIRECTORY_DCIM;
import static android.os.Environment.DIRECTORY_PICTURES;
import static com.example.app.entity.staticData.IN_STORE;
import static com.example.app.util.ToastUtil.show;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.itemsInfo;
import com.example.app.entity.staticData;
import com.example.app.util.DataService;
import com.example.app.util.FileUtil;
import com.example.app.util.pixUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class ActivityAddItems extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {

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

    // 物品首选分类分成衣橱内物品和其他物品，衣橱内用 0 表示，其他用 1 表示
    // clothing代表衣物，bedding代表床单被褥，clothes是上述两者总称呼
    // clothing用 0 表示，bedding用 1 表示
    private int mItems = 0;
    private int clothes = 0;

    // 此处为spinner下拉菜单的适配器数组
    private static final String[] clothingTypeArray = staticData.clothingTypeArray;
    private static final String[] beddingTypeArray = staticData.beddingTypeArray;
    private static final String[] itemsTypeArray = staticData.itemsTypeArray;
    private static final String[] thicknessArray = staticData.thicknessArray;
    private static final String[] seasonArray = staticData.seasonArray;


    private final int ALBUM_REQUEST_CODE = 1;
    private final int CROP_REQUEST_CODE = 2;
    private final int CAMERA_REQUEST_CODE = 3;

    private static String namePath = staticData.EMPTY;
    private static Uri mUri = null;
    private ActivityResultLauncher<Intent> register;
    private File tempFile;
    private Uri fileUri;

    private clothesInfo mClothesInfo;
    private itemsInfo mItemsInfo;
    private ItemsDBHelper mDBHelper;
    private Bitmap bitmap = null;
    private Bitmap bitmapCropped = null;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_items_2);

        RadioGroup rg_clothes_or_items = findViewById(R.id.rg_clothes_or_items);
        rg_clothes_or_items.setOnCheckedChangeListener(this);

        RadioGroup rg_clothes = findViewById(R.id.rg_type);
        rg_clothes.setOnCheckedChangeListener(this);

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
        iv_img_show.setImageResource(R.drawable.img_null);
        iv_img_show.setOnClickListener(this);


        // 为图片添加按钮设置点击事件监听，弹出拍照或从相册选择按钮
        btn_add_img = findViewById(R.id.btn_add_img);
        btn_add_img.setOnClickListener(v -> showPopupWindow());

        btn_crop_img = findViewById(R.id.btn_crop_img);
        btn_crop_img.setOnClickListener(this);

        findViewById(R.id.btn_save).setOnClickListener(this);
        findViewById(R.id.btn_exit).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_test).setOnClickListener(this);

        // 创建数据对象
        mClothesInfo = new clothesInfo();
        mItemsInfo = new itemsInfo();

        mDBHelper = ItemsDBHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();

        DataService instance = DataService.getInstance();
        instance.initData();

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
                iv_img_show.setImageBitmap(bitmapCropped);
            }
        }

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    Uri contentUri = FileProvider.getUriForFile(this, "com.choosecrop.fileprovider", tempFile);

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    iv_img_show.setImageBitmap(bitmap);

                    instance.setEditBitmap(bitmap);
                    Intent intent_crop = new Intent(this,ActivityCropper.class);
                    startActivityForResult(intent_crop,CROP_REQUEST_CODE);

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
                } else {
                    namePath = staticData.EMPTY;
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
                    mClothesInfo.imgPath = namePath;
                    mClothesInfo.status = IN_STORE;

                    // 向clothes数据库插入项
                    mDBHelper.insertClothesInfo(mClothesInfo);

                } else if (mItems == 1) {
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

            case R.id.btn_test:

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
    }



    // 显示下方弹出列表
    private void showPopupWindow() {

        // 动画初始化
        @SuppressLint("ResourceType") Animator animator = AnimatorInflater.loadAnimator(this, R.anim.pop_up);
        animator.setInterpolator(new OvershootInterpolator());


        int height = pixUtil.dip2px(this, 150);

        @SuppressLint("InflateParams") View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_add_img,null);
        mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, height,true);
        mPopWindow.setContentView(contentView);

        // 设置动画
        animator.setTarget(contentView);

        TextView pop_btn_catch = contentView.findViewById(R.id.pop_btn_catch);
        TextView pop_btn_album = contentView.findViewById(R.id.pop_btn_album);
        pop_btn_catch.setOnClickListener(this);
        pop_btn_album.setOnClickListener(this);
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(this).inflate(R.layout.activity_add_items,null);
        mPopWindow.showAtLocation(rootView, Gravity.BOTTOM,0,0);

        // 启用动画
        animator.start();

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
//        mDBHelper.closeLink();
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


//    public String getCachePath(Context context) {
//        String cachePath;
//        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
//            // 外部存储可用
//            cachePath = context.getExternalCacheDir().getPath() ;
//        } else {
//            //外部存储不可用
//            cachePath = context.getCacheDir().getPath() ;
//        }
//        return cachePath ;
//    }



//    @Nullable
//    File getAppSpecificAlbumStorageDir(Context context, String albumName) {
//        // Get the pictures directory that's inside the app-specific directory on
//        // external storage.
//        File file = new File(context.getExternalFilesDir(
//                Environment.DIRECTORY_PICTURES), albumName);
//        if (file == null || !file.mkdirs()) {
//            Log.e("HH", "Directory not created");
//        }
//
//        return file;
//    }





    @RequiresApi(api = Build.VERSION_CODES.Q)
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
            e.printStackTrace();
        } finally {
            //将保存的uri转存为String，保存为path
            namePath = mImageFileName;
        }

    }



    public static String getRealFilePath( final Context context,final Uri uri ) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;

        if (scheme == null)
            data = uri.getPath();

        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme) ) {
            Cursor cursor = context.getContentResolver().query(uri,new String[] { MediaStore.Images.ImageColumns.DATA },null,null );

            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;

    }






    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static void findImageByName(Context context, String name) {

        Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Images.Media.DISPLAY_NAME + " = ?";

        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(externalContentUri,null,selection,new String[]{name},null);

        if (cursor != null && cursor.moveToFirst()) {

            // 获取 _id 所在列的索引
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));

            // 获取 relative_path 所在列的索引
            String relativePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH));

            // 获取 _display_name 所在列的索引
            String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));

            // 绝对路径
            String absolutePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

            // 通过 _id 字段获取图片 Uri
            Uri uri = ContentUris.withAppendedId(externalContentUri, id);

            Log.i("HH", "查询到的 Uri = " + uri + " , 路径 = " + absolutePath);

            // 关闭游标
            cursor.close();

        }

    }



    private void getImgFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File CAMERA_CATCH_FILE = new File(Environment.getExternalStorageState());
        File tempFile = new File(CAMERA_CATCH_FILE, "catch" + System.currentTimeMillis() + ".png");
        fileUri = Uri.fromFile(tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);

    }


}
