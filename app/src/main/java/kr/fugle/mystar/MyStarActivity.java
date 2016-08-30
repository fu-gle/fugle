package kr.fugle.mystar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.webconnection.GetContentList;

/*
 *
 * 내가 별점 준 작품의 목록을 불러오는 액티비티
 *
 */
public class MyStarActivity extends AppCompatActivity {

    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;
    private MyStarAdapter adapter;
    private Toolbar toolbar;
    private Integer userNo;
    private static int pageNo;
    private boolean category;   // true : webtoon, false : cartoon
    private String url;

    // 서버 통신
    private GetContentList getContentList;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(MyStarActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        pageNo = 1;

        // 웹툰인지 만화인지 확인
        Intent intent = getIntent();
        String type = intent.getStringExtra("category");

        if("webtoon".equals(type)){
            category = true;
            url = "myWebtoonStar/";
        }else{
            category = false;
            url = "myCartoonStar/";
        }

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(category)
                getSupportActionBar().setTitle("웹툰 목록");
            else
                getSupportActionBar().setTitle("만화 목록");
        }

        userNo = User.getInstance().getNo();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        contentArrayList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true)
                .setView(R.layout.dialog_rating_option);

        AppCompatDialog dialog = builder.create();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 1000;
        dialog.getWindow().setAttributes(params);

        adapter = new MyStarAdapter(
                MyStarActivity.this,
                dialog,
                contentArrayList,
                userNo,
                recyclerView);

        adapter.setCategory(category);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // add null , so the adapter will check view_type and show progress bar at bottom
                contentArrayList.add(null);
                adapter.notifyItemInserted(contentArrayList.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(MyStarActivity.this, "rating bottom", Toast.LENGTH_SHORT).show();

                        new GetContentList(getApplicationContext(),
                                contentArrayList,
                                adapter,
                                2,
                                userNo)
                                .execute(url, userNo.toString(), pageNo + "");
                        pageNo++;
                    }
                }, 1500);
            }
        });

        recyclerView.setAdapter(adapter);

        // 로딩 시작
        loadingDialog.show();

        getContentList = new GetContentList(getApplicationContext(),
                contentArrayList,
                adapter,
                2,
                userNo);
        getContentList.setLoadingDialog(loadingDialog);
        getContentList.execute(url, userNo.toString(), pageNo + "");

        pageNo++;

        // 위로가기 버튼 Floating Action Button
        findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(MyStarActivity.this, "위로가자!", Toast.LENGTH_SHORT).show();
                recyclerView.scrollToPosition(0);
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
