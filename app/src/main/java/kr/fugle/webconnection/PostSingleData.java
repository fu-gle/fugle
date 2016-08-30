package kr.fugle.webconnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import kr.fugle.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hokyung on 16. 7. 21..
 * 별점, 좋아요같은 한개의 데이터를 전송하기 위한 클래스
 */
public class PostSingleData extends AsyncTask<String, Void, String> {

    Context context;
    String serverUrl;
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    public PostSingleData(Context context){
        this.context = context;
        serverUrl = context.getResources().getString(R.string.server_url);
    }

    @Override
    protected String doInBackground(String... params) {

        Request request = null;

        if(params.length == 3){
            // 보고싶어요, 건의사항, 회원 정보 수정 보내는 경우

            // 서버로 보낼 데이터
            String data = "";
            if(params[0].equals("like/")) { // 보고싶어요
                // 0: serverUrl, 1: userNo, 2: contentNo
                data = "userId=" + params[1] + "&webtoonId=" + params[2];
                Log.d("PostLike.data", data);
            }else if(params[0].equals("dontsee/")){ // 보기싫어요
                // 0: serverUrl, 1: userNo, 2: contentNo
                data = "userId=" + params[1] + "&webtoonId=" + params[2];
                Log.d("PostHate.data", data);
            }else if(params[0].equals("suggestion/")) {  // 건의사항
                // 0: serverUrl, 1: userNo, 2: suggestion
                data = "userId=" + params[1] + "&suggestion=" + params[2];
                Log.d("PostSuggestion.data", data);
            }else if(params[0].equals("message/")){
                // 0: serverUrl, 1: userNo, 2: suggestion
                data = "userId=" + params[1] + "&message=" + params[2];
                Log.d("PostMessage.data", data);
            }else if(params[0].equals("userProfileImg/")) { // 마이페이지 프로필 사진 수정
                data = "userId=" + params[1] + "&imgPath=" + params[2];
                Log.d("PostProfileImg.data", data);
            }else if(params[0].equals("userProfileBackground/")) {  // 마이페이지 커버사진 수정
                data = "userId=" + params[1] + "&imgPath=" + params[2];
                Log.d("PostBackground.data", data);
            }else if(params[0].equals("deleteComment/")){
                data = "userId=" + params[1] + "&commentId=" + params[2];
                Log.d("PostDeleteComment.data", data);
            }else{  // 잘못된 경로
                return null;
            }

            RequestBody body = RequestBody.create(HTML, data);

            request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .post(body)
                    .build();
        }else if(params.length == 4) {
            // 별점 혹은 코멘트를 보내는 경우

            String data = "";

            if(params[0].equals("insert/")) {
                // 서버로 보낼 별점 데이터
                // 0: serverUrl, 1: userNo, 2: contentNo, 3: rating
                data += "userId=" + params[1] + "&webtoonId=" + params[2] + "&star=" + params[3];
                Log.d("PostStar.data", data);
            }else if(params[0].equals("comment/")){
                // 서버로 보낼 코멘트 데이터
                // 0: serverUrl, 1: userNo, 2: contentNo, 3: comment
                data += "userId=" + params[1] + "&webtoonId=" + params[2] + "&comment=" + params[3];
                Log.d("PostComment.data", data);
            }

            RequestBody body = RequestBody.create(HTML, data);

            request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .post(body)
                    .build();
        }

        try {
            // 서버로 전송
            if(request != null) {
                Response response = client.newCall(request).execute();
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if(s == null){
            Log.d("PostSingleData", "post failed " + s);
            Toast.makeText(context, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
        }else {
            Log.d("PostSingleData", "post complete " + s);
            Toast.makeText(context, "입력되었습니다", Toast.LENGTH_SHORT).show();
        }
    }
}
