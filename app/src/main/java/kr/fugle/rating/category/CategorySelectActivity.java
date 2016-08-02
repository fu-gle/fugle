package kr.fugle.rating.category;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import kr.fugle.Item.Category;
import kr.fugle.R;

public class CategorySelectActivity extends AppCompatActivity {

    final int RESULT_CODE = 333;

    Toolbar toolbar;
    RecyclerView recyclerView;

    ArrayList<Category> categoryArrayList;
    CategoryRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoryselect);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("평가 카테고리");

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        categoryArrayList = new ArrayList<>();

        for(int i = 0; i < 7; i++){
            categoryArrayList.add(new Category(i, "카테고리 " + i, ""));
        }

        adapter = new CategoryRecyclerAdapter(categoryArrayList, this);

        adapter.setCategoryClickListener(new CategoryClickListener() {
            @Override
            public void startRatingActivity(Intent intent) {
                setResult(RESULT_CODE, intent);
                finish();
            }
        });

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
}
