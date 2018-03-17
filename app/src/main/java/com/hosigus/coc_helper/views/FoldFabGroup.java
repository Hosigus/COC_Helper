package com.hosigus.coc_helper.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import com.hosigus.coc_helper.R;

import java.util.List;


/**
 * Created by 某只机智 on 2018/2/12.
 * 参考资料自己写的ViewGroup你敢信？
 */

public class FoldFabGroup extends ViewGroup {
    public static final int FOLD = 1 ;
    public static final int UNFOLD = -1 ;

    private int mWidth;
    private int mHeight;
    private int padding;
    private int maxDistance;
    private int maxAngle;
    private int foldFlag ;
    private int mainChildIndex;
    private boolean isNeedDamperScreen;
    private int damperScreenColor;
    private View damperScreen;
    private float mScale ;//展开之后的缩放比例
    private int mDuration;//动画时长
    private View mainButton;//在Activity中显示的button
    private List<OnChildViewClickListener> childViewClickListenerList;

    public FoldFabGroup(Context context) {
        super(context);
    }

    public FoldFabGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public FoldFabGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    /**
     * 初始化变量
     * @param context context
     * @param attrs attrs
     */
    private void init(Context context, AttributeSet attrs){
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.FoldFabGroup);
        maxDistance=typedArray.getInteger(R.styleable.FoldFabGroup_maxDistance,200);
        maxAngle=typedArray.getInteger(R.styleable.FoldFabGroup_maxAngle,90);
        mScale=typedArray.getFloat(R.styleable.FoldFabGroup_scale,0.8f);
        isNeedDamperScreen=typedArray.getBoolean(R.styleable.FoldFabGroup_isNeedDamperScreen,false);
        damperScreenColor=typedArray.getColor(R.styleable.FoldFabGroup_DamperScreenColor, Color.WHITE);
        padding=typedArray.getInteger(R.styleable.FoldFabGroup_fabPadding,16);
        foldFlag = FOLD;
        mDuration = 400;
        typedArray.recycle();

