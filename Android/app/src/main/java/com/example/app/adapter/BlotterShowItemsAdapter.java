package com.example.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.MyApplication;
import com.example.app.R;
import com.example.app.entity.itemsInfo;
import com.example.app.entity.noteInfo;
import com.example.app.entity.staticData;
import com.example.app.util.FileUtil;

import java.util.List;
import java.util.Objects;

public class BlotterShowItemsAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<itemsInfo> mItemsInfoList;
    private boolean isAdded;


    public BlotterShowItemsAdapter(Context mContext, List<itemsInfo> mClothesInfoList) {
        this.mContext = mContext;
        this.mItemsInfoList = mClothesInfoList;
    }


    @Override
    public int getCount() {
        return mItemsInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemsInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            // 根据布局文件item_list.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_blotter_show,null);

            holder = new ViewHolder();
            holder.ll_list_blotter_show = convertView.findViewById(R.id.ll_list_blotter_show);
            holder.iv_list_blotter_show_img = convertView.findViewById(R.id.iv_list_blotter_show_img);
            holder.tv_list_blotter_show_name = convertView.findViewById(R.id.tv_list_blotter_show_name);
            holder.tv_list_blotter_show_desc = convertView.findViewById(R.id.tv_list_blotter_show_desc);
            holder.btn_add = convertView.findViewById(R.id.btn_add);

            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        itemsInfo itemsInfo = mItemsInfoList.get(position);
        holder.ll_list_blotter_show.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        noteInfo noteInfo = new noteInfo();
        noteInfo.items_id = itemsInfo.id;
        noteInfo.type = staticData.TYPE_ITEMS;
        noteInfo.number = 1;

        isAdded = false;
        for(int i = 0; i < MyApplication.noteInfoList.size(); i++) {
            if( MyApplication.noteInfoList.get(i).items_id == (noteInfo.items_id) &&
                    MyApplication.noteInfoList.get(i).type == (noteInfo.type)) {
                isAdded = true;
                holder.btn_add.setTextColor(0xffFF8E8E);
                holder.btn_add.setText("删除");
            }
        }

        if (!Objects.equals(itemsInfo.imgPath, staticData.EMPTY)) {
            // 根据imgPath读取图像为uri并显示出来
            String name = itemsInfo.imgPath;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                Uri uri = FileUtil.findImageByName(MyApplication.getContext(),name);
                holder.iv_list_blotter_show_img.setImageURI(uri);
            }
        } else {
            holder.iv_list_blotter_show_img.setImageResource(R.drawable.img_null_bk);

        }

        holder.tv_list_blotter_show_name.setText(itemsInfo.name);
        holder.tv_list_blotter_show_desc.setText(itemsInfo.brief);


        holder.btn_add.setOnClickListener(v -> {

            if (!isAdded) {
                try {
                    isAdded = true;
                    holder.btn_add.setTextColor(0xffFF8E8E);
                    holder.btn_add.setText("删除");

                    MyApplication.noteInfoList.add(noteInfo);

                    for(int i = 0; i < MyApplication.noteInfoList.size(); i++) {
                        Log.d("HH", "+" + String.valueOf(MyApplication.noteInfoList.get(i).items_id));
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if(isAdded) {
                try {
                    isAdded = false;
                    holder.btn_add.setTextColor(0xffffffff);
                    holder.btn_add.setText("添加");
                    for(int i = 0; i < MyApplication.noteInfoList.size(); i++) {
                        if( MyApplication.noteInfoList.get(i).items_id == (noteInfo.items_id) &&
                                MyApplication.noteInfoList.get(i).type == (noteInfo.type)) {
                            Log.d("HH", "-" + String.valueOf(MyApplication.noteInfoList.get(i).items_id));
                            MyApplication.noteInfoList.remove(i);
                            i--;
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

        return convertView;
    }

    public static final class ViewHolder {
        public LinearLayout ll_list_blotter_show;
        public ImageView iv_list_blotter_show_img;
        public TextView tv_list_blotter_show_name;
        public TextView tv_list_blotter_show_desc;
        public Button btn_add;
    }


}
