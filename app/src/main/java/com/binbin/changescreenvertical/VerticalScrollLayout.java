package com.binbin.changescreenvertical;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by -- on 2016/9/30.
 * 上，中，下三屏，可以垂直循环滑动
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class VerticalScrollLayout extends ViewGroup {
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
    /**-1,0,1*/
    private int currentScreen=0;
    private boolean isFinished=false;
    private int totalScrollY;
    private List<View> mViews=new ArrayList<View>();
    private int ratio;
    private List<ViewGroup> scrollViews=new LinkedList<>();


    public VerticalScrollLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public VerticalScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context);
    }

    public VerticalScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init(context);
    }

    private void init(Context context) {
        this.mContext=context;
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
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
        Log.e("tianbin","======onFinishInflate======"+getChildCount());
    }

    private void layout(int right){
//        setScrollY(0);
        mViews.get(0).layout(0,-CONTENT_HEIGHT,right,0);
        mViews.get(1).layout(0,0,right,CONTENT_HEIGHT);
        mViews.get(2).layout(0,CONTENT_HEIGHT,right,2*CONTENT_HEIGHT);
    }

    /**
     * 此处详细解释请见{@link com.binbin.changescreenvertical.VerticalScrollLayout4}
     */
    private void reLayout(){
        if(currentScreen>0){
            //说明是最后一屏，此时把第一个屏移动到最下面，则当前屏在中间，重新布局
            View tempView=mViews.get(0);
            mViews.remove(0);
            mViews.add(2, tempView);
//            removeViewAt(0);
//            addView(mViews.get(2),2,new LayoutParams(-1,-1));
        }else{
            //说明是第一屏，此时把最后一个屏移动到最上面，则当前屏在中间，重新布局
            View tempView=mViews.get(2);
            mViews.remove(2);
            mViews.add(0,tempView);
//            removeViewAt(2);
//            addView(mViews.get(0),0,new LayoutParams(-1,-1));
        }
        onScrollFinished.onScrollFinished();
        currentScreen=0;
        setScrollY(0);//也可以放到layout中
//        scrollTo(0,0);
        layout(getWidth());
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
        layout(right);

        //怎么处理weight  margin  padding  gravity等？？？日后处理，包括github上的垂直滑动的广告viewpager也有问题
        //待研究。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
//        for (int j = 0; j < mViews.size(); j++) {
//            int pTop=(j-1)*CONTENT_HEIGHT;
//            mViews.get(j).layout(0,pTop,right,pTop+CONTENT_HEIGHT);
//            if(mViews.get(j) instanceof ViewGroup /**&& isScrollViewGroup(mViews.get(j))==null*/){
//                final int childCount = ((ViewGroup)mViews.get(j)).getChildCount();
//                int childLeft = 0;
//                int childTop=(j-1)*CONTENT_HEIGHT;
//                for (int i = 0; i < childCount; i++) {
//                    final View childView = ((ViewGroup)mViews.get(j)).getChildAt(i);
//                    final int childWidth = childView.getMeasuredWidth();
//                    final int childHeight=childView.getMeasuredHeight();
//                    childView.layout(childLeft+getPaddingLeft(), childTop+getPaddingTop(), childLeft + childWidth,
//                            childTop+childHeight);
//                    childTop += childHeight;
//                }
//            }
//        }
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

                    int deltaY = (int) (mLastY - ev.getRawY());
                    mLastY = ev.getRawY();
                    mLastX=ev.getRawX();
                    totalScrollY+=deltaY;

//                    mScroller.startScroll(0, getScrollY(), 0, deltaY,0);
//                    invalidate();
                    scrollBy(0,deltaY);
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
                        Math.abs(totalScrollY)>ratio){
                    //自动滑屏
                    if(totalScrollY>0){
                        //说明向上滑，则自动切换到下一屏
                        if(getScrollY()<0){
                            delta=getScrollY();
                        }else{
                            delta=CONTENT_HEIGHT-scrollY;
                        }
                        mScroller.startScroll(0,getScrollY(), 0,Math.abs(delta));
                        invalidate();
                        currentScreen++;
                        isFinished=true;//切屏才算结束，回弹不算
                    }else if(totalScrollY<0){
                        //说明向下滑，则自动切换到上一屏
                        if(getScrollY()>0){
                            delta=-getScrollY();
                        }else{
                            delta=Math.abs(scrollY)-CONTENT_HEIGHT;
                        }
                        mScroller.startScroll(0,getScrollY(), 0,-Math.abs(delta));
                        invalidate();
                        currentScreen--;
                        isFinished=true;//切屏才算结束，回弹不算
                    }
                }else{
                    //自动回弹
                    if(totalScrollY>0){
                        //说明向上滑，滑动距离或者速度不够，则自动向下回弹
                        if(getScrollY()<0){
                            delta=CONTENT_HEIGHT-Math.abs(getScrollY());
                        }else{
                            delta=-scrollY;
                        }
                        mScroller.startScroll(0,getScrollY(), 0,-Math.abs(delta));
                        invalidate();
                    }else if(totalScrollY<0){
                        //说明向下滑，滑动距离或者速度不够，则自动向上回弹
                        if(getScrollY()>0){
                            delta=CONTENT_HEIGHT-getScrollY();
                        }else{
                            delta=scrollY;
                        }
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
        getAllScrollView(scrollViews,mViews.get(1));//只判断当前屏幕中的view
        if(scrollViews.size()>0&&inRangeOfView(scrollViews,ev)){
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
//                Log.e("tianbin","viewgroup====onInterceptTouchEvent===down");
                mLastX=ev.getRawX();
                mLastY=ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e("tianbin","viewgroup====onInterceptTouchEvent===move");
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
        if(/**view instanceof ViewPager||*/
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
                reLayout();
                Log.e("tianbin","tianbincomplete3333-------------"+getScrollY());
            }
        }
    }

    private OnScrollFinished onScrollFinished;

    public void setOnScrollFinished(OnScrollFinished onScrollFinished){
        this.onScrollFinished=onScrollFinished;
    }

    public interface OnScrollFinished{
        void onScrollFinished();
    }
}
