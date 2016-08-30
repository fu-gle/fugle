package kr.fugle.main.tab4;

import android.content.Intent;
import android.os.Handler;
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
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.commonlist.CommonRecyclerAdapter;
import kr.fugle.webconnection.GetContentList;

/*
*  사용자가 평가한 코멘트, 보고싶어요, 보기싫어요의 리스트를 보여주는 액티비티
* */
public class LikeHateActivity extends AppCompatActivity {

    // 리사이클러뷰의 어뎁터
    private RecyclerView.Adapter adapter;

    // 서버 통신
    private GetContentList getContentList;
    private Handler handler;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private final User user = User.getInstance();
    private ArrayList<Content> contentArrayList;
    private static int pageNo;
    private int category;   // 보고싶어요 혹은 보기싫어요를 파악하기 위한 변수
    private String url;     // 보고싶어요 혹은 보기싫어요에 따른 주소
    private String title;   // 보고싶어요 혹은 보기싫어요에 따른 제목

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(LikeHateActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        pageNo = 1;

        Intent intent = getIntent();
        category = intent.getIntExtra("category", 0);

        if(category == 0) {
            url = "mylike/";
            title = "보고싶어요";
        }else if(category == 1){
            url = "mydontsee/";
            title = "보기싫어요";
        }else{
            url = "myComment/";
            title = "코멘트";
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

        handler = new Handler();

        // 코멘트의 경우 다른 어뎁터를 사용
        if(category == 2){
            adapter = new CommonRecyclerAdapter(LikeHateActivity.this,
                    contentArrayList,
                    user.getNo(),
                    recyclerView);

            ((CommonRecyclerAdapter)adapter).setActivityStartListener(new ActivityStartListener() {
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
        }else {
            adapter = new LikeHateAdapter(LikeHateActivity.this,
                    contentArrayList,
                    user.getNo(),
                    recyclerView);

            ((LikeHateAdapter)adapter).setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    // add null , so the adapter will check view_type and show progress bar at bottom
                    contentArrayList.add(null);
                    adapter.notifyItemInserted(contentArrayList.size() - 1);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(getApplicationContext(), "rating bottom", Toast.LENGTH_SHORT).show();

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

            ((LikeHateAdapter)adapter).setActivityStartListener(new ActivityStartListener() {
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
        }

        recyclerView.setAdapter(adapter);

        if(category == 2){
            getContentList = new GetContentList(getApplicationContext(),
                    contentArrayList,
                    adapter,
                    7,
                    user.getNo());
        }else {
            getContentList = new GetContentList(getApplicationContext(),
                    contentArrayList,
                    adapter,
                    5,
                    user.getNo());
        }

        // 로딩 시작
        loadingDialog.show();

        getContentList.setLoadingDialog(loadingDialog);
        getContentList.execute(url, user.getNo() + "", pageNo + "");
        pageNo++;

        // 위로가기 버튼 Floating Action Button
        findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "위로가자!", Toast.LENGTH_SHORT).show();
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

        if(getContentList != null)
            getContentList.cancel(true);
        if(handler != null)
            handler.removeMessages(0);
    }
}
