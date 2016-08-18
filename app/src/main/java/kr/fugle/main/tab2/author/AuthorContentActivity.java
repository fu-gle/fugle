package kr.fugle.main.tab2.author;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.commonlist.CommonRecyclerAdapter;
import kr.fugle.detail.DetailActivity;
import kr.fugle.webconnection.GetContentList;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * Created by 김은진 on 2016-08-17.
 */
// 작가명에 따른 작품들 출력
public class AuthorContentActivity extends AppCompatActivity {

    // 리스트뷰
    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;
    private CommonRecyclerAdapter adapter;

    // 서버 통신
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client;
    String serverUrl;

    // 검색할 작가명
    String authorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author_content);

        // 받아온 작가명
        authorName = getIntent().getStringExtra("authorName");
        Log.e("authorName----->",authorName);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(authorName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 레이아웃 초기화 (RecyclerView) - start
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // 레이아웃 초기화 (RecyclerView) - finish

        contentArrayList = new ArrayList<>();

        adapter = new CommonRecyclerAdapter(
                getApplicationContext(),
                contentArrayList,
                User.getInstance().getNo(),
                recyclerView);

        ActivityStartListener activityStartListener = new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {
                startActivity(intent);
            }

            @Override
            public void activityStart() {
                startActivity(new Intent(AuthorContentActivity.this, DetailActivity.class));
            }

            @Override
            public void activityFinish() {

            }
        };

        adapter.setActivityStartListener(activityStartListener);

        performSearch();

        recyclerView.setAdapter(adapter);
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

    // 작가명, 작품명 입력받았을때 서버로 보냄
    // 파라미터에 맞는 리스트 받아옴
    public void performSearch() {
        contentArrayList.clear();
        new GetContentList(getApplicationContext(),
                contentArrayList,
                adapter,
                3,
                User.getInstance().getNo())
                .execute("searchAuthorName/", authorName);
    }
}
