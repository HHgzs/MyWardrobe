package com.example.app.entity;

public class itemsInfo {

    public int id;

    public String name = "null";

    // 0代表"摄影器材", 1代表"工具", 2代表"生活用品", 3代表"电气设备", 4代表"其他"
    public int type;

    public String brief = "null";

    // 图片地址
    public String imgPath = staticData.EMPTY;
    // 库存状态，0代表离库，1代表在库，大于1代表数量
    public int status;

}
