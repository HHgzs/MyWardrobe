package com.example.app;

import static android.os.Environment.DIRECTORY_DCIM;
import static com.example.app.util.ToastUtil.show;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.app.util.DataService;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ActivityCropper extends AppCompatActivity implements View.OnClickListener{

    public static final int PICTURE_CROPPING_CODE = 200;
    private static final int CROP_REQUEST_CODE = 300;

    private Bitmap bitmap = null;
    private Bitmap bitmapCropped = null;
    private ImageView iv_picture;
    private Uri uri;
    private Uri fileUri;
    private boolean cropped = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        findViewById(R.id.btn_crop_save).setOnClickListener(this);
        findViewById(R.id.btn_crop_again).setOnClickListener(this);
        iv_picture = findViewById(R.id.iv_picture);
        DataService instance = DataService.getInstance();

        if (instance.getEditBitmap() != null && !cropped) {
            bitmap = instance.getEditBitmap();
            iv_picture.setImageBitmap(bitmap);

        }


        if (!cropped) {
            try {
                cropped = true;
                uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                pictureCropping(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICTURE_CROPPING_CODE:
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            bitmapCropped = MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
                            iv_picture.setImageBitmap(bitmapCropped);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },500);




        }


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crop_save:
                DataService instance = DataService.getInstance();
                if (bitmapCropped != null) {
                    instance.setEditBitmap(bitmapCropped);
                    finish();
                } else {
                    show(this,"请进行裁切");
                }
                break;

            case R.id.btn_crop_again:
                try {
                    cropped = true;
                    uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
                    pictureCropping(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }



    // 图片裁切
    private void pictureCropping(Uri uri) {
        // 调用系统中自带的图片剪裁
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 500);
        intent.putExtra("aspectY", 501);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 501);

        // 返回裁剪后的数据
        intent.putExtra("return-data", false);

        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);

        File CROP_TEMP_FILE = Environment.getExternalStorageDirectory();
        File mFullPath = new File(CROP_TEMP_FILE, "unheadimage" + System.currentTimeMillis() + ".png");
        fileUri = Uri.fromFile(mFullPath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, PICTURE_CROPPING_CODE);

    }


}








