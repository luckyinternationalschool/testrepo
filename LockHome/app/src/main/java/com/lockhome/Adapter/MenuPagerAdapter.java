package com.lockhome.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import com.lockhome.AppData;
import com.lockhome.R;

import java.util.ArrayList;


public class MenuPagerAdapter extends android.support.v4.view.PagerAdapter {
    //
    Activity context;

    ArrayList<AppData> arrayOfData = new ArrayList<>();


    LayoutInflater inflate;

    int pagercount = 0;


    public MenuPagerAdapter(Activity act, ArrayList<AppData> arr, int pagercount) {

        this.context = act;
        this.arrayOfData = arr;
        this.pagercount = pagercount;

    }


    public int getCount() {
        return pagercount;
    }


    public Object instantiateItem(View collection, final int position) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_data, null);


        if (position==0){
            view.setBackgroundColor(Color.parseColor("#000fff"));
        }else {
            view.setBackgroundColor(Color.parseColor("#fff000"));
        }




        ((ViewPager) collection).addView(view, 0);

        return view;
    }


    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
