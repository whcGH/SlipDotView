package com.example.slipdotview;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.test.library.SlipPointView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        ViewPager view_pager = findViewById(R.id.view_pager);
        view_pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                PageFragment fragment = new PageFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("index", position);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getCount() {
                return 10;
            }
        });
        SlipPointView spv = findViewById(R.id.spv);
        spv.setRadius(5)
                .setExpendSize(30)
                .setOffsetSize(10)
                .setPointColor(Color.parseColor("#959595"))
                .setMovePointColor(getResources().getColor(R.color.colorPrimary))
                .attachToViewpager(view_pager);
    }
}
