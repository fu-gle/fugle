package kr.fugle.login;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
import kr.fugle.main.MainActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginProcessActivity extends AppCompatActivity {

    // 카카오톡
    UserProfile userProfile;

    // 페이스북
    private CallbackManager callbackManager;

    // 서버 통신 OkHttp
    final static String serverUrl = "http://52.79.147.163:8000/";
    OkHttpClient client = new OkHttpClient();

    Intent intent;
    JSONObject obj;
    private final int ALREADY_LOGINED = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isLogined = false;

        // 페이스북 초기화
        FacebookSdk.sdkInitialize(getApplicationContext()); // SDK 초기화 (setContentView 보다 먼저 실행되어야합니다. 안그럼 에러납니다.)
        callbackManager = CallbackManager.Factory.create();  //로그인 응답을 처리할 콜백 관리자

        // 이미 카톡로그인이 되어있는 경우 확인
        if(!Session.getCurrentSession().isClosed() && Session.getCurrentSession().implicitOpen()){

            Log.d("--->","already kakao logined");

            userProfile = UserProfile.loadFromCache();

            // 서버로 데이터전송
            new OkHttpLogin().execute(
                    serverUrl,
                    userProfile.getId() + "",
                    userProfile.getNickname(),
                    null,
                    null,
                    userProfile.getProfileImagePath());

//            callback.onSessionOpened();
            isLogined = true;

        }

        // 페이스북 로그인이 되어있는 경우 확인
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null){

            Log.d("--->","already facebook logined");

            isLogined = true;

            //LoginManager - 요청된 읽기 또는 게시 권한으로 로그인 절차를 시작합니다.
            LoginManager.getInstance().logInWithReadPermissions(LoginProcessActivity.this,
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

        if(isLogined == false){
            finish();
        }

        intent = new Intent(LoginProcessActivity.this, MainActivity.class);
    }

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
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(120).height(120)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public class OkHttpLogin extends AsyncTask<String, Void, String> {

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

            setResult(ALREADY_LOGINED, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
