package com.example.app.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.R;
import com.example.app.entity.clothesInfo;
import com.example.app.util.ToastUtil;

import java.util.List;

public class ClothesAdapter extends BaseAdapter {

    private Context mContext;
    private List<clothesInfo> mClothesInfoList;



    public ClothesAdapter(Context mContext, List<clothesInfo> mClothesInfoList) {
        this.mContext = mContext;
        this.mClothesInfoList = mClothesInfoList;
    }



    @Override
    public int getCount() {
        return mClothesInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mClothesInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            // 根据布局文件item_list.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_clothes,null);

            holder = new ViewHolder();
            holder.ll_list_clothes = convertView.findViewById(R.id.ll_list_clothes);
            holder.iv_list_clothes_img = convertView.findViewById(R.id.iv_list_clothes_img);
            holder.tv_list_clothes_name = convertView.findViewById(R.id.tv_list_clothes_name);
            holder.tv_list_clothes_desc = convertView.findViewById(R.id.tv_list_clothes_desc);
            holder.btn_edit = convertView.findViewById(R.id.btn_edit);

            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        clothesInfo clothesInfo = mClothesInfoList.get(position);
        holder.ll_list_clothes.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        holder.iv_list_clothes_img.setImageURI(Uri.parse(clothesInfo.imgPath));
        holder.tv_list_clothes_name.setText(clothesInfo.name);
        holder.tv_list_clothes_desc.setText(clothesInfo.brief);
        holder.btn_edit.setOnClickListener(v -> {
            ToastUtil.show(mContext, "按钮被点击了，" + clothesInfo.name);
        });

        return convertView;
    }

    public final class ViewHolder {
        public LinearLayout ll_list_clothes;
        public ImageView iv_list_clothes_img;
        public TextView tv_list_clothes_name;
        public TextView tv_list_clothes_desc;
        public Button btn_edit;
    }














}
