package kr.fugle.webconnection;

import android.os.AsyncTask;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hokyung on 16. 7. 21..
 */
public class PostStar extends AsyncTask<String, Void, String> {

    final static String serverUrl = "http://52.79.147.163:8000/";
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    protected String doInBackground(String... params) {

        // 별점이 0일경우 전송하지 않는다
        // 0일경우 delete 추가
        if(!params[3].equals(0)) {

            // 서버로 보낼 별점 데이터
            // 0: serverUrl, 1: userNo, 2: contentNo, 3: rating
            String data = "userId=" + params[1] + "&webtoonId=" + params[2] + "&star=" + params[3];
            Log.d("PostStar.data", data);

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
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s == null){
            Log.d("PostStar", "rating is 0");
        }else {
            Log.d("PostStar", "post complete");
        }
    }
}
