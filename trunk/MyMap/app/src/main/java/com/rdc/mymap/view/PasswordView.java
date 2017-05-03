package com.rdc.mymap.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rdc.mymap.R;
import com.rdc.mymap.adapter.MyKeyboardGridViewAdapter;
import com.rdc.mymap.presenter.OnPassWordInputFinish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PasswordView extends RelativeLayout implements View.OnClickListener{

    private Context mContext;
    private String mPassword;
    private TextView[] mTextViewList;
    private GridView mKeyboardGridView;
    private ArrayList<Map<String, String>> mKeyValueList;
    private MyKeyboardGridViewAdapter mMyKeyboardGridViewAdapter;
    private ImageView mBackImageView;
    private int mCurrentIndex = -1;

    public PasswordView(Context context) {
        this(context, null);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        View view = View.inflate(context, R.layout.layout_password_input, null);
        mTextViewList = new TextView[6];
        mTextViewList[0] = (TextView) view.findViewById(R.id.tv_input1);
        mTextViewList[1] = (TextView) view.findViewById(R.id.tv_input2);
        mTextViewList[2] = (TextView) view.findViewById(R.id.tv_input3);
        mTextViewList[3] = (TextView) view.findViewById(R.id.tv_input4);
        mTextViewList[4] = (TextView) view.findViewById(R.id.tv_input5);
        mTextViewList[5] = (TextView) view.findViewById(R.id.tv_input6);
        mKeyboardGridView = (GridView) view.findViewById(R.id.gv_keyboard);
        mKeyValueList = new ArrayList<Map<String, String>>();
        for (int i = 1; i < 13; i++) {
            Map<String, String> map = new HashMap<String, String>();
            if(i < 10) {
                map.put("name", String.valueOf(i));
            } else if(i == 10) {
                map.put("name", "");
            } else if(i == 11) {
                map.put("name", String.valueOf(0));
            } else if(i == 12) {
                map.put("name", "×");
            }
            mKeyValueList.add(map);
        }
        mMyKeyboardGridViewAdapter = new MyKeyboardGridViewAdapter(mContext, mKeyValueList);
        mKeyboardGridView.setAdapter(mMyKeyboardGridViewAdapter);
        mKeyboardGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position < 11 && position != 9) {
                    if(mCurrentIndex >= -1 && mCurrentIndex < 5) {
                        mTextViewList[++ mCurrentIndex].setText(mKeyValueList.get(position).get("name"));
                    }
                } else {
                    if(position == 11) {
                        if(mCurrentIndex - 1 >= -1) {
                            mTextViewList[mCurrentIndex -- ].setText("");
                        }
                    }
                }
            }
        });
        mBackImageView = (ImageView) view.findViewById(R.id.iv_back);
        mBackImageView.setOnClickListener(this);
        addView(view);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnInputFinish(final OnPassWordInputFinish onPassWordInputFinish) {
        mTextViewList[5].addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() == 1) {
                    mPassword = "";
                    for (int i = 0; i < 6; i++) {
                        mPassword += mTextViewList[i].getText().toString().trim();
                    }
                }
                onPassWordInputFinish.onInputFinish();
            }
        });
    }

    public String getPassword() {
        return mPassword;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back :
                break;
            default:
                break;
        }
    }
}
