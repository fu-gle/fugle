package kr.fugle.splash;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.kakao.auth.Session;
import com.kakao.usermgmt.response.model.UserProfile;

import org.json.JSONException;
import org.json.JSONObject;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.R;
import kr.fugle.login.LoginActivity;
import kr.fugle.login.OkHttpLogin;
import kr.fugle.main.MainActivity;

/**
 * Created by 김은진 on 2016-07-06.
 */
public class SplashActivity extends Activity {

    Handler handler;
    ActivityStartListener activityStartListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityStartListener = new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {

            }

            @Override
            public void activityStart() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void activityFinish() {
                finish();
            }
        };

        // 페이스북 초기화
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)
        setContentView(R.layout.splash);

        // 로고 이미지 할당
        ImageView logo = (ImageView)findViewById(R.id.logo);
        logo.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.splash_test03)));

        handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 이메일 자동 로그인 채크 확인
                SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                if(preferences != null && preferences.getString("email", null) != null) {
                    String email = preferences.getString("email", null);
                    String password = preferences.getString("password", null);

                    Log.d("--->", "already email logined");

                    OkHttpLogin okHttpLogin = new OkHttpLogin(getApplicationContext());
                    okHttpLogin.setActivityStartListener(activityStartListener);

                    okHttpLogin.execute(
                            "emailLogin/",
                            email,
                            null,
                            password,
                            null,
                            null);
                }
                // 카톡 & 페북 확인
                else if ((!Session.getCurrentSession().isClosed()) ||
                        (AccessToken.getCurrentAccessToken() != null)) {
                    SessionCall();
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 3000);// 3 초
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);

        recycleView(R.id.logo);

        System.gc();
    }

    // 할당된 이미지 메모리 반환
    private void recycleView(int id){
        View view = findViewById(id);

        if(view == null){
            return;
        }

        switch (id){
            case R.id.logo:
                Drawable image = ((ImageView)view).getDrawable();
                if(image != null){
                    image.setCallback(null);
                    ((BitmapDrawable)image).getBitmap().recycle();
                    ((ImageView)view).setImageDrawable(null);
                }
                break;
        }
    }

    public void SessionCall() {
        // 이미 카톡로그인이 되어있는 경우 확인
        if (!Session.getCurrentSession().isClosed()) {
            Log.d("--->", "already kakao logined");
            UserProfile userProfile = UserProfile.loadFromCache();
            Log.d("id--->", userProfile.getId() + "");

            OkHttpLogin okHttpLogin = new OkHttpLogin(getApplicationContext());
            okHttpLogin.setActivityStartListener(activityStartListener);

            okHttpLogin.execute(
                    "login/",
                    userProfile.getId() + "",
                    userProfile.getNickname(),
                    null,
                    null,
                    userProfile.getProfileImagePath());
            return;
        }

        // 페이스북 로그인이 되어있는 경우 확인
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                Log.d("--->", "already facebook logined");

                                object.put("login_type", "facebook");
                                JSONObject pic_data = new JSONObject(object.get("picture").toString());
                                JSONObject pic_url = new JSONObject(pic_data.getString("data"));

                                // 서버로 로그인 데이터 전송
                                OkHttpLogin okHttpLogin = new OkHttpLogin(getApplicationContext());
                                okHttpLogin.setActivityStartListener(activityStartListener);

                                okHttpLogin.execute(
                                        "login/",
                                        object.getString("id"),
                                        object.getString("name"),
                                        null,
                                        null,
                                        pic_url.getString("url"));

                            } catch (JSONException el) {
                                el.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
            request.setParameters(parameters);
            request.executeAsync();
            return;
        }
    }
}