        mainChildIndex=0;
        if(isNeedDamperScreen){
            initDamperScreen(context);
            mainChildIndex++;
        }
    }
    private void initDamperScreen(Context context) {
        damperScreen = new View(context);
        damperScreen.setBackgroundColor(damperScreenColor);
        damperScreen.setAlpha(0);
        damperScreen.setVisibility(INVISIBLE);
        damperScreen.setOnClickListener((v)-> mainButton.callOnClick());
        addView(damperScreen);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec,heightMeasureSpec);

        if (getChildCount() < mainChildIndex+2) {
            mHeight=0;
            mWidth=0;
        } else {
            if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST ) {
                mWidth = getChildAt(mainChildIndex).getWidth()+maxDistance+getChildAt(getChildCount()-1).getWidth();
            }else {
                mWidth = MeasureSpec.getSize(widthMeasureSpec);
            }
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST){
                mHeight = getChildAt(mainChildIndex).getHeight()+maxDistance+getChildAt(mainChildIndex+1).getHeight();
            }else {
                mHeight=MeasureSpec.getSize(heightMeasureSpec);
            }
        }
        setMeasuredDimension(mWidth,mHeight);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int cCount = getChildCount();
        if(cCount==mainChildIndex){
            return;
        }
        if(isNeedDamperScreen){
            damperScreen.layout(0,0,mWidth,mHeight);
        }
        mainButton = getChildAt(mainChildIndex);
        int width = mainButton.getMeasuredWidth();
        int height = mainButton.getMeasuredHeight();
        int left=mWidth-width-padding,top=mHeight-height-padding,right=mWidth-padding,bottom=top+height;

        mainButton.layout(left,top,right,bottom);
        for(int i=mainChildIndex+1;i<cCount;i++) {
            final View view = getChildAt(i);
            view.layout(left,top,right,bottom);
            view.setVisibility(INVISIBLE);
        }

        setMainButtonListener();
        setChildrenListener();

    }

    /**
     * 设置子按钮的点击事件
     */
    private void setChildrenListener() {
        final int cCount = getChildCount();
        for(int i=mainChildIndex+1;i<cCount;i++){
            final View view = getChildAt(i);
            final OnChildViewClickListener clickListener=childViewClickListenerList.get(i);
            //设置点击时候回调点击事件并且缩回原来的位置
            view.setOnClickListener((v)->{
                foldFlag = FOLD;
                foldTranslation();
                if (clickListener!=null){
                    clickListener.onClick();
                }
            });

        }
    }

    /**
     * 设置主按钮的点击事件
     */
    private void setMainButtonListener() {
        mainButton.setOnClickListener(v -> {
            foldFlag=(-foldFlag);
            if(foldFlag==FOLD) {
                foldTranslation();
            }else {
                unfoldTranslation();
            }
        });
    }

    /**
     * 折叠动画
     */
    private void foldTranslation() {
        int cCount =getChildCount();
        if (isNeedDamperScreen){
            ObjectAnimator alpha = ObjectAnimator.ofFloat(damperScreen,"alpha",0);
            alpha.setDuration(mDuration);
            alpha.setInterpolator(new AccelerateInterpolator());
            alpha.start();
            alpha.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    damperScreen.setVisibility(INVISIBLE);
                }
            });
        }
        for (int i = mainChildIndex+1; i < cCount; i++) {
            final View view = getChildAt(i);
            ObjectAnimator tX = ObjectAnimator.ofFloat(view,"translationX",0);
            ObjectAnimator tY = ObjectAnimator.ofFloat(view,"translationY",0);
            ObjectAnimator alpha  = ObjectAnimator.ofFloat(view,"alpha",0);//透明度 0为完全透明
            ObjectAnimator rotate = ObjectAnimator.ofFloat(view,"rotation",0);
            AnimatorSet set = new AnimatorSet(); //动画集合
            set.play(tX).with(tY).with(alpha).with(rotate);
            set.setDuration(mDuration); //持续时间
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
            //动画完成后 设置为不可见
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(INVISIBLE);
                }
            });
        }
    }

    /**
     * 展开时的动画
     */
    private void unfoldTranslation() {
        if (isNeedDamperScreen){
            damperScreen.setVisibility(VISIBLE);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(damperScreen,"alpha",0f,0.8f);
            alpha.setDuration(mDuration);
            alpha.setInterpolator(new DecelerateInterpolator());
            alpha.start();
        }
        int cCount=getChildCount();
        int perAngle=maxAngle/(cCount-2-mainChildIndex);
        for (int i = mainChildIndex+1,child=mainChildIndex+1; i < cCount; i++) {
            View view = getChildAt(i);
            view.setVisibility(VISIBLE);
            int x  = (int) (maxDistance *Math.sin(Math.toRadians(perAngle * (i - child))));
            int y = (int) (maxDistance *Math.cos(Math.toRadians(perAngle * (i - child))));
            ObjectAnimator tX = ObjectAnimator.ofFloat(view,"translationX",-x);
            ObjectAnimator tY = ObjectAnimator.ofFloat(view,"translationY",-y);
            ObjectAnimator alpha  = ObjectAnimator.ofFloat(view,"alpha",1);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view,"scaleX",mScale);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view,"scaleY",mScale);
            ObjectAnimator rotate=ObjectAnimator.ofFloat(view,"rotation",360);

            AnimatorSet set = new AnimatorSet();
            set.play(tX).with(tY).with(alpha);
            set.play(scaleX).with(scaleY).with(rotate).with(tX);
            set.setDuration(mDuration);
            set.setInterpolator(new AccelerateDecelerateInterpolator());
            set.start();
        }
    }

    public void setClickListeners(List<OnChildViewClickListener> listeners){
        for (int i = -1; i < mainChildIndex ; i++) {
            listeners.add(0,null);
        }
        childViewClickListenerList=listeners;
    }
    public interface OnChildViewClickListener{
        void onClick();
    }

}