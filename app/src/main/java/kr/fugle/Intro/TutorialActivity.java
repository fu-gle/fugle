package kr.fugle.Intro;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.wooplr.spotlight.prefs.PreferencesManager;

import kr.fugle.R;

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

        mPreferencesManager = new PreferencesManager(TutorialActivity.this);

        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.rootLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
