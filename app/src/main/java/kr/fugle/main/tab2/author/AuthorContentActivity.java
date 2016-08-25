package kr.fugle.main.tab2.author;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
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

    // 검색할 작가명
    private String authorName;

    // 서버통신용
    private GetContentList getContentList;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

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

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(AuthorContentActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

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

        // 로딩 시작
        loadingDialog.show();

        contentArrayList.clear();
        getContentList = new GetContentList(getApplicationContext(),
                contentArrayList,
                adapter,
                3,
                User.getInstance().getNo());
        getContentList.setLoadingDialog(loadingDialog);
        getContentList.execute("searchAuthorName/", authorName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(getContentList != null)
            getContentList.cancel(true);
    }
}
