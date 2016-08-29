package kr.fugle.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kr.fugle.R;
import me.relex.circleindicator.CircleIndicator;

public class MenualActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;

    private Fragment currentFragment = new Fragment();

    private TextView skip;
    private TextView done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menual);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

        skip = (TextView) findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenualActivity.this, TutorialActivity.class));
                finish();
            }
        });

        done = (TextView) findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenualActivity.this, TutorialActivity.class));
                finish();
            }
        });
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position<0 || 5<=position)
                return null;
            // 해당하는 page의 Fragment를 생성합니다.
            switch (position) {
                case 0: {
                    skip.setVisibility(View.VISIBLE);
                    done.setVisibility(View.INVISIBLE);
                    return currentFragment = new FirstFragment();
                }
                case 1: {
                    skip.setVisibility(View.VISIBLE);
                    done.setVisibility(View.INVISIBLE);
                    return currentFragment = new SecondFragment();
                }
                case 2: {
                    skip.setVisibility(View.VISIBLE);
                    done.setVisibility(View.INVISIBLE);
                    return currentFragment = new SecondFragment();
                }
                case 3: {
                    skip.setVisibility(View.VISIBLE);
                    done.setVisibility(View.INVISIBLE);
                    return currentFragment = new SecondFragment();
                }
                case 4: {
                    skip.setVisibility(View.INVISIBLE);
                    done.setVisibility(View.VISIBLE);
                    return currentFragment = new SecondFragment();
                }
            }
            return currentFragment;
        }

        @Override
        public int getCount() {
            return 5;  // 총 5개의 page를 보여줍니다.
        }

    }

}
