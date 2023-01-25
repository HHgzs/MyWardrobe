package com.example.app.adapter;

import static com.example.app.util.ToastUtil.show;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app.MyApplication;
import com.example.app.R;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.itemsInfo;
import com.example.app.entity.staticData;
import com.example.app.util.FileUtil;

import java.util.List;
import java.util.Objects;

public class ItemsAdapter extends BaseAdapter implements View.OnClickListener{

    private final Context mContext;
    private final List<itemsInfo> mItemsInfoList;
    private final ItemsDBHelper mDBHelper;
    private int selectedEditTextPosition = -1;


    public ItemsAdapter(Context mContext, List<itemsInfo> mItemsInfoList) {
        this.mContext = mContext;
        this.mItemsInfoList = mItemsInfoList;
        mDBHelper = ItemsDBHelper.getInstance(mContext);

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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_items,null);

            holder = new ViewHolder();
            holder.ll_list_items = convertView.findViewById(R.id.ll_list_items);
            holder.iv_list_items_img = convertView.findViewById(R.id.iv_list_items_img);
            holder.tv_list_items_name = convertView.findViewById(R.id.tv_list_items_name);
            holder.tv_list_items_desc = convertView.findViewById(R.id.tv_list_items_desc);
            holder.et_num_show = convertView.findViewById(R.id.et_num_show);
            holder.btn_num_add = convertView.findViewById(R.id.btn_num_add);
            holder.btn_num_minus = convertView.findViewById(R.id.btn_num_minus);


            // 将视图持有者保存到转换视图当中
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        itemsInfo itemsInfo = mItemsInfoList.get(position);
        holder.ll_list_items.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        if (!Objects.equals(itemsInfo.imgPath, staticData.EMPTY)) {
            // 根据imgPath读取图像为uri并显示出来
            String name = itemsInfo.imgPath;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

                Uri uri = FileUtil.findImageByName(MyApplication.getContext(),name);
                holder.iv_list_items_img.setImageURI(uri);
            }
        } else {
            holder.iv_list_items_img.setImageResource(R.drawable.img_null_bk);
        }


        holder.tv_list_items_name.setText(itemsInfo.name);
        holder.tv_list_items_desc.setText(itemsInfo.brief);

        holder.et_num_show.setText(String.valueOf(itemsInfo.status));
        holder.btn_num_minus.setActivated(false);
        if (itemsInfo.status > 0) {
            holder.btn_num_minus.setActivated(true);
        }


        holder.btn_num_add.setOnClickListener(v -> {
            itemsInfo.status = itemsInfo.status + 1;
            holder.et_num_show.setText(String.valueOf(itemsInfo.status));
            if (itemsInfo.status > 0) {
                holder.btn_num_minus.setActivated(true);
            }
            mDBHelper.openWriteLink();
            mDBHelper.reviseItemsInfo(itemsInfo);
            mDBHelper.closeLink();

        });



        holder.btn_num_minus.setOnClickListener(v -> {
            if (itemsInfo.status > 0) {
                itemsInfo.status = itemsInfo.status - 1;
                holder.et_num_show.setText(String.valueOf(itemsInfo.status));
                if (itemsInfo.status < 1) {
                    holder.btn_num_minus.setActivated(false);
                }

                mDBHelper.openWriteLink();
                mDBHelper.reviseItemsInfo(itemsInfo);
                mDBHelper.closeLink();
            }

        });




//        holder.et_num_show.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                int num_edit = Integer.parseInt(holder.et_num_show.getText().toString());
//
//                if (num_edit != itemsInfo.status) {
//
//                    if (num_edit >= 0) {
//
//                        itemsInfo.status = num_edit;
//                        mDBHelper.openWriteLink();
//                        mDBHelper.reviseItemsInfo(itemsInfo);
//                        mDBHelper.closeLink();
//
//                    } else {
//                        show(mContext,"请输入合法数据");
//                    }
//                }
//            }
//        });


        return convertView;
    }

    @Override
    public void onClick(View v) {

    }


    public static final class ViewHolder {
        public LinearLayout ll_list_items;
        public ImageView iv_list_items_img;
        public TextView tv_list_items_name;
        public TextView tv_list_items_desc;
        public EditText et_num_show;
        public ImageView btn_num_add;
        public ImageView btn_num_minus;
    }


}
