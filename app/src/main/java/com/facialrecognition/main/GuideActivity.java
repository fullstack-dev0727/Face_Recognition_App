package com.facialrecognition.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import me.relex.circleindicator.CircleIndicator;

public class GuideActivity extends Activity {

    private boolean isLastPos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewpager.setAdapter(new GuidePagerAdapter());
        indicator.setViewPager(viewpager);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 2 && positionOffset == 0 && positionOffsetPixels == 0 && isLastPos) {
                    isLastPos = false;
                    GuideActivity.this.startActivity(new Intent(GuideActivity.this, MainActivity.class));
                    GuideActivity.this.finish();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 1)
                    isLastPos = true;
                else
                    isLastPos = false;

            }
        });

        Button skipButton = (Button) findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuideActivity.this.startActivity(new Intent(GuideActivity.this, MainActivity.class));
                GuideActivity.this.finish();
            }
        });
    }


}
