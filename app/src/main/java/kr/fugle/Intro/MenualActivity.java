package kr.fugle.Intro;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import kr.fugle.R;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by 김은진 on 2016-08-29.
 */
public class MenualActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    CircleIndicator indicator;

    private Fragment currentFragment = new Fragment();

    private TextView skip;
    private TextView done;

    private ImageView left_next;
    private ImageView right_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menual);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(0);

        indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

        skip = (TextView) findViewById(R.id.skip);
        done = (TextView) findViewById(R.id.done);
        left_next = (ImageView) findViewById(R.id.left_next);
        right_next = (ImageView) findViewById(R.id.right_next);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenualActivity.this, TutorialActivity.class));
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenualActivity.this, TutorialActivity.class));
                finish();
            }
        });

        left_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()-1);
            }
        });

        right_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position<0 || 6<=position)
                    return;
                // 해당하는 page의 Fragment를 생성합니다.
                switch (position) {
                    case 0: {
                        skip.setVisibility(View.VISIBLE);
                        done.setVisibility(View.GONE);
                        left_next.setVisibility(View.GONE);
                        right_next.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 1: {
                        skip.setVisibility(View.GONE);
                        done.setVisibility(View.GONE);
                        left_next.setVisibility(View.VISIBLE);
                        right_next.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 2: {
                        skip.setVisibility(View.GONE);
                        done.setVisibility(View.GONE);
                        left_next.setVisibility(View.VISIBLE);
                        right_next.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 3: {
                        skip.setVisibility(View.GONE);
                        done.setVisibility(View.GONE);
                        left_next.setVisibility(View.VISIBLE);
                        right_next.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 4: {
                        skip.setVisibility(View.GONE);
                        done.setVisibility(View.GONE);
                        left_next.setVisibility(View.VISIBLE);
                        right_next.setVisibility(View.VISIBLE);
                        break;
                    }
                    case 5: {
                        skip.setVisibility(View.GONE);
                        done.setVisibility(View.VISIBLE);
                        left_next.setVisibility(View.VISIBLE);
                        right_next.setVisibility(View.GONE);
                        break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("position--->",position+"");
            if(position<0 || 6<=position)
                return null;
            // 해당하는 page의 Fragment를 생성합니다.
            switch (position) {
                case 0: {
                    return currentFragment = new FirstFragment();
                }
                case 1: {
                    return currentFragment = new SecondFragment();
                }
                case 2: {
                    return currentFragment = new ThirdFragment();
                }
                case 3: {
                    return currentFragment = new FourthFragment();
                }
                case 4: {
                    return currentFragment = new FifthFragment();
                }
                case 5: {
                    return currentFragment = new SixthFragment();
                }
            }
            return currentFragment;
        }

        @Override
        public int getCount() {
            return 6;  // 총 5개의 page를 보여줍니다.
        }

    }

}
