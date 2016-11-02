package com.binbin.changescreenvertical.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binbin.changescreenvertical.R;

/**
 * Created by -- on 2016/11/2.
 */

public class Fragment2 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.view, container, false);
        view.setBackgroundColor(Color.YELLOW);
        ((TextView)view.findViewById(R.id.tv)).setText("fragment2===========");
        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
