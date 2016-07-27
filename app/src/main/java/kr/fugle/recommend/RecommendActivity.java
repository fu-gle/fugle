package kr.fugle.recommend;

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
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.R;
import kr.fugle.webconnection.GetContentList;

public class RecommendActivity extends AppCompatActivity {

    ArrayList<Content> contentArrayList;
    RecyclerView recyclerView;
    RecommendAdapter adapter;
    Toolbar toolbar;
    Integer userNo;
    static int pageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        pageNo = 1;

        Intent intent = getIntent();
        userNo = intent.getIntExtra("userNo", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        contentArrayList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true)
                .setView(R.layout.dialog_rating);

        AppCompatDialog dialog = builder.create();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 1200;
        dialog.getWindow().setAttributes(params);

        adapter = new RecommendAdapter(
                getApplicationContext(),
                RecommendActivity.this,
                dialog,
                contentArrayList,
                userNo,
                recyclerView);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // add null , so the adapter will check view_type and show progress bar at bottom
                contentArrayList.add(null);
                adapter.notifyItemInserted(contentArrayList.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RecommendActivity.this, "rating bottom", Toast.LENGTH_SHORT).show();

                        new GetContentList(
                                contentArrayList,
                                adapter,
                                0,
                                userNo)
                                .execute("", userNo.toString(), pageNo + "");
                        pageNo++;
                    }
                }, 1500);
            }
        });

        recyclerView.setAdapter(adapter);

        new GetContentList(
                contentArrayList,
                adapter,
                0,
                userNo)
                .execute("", userNo.toString(), pageNo + "");

        pageNo++;

        // 위로가기 버튼 Floating Action Button
        findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecommendActivity.this, "위로가자!", Toast.LENGTH_SHORT).show();
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

    public RecommendHeader getHeader(){
        RecommendHeader header = new RecommendHeader();
        return header;
    }
}
