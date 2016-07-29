package kr.fugle.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import kr.fugle.R;
import kr.fugle.login.LoginActivity;
import kr.fugle.login.LoginProcessActivity;

/**
 * Created by 김은진 on 2016-07-06.
 */
public class SplashActivity extends Activity {

    private final int SPLASH_REQUEST_CODE = 777;
    private final int ALREADY_LOGINED = 101;

    Handler handler;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        startActivityForResult(new Intent(SplashActivity.this, LoginProcessActivity.class), SPLASH_REQUEST_CODE);

        intent = new Intent(SplashActivity.this, LoginActivity.class);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
                finish();
            }
        }, 3000);// 3 초
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("----->", "SplashActivity.onActivityResult requestCode " + requestCode + " resultCode " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SPLASH_REQUEST_CODE && resultCode == ALREADY_LOGINED){
            intent = data;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
    }
}
