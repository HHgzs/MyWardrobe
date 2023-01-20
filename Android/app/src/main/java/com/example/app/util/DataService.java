package com.example.app.util;

import android.graphics.Bitmap;

public class DataService {
    public  Bitmap editBitmap;

    private static DataService dataService;

    public Bitmap getEditBitmap() {
        return editBitmap;
    }
    public void setEditBitmap(Bitmap editBitmap) {
        this.editBitmap = editBitmap;
    }
    public static DataService getInstance(){
        if (dataService==null){
            synchronized (DataService.class){
                if (dataService==null){
                    dataService=new DataService();
                }
            }
        }
        return dataService;
    }
}
