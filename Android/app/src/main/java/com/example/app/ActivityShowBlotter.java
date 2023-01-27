package com.example.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.app.adapter.BlotterAdapter;
import com.example.app.adapter.BlotterShowClothesAdapter;
import com.example.app.adapter.BlotterShowItemsAdapter;
import com.example.app.adapter.NoteShowAdapter;
import com.example.app.database.BlotterDBHelper;
import com.example.app.database.ItemsDBHelper;
import com.example.app.entity.blotterInfo;
import com.example.app.entity.clothesInfo;
import com.example.app.entity.noteInfo;
import com.example.app.entity.staticData;

import java.util.List;

public class ActivityShowBlotter extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private TextView tv_show_blotter_name;
    private TextView tv_show_blotter_created_time;
    private TextView tv_show_blotter_apply_time;
    private TextView tv_show_blotter_brief;
    private ListView lv_list_show_blotter_items;
    private ImageView btn_show_blotter_back;
    private String tableName;
    private int blotterID;

    private BlotterDBHelper mBlotterDBHelper;
    private List<noteInfo> noteInfoList;
    private blotterInfo blotterInfo;
    private NoteShowAdapter noteShowAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_blotter);

        tv_show_blotter_name = findViewById(R.id.tv_show_blotter_name);
        tv_show_blotter_created_time = findViewById(R.id.tv_show_blotter_created_time);
        tv_show_blotter_apply_time = findViewById(R.id.tv_show_blotter_apply_time);
        tv_show_blotter_brief = findViewById(R.id.tv_show_blotter_brief);
        lv_list_show_blotter_items = findViewById(R.id.lv_list_show_blotter_items);
        btn_show_blotter_back = findViewById(R.id.btn_show_blotter_back);
        btn_show_blotter_back.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        tableName = bundle.getString("tableName");
        blotterID = bundle.getInt("blotterID");

        mBlotterDBHelper = BlotterDBHelper.getInstance(this);
        mBlotterDBHelper.openReadLink();

        noteInfoList = mBlotterDBHelper.queryAllNotesInfo(tableName);
        blotterInfo = mBlotterDBHelper.queryBlotterInfoByID(blotterID);

        tv_show_blotter_name.setText(blotterInfo.publish_name);
        tv_show_blotter_created_time.setText(blotterInfo.createdTime);
        tv_show_blotter_apply_time.setText(blotterInfo.applyTime);
        tv_show_blotter_brief.setText(blotterInfo.brief);

        noteShowAdapter = new NoteShowAdapter(this,noteInfoList);
        lv_list_show_blotter_items.setAdapter(noteShowAdapter);
        lv_list_show_blotter_items.setOnItemClickListener(this);


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_show_blotter_back) {
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent intent_items = new Intent(this,ActivityReviseItems.class);
        Bundle bundle_items = new Bundle();

        switch (noteInfoList.get(position).type) {

            case staticData.TYPE_CLOTHES:

                bundle_items.putInt("id", noteInfoList.get(position).items_id);
                bundle_items.putInt("mItems", staticData.TYPE_CLOTHES);

                intent_items.putExtras(bundle_items);
                startActivity(intent_items);

                break;

            case staticData.TYPE_ITEMS:
                bundle_items.putInt("id", noteInfoList.get(position).items_id);
                bundle_items.putInt("mItems", staticData.TYPE_ITEMS);

                intent_items.putExtras(bundle_items);
                startActivity(intent_items);

                break;
        }




    }
}