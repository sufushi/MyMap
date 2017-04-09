package com.rdc.mymap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

public class UnderlineEditText extends EditText {

    private Paint mPaint;
    private Context mContext;

    public UnderlineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#FFA1A0A0"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, mPaint);
    }
}
