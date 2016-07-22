package kr.fugle.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import kr.fugle.Item.Content;
import kr.fugle.R;
import kr.fugle.webconnection.PostStar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    final static String serverUrl = "http://52.79.147.163:8000/";
    OkHttpClient client = new OkHttpClient();

    Content content;
    Toolbar toolbar;
    Integer userNo, contentNo;

    ImageView thumbnailImg;
    TextView title;
    TextView average;
    TextView preferenceBtn;
    TextView ratingBtn;
    TextView commentBtn;
    TextView hrefBtn;
    TextView prediction;
    TextView tag;
    TextView friends;
    TextView title2;
    TextView author;
    TextView genre;
    TextView media;
    TextView publish;
    TextView summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent data = getIntent();
        userNo = data.getIntExtra("userNo", 0);
        contentNo = data.getIntExtra("contentNo", 0);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 위젯 할당
        thumbnailImg = (ImageView)findViewById(R.id.thumbnailImg);
        title = (TextView)findViewById(R.id.title);
        average = (TextView)findViewById(R.id.average);
        preferenceBtn = (TextView)findViewById(R.id.preferenceBtn);
        ratingBtn = (TextView)findViewById(R.id.ratingBtn);
        commentBtn = (TextView)findViewById(R.id.commentBtn);
        hrefBtn = (TextView)findViewById(R.id.hrefBtn);
        prediction = (TextView)findViewById(R.id.prediction);
        tag = (TextView)findViewById(R.id.tag);
        friends = (TextView)findViewById(R.id.friends);
        title2 = (TextView)findViewById(R.id.title2);
        author = (TextView)findViewById(R.id.author);
        genre = (TextView)findViewById(R.id.genre);
        media = (TextView)findViewById(R.id.media);
        publish = (TextView)findViewById(R.id.publish);
        summary  = (TextView)findViewById(R.id.summary);

        // 0: serverUrl , 1: userNo, 2:contentNo
        new OkHttpGet().execute(serverUrl, userNo.toString(), contentNo.toString());

        // 보고싶어요 버튼
        preferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(content.getHeart()){
                    preferenceBtn.setTextColor(Color.parseColor("#777777"));
                    content.setHeart(false);
                }else{
                    preferenceBtn.setTextColor(Color.parseColor("#F13839"));
                    content.setHeart(true);
                }
            }
        });

        // 평가하기 버튼
        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 코멘트 버튼
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 보러가기 버튼
        hrefBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.getHref()));
                startActivity(intent);
            }
        });
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
            Log.d("ho's activity", "DetailActivity.OkHttpGet.doInBackground");

            // 0: serverUrl , 1: userNo, 2:contentNo
            String data = "userId=" + params[1] + "&webtoonId=" + params[2];

            RequestBody body = RequestBody.create(HTML, data);

            // OkHttp 사용을 위한 문법
            Request request = new Request.Builder()
                    .url(params[0] + "detail/")     // 주소 확인 필요
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
            Log.d("ho's activity", "DetailActivity.OkHttpGet.onPostExecute" + s);

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
//                        content.setRating((float)(obj.getInt("star")*1.0)/10);
//                        content.setAverage((float)obj.getDouble("average"));
//                        content.setPrediction((float)(obj.getInt("prediction")*1.0)/10);
//                        content.setHeart(obj.getBoolean("heart"));
                        content.setHref(obj.getString("href"));
                        content.setSummary(obj.getString("summary"));
                        content.setMedia(obj.getString("media"));
                        content.setPublish(obj.getBoolean("publish"));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            Picasso.with(getApplicationContext())
                    .load(content.getThumbnail())
                    .into(thumbnailImg);

            // 이미지 뷰 가운데 정렬 후 세로 길이 맞추기. 잘 되는지 테스트가 필요한디.
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);
            thumbnailImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams)thumbnailImg.getLayoutParams();
            params.height = metrics.heightPixels / 3;

            title.setText(content.getTitle());
            average.setText(content.getAverage().toString());
            prediction.setText(content.getPrediction().toString());
            tag.setText("");
            friends.setText("");
            title2.setText(content.getTitle());

            String authorData = content.getAuthor1();
            if(!content.getAuthor2().equals("null")){
                authorData += ", " + content.getAuthor2();
            }
            author.setText(authorData);

            String genreData = content.getGenre1();
            if(!content.getGenre2().equals("null")){
                genreData += ", " + content.getGenre2();
                if(!content.getGenre3().equals("null")){
                    genreData += ", " + content.getGenre3();
                }
            }
            genre.setText(genreData);

            media.setText(content.getMedia());

            if(content.getPublish()){
                publish.setText("연재중");
            } else {
                publish.setText("완결");
            }

            summary.setText(content.getSummary());
        }
    }

    public class OkHttpPost extends AsyncTask<String, Void, String> {

        public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

        @Override
        protected String doInBackground(String... params) {

            // 서버로 보낼 별점 데이터
            // 0: serverUrl, 1: userNo, 2: contentNo, 3: rating
            String data = "userId=" + params[1] + "&webtoonId=" + params[2] + "&star=" + params[3];
            Log.d("OkHttpPost.data", data);

            RequestBody body = RequestBody.create(HTML, data);

            Request request = new Request.Builder()
                    .url(params[0] + "insert/")
                    .post(body)
                    .build();

            try{
                // 서버로 전송
                Response response = client.newCall(request).execute();
                return response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("OkHttpPost","post complete");
        }
    }
}
