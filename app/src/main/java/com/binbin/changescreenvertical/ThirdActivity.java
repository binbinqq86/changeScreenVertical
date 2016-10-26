package com.binbin.changescreenvertical;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends AppCompatActivity {

    private VerticalScrollLayout3 verticalScrollLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        verticalScrollLayout= (VerticalScrollLayout3) findViewById(R.id.activity_main);
        final View v1=LayoutInflater.from(this).inflate(R.layout.view,null);
        final View v2=LayoutInflater.from(this).inflate(R.layout.view2,null);
        final View v3=LayoutInflater.from(this).inflate(R.layout.view3,null);
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
        v2.findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ThirdActivity.this,"00000000000000",Toast.LENGTH_SHORT).show();
            }
        });
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i+"");
        }
        lv.setAdapter(new ArrayAdapter<String>(this,R.layout.view,R.id.tv,list));
        verticalScrollLayout.initViews(v1,v2,v3);
    }

}
