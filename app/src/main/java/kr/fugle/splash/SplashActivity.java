package kr.fugle.splash;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import kr.fugle.R;

/**
 * Created by 김은진 on 2016-07-06.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 3000);// 3 초
    }
}
