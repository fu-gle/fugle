package kr.fugle.preference;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;
import kr.fugle.rating.RatingActivity;

/**
 * Created by 김은진 on 2016-08-22.
 */
// 취향분석
public class PreferenceAnalysisActivity extends AppCompatActivity {

    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_analysis);

        user = User.getInstance();

        // 툴바 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(user.getName() + "의 취향분석");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 프로필사진
        ImageView profileImg = (ImageView) findViewById(R.id.profile_img);
        CircleTransform circleTransform = new CircleTransform();
        Picasso.with(getApplicationContext())
                .load(user.getProfileImg())
                .transform(circleTransform)
                .into(profileImg);

        // 사용자 이름
        TextView name = (TextView) findViewById(R.id.user_name);
        name.setText(user.getName());

        // 평가 갯수
        TextView starCount = (TextView) findViewById(R.id.star_count);
        starCount.setText(user.getCartoonStars()+user.getWebtoonStars()+"");

        // 평가가 없을때
        CardView cardView = (CardView) findViewById(R.id.zero_cardview);
        if(user.getWebtoonStars()+user.getCartoonStars() != 0) {
            cardView.setVisibility(View.GONE);
            TextView zeroText = (TextView) findViewById(R.id.zero_count_text);
            zeroText.setText(user.getName() + "님 아직 평가가 없어서 \n취향을 알수없어요ㅠㅠ"
                    + "\n평가를 하셔야 취향을 알 수 있어요!");
            // 평가하러가기
            Button button = (Button) findViewById(R.id.go_star);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PreferenceAnalysisActivity.this, RatingActivity.class));
                    finish();
                }
            });
        }

//        // 선호 키워드
//        LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );
//        CardView cardview1 = (CardView) findViewById(R.id.cardview1);
//        inflater.inflate( R.layout.preference_keyword, cardview1 );
//
//        // 차트 통계 (미디어)
//        CardView cardview2 = (CardView) findViewById(R.id.cardview2);
//        inflater.inflate( R.layout.preference_chart, cardview2 );

        Fragment preferenceKeywordFragment = new PreferenceKeywordFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.cardview1, preferenceKeywordFragment);
        fragmentTransaction.commit();
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
