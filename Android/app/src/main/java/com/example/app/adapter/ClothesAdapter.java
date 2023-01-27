package com.example.app.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.MyApplication;
import com.example.app.R;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.staticData;
import com.example.app.util.FileUtil;

import java.util.List;
import java.util.Objects;

public class ClothesAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<clothesInfo> mClothesInfoList;
    private final ItemsDBHelper mDBHelper;


    public ClothesAdapter(Context mContext, List<clothesInfo> mClothesInfoList) {
        this.mContext = mContext;
        this.mClothesInfoList = mClothesInfoList;
        mDBHelper = ItemsDBHelper.getInstance(mContext);

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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            // 根据布局文件item_list.xml生成转换视图对象
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_clothes,null);

            holder = new ViewHolder();
            holder.ll_list_clothes = convertView.findViewById(R.id.ll_list_clothes);
            holder.iv_list_clothes_img = convertView.findViewById(R.id.iv_list_clothes_img);
            holder.iv_cover_image = convertView.findViewById(R.id.iv_cover_image);
            holder.tv_list_clothes_name = convertView.findViewById(R.id.tv_list_clothes_name);
            holder.tv_list_clothes_desc = convertView.findViewById(R.id.tv_list_clothes_desc);
            holder.btn_edit = convertView.findViewById(R.id.btn_edit);

            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        // 设置动画
        @SuppressLint("ResourceType") Animator animator = AnimatorInflater.loadAnimator(mContext, R.anim.bounce_here);
        animator.setInterpolator(new OvershootInterpolator());
        animator.setTarget(holder.btn_edit);

        @SuppressLint("ResourceType") Animator animator_img = AnimatorInflater.loadAnimator(mContext, R.anim.pop_in);
        animator_img.setInterpolator(new OvershootInterpolator());
        animator_img.setTarget(holder.iv_list_clothes_img);

        @SuppressLint("ResourceType") Animator animator_cover = AnimatorInflater.loadAnimator(mContext, R.anim.pop_in);
        animator_cover.setInterpolator(new OvershootInterpolator());
        animator_cover.setTarget(holder.iv_cover_image);

        @SuppressLint("ResourceType") Animator animator_name = AnimatorInflater.loadAnimator(mContext, R.anim.show_in);
        animator_name.setInterpolator(new OvershootInterpolator());
        animator_name.setTarget(holder.tv_list_clothes_name);

        @SuppressLint("ResourceType") Animator animator_desc = AnimatorInflater.loadAnimator(mContext, R.anim.show_in);
        animator_desc.setInterpolator(new OvershootInterpolator());
        animator_desc.setTarget(holder.tv_list_clothes_desc);

        @SuppressLint("ResourceType") Animator animator_btn = AnimatorInflater.loadAnimator(mContext, R.anim.pop_in);
        animator_btn.setInterpolator(new OvershootInterpolator());
        animator_btn.setTarget(holder.btn_edit);





        clothesInfo clothesInfo = mClothesInfoList.get(position);
        holder.ll_list_clothes.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        if (!Objects.equals(clothesInfo.imgPath, staticData.EMPTY)) {
            // 根据imgPath读取图像为uri并显示出来
            String name = clothesInfo.imgPath;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                Uri uri = FileUtil.findImageByName(MyApplication.getContext(),name);
                holder.iv_list_clothes_img.setImageURI(uri);
            }
        } else {
            holder.iv_list_clothes_img.setImageResource(R.drawable.img_null_bk);

        }

        holder.tv_list_clothes_name.setText(clothesInfo.name);
        holder.tv_list_clothes_desc.setText(clothesInfo.brief);

        if (clothesInfo.status >= staticData.IN_STORE) {
            holder.btn_edit.setText("在库");
            holder.btn_edit.setTextColor(0xffffffff);

        } else if (clothesInfo.status == staticData.OUT_STORE) {
            holder.btn_edit.setText("离库");
            holder.btn_edit.setTextColor(0xffFF8E8E);
        }


        // 启动动画
        animator_img.start();
        animator_cover.start();
        animator_name.start();
        animator_desc.start();
        animator_btn.start();



        holder.btn_edit.setOnClickListener(v -> {

            if (clothesInfo.status == staticData.OUT_STORE) {

                clothesInfo.status = staticData.IN_STORE;
                holder.btn_edit.setText("在库");
                holder.btn_edit.setTextColor(0xffffffff);

            } else if (clothesInfo.status >= staticData.IN_STORE) {

                clothesInfo.status = staticData.OUT_STORE;
                holder.btn_edit.setText("离库");
                holder.btn_edit.setTextColor(0xffFF8E8E);
            }
            mDBHelper.openWriteLink();
            mDBHelper.reviseClothesInfo(clothesInfo);

            // 开始动画
            animator.start();

        });

        return convertView;
    }

    public static final class ViewHolder {
        public LinearLayout ll_list_clothes;
        public ImageView iv_list_clothes_img;
        public ImageView iv_cover_image;
        public TextView tv_list_clothes_name;
        public TextView tv_list_clothes_desc;
        public Button btn_edit;
    }


}
