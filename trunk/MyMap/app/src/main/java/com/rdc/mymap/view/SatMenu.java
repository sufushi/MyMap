package com.rdc.mymap.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.rdc.mymap.R;

import static com.rdc.mymap.config.PositionConfig.POS_LEFT_BOTTOM;
import static com.rdc.mymap.config.PositionConfig.POS_LEFT_TOP;
import static com.rdc.mymap.config.PositionConfig.POS_RIGHT_BOTTOM;
import static com.rdc.mymap.config.PositionConfig.POS_RIGHT_TOP;

public class SatMenu extends ViewGroup implements View.OnClickListener {

    private View mMainMenu;
    private enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }
    private enum Status {
        OPEN, CLOSE
    }
    private Position mPosition = Position.RIGHT_BOTTOM;
    private int mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
    private Status mStatus = Status.CLOSE;
    private OnSatMenuClickListener mOnSatMenuClickListener;

    public SatMenu(Context context) {
        this(context, null);
    }

    public SatMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SatMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SatMenu, defStyleAttr, 0);
        int position = typedArray.getInt(R.styleable.SatMenu_position, POS_RIGHT_BOTTOM);
        switch (position) {
            case POS_LEFT_TOP :
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM :
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP :
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM :
                mPosition = Position.RIGHT_BOTTOM;
                break;
            default:
                break;
        }
        mRadius = (int) typedArray.getDimension(R.styleable.SatMenu_radius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics()));
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for(int i = 0; i < count; i ++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            layoutMainMenu();
            layoutChildMenu();
        }
    }

    @Override
    public void onClick(View v) {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 720f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        v.startAnimation(rotateAnimation);
        dealChildMenu(300);
    }

    private void dealChildMenu(int duration) {
        int count = getChildCount();
        for(int i = 0; i < count - 1; i ++) {
            final View childView = getChildAt(i + 1);
            AnimationSet animationSet = new AnimationSet(true);
            TranslateAnimation translateAnimation;
            int x = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
            int y = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int xflag = 1;
            int yflag = 1;
            if(mPosition == Position.LEFT_TOP || mPosition == Position.LEFT_BOTTOM) {
                xflag = -1;
            }
            if(mPosition == Position.LEFT_TOP || mPosition == Position.RIGHT_TOP) {
                yflag = -1;
            }
            if(mStatus == Status.CLOSE) {
                translateAnimation = new TranslateAnimation(x * xflag - mMainMenu.getMeasuredWidth() / 3, 0, y * yflag - mMainMenu.getMeasuredHeight() / 3, 0);
                translateAnimation.setDuration(duration);
                translateAnimation.setFillAfter(true);
            } else {
                translateAnimation = new TranslateAnimation(0, x * xflag - mMainMenu.getMeasuredWidth() / 3, 0, y * yflag - mMainMenu.getMeasuredHeight() / 3);
                translateAnimation.setDuration(duration);
                translateAnimation.setFillAfter(true);
            }
            translateAnimation.setStartOffset((i * 100) / count);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mStatus == Status.CLOSE) {
                        childView.setVisibility(GONE);
                        childView.setClickable(false);
                        childView.setFocusable(false);
                    }
                    if(mStatus == Status.OPEN) {
                        childView.setVisibility(VISIBLE);
                        childView.setClickable(true);
                        childView.setFocusable(true);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            RotateAnimation rotateAnimation = new RotateAnimation(0f, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(duration);
            rotateAnimation.setFillAfter(true);

            animationSet.addAnimation(rotateAnimation);
            animationSet.addAnimation(translateAnimation);
            childView.startAnimation(animationSet);

            final int pos = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickChildMenu(pos);
                    if(mOnSatMenuClickListener != null) {
                        mOnSatMenuClickListener.onSatMenuClick(childView);
                    }
                    changeStatus();
                }
            });
        }
        changeStatus();
    }

    private void changeStatus() {
        mStatus = (mStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
    }

    private void clickChildMenu(int pos) {
        for(int i = 0; i < getChildCount() - 1; i ++) {
            View childView = getChildAt(i + 1);
            if(pos == i + 1) {
                AnimationSet animationSet = new AnimationSet(true);
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 3.0f, 1.0f, 3.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setFillAfter(true);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
                alphaAnimation.setFillAfter(true);
                animationSet.addAnimation(scaleAnimation);
                animationSet.addAnimation(alphaAnimation);
                animationSet.setDuration(300);
                childView.startAnimation(animationSet);
            } else {
                AnimationSet animationSet = new AnimationSet(true);
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setFillAfter(true);
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
                alphaAnimation.setFillAfter(true);
                animationSet.addAnimation(scaleAnimation);
                animationSet.addAnimation(alphaAnimation);
                animationSet.setDuration(300);
                childView.startAnimation(animationSet);
            }
            childView.setVisibility(GONE);
        }
    }

    private void layoutMainMenu() {
        mMainMenu = getChildAt(0);
        mMainMenu.setOnClickListener(this);
        int left = 0;
        int top = 0;
        switch (mPosition) {
            case LEFT_TOP:
                left = 0;
                top = 0;
                break;
            case LEFT_BOTTOM:
                left = 0;
                top = getMeasuredHeight() - mMainMenu.getMeasuredHeight();
                break;
            case RIGHT_TOP:
                left = getMeasuredWidth() - mMainMenu.getMeasuredWidth();
                top = 0;
                break;
            case RIGHT_BOTTOM:
                left = getMeasuredWidth() - mMainMenu.getMeasuredWidth();
                top = getMeasuredHeight() - mMainMenu.getMeasuredHeight();
                break;
        }
        mMainMenu.layout(left, top, left + mMainMenu.getMeasuredWidth(), top + mMainMenu.getMeasuredHeight());
    }

    private void layoutChildMenu() {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            View childView = getChildAt(i + 1);
            childView.setVisibility(INVISIBLE);
            int left = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));
            int top = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            switch (mPosition) {
                case LEFT_TOP:
                    break;
                case LEFT_BOTTOM:
                    top = getMeasuredHeight() - top - childView.getMeasuredHeight();
                    break;
                case RIGHT_TOP:
                    left = getMeasuredWidth() - left-  childView.getMeasuredWidth();
                    break;
                case RIGHT_BOTTOM:
                    left = getMeasuredWidth() - left-  childView.getMeasuredWidth();
                    top = getMeasuredHeight() - top - childView.getMeasuredHeight();
                    break;
            }
            childView.layout(left, top, left + childView.getMeasuredWidth(), top + childView.getMeasuredHeight());
        }
    }

    public Boolean isExpend() {
        return mStatus == Status.OPEN;
    }

    public void change() {
        dealChildMenu(150);
    }

    public void setOnSatMenuClickListener(OnSatMenuClickListener onSatMenuClickListener) {
        mOnSatMenuClickListener = onSatMenuClickListener;
    }

    public interface OnSatMenuClickListener {
        void onSatMenuClick(View view);
    }

}
