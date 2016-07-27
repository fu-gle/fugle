package kr.fugle.webconnection;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.mystar.MyStarAdapter;
import kr.fugle.rating.RatingAdapter;
import kr.fugle.recommend.RecommendAdapter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hokyung on 16. 7. 21..
 */
public class GetContentList extends AsyncTask<String, Void, String> {

    final static String serverUrl = "http://52.79.147.163:8000/";
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    ArrayList<Content> list;
    RecyclerView.Adapter adapter;
    int activity;
    int userNo;

    public GetContentList(ArrayList<Content> list,
                          RecyclerView.Adapter adapter,
                          int activity,
                          int userNo){
        this.list = list;
        this.adapter = adapter;
        this.activity = activity;
        this.userNo = userNo;
    }

    @Override
    protected String doInBackground(String... params) {

        Log.d("ho's activity", "GetContentList.doInBackground");

        String data;
        RequestBody body;
        Request request;

        // userNo를 넘기는 경우
        if(params.length != 1) {
            Log.d("---->","GetContentList param != 1");
//            data = "userId=" + params[1];
            data = "userId=" + params[1] + "&pageNo=" + params[2];
            body = RequestBody.create(HTML, data);
            request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .post(body)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .build();
        }

        // json 데이터가 담길 변수
        String result = "";

        try{
            // 서버 통신 실행
            Response response = client.newCall(request).execute();

            // json 형태로의 변환을 위해 { "" :  } 추가
            result = "{\"\":" + response.body().string() + "}";
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
        Log.d("ho's activity", "GetContentList " + s);

        Content content;
        ArrayList<Content> tempList = new ArrayList<>();

        if(s != null && s != ""){
            try{
                // 통째로 받아들여서 하나씩 자르기 위한 json object
                JSONObject reader = new JSONObject(s);

                // 하나씩 잘라서 adapter에 저장해야 한다
                JSONArray dataList = reader.getJSONArray("");

                for(int i=0;i<dataList.length();i++){
                    JSONObject obj = dataList.getJSONObject(i);

                    content = new Content();
                    content.setNo(obj.getInt("id"));
                    content.setTitle(obj.getString("title"));
                    content.setAuthor1(obj.getString("author1"));
                    content.setAuthor2(obj.getString("author2"));
                    content.setGenre1(obj.getString("genre1"));
                    content.setGenre2(obj.getString("genre2"));
                    content.setGenre3(obj.getString("genre3"));
                    content.setAge(obj.getString("age"));
                    content.setThumbnail(obj.getString("thumbnail"));
                    if(!obj.isNull("star__star")) {
                        content.setRating((float) (obj.getInt("star__star") * 1.0) / 10);
                    }
                    if(!obj.isNull("preference")){  // 보고싶어요 버튼 예시
                        content.setHeart(obj.getBoolean("preference"));
                    }

                    tempList.add(content);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        // 리스트를 추가로 불러오는 경우는 리스트 맨 뒤에 null이 들어가있다
        if(list.size() != 0){
            list.remove(list.size() - 1);
            adapter.notifyItemRemoved(list.size());
        }

        // 리스트 추가 후 적용
        list.addAll(tempList);
        adapter.notifyDataSetChanged();

        // 각 어댑터에 로딩완료 적용
        if(activity == 0){
            // Recommend Activity
            Log.d("ho's activity", "GetContentList Recommend Activity");
            ((RecommendAdapter)adapter).setLoaded();
        } else if(activity == 1){
            // Rating Activity
            Log.d("ho's activity", "GetContentList Rating Activity");
            ((RatingAdapter)adapter).setLoaded();
        } else if(activity == 2){
            // MyStar Activity
            Log.d("ho's activity", "GetContentList MyStar Activity");
            ((MyStarAdapter)adapter).setLoaded();
        }
    }
}
