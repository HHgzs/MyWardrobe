package com.example.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.example.app.util.DataService;

public class ActivityCropper extends AppCompatActivity implements View.OnClickListener{

    public static final int PICTURE_CROPPING_CODE = 200;

    private Bitmap bitmap =null;
    private Bitmap bitmapCropped =null;
    private ImageView iv_picture;
    private Uri uri;
    private boolean cropped = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        findViewById(R.id.btn_crop_save).setOnClickListener(this);
        findViewById(R.id.btn_crop_again).setOnClickListener(this);
        iv_picture = findViewById(R.id.iv_picture);
        DataService instance = DataService.getInstance();

        if (instance.getEditBitmap() != null && cropped == false) {
            bitmap = instance.getEditBitmap();
            uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
        }
        iv_picture.setImageURI(uri);

        if (cropped == false) {
            try {
                cropped = true;
                pictureCropping(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_CROPPING_CODE && resultCode == RESULT_OK) {
            // 图片剪裁返回
            Uri uriCropped = data.getData();
            try {
                // 获得剪裁后的Bitmap对象
                bitmapCropped = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriCropped);
                // 设置到ImageView上
                iv_picture.setImageBitmap(bitmapCropped);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_crop_save:
                DataService instance = DataService.getInstance();
                instance.setEditBitmap(bitmapCropped);
                finish();
                break;

            case R.id.btn_crop_again:
                pictureCropping(uri);
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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        // 返回裁剪后的数据
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICTURE_CROPPING_CODE);
    }

}








