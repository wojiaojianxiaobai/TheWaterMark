package com.xiaobai.thewatermark.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


public class LeftHold extends HorizontalScrollView{
    private LinearLayout mScrollView;   //定义横向滚动条布局
    private ViewGroup mMenu;            //定义菜单区域
    private ViewGroup mContent;         //定义主显示区域
    private int mScreenWidth;           //定义屏幕宽度
    private int mMenuRightPadding;      //定义菜单右边距为50dp
    private boolean call;               //定义只设置一次横向滚动师徒与子视图的宽度
    private int mMenuWidth;             //定义菜单宽度

    private boolean isOpen;     //定义初始状态，默认false






    //构造方法
    public LeftHold(Context context, AttributeSet attrs) {
        super(context, attrs);





        this.setHorizontalScrollBarEnabled(false);  //隐藏滚动条

        //获取窗口管理服务
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //创建显示尺寸对象
        DisplayMetrics outMetrics = new DisplayMetrics();
        //获取屏幕当前的宽高尺寸
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //为屏幕的宽度赋值
        mScreenWidth = outMetrics.widthPixels;
        //将50dp转换为像素
        mMenuRightPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,70,context.getResources().getDisplayMetrics());

    }

    //设置横向滚动视图中主显示区域与隐藏区域的宽度
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!call){
            mScrollView = (LinearLayout) getChildAt(0); //获取横向滚动视图
            mMenu = (ViewGroup) mScrollView.getChildAt(0);          //获取菜单显示区域
            mContent = (ViewGroup) mScrollView.getChildAt(1);       //获取主显示区域
            //设置菜单宽度
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth-mMenuRightPadding;
            //设置主显示区域的宽度
            mContent.getLayoutParams().width = mScreenWidth;
            call = true;

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //设置偏移量让菜单隐藏
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            this.scrollTo(mMenuWidth,0);        //隐藏菜单区域
        }
    }

    //判断手指抬起时隐藏菜单还是显示菜单
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action){
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();     //获取隐藏左边的宽度
                if (scrollX>=mMenuWidth/2){
                    this.scrollTo(mMenuWidth,0);        //设置隐藏菜单
                    isOpen = false;
                }else{
                    this.scrollTo(0,0);             //显示菜单
                    isOpen = true;
                }
                return true;

        }
        return super.onTouchEvent(ev);
    }

    public void openMenu(){         //打开菜单
        if (isOpen)return;
        this.smoothScrollTo(0,0);
        isOpen = true;
    }

    public void  closeMenu(){       //关闭菜单
        if (!isOpen)return;
        this.smoothScrollTo(mMenuWidth,0);
        isOpen = false;
    }

    /*切换菜单*/
    public void toggle(){
        if (isOpen){
            closeMenu();
        }else
        {
            openMenu();
        }
    }



}
