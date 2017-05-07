package com.rdc.mymap.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.rdc.mymap.R;

public class MyMenu extends ViewGroup implements View.OnClickListener{

    private static final int POS_LEFT_TOP = 0;
    private static final int POS_RIGHT_TOP = 1;
    private static final int POS_LEFT_BOTTOM = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    public enum Status {
        OPEN, CLOSE
    }

    public interface OnMenuItemClickListener {
        void onClick(View view, int position);
    }

    private Position mPosition = Position.RIGHT_BOTTOM;
    private Status mStatus = Status.CLOSE;
    private View mMainMenu;
    private int mInterval;
    private OnMenuItemClickListener mOnMenuItemClickListener;

    public MyMenu(Context context) {
        this(context, null);
    }

    public MyMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mInterval = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyMenu, defStyleAttr, 0);
        int position = typedArray.getInt(R.styleable.MyMenu_pos, POS_RIGHT_BOTTOM);
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
        mInterval = (int) typedArray.getDimension(R.styleable.MyMenu_interval, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
        typedArray.recycle();
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            layoutMainMenu();
            int count = getChildCount();
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);
                child.setVisibility(GONE);
                int left = l;
                int top = mInterval * (i + 1) + 20;
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                if(mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                    top = getMeasuredHeight() - height - top;
                }
                if(mPosition == Position.RIGHT_TOP || mPosition == Position.RIGHT_BOTTOM) {
                    left = getMeasuredWidth() - width - left;
                }
                child.layout(left, top, left + width, top + height);
            }
        }
    }

    private void layoutMainMenu() {
        mMainMenu = getChildAt(0);
        mMainMenu.setOnClickListener(this);
        int left = 0;
        int top = 0;
        int width = mMainMenu.getMeasuredWidth();
        int height = mMainMenu.getMeasuredHeight();
        switch (mPosition) {
            case LEFT_TOP:
                left = 0;
                top = 0;
                break;
            case LEFT_BOTTOM:
                left = 0;
                top = getMeasuredHeight() - height;
                break;
            case RIGHT_TOP:
                left = getMeasuredWidth() - width;
                top = 0;
                break;
            case RIGHT_BOTTOM:
                left = getMeasuredWidth() - width;
                top = getMeasuredHeight() - height;
                break;
            default:
                break;
        }
        mMainMenu.layout(left, top, left + width, top + height);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main_menu :
                dealMainMenu(300);
                Log.e("error", "click main menu");
                break;
            default:
                break;
        }
    }

    private void dealMainMenu(int duration) {
        int count = getChildCount();
        for (int i = 0; i < count - 1; i++) {
            final View child = getChildAt(i + 1);
            child.setVisibility(VISIBLE);
            int top = mInterval * (i + 1);
            int yflag = -1;
            if(mPosition == Position.LEFT_BOTTOM || mPosition == Position.RIGHT_BOTTOM) {
                yflag = 1;
            }
            Animation animation = null;
            if(mStatus == Status.OPEN) {
                animation = new TranslateAnimation(0, 0, 0, top * yflag);
                child.setClickable(false);
                child.setFocusable(false);
            } else {
                animation = new TranslateAnimation(0, 0, top * yflag, 0);
                child.setClickable(true);
                child.setFocusable(true);
            }
            animation.setFillAfter(true);
            animation.setDuration(duration);
            animation.setStartOffset((i * 100) / count);
            child.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mStatus == Status.CLOSE) {
                        child.clearAnimation();
                        child.setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            final int position = i;
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.onClick(v, position);
                    }
                    dealMenuItem(position);
                    changeStatus();
                }
            });
        }
        findViewById(R.id.my_menu).setAnimation(new AlphaAnimation(0.0f, 1.0f));
        // TODO: 2017/5/1  
        changeStatus();
    }

    private void changeStatus() {
        mStatus = (mStatus == Status.OPEN ? Status.CLOSE : Status.OPEN);
    }

    private void dealMenuItem(int position) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View child = getChildAt(i + 1);
            if(i == position) {
                child.startAnimation(scaleBigAnim(150));
            } else {
                child.startAnimation(scaleSmallAnim(150));
            }
            child.setClickable(false);
            child.setFocusable(false);
        }
    }

    private Animation scaleSmallAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2.0f, 1.0f, 2.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    public void setMainMenuBackground(int pos) {
        switch (pos) {
            case 0 :
                findViewById(R.id.iv_main_menu).setBackgroundResource(R.drawable.ofo);
                break;
            case 1 :
                findViewById(R.id.iv_main_menu).setBackgroundResource(R.drawable.mobike);
                break;
            case 2 :
                findViewById(R.id.iv_main_menu).setBackgroundResource(R.drawable.mingbike);
                break;
            case 3 :
                findViewById(R.id.iv_main_menu).setBackgroundResource(R.drawable.uibike);
                break;
            case 4 :
                findViewById(R.id.iv_main_menu).setBackgroundResource(R.drawable.bluegogo);
                break;
            case 5 :
                findViewById(R.id.iv_main_menu).setBackgroundResource(R.drawable.hellobike);
                break;
            default:
                break;
        }
    }

    public Boolean isExpand() {
        return mStatus == Status.OPEN;
    }

    public void change() {
        dealMenuItem(-1);
        changeStatus();
    }
}
