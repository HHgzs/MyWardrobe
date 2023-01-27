package com.example.app.adapter;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.MyApplication;
import com.example.app.R;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.itemsInfo;
import com.example.app.entity.noteInfo;
import com.example.app.entity.staticData;
import com.example.app.util.FileUtil;

import java.util.List;
import java.util.Objects;

public class NoteShowAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<noteInfo> mNoteInfoList;
    private ItemsDBHelper mDBHelper;
    private itemsInfo itemsInfo;
    private clothesInfo clothesInfo;
    private String imgName;


    public NoteShowAdapter(Context mContext, List<noteInfo> mNoteInfoList) {
        this.mContext = mContext;
        this.mNoteInfoList = mNoteInfoList;
    }


    @Override
    public int getCount() {
        return mNoteInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mNoteInfoList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_note_show,null);

            holder = new ViewHolder();
            holder.ll_list_note_show = convertView.findViewById(R.id.ll_list_blotter_show);
            holder.iv_list_note_show_img = convertView.findViewById(R.id.iv_list_note_show_img);
            holder.iv_cover_image = convertView.findViewById(R.id.iv_cover_image);
            holder.tv_list_note_show_name = convertView.findViewById(R.id.tv_list_note_show_name);
            holder.tv_list_note_show_desc = convertView.findViewById(R.id.tv_list_note_show_desc);

            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        // 设置显示动画
        @SuppressLint("ResourceType") Animator animator_img = AnimatorInflater.loadAnimator(mContext, R.anim.pop_in);
        animator_img.setInterpolator(new OvershootInterpolator());
        animator_img.setTarget(holder.iv_list_note_show_img);

        @SuppressLint("ResourceType") Animator animator_cover = AnimatorInflater.loadAnimator(mContext, R.anim.pop_in);
        animator_cover.setInterpolator(new OvershootInterpolator());
        animator_cover.setTarget(holder.iv_cover_image);

        @SuppressLint("ResourceType") Animator animator_name = AnimatorInflater.loadAnimator(mContext, R.anim.show_in);
        animator_name.setInterpolator(new OvershootInterpolator());
        animator_name.setTarget(holder.tv_list_note_show_name);

        @SuppressLint("ResourceType") Animator animator_desc = AnimatorInflater.loadAnimator(mContext, R.anim.show_in);
        animator_desc.setInterpolator(new OvershootInterpolator());
        animator_desc.setTarget(holder.tv_list_note_show_desc);



        mDBHelper = ItemsDBHelper.getInstance(mContext);
        mDBHelper.openReadLink();

        noteInfo noteInfo = mNoteInfoList.get(position);
//        holder.ll_list_note_show.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);


        if(noteInfo.type == staticData.TYPE_CLOTHES) {

            clothesInfo = mDBHelper.queryClothesInfoByID(noteInfo.items_id);
            imgName = clothesInfo.imgPath;

            holder.tv_list_note_show_name.setText(clothesInfo.name);
            holder.tv_list_note_show_desc.setText(clothesInfo.brief);

        } else if (noteInfo.type == staticData.TYPE_ITEMS) {

            itemsInfo = mDBHelper.queryItemsInfoByID(noteInfo.items_id);
            imgName = itemsInfo.imgPath;

            holder.tv_list_note_show_name.setText(itemsInfo.name);
            holder.tv_list_note_show_desc.setText(itemsInfo.brief);
        }


        if (!Objects.equals(imgName, staticData.EMPTY)) {
            // 根据imgName读取图像为uri并显示出来
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                Uri uri = FileUtil.findImageByName(MyApplication.getContext(),imgName);
                holder.iv_list_note_show_img.setImageURI(uri);
            }
        } else {
            holder.iv_list_note_show_img.setImageResource(R.drawable.img_null_bk);
        }


        animator_img.start();
        animator_cover.start();
        animator_name.start();
        animator_desc.start();


        return convertView;
    }


    public static final class ViewHolder {
        public LinearLayout ll_list_note_show;
        public ImageView iv_list_note_show_img;
        public ImageView iv_cover_image;
        public TextView tv_list_note_show_name;
        public TextView tv_list_note_show_desc;
    }


}
