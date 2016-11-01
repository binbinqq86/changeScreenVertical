package com.binbin.changescreenvertical;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by -- on 2016/9/30.
 * 只有一个界面，用来滑动，滑动完成仍然显示该界面
 * 更完整的使用——详见项目(酷客2.1版本)。。。
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class VerticalScrollLayout2 extends FrameLayout {
    /** 用于滑动的类*/
    private Scroller mScroller;
    /** 用来跟踪触摸速度的类*/
    private VelocityTracker mVelocityTracker;
    /** 最小滑动的速度*/
    private static final int SNAP_VELOCITY = 600;
    /**最小滑动距离，超过了，才认为开始滑动  */
    private int mTouchSlop = 0 ;
    /**上次触摸的X坐标*/
    private float mLastX = -1;
    /**上次触摸的Y坐标*/
    private float mLastY = -1;
    private Context mContext;
    /**view的高度*/
    private int CONTENT_HEIGHT=0;
    private boolean isFinished=false;
    private int totalScrollY;
    private int ratio;
    private List<ViewGroup> scrollViews=new LinkedList<>();


    public VerticalScrollLayout2(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public VerticalScrollLayout2(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public VerticalScrollLayout2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init(context);
    }

    private void init(Context context) {
        this.mContext=context;
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(changed&&CONTENT_HEIGHT==0){
            CONTENT_HEIGHT=((Activity)getContext()).getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
            ratio=CONTENT_HEIGHT/3;
        }
    }

    /**
     * 滚动，startX, startY为开始滚动的位置（包括移出屏幕的地方的坐标，一般以滑动的距离为起点）
     * dx,dy为滚动的偏移量（正值代表向左，向上移动，负值代表向右，向下移动）
     * duration为完成滚动的时间
     * mScroller.startScroll(int startX, int startY, int dx, int dy)
     * 使用默认完成时间250ms
     * mScroller.startScroll(int startX, int startY, int dx, int dy, int duration)
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if (mVelocityTracker == null) {
            // 使用obtain方法得到VelocityTracker的一个对象
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // 计算当前的速度
                if((Math.abs(mLastY-ev.getRawY())>mTouchSlop)&&
                                (Math.abs(mLastX-ev.getRawX())<mTouchSlop)){

                    int deltaY = (int) (mLastY - ev.getRawY());//负的代表向下滑
                    mLastY = ev.getRawY();
                    mLastX=ev.getRawX();
                    totalScrollY+=deltaY;

                    scrollBy(0,deltaY);
                    if(onScrollListener!=null){
                        onScrollListener.onScrolling(deltaY);
                    }
                    Log.e("tianbin",getScrollY()+"tianbin@@@@@moveeeeee@@@@@@@"+totalScrollY);
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(100);
                //正的代表向下滑动，速度沿Y轴正方向
                int velocityY = (int) velocityTracker.getYVelocity();
                Log.e("tianbin",velocityY+"$$$$$$$$$$$$$$velocityY");
                //总共滑动的距离：getScrollY()（负值代表向下）
                //速度加滑动距离满足一个即自动显示或隐藏
                int delta=0;
                int scrollY=getScrollY()%CONTENT_HEIGHT;

                Log.e("tianbin",getScrollY()+"tianbin@@@@@uppppppppp"+totalScrollY+"###"+scrollY);
                if(Math.abs(velocityY)>= SNAP_VELOCITY||
                        Math.abs(getScrollY())>ratio){
                    //自动滑屏
                    if(getScrollY()>0){
                        //说明向上滑，则自动切换到下一屏
                        delta=CONTENT_HEIGHT-scrollY;
                        mScroller.startScroll(0,getScrollY(), 0,Math.abs(delta));
                        invalidate();
                        isFinished=true;//切屏才算结束，回弹不算
                    }else if(getScrollY()<0){
                        //说明向下滑，则自动切换到上一屏
                        delta=Math.abs(scrollY)-CONTENT_HEIGHT;
                        mScroller.startScroll(0,getScrollY(), 0,-Math.abs(delta));
                        invalidate();
                        isFinished=true;//切屏才算结束，回弹不算
                    }
                }else{
                    //自动回弹
                    if(getScrollY()>0){
                        //说明向上滑，滑动距离或者速度不够，则自动向下回弹
                        delta=-scrollY;
                        mScroller.startScroll(0,getScrollY(), 0,-Math.abs(delta));
                        invalidate();
                    }else if(getScrollY()<0){
                        //说明向下滑，滑动距离或者速度不够，则自动向上回弹
                        delta=scrollY;
                        mScroller.startScroll(0,getScrollY(), 0,Math.abs(delta));
                        invalidate();
                    }
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                totalScrollY=0;
                break;
        }
        return true;
    }

    /**
     * 是否拦截事件交给自己处理，否则交给子view处理
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        getAllScrollView(scrollViews,this);//只判断当前屏幕中的view
        if(scrollViews.size()>0&&inRangeOfView(scrollViews,ev)){
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX=ev.getRawX();
                mLastY=ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if((Math.abs(mLastY-ev.getRawY())>mTouchSlop)&&
                                (Math.abs(mLastX-ev.getRawX())<mTouchSlop)){
                    //自己来处理滑动
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * ViewGroup在分发绘制自己的孩子的时候，会对其子View调用computeScroll()方法
     */
    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }else{//滑动结束后
            if(isFinished){
                isFinished=false;
                Log.e("tianbin","tianbincomplete-------------"+getScrollY());
                mScroller.startScroll(0,0,0,0,0);
                invalidate();
                if(onScrollListener!=null){
                    onScrollListener.onScrollFinished();
                }
                Log.e("tianbin","tianbincomplete3333-------------"+getScrollY());
            }
        }
    }

    /**
     * 判断子view是否包含可滑动的view
     * 当子view包含viewPager，listView，GridView，scrollView等控件时，交给子view处理滑动事件
     * @return  注意递归循环return只能退出当前递归，不能退出整个函数，可以使用抛异常或者全局变量控制
     */
    private void getAllScrollView(List<ViewGroup> scrollViews,View parent){
        if(isScrollViewGroup(parent)!=null){
            scrollViews.add((ViewGroup) isScrollViewGroup(parent));
        }else if(parent instanceof ViewGroup){
            int childCount = ((ViewGroup)parent).getChildCount();
            for(int i=0; i<childCount; i++){
                View child = ((ViewGroup)parent).getChildAt(i);
                if(isScrollViewGroup(child)!=null){
                    scrollViews.add((ViewGroup) isScrollViewGroup(child));
                }else if(child instanceof ViewGroup){
                    getAllScrollView(scrollViews,child);
                }
            }
        }
    }

    /**
     * 判断当前view是否是可滑动的view
     * @param view
     * @return
     */
    private View isScrollViewGroup(View view){
        if(view instanceof ViewPager||
                view instanceof ScrollView ||
                view instanceof ListView||
                view instanceof RecyclerView ||
                view instanceof GridView){
            return view;
        }
        return null;
    }

    /**
     * 判断是否点击在view的内部
     * @param view
     * @param ev
     * @return
     *            true 点击在view的内部
     *            false 点击在view的外部
     */
    private boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }
    private boolean inRangeOfView(List<ViewGroup> scrollViews, MotionEvent ev) {
        for (int i = 0; i < scrollViews.size(); i++) {
            int[] location = new int[2];
            View view=scrollViews.get(i);
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y || ev.getY() > (y + view.getHeight())) {
            }else{
                return true;//有一个被点击到就返回
            }
        }
        return false;
    }

    private OnScrollListener onScrollListener;

    public void setOnScrollListener(OnScrollListener onScrollListener){
        this.onScrollListener=onScrollListener;
    }

    public interface OnScrollListener{
        void onScrollFinished();
        void onScrolling(int deltaY);
    }
}
