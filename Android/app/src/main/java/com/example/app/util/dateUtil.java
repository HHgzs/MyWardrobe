package com.example.app.util;

import com.example.app.entity.dateInfo;

public class dateUtil {

    private static int mYear;
    private static int mMonth;
    private static int mDay;
    private static String mDateStr;

    public static String encodeDate(dateInfo info) {

        mYear = info.year;
        mMonth = info.month;
        mDay = info.day;

        if (mMonth + 1 < 10) {
            if (mDay < 10) {
                mDateStr = mYear  + "0" + (mMonth + 1)  + "0" + mDay;
            } else {
                mDateStr = mYear +  "0" + (mMonth + 1) + mDay;
            }
        } else {
            if (mDay < 10) {
                mDateStr = mYear + (mMonth + 1) + "0" + mDay;
            } else {
                mDateStr = String.valueOf(mYear + (mMonth + 1) + mDay);
            }
        }

        return mDateStr;
    }


    public static dateInfo readDate(String str){

        dateInfo dateInfo = new dateInfo();
        int initDate = Integer.parseInt(str);

        mYear = initDate / 10000;
        mMonth = (initDate / 100) % 100;
        mDay = initDate % 100;

        dateInfo.year = mYear;
        dateInfo.month = mMonth;
        dateInfo.day = mDay;

        return dateInfo;
    }


    public static String publishDate(dateInfo info) {
        mYear = info.year;
        mMonth = info.month;
        mDay = info.day;

        if (mMonth < 10) {
            if (mDay < 10) {
                mDateStr = mYear + "-" + "0" + mMonth + "-"  + "0" + mDay;
            } else {
                mDateStr = mYear + "-" +  "0" + mMonth + "-" + mDay;
            }
        } else {
            if (mDay < 10) {
                mDateStr = mYear + "-" + mMonth + "-" + "0" + mDay;
            } else {
                mDateStr = mYear + "-" + mMonth + "-" + mDay;
            }
        }

        return mDateStr;



    }








}
