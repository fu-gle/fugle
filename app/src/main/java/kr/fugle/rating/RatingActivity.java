package kr.fugle.rating;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RatingActivity extends AppCompatActivity {

    final static String serverUrl = "http://52.79.147.163:8000/";
    OkHttpClient client;
    RatingAdapter adapter;
    ArrayList<Content> contentArrayList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_list);

        contentArrayList = new ArrayList<>();
        client = new OkHttpClient();

        // Adapter 생성
        adapter = new RatingAdapter();

        // 리스트뷰 참조 및 Adapter 달기
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // 아이템 넣기
        // 이 부분에서 서버와 연동하여 데이터 넣기
        new OkHttpGet().execute(serverUrl);
        //adapter.addItem("http://thumb.comic.naver.net/webtoon/675554/thumbnail/title_thumbnail_20160303181701_t83x90.jpg", "가우스 전자", "곽백수", 3.0f);

        // 클릭시 이벤트 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 아이템 가져오기
                RatingItem item = (RatingItem)parent.getItemAtPosition(position);

                // view 는 리니어 레이아웃 한 줄을 뜻함
//                LinearLayout layout = (LinearLayout) view;

                //상세정보 액티비티로 이동
                Toast.makeText(RatingActivity.this, item.getTitle(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    // 서버로부터 데이터를 json 형태로 긁어온다
    private class OkHttpGet extends AsyncTask<String, Void, String>{

        // 서버와 통신을 하는 doInBackground 메소드
        @Override
        protected String doInBackground(String... params) {
            Log.d("ho's activity", "RatingActivity.OkHttpGet.doInBackground");

            // OkHttp 사용을 위한 문법
            Request request = new Request.Builder()
                    .url(params[0])
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
            Log.d("ho's activity", "RatingActivity.OkHttpGet.onPostExecute");

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

                        contentArrayList.add(content);
//                        String key = obj.getString("key");
//                        String value = obj.getString("value");
//
//                        resultText.setText(resultText.getText().toString() + key + ":" + value + "\n");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            for(Content data : contentArrayList){
                adapter.addItem(data);
            }

            listView.setAdapter(adapter);
        }
    }
}
