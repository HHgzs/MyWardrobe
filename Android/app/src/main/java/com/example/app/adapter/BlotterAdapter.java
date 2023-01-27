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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.MyApplication;
import com.example.app.R;
import com.example.app.database.BlotterDBHelper;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.blotterInfo;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.noteInfo;
import com.example.app.entity.staticData;
import com.example.app.util.FileUtil;
import com.example.app.util.dateUtil;

import java.util.List;
import java.util.Objects;

public class BlotterAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<blotterInfo> mBlotterInfoList;
    private final BlotterDBHelper mDBHelper;


    public BlotterAdapter(Context mContext, List<blotterInfo> mBlotterInfoList) {
        this.mContext = mContext;
        this.mBlotterInfoList = mBlotterInfoList;
        mDBHelper = BlotterDBHelper.getInstance(mContext);

    }


    @Override
    public int getCount() {
        return mBlotterInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mBlotterInfoList.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_blotter,null);

            holder = new ViewHolder();
            holder.ll_list_blotter = convertView.findViewById(R.id.ll_list_blotter);
            holder.tv_list_blotter_name = convertView.findViewById(R.id.tv_list_blotter_name);
            holder.tv_list_blotter_desc = convertView.findViewById(R.id.tv_list_blotter_desc);
            holder.tv_list_blotter_time = convertView.findViewById(R.id.tv_list_blotter_time);
            holder.btn_list_blotter_add = convertView.findViewById(R.id.btn_list_blotter_add);

            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        // 设置显示动画
        @SuppressLint("ResourceType") Animator animator_name = AnimatorInflater.loadAnimator(mContext, R.anim.slide_in_left);
        animator_name.setInterpolator(new OvershootInterpolator());
        animator_name.setTarget(holder.tv_list_blotter_name);

        @SuppressLint("ResourceType") Animator animator_desc = AnimatorInflater.loadAnimator(mContext, R.anim.slide_in_left);
        animator_desc.setInterpolator(new OvershootInterpolator());
        animator_desc.setTarget(holder.tv_list_blotter_desc);

        @SuppressLint("ResourceType") Animator animator_time = AnimatorInflater.loadAnimator(mContext, R.anim.slide_in_left);
        animator_time.setInterpolator(new OvershootInterpolator());
        animator_time.setTarget(holder.tv_list_blotter_time);

        @SuppressLint("ResourceType") Animator animator_btn = AnimatorInflater.loadAnimator(mContext, R.anim.pop_in);
        animator_btn.setInterpolator(new OvershootInterpolator());
        animator_btn.setTarget(holder.btn_list_blotter_add);

        @SuppressLint("ResourceType") Animator animator_press = AnimatorInflater.loadAnimator(mContext, R.anim.bounce_here);
        animator_press.setInterpolator(new OvershootInterpolator());
        animator_press.setTarget(holder.btn_list_blotter_add);










        blotterInfo blotterInfo = mBlotterInfoList.get(position);
        holder.ll_list_blotter.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);


        holder.tv_list_blotter_name.setText(blotterInfo.publish_name);
        holder.tv_list_blotter_desc.setText(blotterInfo.brief);

        String applyDate = dateUtil.publishDate(dateUtil.readDate(blotterInfo.applyTime));
        holder.tv_list_blotter_time.setText(applyDate);


        animator_name.start();
        animator_desc.start();
        animator_time.start();
        animator_btn.start();



        holder.btn_list_blotter_add.setOnClickListener(v -> {

            animator_press.start();

            String tableName = blotterInfo.table_name;
            List<noteInfo> noteInfoList = mDBHelper.queryAllNotesInfo(tableName);

            for(int i = 0; i < noteInfoList.size(); i++) {
                noteInfo info = new noteInfo();
                info.items_id = noteInfoList.get(i).items_id;
                info.type = noteInfoList.get(i).type;
                info.number = noteInfoList.get(i).number;

                boolean exist = false;

                for(int j = 0; j < MyApplication.noteInfoList.size(); j++) {

                    if (MyApplication.noteInfoList.get(j).items_id == (info.items_id) &&
                            MyApplication.noteInfoList.get(j).type == (info.type)) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    MyApplication.noteInfoList.add(info);
                }
            }
        });


        return convertView;
    }

    public static final class ViewHolder {
        public LinearLayout ll_list_blotter;
        public TextView tv_list_blotter_name;
        public TextView tv_list_blotter_desc;
        public TextView tv_list_blotter_time;
        public Button btn_list_blotter_add;
    }


}
