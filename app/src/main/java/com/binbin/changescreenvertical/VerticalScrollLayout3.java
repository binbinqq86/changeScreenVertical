package com.binbin.changescreenvertical;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by -- on 2016/9/30.
 * 上，中，下三屏，可以垂直循环滑动
 * ViewDragHelper实现
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class VerticalScrollLayout3 extends ViewGroup {
    private static final int MIN_FLING_VELOCITY = 3000; // dips per second
    private ViewDragHelper mDragger;
    private Context mContext;
    /**view的高度*/
    private int CONTENT_HEIGHT=0;
    /**-1,0,1*/
    private int currentScreen=0;
    private List<View> mViews=new ArrayList<View>();
    private int ratio;

    public VerticalScrollLayout3(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public VerticalScrollLayout3(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public VerticalScrollLayout3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init(context);
    }

    private void init(Context context) {
        this.mContext=context.getApplicationContext();
        final float density = mContext.getResources().getDisplayMetrics().density;
        mDragger = ViewDragHelper.create(this, 0.5f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return true;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return 0;//禁止左右滑动
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return super.getViewHorizontalDragRange(child);
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return CONTENT_HEIGHT;
            }


            //手指释放的时候回调,后面两个参数代表速度，向右，下滑动为正值
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                Log.e("tianbin","====onViewReleased======"+yvel+"@"+mDragger.getMinVelocity());
                int top=mViews.get(1).getTop();
                //此处也可以通过速度正负来判断滑动方向
                if(top>0&&(top>ratio||yvel>mDragger.getMinVelocity())){
                    //说明是向下滑动，并且距离或者速度符合标准，自动切下一屏
                    mDragger.smoothSlideViewTo(mViews.get(1),0,CONTENT_HEIGHT);
                }else if(top<0&&(Math.abs(top)>ratio||Math.abs(yvel)>mDragger.getMinVelocity())){
                    mDragger.smoothSlideViewTo(mViews.get(1),0,-CONTENT_HEIGHT);
                }else{
                    //回弹
                    mDragger.settleCapturedViewAt(0, 0);
                }
                invalidate();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
//                Log.e("tianbin",dy+"$$$"+top+"=========onViewPositionChanged============");
                if(top>0){
//                    mViews.get(0).layout(0, top-CONTENT_HEIGHT, getWidth(), top );
                    mViews.get(0).setTranslationY(top);
                }else{
//                    mViews.get(2).layout(0,top+CONTENT_HEIGHT,getWidth(),top+2*CONTENT_HEIGHT);
                    mViews.get(2).setTranslationY(top);
                }
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
//                Log.e("tianbin",state+"=========onViewDragStateChanged============");
            }

            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
            }
        });
        mDragger.setMinVelocity(MIN_FLING_VELOCITY * density);
    }

    public void initViews(View frontView,View currentView,View nextView){
        mViews.add(frontView);
        mViews.add(currentView);
        mViews.add(nextView);
        addView(frontView,0,new LayoutParams(-1,-1));
        addView(currentView,1,new LayoutParams(-1,-1));
        addView(nextView,2,new LayoutParams(-1,-1));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        Log.e("tianbin",getChildCount()+"$$%%");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        Log.e("tianbin",getChildCount()+"$$%%===================="+w+"@+"+h+"#"+oldw+"#"+oldh);
    }

    @SuppressLint("NewApi")
    private void reLayout(){
        if(currentScreen>0){
            //说明是最后一屏，此时把第一个屏移动到最下面，则当前屏在中间，重新布局
            View tempView=mViews.get(0);
            mViews.remove(0);
            mViews.add(2, tempView);
            removeViewAt(0);
            addView(mViews.get(2),2,new LayoutParams(-1,-1));
        }else{
            //说明是第一屏，此时把最后一个屏移动到最上面，则当前屏在中间，重新布局
            View tempView=mViews.get(2);
            mViews.remove(2);
            mViews.add(0,tempView);
            removeViewAt(2);
            addView(mViews.get(0),0,new LayoutParams(-1,-1));
        }
        OnScrollListener.onScrollFinished();
        currentScreen=0;
    }

    public List<View> getViews(){
        return mViews;
    }


    private int maxWidth,maxHeight;
    /**
     * 计算所有ChildView的宽度和高度 然后根据ChildView的计算结果，设置ViewGroup自己的宽和高
     * Exactly：width代表的是精确的尺寸
     AT_MOST：width代表的是最大可获得的空间
     MATCH_PARENT(FILL_PARENT)对应于EXACTLY，WRAP_CONTENT对应于AT_MOST
     其他情况(有具体值的)也对应于EXACTLY
     */
    @SuppressLint("NewApi")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //当我们需要重写onMeasure时，记得要调用setMeasuredDimension来设置自身的mMeasuredWidth和mMeasuredHeight，否则，就会抛出异常
        /**
         * 获得此ViewGroup上级容器为其推荐的宽和高，以及计算模式
         */
