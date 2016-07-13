package kr.fugle.recommend;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.fugle.Item;
import kr.fugle.R;
import kr.fugle.rating.RecyclerAdapter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecommendActivity extends AppCompatActivity {

    final static String serverUrl = "http://52.79.147.163:8000/";
    OkHttpClient client = new OkHttpClient();
    List<Item> itemArrayList;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        itemArrayList = new ArrayList<>();

        new OkHttpGet().execute(serverUrl);


    }

    public RecommendHeader getHeader(){
        RecommendHeader header = new RecommendHeader();
        return header;
    }

    // 서버에서 데이터 받아오는 코드 추가
    private class OkHttpGet extends AsyncTask<String, Void, String> {

        // 서버와 통신을 하는 doInBackground 메소드
        @Override
        protected String doInBackground(String... params) {

            // OkHttp 사용을 위한 문법
            Request request = new Request.Builder()
                    .url(params[0])  // 추천 주소 예시
                    .url(params[0])  // 추천 주소 예시
                    .build();

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

        // 뷰에 반영 할 메소드
        @Override
        protected void onPostExecute(String s) {

            Item item;

            if(s != null && s != ""){
                try{
                    // 통째로 받아들여서 하나씩 자르기 위한 json object
                    JSONObject reader = new JSONObject(s);

                    // 하나씩 잘라서 adapter에 저장해야 한다
                    JSONArray list = reader.getJSONArray("");

                    for(int i=0;i<list.length();i++){
                        JSONObject obj = list.getJSONObject(i);

                        item = new Item();
                        item.setNo(obj.getInt("id"));
                        item.setTitle(obj.getString("title"));
                        item.setAuthor1(obj.getString("author1"));
                        item.setAuthor2(obj.getString("author2"));
                        item.setGenre1(obj.getString("genre1"));
                        item.setGenre2(obj.getString("genre2"));
                        item.setGenre3(obj.getString("genre3"));
                        item.setAge(obj.getString("age"));
                        item.setThumbnail(obj.getString("thumbnail"));

                        itemArrayList.add(item);
//                        String key = obj.getString("key");
//                        String value = obj.getString("value");
//
//                        resultText.setText(resultText.getText().toString() + key + ":" + value + "\n");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            recyclerView.setAdapter(new RecommendRecyclerAdapter(getApplicationContext(),getHeader(), itemArrayList, R.layout.activity_recommend));
        }
    }
}
