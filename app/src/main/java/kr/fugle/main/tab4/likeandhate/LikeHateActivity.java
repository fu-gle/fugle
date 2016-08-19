package kr.fugle.main.tab4.likeandhate;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.webconnection.GetContentList;

public class LikeHateActivity extends AppCompatActivity {

    LikeHateAdapter adapter;

    Toolbar toolbar;
    RecyclerView recyclerView;

    final User user = User.getInstance();
    ArrayList<Content> contentArrayList;
    static int pageNo;
    Handler handler;
    int category;   // 보고싶어요 혹은 보기싫어요를 파악하기 위한 변수
    String url;     // 보고싶어요 혹은 보기싫어요에 따른 주소
    String title;   // 보고싶어요 혹은 보기싫어요에 따른 제목

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        pageNo = 1;

        Intent intent = getIntent();
        category = intent.getIntExtra("category", 0);

        if(category == 0) {
            url = "mylike/";
            title = "보고싶어요";
        }else {
            url = "mydontsee/";
            title = "보기싫어요";
        }

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        contentArrayList = new ArrayList<>();

        adapter = new LikeHateAdapter(LikeHateActivity.this,
                contentArrayList,
                user.getNo(),
                recyclerView);

        handler = new Handler();

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // add null , so the adapter will check view_type and show progress bar at bottom
                contentArrayList.add(null);
                adapter.notifyItemInserted(contentArrayList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "rating bottom", Toast.LENGTH_SHORT).show();

                        GetContentList getContentList = new GetContentList(getApplicationContext(),
                                contentArrayList,
                                adapter,
                                5,
                                user.getNo());

                        getContentList.execute(url, user.getNo() + "", pageNo + "");

                        pageNo++;
                    }
                }, 1500);
            }
        });

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

        GetContentList getContentList = new GetContentList(getApplicationContext(),
                contentArrayList,
                adapter,
                5,
                user.getNo());

        getContentList.execute(url, user.getNo() + "", pageNo + "");
        pageNo++;

        // 위로가기 버튼 Floating Action Button
        findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "위로가자!", Toast.LENGTH_SHORT).show();
                recyclerView.scrollToPosition(3);
                recyclerView.smoothScrollToPosition(0);
            }
        });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        handler.removeMessages(0);
    }
}
