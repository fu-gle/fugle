package kr.fugle.main.tab3;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
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

    // 리사이클러뷰의 어뎁터
    private CommonRecyclerAdapter adapter;

    // 서버 통신
    private GetContentList getContentList;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private ArrayList<Content> contentArrayList;
    private static int pageNo;
    private final User user = User.getInstance();
    private String tag;     // 검색할 태그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(TagActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        pageNo = 1;

        tag = getIntent().getStringExtra("tag");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
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

        // 로딩 시작
        loadingDialog.show();

        getContentList = new GetContentList(getApplicationContext(),
                contentArrayList,
                adapter,
                6,
                user.getNo());
        getContentList.setLoadingDialog(loadingDialog);
        getContentList.execute("searchTagName/", tag);

        // 위로가기 버튼 Floating Action Button
        findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "위로가자!", Toast.LENGTH_SHORT).show();

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

        if(getContentList != null)
            getContentList.cancel(true);
    }
}
