package com.rdc.mymap.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.animation.ValueAnimator;

public class PayResultAnimation extends View implements ValueAnimator.AnimatorUpdateListener {

    public static final int RESULT_RIGHT = 1;
    public static final int RESULT_WRONG = 2;

    private Context mContext;
    private Paint mPaint;
    private Path mPathCircle;
    private Path mPathCircleDst;
    private Path mPathRight;
    private Path mPathRightDst;
    private Path mPathWrong1;
    private Path mPathWrong1Dst;
    private Path mPathWrong2;
    private Path mPathWrong2Dst;
    private PathMeasure mPathMeasure;
    private ValueAnimator mCircleAnimator;
    private ValueAnimator mRightAnimator;
    private ValueAnimator mWrong1Animator;
    private ValueAnimator mWrong2Animator;
    private float mCirclePercent;
    private float mRightPercent;
    private float mWrong1Percent;
    private float mWrong2Percent;
    private int mLineWidth;
    private int mResultType = RESULT_WRONG;

    public PayResultAnimation(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PayResultAnimation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PayResultAnimation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(dp2dx(100), dp2dx(100));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mResultType == RESULT_RIGHT) {
            mPaint.setColor(Color.GREEN);
        } else {
            mPaint.setColor(Color.RED);
        }
        mPathCircle.addCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - mLineWidth, Path.Direction.CW);
        mPathMeasure.setPath(mPathCircle, false);
        Log.e("error", "mCirclePercent * mPathMeasure.getLength()=" + mCirclePercent * mPathMeasure.getLength());
        mPathMeasure.getSegment(0, mCirclePercent * mPathMeasure.getLength(), mPathCircleDst, true);
        canvas.drawPath(mPathCircleDst, mPaint);
        if(mResultType == RESULT_RIGHT) {
            mPathRight.moveTo(getWidth() / 4, getWidth() / 2);
            mPathRight.lineTo(getWidth() / 2, getWidth() / 4 * 3);
            mPathRight.lineTo(getWidth() / 4 * 3, getWidth() / 4);
            if(mCirclePercent == 1) {
                mPathMeasure.nextContour();
                mPathMeasure.setPath(mPathRight, false);
                mPathMeasure.getSegment(0, mRightPercent * mPathMeasure.getLength(), mPathRightDst, true);
                canvas.drawPath(mPathRightDst, mPaint);
            }
        } else {
            mPathWrong1.moveTo(getWidth() / 4 * 3, getWidth() / 4);
            mPathWrong1.lineTo(getWidth() / 4, getWidth() / 4 * 3);
            mPathWrong2.moveTo(getWidth() / 4, getWidth() / 4);
            mPathWrong2.lineTo(getWidth() / 4 * 3, getWidth() / 4 * 3);
            if(mCirclePercent == 1) {
                mPathMeasure.nextContour();
                mPathMeasure.setPath(mPathWrong1, false);
                mPathMeasure.getSegment(0, mWrong1Percent * mPathMeasure.getLength(), mPathWrong1Dst, true);
                canvas.drawPath(mPathWrong1Dst, mPaint);
            }
            if(mWrong1Percent == 1) {
                mPathMeasure.nextContour();
                mPathMeasure.setPath(mPathWrong2, false);
                mPathMeasure.getSegment(0, mWrong2Percent * mPathMeasure.getLength(), mPathWrong2Dst, true);
                canvas.drawPath(mPathWrong2Dst, mPaint);
            }
        }

    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if(animation.equals(mCircleAnimator)) {
            mCirclePercent = (float) animation.getAnimatedValue();
            invalidate();
            Log.e("error", "percent=" + mCirclePercent);
            if(mCirclePercent == 1) {
                if(mResultType == RESULT_RIGHT) {
                    mRightAnimator.start();
                } else {
                    mWrong1Animator.start();
                }
            }
        } else if(animation.equals(mRightAnimator)) {
            mRightPercent = (float) animation.getAnimatedValue();
            invalidate();
        } else if(animation.equals(mWrong1Animator)) {
            mWrong1Percent = (float) animation.getAnimatedValue();
            invalidate();
            if(mWrong1Percent == 1) {
                mWrong2Animator.start();
            }
        } else if(animation.equals(mWrong2Animator)) {
            mWrong2Percent = (float) animation.getAnimatedValue();
            invalidate();
        }
     }

    private void init() {
        initPaint();
        initPath();
        initAnimator();
    }

    private void initPaint() {
        mLineWidth = dp2dx(3);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mLineWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.GREEN);
    }

    private void initPath() {
        mPathCircle = new Path();
        mPathCircleDst = new Path();
        mPathRight = new Path();
        mPathRightDst = new Path();
        mPathWrong1 = new Path();
        mPathWrong1Dst = new Path();
        mPathWrong2 = new Path();
        mPathWrong2Dst = new Path();
        mPathMeasure = new PathMeasure();
    }

    private void initAnimator() {
        mCircleAnimator = ValueAnimator.ofFloat(0, 1);
        mCircleAnimator.setDuration(1000);
        startAnimator();
        mCircleAnimator.addUpdateListener(this);
        mRightAnimator = ValueAnimator.ofFloat(0, 1);
        mRightAnimator.setDuration(500);
        mRightAnimator.addUpdateListener(this);
        mWrong1Animator = ValueAnimator.ofFloat(0, 1);
        mWrong1Animator.setDuration(300);
        mWrong1Animator.addUpdateListener(this);
        mWrong2Animator = ValueAnimator.ofFloat(0, 1);
        mWrong2Animator.setDuration(300);
        mWrong2Animator.addUpdateListener(this);
    }

    public void startAnimator() {
        mCircleAnimator.start();
    }

    private int dp2dx(int dp) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (scale * dp + 0.5f);
    }

    public void setResultType(int resultType) {
        this.mResultType = resultType;
        invalidate();
    }
}
