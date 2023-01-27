package com.example.app.entity;

import com.example.app.util.dateUtil;

import java.util.Calendar;

public class dateInfo {
    public int year;
    public int month;
    public int day;
    public String dateStr;


    public dateInfo() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dateStr = dateUtil.encodeDate(this);
    }

    public dateInfo(int year, int monthOfYear, int dayOfMonth) {

        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
        dateStr = dateUtil.encodeDate(this);
    }


}
