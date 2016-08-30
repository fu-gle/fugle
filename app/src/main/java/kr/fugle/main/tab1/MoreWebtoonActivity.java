package kr.fugle.main.tab1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.io.Serializable;
import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.commonlist.CommonRecyclerAdapter;

/**
 * Created by 김은진 on 2016-08-10.
 */
// 오늘의 웹툰 더보기 activity
public class MoreWebtoonActivity extends AppCompatActivity {

    // 리스트뷰
    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;

    private CommonRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_webtoon);

        // 툴바 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.more_webtoon_toolbar);
        toolbar.setTitle("주차별 인기 웹툰 랭킹");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 레이아웃 초기화 (RecyclerView) - start
        recyclerView = (RecyclerView)findViewById(R.id.more_web_recyclerview);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // 레이아웃 초기화 (RecyclerView) - finish

        // 리스트뷰 띄우기
//        contentArrayList = getIntent().getSerializableExtra("contentArrayList");

        Intent intent = getIntent();
        Serializable intentListData = intent.getSerializableExtra("contentArrayList");
        contentArrayList = (ArrayList<Content>)intentListData;

        adapter = new CommonRecyclerAdapter(
                getApplicationContext(),
                contentArrayList,
                User.getInstance().getNo(),
                recyclerView);

        adapter.setActivityStartListener(new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {
                startActivity(intent);
            }

            @Override
            public void activityStart() {
            }

            @Override
            public void activityFinish() {

            }
        });

        recyclerView.setAdapter(adapter);
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
}
