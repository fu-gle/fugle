package kr.fugle.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.Item.SearchData;
import kr.fugle.R;
import kr.fugle.mystar.MyStarActivity;
import kr.fugle.preference.PreferenceAnalysisActivity;
import kr.fugle.rating.RatingActivity;
import kr.fugle.search.SearchActivity;
import kr.fugle.splash.SplashActivity;
import kr.fugle.main.tab1.MoreWebtoonActivity;
import kr.fugle.webconnection.GetContentList;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class MainActivity extends AppCompatActivity {

    final int RATING_REQUEST_CODE = 501;
    final int RATING_RESULT_CODE = 505;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public int[] tabIcons = {
            R.drawable.ic_home_white,
            R.drawable.ic_trending_up_white,
            R.drawable.ic_favorite_white,
            R.drawable.ic_person_white
    };
    TabFragment1 tabFragment1;
    TabFragment2 tabFragment2;
    TabFragment3 tabFragment3;
    TabFragment4 tabFragment4;

    ArrayList<Content> contentArrayList;
    int pageNo;
    TabStatusListener tabStatusListener;
    boolean refresh;

    // 작가명, 작품명만 들어있는 리스트
    private ArrayList<String> searchItem = SearchData.getInstance().getList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(searchItem.isEmpty()) {
            new GetContentList(getApplicationContext()).execute("searchName/");
        }

        // 추천 뷰 탭을 초기화 해야하는지 판별하는 값
        refresh = true;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // 탭 클릭시 indicator color
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        // 탭 클릭시 indicator height
        //tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        tabLayout.setSelectedTabIndicatorHeight(10);
        setupTabIcons();

        // 추천 뷰용으로 arraylist 생성
        contentArrayList = new ArrayList<>();
        pageNo = 1;
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        tabStatusListener = new TabStatusListener() {
            @Override
            public void setContentList(ArrayList<Content> list) {
                contentArrayList = list;
            }

            @Override
            public ArrayList<Content> getContentList() {
                return contentArrayList;
            }

            @Override
            public void setPageNo(int pageNum) {
                pageNo = pageNum;
            }

            @Override
            public int getPageNo() {
                return pageNo;
            }

            @Override
            public boolean getRefresh() {
                return refresh;
            }

            @Override
            public void setRefresh(boolean re) {
                refresh = re;
            }
        };

        tabFragment1 = new TabFragment1();
        tabFragment1.setTabStatusListener(tabStatusListener);

        tabFragment2 = new TabFragment2();
        tabFragment2.setTabStatusListener(tabStatusListener);

        tabFragment3 = new TabFragment3();
        tabFragment3.setTabStatusListener(tabStatusListener);

        tabFragment4 = new TabFragment4();
        tabFragment4.setTabStatusListener(tabStatusListener);

        // 홈, 순위, 추천, 마이페이지
        adapter.addFragment(tabFragment1, "");
        adapter.addFragment(tabFragment2, "");
        adapter.addFragment(tabFragment3, "");
        adapter.addFragment(tabFragment4, "");
        viewPager.setAdapter(adapter);
    }

    public void onFragmentChanged(int index) {
        if (index == 0) {
            Intent intent = new Intent(MainActivity.this, RatingActivity.class);
            //intent.putExtra("userNo", user.getNo());
            startActivityForResult(intent, RATING_REQUEST_CODE);
        } else if(index == 1) { // 로그아웃 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            //intent.putExtra("logout",true);
            startActivity(intent);
            finish();
            System.gc();
        } else if (index == 2) {    // 내 웹툰 별점 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, MyStarActivity.class);
            intent.putExtra("category", "webtoon");
            startActivity(intent);
        } else if (index == 3) {    // 오늘의 웹툰 더보기 눌렀을시
            Intent intent = new Intent(MainActivity.this, MoreWebtoonActivity.class);
            intent.putExtra("contentArrayList", tabFragment1.contentArrayList1);
            startActivity(intent);
        } else if (index == 4) {    // 오늘의 만화 더보기 눌렀을시
            Intent intent = new Intent(MainActivity.this, MoreWebtoonActivity.class);
            intent.putExtra("contentArrayList", tabFragment1.contentArrayList2);
            startActivity(intent);
        } else if (index == 5) {    // 내 만화 별점 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, MyStarActivity.class);
            intent.putExtra("category", "cartoon");
            startActivity(intent);
        } else if (index == 6) {    // 내 만화 별점 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, PreferenceAnalysisActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_search) {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
//            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 평점을 매겼는지 확인
        if(requestCode == RATING_REQUEST_CODE && resultCode == RATING_RESULT_CODE) {
            refresh = true;
        }
    }
}
