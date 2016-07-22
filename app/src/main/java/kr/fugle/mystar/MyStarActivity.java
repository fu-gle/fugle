package kr.fugle.mystar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.R;
import kr.fugle.rating.RatingRecyclerAdapter;
import kr.fugle.webconnection.GetContentList;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
 *
 * 내가 별점 준 작품의 목록을 불러오는 액티비티
 *
 */
public class MyStarActivity extends AppCompatActivity {

    final static String serverUrl = "http://52.79.147.163:8000/";

    ArrayList<Content> contentArrayList;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Integer userNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        userNo = intent.getIntExtra("userNo", 0);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        contentArrayList = new ArrayList<>();

        new GetContentList(
                contentArrayList,
                recyclerView,
                getApplicationContext(),
                MyStarActivity.this,
                2,
                userNo)
                .execute(serverUrl, userNo.toString());
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
