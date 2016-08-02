package kr.fugle.webconnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import kr.fugle.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hokyung on 16. 7. 21..
 */
public class PostChoiceTraces extends AsyncTask<String, Void, String> {

    String serverUrl;
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    public PostChoiceTraces(Context context){
        serverUrl = context.getResources().getString(R.string.server_url);
    }

    @Override
    protected String doInBackground(String... params) {

        // 서버로 보낼 별점 데이터
        // 0: serverUrl, 1: userNo, 2: contentNo
        String data = "userId=" + params[1] + "&webtoonId=" + params[2];
        Log.d("PostChoiceTraces.data", data);

        RequestBody body = RequestBody.create(HTML, data);

        Request request = new Request.Builder()
                .url(serverUrl + params[0])
                .post(body)
                .build();

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
//        if(s == null){
//            Log.d("PostChoiceTraces", "post");
//        }else {
            Log.d("PostChoiceTraces", "post complete");
//        }
    }
}
