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
import kr.fugle.R;
import kr.fugle.webconnection.GetContentList;

/*
 *
 * 내가 별점 준 작품의 목록을 불러오는 액티비티
 *
 */
public class MyStarActivity extends AppCompatActivity {

    ArrayList<Content> contentArrayList;
    RecyclerView recyclerView;
    MyStarAdapter adapter;
    Toolbar toolbar;
    Integer userNo;
    static int pageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        pageNo = 1;

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        userNo = intent.getIntExtra("userNo", 0);
        Log.d("----","MyStar userNo " + userNo);

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
                getApplicationContext(),
                MyStarActivity.this,
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
                        Toast.makeText(MyStarActivity.this, "rating bottom", Toast.LENGTH_SHORT).show();

                        new GetContentList(
                                contentArrayList,
                                adapter,
                                2,
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
                2,
                userNo)
                .execute("", userNo.toString(), pageNo + "");

        pageNo++;

        // 위로가기 버튼 Floating Action Button
        findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyStarActivity.this, "위로가자!", Toast.LENGTH_SHORT).show();
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
}
