package kr.fugle.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.R;
import kr.fugle.main.MainActivity;
import kr.fugle.register.RegisterActivity;

/**
 * Created by 김은진 on 2016-07-04.
 */
public class LoginActivity extends AppCompatActivity {

    // 카카오
    SessionCallback callback;

    // 페이스북
    private CallbackManager callbackManager;

    // 이메일
    private AppCompatDialog emailLoginDialog;

    // 서버통신
    ActivityStartListener activityStartListener;
    OkHttpLogin okHttpLogin;

    // 로그인 기다리는 프로그래스바
    private AppCompatDialog loadingDialog;

    // User 정보 저장
    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activityStartListener = new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {

            }

            @Override
            public void activityStart() {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void activityFinish() {

            }
        };

        // 배경 이미지 할당
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.linearLayout);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            linearLayout.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.background1)));
        }else {
            linearLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.background1)));
        }

        // 타이틀 이미지 할당
        ImageView logo = (ImageView)findViewById(R.id.logo);
        logo.setImageDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.toondere_logo01)));

        // 페이스북 초기화
//        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)
        callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자

        // 회원가입
        findViewById(R.id.register_button).setOnClickListener(onRegisterButtonClicked);

        // 페이스북 로그인
        findViewById(R.id.com_facebook_login).setOnClickListener(onFacebookButtonClicked);

        // 이메일 로그인
        findViewById(R.id.com_email_login).setOnClickListener(onEmailButtonClicked);

        findViewById(R.id.com_kakao_login).setOnClickListener(onKakaoButtonClicked);

        // 이메일 로그인 다이얼로그
        AlertDialog.Builder emailDialogBuilder = new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
        emailDialogBuilder.setCancelable(true)
                .setView(R.layout.dialog_email_login);

        emailLoginDialog = emailDialogBuilder.create();

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(LoginActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        // 다이얼로그 배경 설정
        emailLoginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));

        // 다이얼로그 크기 정하기
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        WindowManager.LayoutParams params = emailLoginDialog.getWindow().getAttributes();
        params.width = (int)(metrics.widthPixels * 0.9);
        emailLoginDialog.getWindow().setAttributes(params);
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
        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().checkAndImplicitOpen();
        Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, LoginActivity.this);
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
                    Session.getCurrentSession().checkAccessTokenInfo();
                    obj = new JSONObject();
                    try {
                        obj.put("login_type","kakao");
                        obj.put("id", userProfile.getId()+"");
                        obj.put("name", userProfile.getNickname());
                        obj.put("image", userProfile.getProfileImagePath());

                        // 로딩 다이얼로그 띄우기
                        if(!LoginActivity.this.isFinishing())
                            loadingDialog.show();

                        // 서버로 데이터전송
                        okHttpLogin = new OkHttpLogin(getApplication());
                        okHttpLogin.setActivityStartListener(activityStartListener);
                        okHttpLogin.setLoadingDialog(loadingDialog);
                        okHttpLogin.execute(
                                "login/",
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

                            // 로딩 다이얼로그 띄우기
                            if(!LoginActivity.this.isFinishing())
                                loadingDialog.show();

                            // 서버로 로그인 데이터 전송
                            okHttpLogin = new OkHttpLogin(getApplicationContext());
                            okHttpLogin.setActivityStartListener(activityStartListener);
                            okHttpLogin.setLoadingDialog(loadingDialog);
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
            // 로그인 다이얼로그
//            Toast.makeText(LoginActivity.this, "email login clicked", Toast.LENGTH_SHORT).show();

            emailLoginDialog.show();

            emailLoginDialog.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText inputEmail = (EditText)emailLoginDialog.findViewById(R.id.input_email);
                    EditText inputPassword = (EditText)emailLoginDialog.findViewById(R.id.input_password);
                    CheckBox checkAutoLogin = (CheckBox)emailLoginDialog.findViewById(R.id.check_auto_login);

                    String email = inputEmail.getText().toString();
                    String password = inputPassword.getText().toString();
                    Boolean auto = checkAutoLogin.isChecked();

                    if("".equals(email) || "".equals(password)){
                        Toast.makeText(LoginActivity.this, "제대로 입력해", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 자동로그인 채크상태일시 SharedPreferences에 저장
                    if(auto){
                        Log.d("ho's activity", "자동로그인 눌러진 상태");
                        SharedPreferences preferences = getSharedPreferences("login", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.commit();
                    }

                    // 로딩 다이얼로그 띄우기
                    loadingDialog.show();

                    // 서버로 전송
                    okHttpLogin = new OkHttpLogin(getApplicationContext());
                    okHttpLogin.setActivityStartListener(activityStartListener);
                    okHttpLogin.setLoadingDialog(loadingDialog);
                    okHttpLogin.execute(
                            "emailLogin/",
                            email,
                            null,
                            password,
                            null,
                            null);

                    emailLoginDialog.cancel();
                }
            });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recyleView(R.id.linearLayout);
        recyleView(R.id.logo);

        if(okHttpLogin != null)
            okHttpLogin.cancel(true);

        System.gc();
    }

    // 할당된 이미지 메모리 반환
    private void recyleView(int id){
        View view = findViewById(id);

        if(view == null){
            return;
        }

        switch (id){
            case R.id.linearLayout:
                Drawable background = view.getBackground();
                if(background != null){
                    background.setCallback(null);
                    ((BitmapDrawable)background).getBitmap().recycle();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackground(null);
                    }else{
                        view.setBackgroundDrawable(null);
                    }
                }
                break;
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
}

