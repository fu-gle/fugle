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
import kr.fugle.R;
import kr.fugle.webconnection.GetContentList;

public class RatingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    ArrayList<Content> contentArrayList;
    RecyclerView recyclerView;
    RatingRecyclerAdapter adapter;
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

        Intent intent = getIntent();
        userNo = intent.getIntExtra("userNo", 0);

//        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
//        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(manager);
//
//        contentArrayList = new ArrayList<>();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
//        builder.setCancelable(true)
//                .setView(R.layout.dialog_rating_option);
//
//        AppCompatDialog dialog = builder.create();
//
//        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
//        params.width = 1000;
//        dialog.getWindow().setAttributes(params);
//
//        adapter = new RatingRecyclerAdapter(
//                getApplicationContext(),
//                RatingActivity.this,
//                dialog,
//                contentArrayList,
//                userNo,
//                recyclerView);
//
//        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
//            @Override
//            public void onLoadMore() {
//                // add null , so the adapter will check view_type and show progress bar at bottom
//                contentArrayList.add(null);
//                adapter.notifyItemInserted(contentArrayList.size() - 1);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(RatingActivity.this, "rating bottom", Toast.LENGTH_SHORT).show();
//
//                        new GetContentList(
//                                contentArrayList,
//                                adapter,
//                                1,
//                                userNo)
//                                .execute("", userNo + "", pageNo + "");
//                        pageNo++;
//                    }
//                }, 1500);
//            }
//        });
//
//        recyclerView.setAdapter(adapter);
//
//        // 아이템 넣기
//        new GetContentList(
//                contentArrayList,
//                adapter,
//                1,
//                userNo)
//                .execute("", userNo + "", pageNo + "");
//
//        pageNo++;
//
//        // 위로가기 버튼 Floating Action Button
//        findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(RatingActivity.this, "위로가자!", Toast.LENGTH_SHORT).show();
//
//                Log.d("---->","extent " + recyclerView.computeVerticalScrollExtent() + " offset " + recyclerView.computeVerticalScrollOffset() + " range " + recyclerView.computeVerticalScrollRange());
//                recyclerView.smoothScrollToPosition(0);
//            }
//        });
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

        Bundle bundle = new Bundle();
        bundle.putInt("userNo", userNo);

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
        ratingTabFragment1.setArguments(bundle);
        ratingTabFragment1.setCountChangeListener(countChangeListener);

        RatingTabFragment2 ratingTabFragment2 = new RatingTabFragment2();
        ratingTabFragment2.setArguments(bundle);
        ratingTabFragment2.setCountChangeListener(countChangeListener);

        adapter.addFragment(ratingTabFragment1, "");
        adapter.addFragment(ratingTabFragment2, "");

        viewPager.setAdapter(adapter);
    }
}
