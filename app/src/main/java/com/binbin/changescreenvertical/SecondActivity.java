package com.binbin.changescreenvertical;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private VerticalScrollLayout2 verticalScrollLayout;
    private ImageView iv;
    private int start;
    private boolean isBeginUp=false;
    private boolean isBeginDown=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        verticalScrollLayout= (VerticalScrollLayout2) findViewById(R.id.activity_main2);
        ListView lv= (ListView) findViewById(R.id.lv);
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i+"");
        }
        lv.setAdapter(new ArrayAdapter<String>(this,R.layout.view,R.id.tv,list));
        iv= (ImageView) findViewById(R.id.iv);
        verticalScrollLayout.setOnScrollListener(new VerticalScrollLayout2.OnScrollListener() {
            @Override
            public void onScrollFinished() {
                iv.setVisibility(View.GONE);
                isBeginUp=false;
                isBeginDown=false;
            }

            @Override
            public void onScrolling(int deltaY) {
                Log.e("tianbin1",verticalScrollLayout.getScrollY()+"gggggggggggggggggg"+deltaY);
                int CONTENT_HEIGHT=0;
//                if(deltaY>0){
//                    if(!isBeginUp){
//                        isBeginUp=true;
//                        iv.setVisibility(View.VISIBLE);
//                        CONTENT_HEIGHT=getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
//                        start=CONTENT_HEIGHT;
//                    }
//                    ObjectAnimator.ofFloat(iv,"translationY",start,start-deltaY).start();
//                    start-=deltaY;
//                }else if(deltaY<0){
//                    if(!isBeginDown){
//                        isBeginDown=true;
//                        iv.setVisibility(View.VISIBLE);
//                        CONTENT_HEIGHT=getWindow().getDecorView().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
//                        start=-CONTENT_HEIGHT;
//                    }
//                    ObjectAnimator.ofFloat(iv,"translationY",start,start-deltaY).start();
//                    start-=deltaY;
//                }
            }
        });
    }
}
