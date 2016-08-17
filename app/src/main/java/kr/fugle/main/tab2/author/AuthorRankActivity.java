package kr.fugle.main.tab2.author;

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
public class AuthorRankActivity extends AppCompatActivity {

    // 툴바, 탭레이아웃
    private TabLayout tabLayout;
    private ViewPager viewPager;
    CountOfAuthorRankFragment countOfAuthorRankFragment;
    ScoreOfAuthorRankFragment scoreOfAuthorRankFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_rank);

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("작가 랭킹!");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // 탭 클릭시 indicator color
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        // 탭 클릭시 indicator height
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

        countOfAuthorRankFragment = new CountOfAuthorRankFragment();
        scoreOfAuthorRankFragment = new ScoreOfAuthorRankFragment();

        // 평가갯수순, 평점순 랭킹
        adapter.addFragment(countOfAuthorRankFragment, "평가갯수");
        adapter.addFragment(scoreOfAuthorRankFragment, "평균평점");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }
}
