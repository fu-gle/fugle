package kr.fugle.Intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wooplr.spotlight.prefs.PreferencesManager;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.rating.RatingActivity;

/**
 * Created by 김은진 on 2016-08-19.
 */
// 첫 로그인시 튜토리얼
public class TutorialActivity extends Activity {

    PreferencesManager mPreferencesManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        TextView goStarOk = (TextView) findViewById(R.id.go_star_ok);
        TextView goStarNo = (TextView) findViewById(R.id.go_star_no);
        TextView starText = (TextView) findViewById(R.id.star_text);

        goStarOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TutorialActivity.this, RatingActivity.class));
                finish();
            }
        });

        goStarNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        starText.setText(User.getInstance().getName()+
                "님,"+ "\n" + "툰데레에게 취향을 알려주세요.");

    }
}
