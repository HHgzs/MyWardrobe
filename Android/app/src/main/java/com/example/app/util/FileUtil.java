package com.example.app.util;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

public class FileUtil {
    public static void saveImageToGallery(Context context, Bitmap image) {

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


//        String path = getRealFilePath(context, uri);


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

        } catch (IOException e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                resolver.delete(uri, null);
            }
            e.printStackTrace();
        }
//        return path;
    }



    public static void saveImage(String path, Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            // 把位图数据压缩到文件输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Bitmap openImage(String path) {
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
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



}
