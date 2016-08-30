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
        Integer[] categoryImg;  // 장르별 사진 주소

        if(category == 0) { // 웹툰 카테고리
            categoryNames = getResources().getStringArray(R.array.webtoon_category);

            categoryImg = new Integer[categoryNames.length];
            categoryImg[0] = R.drawable.egg_profile;
            categoryImg[1] = R.drawable.naver;
            categoryImg[2] = R.drawable.daum;
            categoryImg[3] = R.drawable.lezhin;
        }else{  // 만화 카테고리
            categoryNames = getResources().getStringArray(R.array.cartoon_category);

            categoryImg = new Integer[categoryNames.length];
            categoryImg[0] = R.drawable.egg_profile;
            categoryImg[1] = R.drawable.cartoon_1;
            categoryImg[2] = R.drawable.cartoon_2;
            categoryImg[3] = R.drawable.cartoon_3;
            categoryImg[4] = R.drawable.cartoon_4;
            categoryImg[5] = R.drawable.cartoon_5;
            categoryImg[6] = R.drawable.cartoon_6;
        }

        for(int i = 0; i < categoryNames.length; i++){
            categoryArrayList.add(new Category(i, categoryNames[i], categoryImg[i]));
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
