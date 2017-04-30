package com.rdc.mymap.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyGridViewAdapter;
import com.rdc.mymap.model.MyGridViewItem;

import java.util.ArrayList;
import java.util.List;

public class NearbyActivity extends Activity implements View.OnClickListener{

    private GridView mGridView;
    private List<MyGridViewItem> mList = new ArrayList<>();

    private ImageView mBackImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_nearby);
        init();
    }

    private void init() {
        initData();
        initView();
    }

    private void initData() {

        mList.add(new MyGridViewItem(R.drawable.food, "美食"));
        mList.add(new MyGridViewItem(R.drawable.interest, "景点"));
        mList.add(new MyGridViewItem(R.drawable.hotel, "旅馆"));
        mList.add(new MyGridViewItem(R.drawable.entertainment, "娱乐"));
        mList.add(new MyGridViewItem(R.drawable.movie, "电影"));
        mList.add(new MyGridViewItem(R.drawable.market, "超市"));
        mList.add(new MyGridViewItem(R.drawable.wc, "厕所"));
        mList.add(new MyGridViewItem(R.drawable.bus_station, "公交站"));
        mList.add(new MyGridViewItem(R.drawable.gas_station, "加油站"));
        mList.add(new MyGridViewItem(R.drawable.pass_stop, "停车场"));
        mList.add(new MyGridViewItem(R.drawable.bank, "银行"));
        mList.add(new MyGridViewItem(R.drawable.repair, "维修"));

        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.gv);
        mGridView.setAdapter(new MyGridViewAdapter(this, mList, R.layout.item_gridview));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NearbyActivity.this, NearbyInfoActivity.class);
                switch (position) {
                    case 0 :
                        intent.putExtra("category", "food");
                        break;
                    case 1 :
                        intent.putExtra("category", "interest");
                        break;
                    case 2 :
                        intent.putExtra("category", "hotel");
                        break;
                    case 3 :
                        intent.putExtra("category", "entertainment");
                        break;
                    case 4 :
                        intent.putExtra("category", "movie");
                        break;
                    case 5 :
                        intent.putExtra("category", "market");
                        break;
                    case 6 :
                        intent.putExtra("category", "wc");
                        break;
                    case 7 :
                        intent.putExtra("category", "station");
                        break;
                    case 8 :
                        intent.putExtra("category", "gas");
                        break;
                    case 9 :
                        intent.putExtra("category", "pass");
                        break;
                    case 10 :
                        intent.putExtra("category", "bank");
                        break;
                    case 11 :
                        intent.putExtra("category", "repair");
                        break;
                    default:
                        break;
                }
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                finish();
                break;
            default:
                break;
        }
    }
}
