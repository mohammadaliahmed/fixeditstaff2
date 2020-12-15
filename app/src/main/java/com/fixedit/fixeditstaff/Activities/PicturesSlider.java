package com.fixedit.fixeditstaff.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.fixedit.fixeditstaff.Adapters.MainSliderAdapter;
import com.fixedit.fixeditstaff.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


public class PicturesSlider extends AppCompatActivity {
    ViewPager viewPager;
    ArrayList<String> pics = new ArrayList<>();
    MainSliderAdapter mViewPagerAdapter;
    private int currentPic;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures_slider);
        viewPager = findViewById(R.id.viewPager);

        pics = getIntent().getStringArrayListExtra("list");
        position = getIntent().getIntExtra("position", 0);

        initViewPager();

    }

    private void initViewPager() {
        mViewPagerAdapter = new MainSliderAdapter(this, pics);
        viewPager.setAdapter(mViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPic = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(position);

        mViewPagerAdapter.notifyDataSetChanged();
    }
}
