package com.binbin.changescreenvertical;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.binbin.changescreenvertical.fragment.Fragment1;
import com.binbin.changescreenvertical.fragment.Fragment2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private VerticalScrollLayout verticalScrollLayout;
    private View frontView,currentView,nextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verticalScrollLayout= (VerticalScrollLayout) findViewById(R.id.activity_main);
        final View v1=LayoutInflater.from(this).inflate(R.layout.view,null);
        final View v2=LayoutInflater.from(this).inflate(R.layout.view2,null);
        final View v3=LayoutInflater.from(this).inflate(R.layout.view3,null);
        verticalScrollLayout.initViews(v1,v2,v3);//放在前面后面没有影响
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final View v = ((Activity) MainActivity.this).findViewById(android.R.id.content);
//                Bitmap bit=Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
//                v.draw(new Canvas(bit));
//                v1.setBackgroundDrawable(new BitmapDrawable(bit));
//            }
//        },2000);
        ListView lv= (ListView) v2.findViewById(R.id.lv);
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i+"");
        }
        lv.setAdapter(new ArrayAdapter<String>(this,R.layout.view,R.id.tv,list));

        ViewPager vp= (ViewPager) v3.findViewById(R.id.vp);
        final List<Fragment> fragmentList=new ArrayList<>();
        fragmentList.add(new Fragment1());
        fragmentList.add(new Fragment2());
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                if (fragmentList != null)
                    return fragmentList.size();
                else return 0;
            }
        });

        verticalScrollLayout.setOnScrollFinished(new VerticalScrollLayout.OnScrollFinished() {
            @Override
            public void onScrollFinished() {
                initListeners();
            }
        });
        initListeners();
    }

    private void initListeners(){
        verticalScrollLayout.getViews().get(1).findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"hhhhhhhhhhhhhhhhhhh",Toast.LENGTH_LONG).show();
//                startActivity(new Intent(MainActivity.this,SecondActivity.class));
//                startActivity(new Intent(MainActivity.this,ThirdActivity.class));
                startActivity(new Intent(MainActivity.this,FourthActivity.class));
            }
        });
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                Log.e("tianbin","activity====dispatchTouchEvent===down");
////                return false;//无论是true false  move up都会继续触发，viewgroup中的不再触发，onTouchEvent中down不再触发，move up会触发
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.e("tianbin","activity====dispatchTouchEvent===move");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.e("tianbin","activity===dispatchTouchEvent====up");
//                break;
//        }
//        return super.dispatchTouchEvent(ev);//此处无论true false只走此方法，后面的所有的都不触发
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                Log.e("tianbin","activity====onTouchEvent===down");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.e("tianbin","activity====onTouchEvent===move");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.e("tianbin","activity===onTouchEvent====up");
//                break;
//        }
//        return super.onTouchEvent(ev);
//    }
}
