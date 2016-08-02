package kr.fugle.rating;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.webconnection.GetContentList;

public class RatingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    int userNo;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        count = 0;

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("15개 평가해");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // 탭 클릭시 indicator color
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        // 탭 클릭시 indicator height
        //tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        tabLayout.setSelectedTabIndicatorHeight(10);

        tabLayout.getTabAt(0).setText("웹툰");
        tabLayout.getTabAt(1).setText("만화책");

        userNo = User.getInstance().getNo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        RatingViewpagerAdapter adapter = new RatingViewpagerAdapter(getSupportFragmentManager());

        CountChangeListener countChangeListener = new CountChangeListener() {
            @Override
            public void setTitle(String title) {
                getSupportActionBar().setTitle("title");
            }

            @Override
            public void addCount() {
                count++;
            }
        };

        RatingTabFragment1 ratingTabFragment1 = new RatingTabFragment1();
        ratingTabFragment1.setCountChangeListener(countChangeListener);

        // 1로 변경 실험
        RatingTabFragment1 ratingTabFragment2 = new RatingTabFragment1();
        ratingTabFragment2.setCountChangeListener(countChangeListener);

        adapter.addFragment(ratingTabFragment1, "");
        adapter.addFragment(ratingTabFragment2, "");

        viewPager.setAdapter(adapter);
    }
}
