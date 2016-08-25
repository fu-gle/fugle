package kr.fugle.rating.category;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Category;
import kr.fugle.R;

public class CategorySelectActivity extends AppCompatActivity {

    final int RESULT_CODE = 333;

    Toolbar toolbar;
    RecyclerView recyclerView;
    CategoryRecyclerAdapter adapter;

    ArrayList<Category> categoryArrayList;
    Integer category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoryselect);

        Intent data = getIntent();
        category = data.getIntExtra("category", 0); // 웹툰 : 0, 만화 : 1

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

        String[] categoryNames;

        if(category == 0) {
            categoryNames = getResources().getStringArray(R.array.webtoon_category);
        }else{
            categoryNames = getResources().getStringArray(R.array.cartoon_category);
        }

        for(int i = 0; i < categoryNames.length; i++){
            categoryArrayList.add(new Category(i, categoryNames[i], ""));
        }

        adapter = new CategoryRecyclerAdapter(categoryArrayList, this);

        adapter.setActivityStartListener(new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {
                setResult(RESULT_CODE, intent);
                finish();
            }

            @Override
            public void activityStart() {

            }

            @Override
            public void activityFinish() {

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
