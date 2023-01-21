package com.example.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.app.entity.clothesInfo;
import com.example.app.entity.itemsInfo;

import java.util.ArrayList;
import java.util.List;

public class ItemsDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "items.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_CLOTHES_INFO = "clothes_info";
    private static final String TABLE_ITEMS_INFO = "items_info";
    private static ItemsDBHelper mHelper = null;
    private SQLiteDatabase mReadDB = null;
    private SQLiteDatabase mWriteDB = null;

    public ItemsDBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
    }


    public static ItemsDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new ItemsDBHelper(context);
        }
        return mHelper;
    }

    public SQLiteDatabase openReadLink() {
        if (mReadDB == null || !mReadDB.isOpen()) {
            mReadDB = mHelper.getReadableDatabase();
        }
        return mReadDB;
    }

    public SQLiteDatabase openWriteLink() {
        if (mWriteDB == null || !mWriteDB.isOpen()) {
            mWriteDB = mHelper.getWritableDatabase();
        }
        return mWriteDB;
    }

    public void closeLink() {

        if (mReadDB != null && mReadDB.isOpen()) {
            mReadDB.close();
            mReadDB = null;
        }

        if (mWriteDB != null && mWriteDB.isOpen()) {
            mWriteDB.close();
            mWriteDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CLOTHES_INFO +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " basic_type INTEGER NOT NULL," +
                " second_type INTEGER NOT NULL," +
                " thickness INTEGER NOT NULL," +
                " season INTEGER NOT NULL," +
                " brief VARCHAR NOT NULL," +
                " img_path VARCHAR NOT NULL," +
                " status INTEGER NOT NULL);";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS_INFO +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " name VARCHAR NOT NULL," +
                " type INTEGER NOT NULL," +
                " brief VARCHAR NOT NULL," +
                " img_path VARCHAR NOT NULL," +
                " status INTEGER NOT NULL);";
        db.execSQL(sql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    // 添加衣物信息
    public void insertClothesInfo(clothesInfo info) {
        try {
            mWriteDB.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("name",info.name);
            values.put("basic_type",info.basicType);
            values.put("second_type",info.secondType);
            values.put("thickness",info.thickness);
            values.put("season",info.season);
            values.put("brief",info.brief);
            values.put("img_path",info.imgPath);
            values.put("status",info.status);
            mWriteDB.insert(TABLE_CLOTHES_INFO,null,values);
            mWriteDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWriteDB.endTransaction();
        }
    }


    public void insertItemsInfo(itemsInfo info) {
        try {
            mWriteDB.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("name",info.name);
            values.put("type",info.type);
            values.put("brief",info.brief);
            values.put("img_path",info.imgPath);
            values.put("status",info.status);
            mWriteDB.insert(TABLE_ITEMS_INFO,null,values);
            mWriteDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWriteDB.endTransaction();
        }
    }


    public List<clothesInfo> queryAllClothesInfo() {
        String sql = "select * from " + TABLE_CLOTHES_INFO;
        List<clothesInfo> list = new ArrayList<>();
        Cursor cursor = mReadDB.rawQuery(sql,null);
        while (cursor.moveToNext()) {
            clothesInfo info = new clothesInfo();
            info.id = cursor.getInt(0);
            info.name = cursor.getString(1);
            info.basicType = cursor.getInt(2);
            info.secondType = cursor.getInt(3);
            info.thickness = cursor.getInt(4);
            info.season = cursor.getInt(5);
            info.brief = cursor.getString(6);
            info.imgPath = cursor.getString(7);
            info.status = cursor.getInt(8);
            list.add(info);
        }
        cursor.close();
        return list;
    }


    public List<itemsInfo> queryAllItemsInfo() {
        String sql = "select * from " + TABLE_ITEMS_INFO;
        List<itemsInfo> list = new ArrayList<>();
        Cursor cursor = mReadDB.rawQuery(sql,null);
        while (cursor.moveToNext()) {
            itemsInfo info = new itemsInfo();
            info.id = cursor.getInt(0);
            info.name = cursor.getString(1);
            info.type = cursor.getInt(2);
            info.brief = cursor.getString(3);
            info.imgPath = cursor.getString(4);
            info.status = cursor.getInt(5);
            list.add(info);
        }
        cursor.close();
        return list;
    }


    public clothesInfo queryClothesInfoByID(int clothesID) {
        clothesInfo info = null;
        Cursor cursor = mReadDB.query(TABLE_CLOTHES_INFO,null,"_id=?",new String[]{String.valueOf(clothesID)},null,null,null);
        if (cursor.moveToNext()) {
            info = new clothesInfo();
            info.id = cursor.getInt(0);
            info.name = cursor.getString(1);
            info.basicType = cursor.getInt(2);
            info.secondType = cursor.getInt(3);
            info.thickness = cursor.getInt(4);
            info.season = cursor.getInt(5);
            info.brief = cursor.getString(6);
            info.imgPath = cursor.getString(7);
            info.status = cursor.getInt(8);
        }
        return info;
    }


    public itemsInfo queryItemsInfoByID(int itemsID) {
        itemsInfo info = null;
        Cursor cursor = mReadDB.query(TABLE_ITEMS_INFO,null,"_id=?",new String[]{String.valueOf(itemsID)},null,null,null);
        if (cursor.moveToNext()) {
            info.id = cursor.getInt(0);
            info.name = cursor.getString(1);
            info.type = cursor.getInt(2);
            info.brief = cursor.getString(3);
            info.imgPath = cursor.getString(4);
            info.status = cursor.getInt(5);
        }
        return info;
    }


    public void reviseClothesInfo(clothesInfo info) {
        try {
            mWriteDB.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("name",info.name);
            values.put("basic_type",info.basicType);
            values.put("second_type",info.secondType);
            values.put("thickness",info.thickness);
            values.put("season",info.season);
            values.put("brief",info.brief);
            values.put("img_path",info.imgPath);
            values.put("status",info.status);
            mWriteDB.update(TABLE_CLOTHES_INFO,values,"_id=?",new String[]{String.valueOf(info.id)});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reviseItemsInfo(itemsInfo info) {
        try {
            mWriteDB.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("name",info.name);
            values.put("type",info.type);
            values.put("brief",info.brief);
            values.put("img_path",info.imgPath);
            values.put("status",info.status);
            mWriteDB.update(TABLE_ITEMS_INFO,values,"_id=?",new String[]{String.valueOf(info.id)});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteAllInfo() {
        mWriteDB.delete(TABLE_CLOTHES_INFO,"1=1",null);
        mWriteDB.delete(TABLE_ITEMS_INFO,"1=1",null);
    }

    public void dataInit() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CLOTHES_INFO);
        db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = " + TABLE_CLOTHES_INFO);

        db.execSQL("delete from " + TABLE_CLOTHES_INFO);
        db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = " + TABLE_CLOTHES_INFO);
    }


}
