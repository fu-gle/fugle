package kr.fugle.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.kakao.auth.AuthType;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.main.MainActivity;
import kr.fugle.register.RegisterActivity;
import kr.fugle.splash.SplashActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    // 카카오
    SessionCallback callback;

    // 페이스북
    private CallbackManager callbackManager;

    // 이메일
    private EmailLoginDialog emailLogin;

    // 서버 통신 OkHttp
    final static String serverUrl = "http://52.79.147.163:8000/";
    OkHttpClient client = new OkHttpClient();

    // User 정보 저장
//    User user;
    String user;
    JSONObject obj;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 페이스북 초기화
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자

        // 로그아웃 버튼 클릭시의 intent 채크
        Intent data = getIntent();
        boolean logout = data.getBooleanExtra("logout",false);

        // Splash 화면 이동
        if(!logout) {
            startActivity(new Intent(this, SplashActivity.class));
        }

        callback = new SessionCallback();

        // 이미 카톡로그인이 되어있는 경우 확인
        if(!logout && !Session.getCurrentSession().isClosed()){
            Log.d("--->","already logined");
            new SessionCallback().onSessionOpened();
        }



        // 페이스북 로그인이 되어있는 경우 확인
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){
            Log.d("-----","이미 페북로그인");
            //LoginManager - 요청된 읽기 또는 게시 권한으로 로그인 절차를 시작합니다.
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                    Arrays.asList("public_profile", "user_friends"));
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {

                        private ProfileTracker mProfileTracker;

                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            getUserInfo(loginResult);
                        }

                        @Override
                        public void onCancel() {
                            Log.e("onCancel", "onCancel");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e("onError", "onError " + exception.getLocalizedMessage());
                        }
                    });
        }

        Session.getCurrentSession().addCallback(callback);
        //GlobalApplication.setCurrentActivity(LoginActivity.this);

        // 회원가입
        findViewById(R.id.register_button).setOnClickListener(onRegisterButtonClicked);

        // 페이스북 로그인
        findViewById(R.id.com_facebook_login).setOnClickListener(onFacebookButtonClicked);

        // 이메일 로그인
        findViewById(R.id.com_email_login).setOnClickListener(onEmailButtonClicked);

        findViewById(R.id.com_kakao_login).setOnClickListener(onKakaoButtonClicked);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)
                && callbackManager.onActivityResult(requestCode, resultCode, data)
                ) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void isKakaoLogin() {
        // 카카오 세션을 오픈한다
        callback = new SessionCallback();
        com.kakao.auth.Session.getCurrentSession().addCallback(callback);
        com.kakao.auth.Session.getCurrentSession().checkAndImplicitOpen();
        com.kakao.auth.Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, LoginActivity.this);
    }

    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인에 성공하면 로그인한 사용자의 일련번호, 닉네임, 이미지url등을 리턴합니다.
                    //사용자 ID는 보안상의 문제로 제공하지 않고 일련번호는 제공합니다.

                    Log.e("UserProfile", userProfile.toString());
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    Session.getCurrentSession().checkAccessTokenInfo();
                    obj = new JSONObject();
                    try {
                        obj.put("login_type","kakao");
                        obj.put("id", userProfile.getId()+"");
                        obj.put("name", userProfile.getNickname());
                        obj.put("image", userProfile.getProfileImagePath());
                        // 서버로 데이터전송
                        new OkHttpLogin().execute(
                                serverUrl,
                                obj.getString("id"),
                                obj.getString("name"),
                                null,
                                null,
                                obj.getString("image"));
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            // 어쩔때 실패되는지는 테스트를 안해보았음 ㅜㅜ
        }
    }

    private class OkHttpLogin extends AsyncTask<String, Void, String> {

        public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        @Override
        protected String doInBackground(String... params) {
            // 서버로 보낼 사용자 데이터
            // server address, primary, name, password, message, profile
            String data = "primary=" + params[1] + "&name=" + params[2]
                    + "&password=" + params[3] + "&message=" + params[4]
                    + "&profile=" + params[5];
            Log.i("OkHttpLogin.data", data);

            RequestBody body = RequestBody.create(HTML, data);

            Request request = new Request.Builder()
                    .url(params[0] + "login/")
                    .post(body)
                    .build();

            Log.i("OkHttpLogin.request", request.toString());

            try {
                // 서버로 전송
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // 서버에서 로그인 성공여부 받음
            // 성공시 startActivity. 실패시 토스트 메세지
            Log.i("ho's activity", "LoginActivity.OkHttpLogin.onPostExecute " + s);

            JSONObject jsonObject;

            User user = User.getInstance();

            try {
                jsonObject = new JSONObject(s);

//                int no, String name, String primaryKey, String profileImg, String message
                user.setAttributes(
                        jsonObject.getInt("id"),
                        jsonObject.getString("name"),
                        jsonObject.getString("primary"),
                        jsonObject.getString("profile"),
                        jsonObject.getString("message")
                );
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
            finish();
        }
    }

    // 카카오톡 로그인 버튼 클릭시
    TextView.OnClickListener onKakaoButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isKakaoLogin();
        }
    };

    // 페이스북 로그인 버튼 클릭시
    TextView.OnClickListener onFacebookButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //LoginManager - 요청된 읽기 또는 게시 권한으로 로그인 절차를 시작합니다.
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                    Arrays.asList("public_profile", "user_friends"));

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {

                        private ProfileTracker mProfileTracker;

                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            getUserInfo(loginResult);
                        }

                        @Override
                        public void onCancel() {
                            Log.e("onCancel", "onCancel");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e("onError", "onError " + exception.getLocalizedMessage());
                        }
                    });
        }
    };

    /*
    To get the facebook user's own profile information via  creating a new request.
    When the request is completed, a callback is called to handle the success condition.
 */
    protected void getUserInfo(LoginResult loginResult) {

        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object,
                                            GraphResponse response) {

                        try{
                            object.put("login_type","facebook");
                            JSONObject pic_data = new JSONObject(object.get("picture").toString());
                            JSONObject pic_url = new JSONObject(pic_data.getString("data"));

                            // 서버로 로그인 데이터 전송
                            new OkHttpLogin().execute(
                                    serverUrl,
                                    object.getString("id"),
                                    object.getString("name"),
                                    null,
                                    null,
                                    pic_url.getString("url"));

                        } catch (JSONException el) {
                            el.printStackTrace();
                        }

                        intent = new Intent(LoginActivity.this, MainActivity.class);
                        //intent.putExtra("jsondata", object.toString());
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    // 이메일 로그인 버튼 클릭시
    TextView.OnClickListener onEmailButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //emailLogin = new EmailLoginDialog();
           //emailLogin.show
        }
    };


    // 회원가입 버튼 클릭시
    TextView.OnClickListener onRegisterButtonClicked = new View.OnClickListener() {
        // 회원가입 버튼 클릭
        public void onClick(View v) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
    };
}

