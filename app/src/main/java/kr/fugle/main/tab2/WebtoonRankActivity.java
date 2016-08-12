package kr.fugle.main.tab2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import kr.fugle.R;

/**
 * Created by 김은진 on 2016-08-12.
 */
public class WebtoonRankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webtoon_rank);

        // 툴바 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.more_webtoon_toolbar);
        toolbar.setTitle("웹툰 랭킹!");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // 뒤로가기 버튼 눌렀을때 (backbutton)
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
