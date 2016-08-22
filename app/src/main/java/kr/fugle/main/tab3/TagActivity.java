package kr.fugle.main.tab3;

import android.content.Intent;
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
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.commonlist.CommonRecyclerAdapter;
import kr.fugle.detail.DetailActivity;
import kr.fugle.webconnection.GetContentList;

public class TagActivity extends AppCompatActivity {

    CommonRecyclerAdapter adapter;

    Toolbar toolbar;
    RecyclerView recyclerView;

    ArrayList<Content> contentArrayList;
    static int pageNo;
    final User user = User.getInstance();
    String tag;     // 검색할 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        pageNo = 1;

        tag = getIntent().getStringExtra("tag");

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(tag);
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
                startActivity(new Intent(TagActivity.this, DetailActivity.class));
            }

            @Override
            public void activityFinish() {

            }
        };

        adapter.setActivityStartListener(activityStartListener);

        recyclerView.setAdapter(adapter);

        new GetContentList(getApplicationContext(),
                contentArrayList,
                adapter,
                6,
                user.getNo())
                .execute("searchTagName/", tag);

        // 위로가기 버튼 Floating Action Button
        findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "위로가자!", Toast.LENGTH_SHORT).show();

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
}
