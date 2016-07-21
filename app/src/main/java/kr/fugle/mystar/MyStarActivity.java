package kr.fugle.mystar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.R;
import kr.fugle.rating.RatingRecyclerAdapter;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*
 *
 * 내가 별점 준 작품의 목록을 불러오는 액티비티
 *
 */
public class MyStarActivity extends AppCompatActivity {

    final static String serverUrl = "http://52.79.147.163:8000/";
    OkHttpClient client;

    ArrayList<Content> contentArrayList;
    RecyclerView recyclerView;
    Toolbar toolbar;
    Integer userNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mystar);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        userNo = intent.getIntExtra("userNo", 0);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        contentArrayList = new ArrayList<>();
        client = new OkHttpClient();

        // 아이템 넣기
        new OkHttpGet().execute(serverUrl, userNo.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 서버로부터 데이터를 json 형태로 긁어온다
    private class OkHttpGet extends AsyncTask<String, Void, String> {

        public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        // 서버와 통신을 하는 doInBackground 메소드
        @Override
        protected String doInBackground(String... params) {
            Log.d("ho's activity", "MyStarActivity.OkHttpGet.doInBackground");

            String data = "userId=" + params[1];

            RequestBody body = RequestBody.create(HTML, data);

            // OkHttp 사용을 위한 문법
            Request request = new Request.Builder()
                    .url(params[0] + "mystar/")
                    .post(body)
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
            Log.d("ho's activity", "MyStarActivity.OkHttpGet.onPostExecute" + s);

            Content content;

            if(s != null && s != ""){
                try{
                    // 통째로 받아들여서 하나씩 자르기 위한 json object
                    JSONObject reader = new JSONObject(s);

                    // 하나씩 잘라서 adapter에 저장해야 한다
                    JSONArray list = reader.getJSONArray("");

                    for(int i=0;i<list.length();i++){
                        JSONObject obj = list.getJSONObject(i);

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
                        content.setRating((float)(obj.getInt("star")*1.0)/10);

                        contentArrayList.add(content);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            Log.d("------",contentArrayList.get(0).getTitle());
            recyclerView.setAdapter(new RatingRecyclerAdapter(getApplicationContext(), contentArrayList, MyStarActivity.this, userNo));
        }
    }
}