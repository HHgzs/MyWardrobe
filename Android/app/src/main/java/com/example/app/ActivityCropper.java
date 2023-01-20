package com.example.app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    Bitmap bitmap =null;
    private Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);

        DataService instance = DataService.getInstance();

        if (instance.getEditBitmap()!=null){
            bitmap = instance.getEditBitmap();
        }
        uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));

        ImageView iv_picture = findViewById(R.id.iv_picture);
        iv_picture.setImageURI(uri);




    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_cropped:
                DataService instance = DataService.getInstance();
                instance.setEditBitmap(bitmap);
                finish();

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








