package com.example.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.app.entity.blotterInfo;
import com.example.app.entity.noteInfo;

import java.util.ArrayList;
import java.util.List;

public class BlotterDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "blotter.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_MASTER = "blotter_master";
    private static final String TABLE_NOTE = "note_info";
    private static BlotterDBHelper mHelper = null;
    private SQLiteDatabase mReadDB = null;
    private SQLiteDatabase mWriteDB = null;


    public BlotterDBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);

    }


    public static BlotterDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new BlotterDBHelper(context);
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
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_MASTER +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " table_name VARCHAR NOT NULL," +
                " publish_name VARCHAR NOT NULL," +
                " brief VARCHAR NOT NULL," +
                " createdTime VARCHAR NOT NULL," +
                " applyTime VARCHAR NOT NULL);";
        db.execSQL(sql);

    }



    public void createNoteTable(String table_name) {
        String sql = "CREATE TABLE IF NOT EXISTS " + table_name +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " type VARCHAR NOT NULL," +
                " items_id VARCHAR NOT NULL," +
                " number VARCHAR NOT NULL);";
        mWriteDB.execSQL(sql);
    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    // 添加总表信息
    public void insertBlotterInfo(blotterInfo info) {
        try {
            mWriteDB.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("table_name",info.table_name);
            values.put("publish_name",info.publish_name);
            values.put("brief",info.brief);
            values.put("createdTime",info.createdTime);
            values.put("applyTime",info.applyTime);

            mWriteDB.insert(TABLE_MASTER,null,values);
            mWriteDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWriteDB.endTransaction();
        }
    }


    public void insertNoteInfo(List<noteInfo> infoList, String table_name) {
        noteInfo info;
        for (int i = 0; i < infoList.size(); i++) {
            info = infoList.get(i);
            try {
                mWriteDB.beginTransaction();
                ContentValues values = new ContentValues();
                values.put("type", info.type);
                values.put("items_id", info.items_id);
                values.put("number", info.number);
                mWriteDB.insert(table_name, null, values);
                mWriteDB.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mWriteDB.endTransaction();
            }
        }
    }


    public List<blotterInfo> queryAllBlottersInfo() {
        String sql = "select * from " + TABLE_MASTER;
        List<blotterInfo> list = new ArrayList<>();
        Cursor cursor = mReadDB.rawQuery(sql,null);
        while (cursor.moveToNext()) {
            blotterInfo info = new blotterInfo();
            info.id = cursor.getInt(0);
            info.table_name = cursor.getString(1);
            info.publish_name = cursor.getString(2);
            info.brief = cursor.getString(3);
            info.createdTime = cursor.getString(4);
            info.applyTime = cursor.getString(5);
            list.add(info);
        }
        cursor.close();
        return list;
    }


    public List<noteInfo> queryAllNotesInfo(String table_name) {
        String sql = "select * from " + table_name;
        List<noteInfo> list = new ArrayList<>();
        Cursor cursor = mReadDB.rawQuery(sql,null);
        while (cursor.moveToNext()) {
            noteInfo info = new noteInfo();
            info.id = cursor.getInt(0);
            info.type = cursor.getInt(1);
            info.items_id = cursor.getInt(2);
            info.number = cursor.getInt(3);
            list.add(info);
        }
        cursor.close();
        return list;
    }


    public blotterInfo queryBlotterInfoByID(int blotterID) {
        blotterInfo info = null;
        Cursor cursor = mReadDB.query(TABLE_MASTER,null,"_id=?",new String[]{String.valueOf(blotterID)},null,null,null);
        if (cursor.moveToNext()) {
            info = new blotterInfo();
            info.id = cursor.getInt(0);
            info.table_name = cursor.getString(1);
            info.publish_name = cursor.getString(2);
            info.brief = cursor.getString(3);
            info.createdTime = cursor.getString(4);
            info.applyTime = cursor.getString(5);
        }
        return info;
    }




    public void reviseBlotterInfo(blotterInfo info) {
        try {
            ContentValues values = new ContentValues();
            values.put("_id",info.id);
            values.put("table_name",info.table_name);
            values.put("publish_name",info.publish_name);
            values.put("brief",info.brief);
            values.put("createdTime",info.createdTime);
            values.put("applyTime",info.applyTime);

            mWriteDB.update(TABLE_MASTER,values,"_id=?",new String[]{String.valueOf(info.id)});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reviseNoteInfo(noteInfo info,String table_name) {
        try {

            ContentValues values = new ContentValues();
            values.put("_id",info.id);
            values.put("type",info.type);
            values.put("items_id",info.items_id);
            values.put("number",info.number);

            mWriteDB.update(table_name,values,"_id=?",new String[]{String.valueOf(info.id)});

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteBlotterInfo(long id) {
        try {
            mWriteDB.delete(TABLE_MASTER,"_id=?",new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void deleteNoteInfo(long id,String table_name) {
        try {
            mWriteDB.delete(table_name,"_id=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteTableInfo(String name) {
        String sql = "DROP TABLE IF EXISTS " + name;
        mWriteDB.execSQL(sql);

    }






}
