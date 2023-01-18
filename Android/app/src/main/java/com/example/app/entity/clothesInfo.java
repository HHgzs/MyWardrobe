package com.example.app.entity;

public class clothesInfo {

    public int id;
    // 名称
    public String name;

    // 种类标签首选项，0代表衣物(clothing)，1代表床具被褥(bedding)
    public int basicType;

    // 种类标签次选项
    // 如果basicType为0, secondType中 0代表"外套", 1代表"外裤", 2代表"衬衫", 3代表"卫衣", 4代表"内衣裤"
    // 如果basicType为1, secondType中 0代表"床单", 1代表"被褥", 2代表"其他"
    public int secondType;

    // 厚度标签，0代表"薄"，1代表"中"，2代表"厚"
    public int thickness;

    // 季节标签，0代表"春秋"，1代表"夏天"，2代表"冬天"
    public int season;

    // 简介
    public String brief;

    // 图片地址
    public String imgPath = "Empty";


}