//      final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//      final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

        /**
         * 根据childView计算的出的宽和高，计算容器的宽和高，主要用于容器是warp_content时
         */
//        for (int i = 0,count = getChildCount(); i < count; i++) {
//            View childView = getChildAt(i);
//            //获取每个子view的自己高度宽度，取最大的就是viewGroup的大小
//            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
//            maxWidth = Math.max(maxWidth,childView.getMeasuredWidth());
//            maxHeight = Math.max(maxHeight,childView.getMeasuredHeight());
//        }
        //为ViewGroup设置宽高
        setMeasuredDimension(measuredWidth,measuredHeight);

        // 计算出所有的childView的宽和高---可用
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        /**
         * 设置所有的childView的宽和高，此处如果不设置，会造成多个子view的情况下，有的子view设置成match_parent但是不能充满父控件的问题
         */
        //首先判断params.width的值是多少，有三种情况。
        //如果是大于零的话，及传递的就是一个具体的值，那么，构造MeasupreSpec的时候可以直接用EXACTLY。
        //如果为-1的话，就是MatchParent的情况，那么，获得父View的宽度，再用EXACTLY来构造MeasureSpec。
        //如果为-2的话，就是wrapContent的情况，那么，构造MeasureSpec的话直接用一个负数就可以了。
//        for (int i = 0,count = getChildCount(); i < count; i++) {
//            View childView = getChildAt(i);
//            int widthSpec = 0;
//            int heightSpec = 0;
//            ViewGroup.LayoutParams params = childView.getLayoutParams();
//            if(params.width > 0){
//                widthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
//            }else if (params.width == -1) {
//                widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY);
//            } else if (params.width == -2) {
//                widthSpec = MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.AT_MOST);
//            }
//
//            if(params.height > 0){
//                heightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
//            }else if (params.height == -1) {
//                heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
//            } else if (params.height == -2) {
//                heightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
//            }
//            childView.measure(widthSpec, heightSpec);
//        }
    }

    /**
     * 首先执行onMeasure，然后就会执行onLayout
     * 为子View指定位置：相对父控件的位置！！！！！！
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(changed&&CONTENT_HEIGHT==0){
            CONTENT_HEIGHT=((Activity)getContext()).getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
            ratio=CONTENT_HEIGHT/3;
        }
//        Log.e("tianbin",CONTENT_HEIGHT+"#########"+right+"#"+bottom);

        //自定义的viewGroup，重写这里是每个子view的margin padding gravity等属性需要重写ViewGroup.MarginLayoutParams，不需要的则不用重写，子view中的布局按照布好的规则自动就是好的，但是必须要重写onmeasure
        //若是继承LinearLayout等现成的布局，则不用重写onmeasure onlayout
        //参考：http://ticktick.blog.51cto.com/823160/1542200/
        mViews.get(0).layout(0,-CONTENT_HEIGHT,right,0);
        mViews.get(1).layout(0,0,right,CONTENT_HEIGHT);
        mViews.get(2).layout(0,CONTENT_HEIGHT,right,2*CONTENT_HEIGHT);

    }

    /**
     * 主要处理事件分发，自己消费或者传递给activity处理
     * @param ev
     * @return
     */
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                Log.e("tianbin","viewgroup====dispatchTouchEvent===down");
////                return true;
//            //true，代表自己处理了，同时onInterceptTouchEvent中的down不会触发,move up会触发，Activity中的dispatchTouchEvent会继续走，但onTouchEvent不触发
//            // false返回给activity处理并且move  up都不触发，同时activity的onTouchEvent执行
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.e("tianbin","viewgroup====dispatchTouchEvent===move");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.e("tianbin","viewgroup===dispatchTouchEvent====up");
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        mDragger.processTouchEvent(ev);
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
        return mDragger.shouldInterceptTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if(mDragger.continueSettling(true)) {
            invalidate();
        }else{
            reLayout();
        }
    }

    private OnScrollListener OnScrollListener;

    public void setOnScrollListener(OnScrollListener OnScrollListener){
        this.OnScrollListener=OnScrollListener;
    }

    public interface OnScrollListener{
        void onScrollFinished();
    }
}
