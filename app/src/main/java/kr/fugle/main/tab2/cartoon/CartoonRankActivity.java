package kr.fugle.main.tab2.cartoon;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import kr.fugle.R;
import kr.fugle.main.ViewPagerAdapter;

/**
 * Created by 김은진 on 2016-08-16.
 */
public class CartoonRankActivity extends AppCompatActivity {

    // 툴바, 탭레이아웃
    private TabLayout tabLayout;
    private ViewPager viewPager;
    CountOfCartoonRankFragment countOfCartoonRankFragment;
    ScoreOfCartoonRankFragment scoreOfCartoonRankFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webtoon_rank);

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("만화책 랭킹!");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // 탭 클릭시 indicator color
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        // 탭 클릭시 indicator height
        //tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        tabLayout.setSelectedTabIndicatorHeight(10);
    }

    // 뒤로가기 버튼 눌렀을때 (backbutton)
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        countOfCartoonRankFragment = new CountOfCartoonRankFragment();
        scoreOfCartoonRankFragment = new ScoreOfCartoonRankFragment();

        // 평가갯수순, 평점순 랭킹
        adapter.addFragment(countOfCartoonRankFragment, "평가갯수");
        adapter.addFragment(scoreOfCartoonRankFragment, "평균평점");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }
}
