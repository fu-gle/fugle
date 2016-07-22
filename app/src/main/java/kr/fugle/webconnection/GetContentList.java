package kr.fugle.webconnection;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.fugle.Item.Content;
import kr.fugle.rating.RatingRecyclerAdapter;
import kr.fugle.recommend.RecommendActivity;
import kr.fugle.recommend.RecommendRecyclerAdapter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hokyung on 16. 7. 21..
 */
public class GetContentList extends AsyncTask<String, Void, String> {

    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    ArrayList<Content> list;
    RecyclerView recyclerView;
    Context applicationContext;
    Context activityContext;
    int activity;
    int userNo;
    boolean lock = true;

    public GetContentList(){}

    public GetContentList(ArrayList<Content> list, RecyclerView recyclerView,
                          Context applicationContext, Context activityContext,
                          int activity, int userNo){
        this.list = list;
        this.recyclerView = recyclerView;
        this.applicationContext = applicationContext;
        this.activityContext = activityContext;
        this.activity = activity;
        this.userNo = userNo;
    }

    public void setRecyclerView(RecyclerView recyclerView){
        this.recyclerView = recyclerView;
    }

    public void setApplicationContext(Context applicationContext){
        this.applicationContext = applicationContext;
    }

    public void setActivityContext(Context activityContext){
        this.activityContext = activityContext;
    }

    public void setActivity(int activity){
        // 0: RecommendActivity, 1: RatingActivity, 2: MyStarActivity
        this.activity = activity;
    }

    public void setList(ArrayList<Content> list){
        this.list = list;
    }

    public ArrayList<Content> getList(){
        if(lock){
            return null;
        }
        return list;
    }

    @Override
    protected String doInBackground(String... params) {

        Log.d("ho's activity", "GetContentList.doInBackground");

        String data;
        RequestBody body;
        Request request;

        // userNo를 넘기는 경우
        if(params.length != 1) {
            data = "userId=" + params[1];
            body = RequestBody.create(HTML, data);
            request = new Request.Builder()
                    .url(params[0])
                    .post(body)
                    .build();
        }else{
            request = new Request.Builder()
                    .url(params[0])
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
//                    content.setRating((float)(obj.getInt("star")*1.0)/10);

                    list.add(content);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        lock = false;

        if(activity == 0){
            // Recommend Activity
            recyclerView.setAdapter(new RecommendRecyclerAdapter(applicationContext, list, activityContext, userNo));
        } else if(activity == 1){
            // Rating Activity
            recyclerView.setAdapter(new RatingRecyclerAdapter(applicationContext, list, activityContext, userNo));
        } else if(activity == 2){
            // MyStar Activity
            recyclerView.setAdapter(new RatingRecyclerAdapter(applicationContext, list, activityContext, userNo));
        }
    }
}
