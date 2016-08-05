package kr.fugle.login;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 김은진 on 2016-08-01.
 */
public class OkHttpLogin extends AsyncTask<String, Void, String> {

    ActivityStartListener activityStartListener;

    String serverUrl;
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    Context context;

    public OkHttpLogin(Context context) {
        this.context = context;
        serverUrl = context.getResources().getString(R.string.server_url);
    }

    public void setActivityStartListener(ActivityStartListener activityStartListener) {
        this.activityStartListener = activityStartListener;
    }

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
                .url(serverUrl + params[0])
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

        if(activityStartListener != null)
            activityStartListener.activityStart();
    }
}
