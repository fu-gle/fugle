package kr.fugle.rating;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import kr.fugle.R;

public class RatingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        ListView listView;
        RatingAdapter adapter;

        // Adapter 생성
        adapter = new RatingAdapter();

        // 리스트뷰 참조 및 Adapter 달기
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // 아이템 넣기
        // 이 부분에서 서버와 연동하여 데이터 넣기
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.home), "Home", "home is good", 3.0f);
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.star), "Star", "star is bling bling", 2.0f);
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.x), "X", "x is wrong", 2.5f);

        Log.d("----->", "loading is complete");

        // 클릭시 이벤트 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("---->", "item is clicked");
                // 아이템 가져오기
                RatingItem item = (RatingItem)parent.getItemAtPosition(position);

                LinearLayout layout = (LinearLayout) view;

//                ((RatingBar) view).setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//                    @Override
//                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                        Toast.makeText(RatingActivity.this, rating + "clicked", Toast.LENGTH_SHORT).show();
//                    }
//                });

//                RatingBar ratingBar = (RatingBar)layout.findViewById(R.id.ratingBar);
//
//                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//                    @Override
//                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                        Toast.makeText(RatingActivity.this, rating + "", Toast.LENGTH_SHORT).show();
//                    }
//                });

                Toast.makeText(RatingActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();


            }
        });
    }
}
