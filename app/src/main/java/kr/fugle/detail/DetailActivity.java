package kr.fugle.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kr.fugle.Item.Comment;
import kr.fugle.Item.Content;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.comment.CommentActivity;
import kr.fugle.webconnection.PostSingleData;
import kr.fugle.webconnection.PostUserLog;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailActivity extends AppCompatActivity {

    String serverUrl;
    OkHttpClient client = new OkHttpClient();
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    CommentRecyclerAdapter commentAdapter;
    ArrayList<Comment> commentArrayList;

    Content content;
    Toolbar toolbar;
    Integer userNo, contentNo;

    ImageView adultImg;
    ImageView thumbnailImg;
    TextView title;
    TextView average;
    TextView preferenceBtn;
    TextView ratingBtn;
    TextView commentBtn;
    TextView linkBtn;
    TextView prediction;
    TextView tag;
    TextView friends;
    TextView title2;
    TextView author;
    TextView genre;
    TextView media;
    TextView publish;
    TextView summary;
    CardView commentCard;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        serverUrl = this.getResources().getString(R.string.server_url);

        Intent data = getIntent();
        userNo = User.getInstance().getNo();
        contentNo = data.getIntExtra("contentNo", 0);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 위젯 할당
        adultImg = (ImageView)findViewById(R.id.adultImg);
        thumbnailImg = (ImageView)findViewById(R.id.thumbnailImg);
        title = (TextView)findViewById(R.id.title);
        average = (TextView)findViewById(R.id.average);
        preferenceBtn = (TextView)findViewById(R.id.preferenceBtn);
        ratingBtn = (TextView)findViewById(R.id.ratingBtn);
        commentBtn = (TextView)findViewById(R.id.commentBtn);
        linkBtn = (TextView)findViewById(R.id.linkBtn);
        prediction = (TextView)findViewById(R.id.prediction);
        tag = (TextView)findViewById(R.id.tag);
        friends = (TextView)findViewById(R.id.friends);
        title2 = (TextView)findViewById(R.id.title2);
        author = (TextView)findViewById(R.id.author);
        genre = (TextView)findViewById(R.id.genre);
        media = (TextView)findViewById(R.id.media);
        publish = (TextView)findViewById(R.id.publish);
        summary  = (TextView)findViewById(R.id.summary);
        commentCard = (CardView)findViewById(R.id.commentCard);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        // 0: serverUrl , 1: userNo, 2:contentNo
        new OkHttpGet().execute(serverUrl, userNo.toString(), contentNo.toString());

        // 별점 다이얼로그 객체 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true)
                .setView(R.layout.dialog_rating);

        final AppCompatDialog dialog = builder.create();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 1200;
        dialog.getWindow().setAttributes(params);

        // 코멘트 목록
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        commentArrayList = new ArrayList<>();

        commentAdapter = new CommentRecyclerAdapter(
                DetailActivity.this,
                commentArrayList,
                recyclerView);

        recyclerView.setAdapter(commentAdapter);

        // 코멘트 불러오기
        new GetCommentList().execute(serverUrl, contentNo.toString());

        // 보고싶어요 버튼
        preferenceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(content == null){
                    Toast.makeText(DetailActivity.this, "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 서버로 데이터 전송
                new PostSingleData(getApplicationContext())
                        .execute("like/", userNo.toString(), contentNo.toString());

                if(content.getLike()){
                    preferenceBtn.setTextColor(Color.parseColor("#777777"));
                    content.setLike(false);
                }else{
                    preferenceBtn.setTextColor(Color.parseColor("#F13839"));
                    content.setLike(true);
                }
            }
        });

        // 평가하기 버튼
        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ho's activity", "DetailActivity ratingBtn clicked");

                dialog.show();

                if(content == null){
                    Toast.makeText(DetailActivity.this, "잠시후 눌러주세요", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                    return;
                }

                ((TextView)dialog.findViewById(R.id.title)).setText(content.getTitle());

                RatingBar ratingBar = (RatingBar)dialog.findViewById(R.id.ratingBar);
                if(content != null) {
                    assert ratingBar != null;
                    ratingBar.setRating(content.getRating());
                }
                assert ratingBar != null;
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                if(content == null){

                                    Integer Rating = (int)(rating * 10);

                                    Toast.makeText(getApplicationContext(), "작품 번호 : " + contentNo.toString() + ", 별점 : " + Rating.toString(), Toast.LENGTH_SHORT).show();

                                    new PostSingleData(getApplicationContext()).execute("insert/", userNo.toString(), contentNo.toString(), Rating.toString());

                                }else if(fromUser){

                                    // 별점 준 갯수 증가
                                    if(rating == 0){
                                        if(content.getCartoon())
                                            User.getInstance().setCartoonStars(User.getInstance().getCartoonStars() - 1);
                                        else
                                            User.getInstance().setWebtoonStars(User.getInstance().getWebtoonStars() - 1);
                                    }else if(content.getRating() == 0){
                                        if(content.getCartoon())
                                            User.getInstance().setCartoonStars(User.getInstance().getCartoonStars() + 1);
                                        else
                                            User.getInstance().setWebtoonStars(User.getInstance().getWebtoonStars() + 1);
                                    }

                                    Integer Rating = (int)(rating * 10);

                                    content.setRating(rating);

                                    Toast.makeText(getApplicationContext(), "작품 번호 : " + contentNo.toString() + ", 별점 : " + Rating.toString(), Toast.LENGTH_SHORT).show();

                                    new PostSingleData(getApplicationContext()).execute("insert/", userNo.toString(), contentNo.toString(), Rating.toString());

                                    dialog.cancel();
                                }
                            }
                        });
            }
        });

        // 코멘트 버튼
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CommentActivity.class);

                intent.putExtra("title", content.getTitle());
                intent.putExtra("contentNo", content.getNo());
                intent.putExtra("star", content.getRating());
                intent.putExtra("isCartoon", content.getCartoon());

                startActivity(intent);
            }
        });

        // 보러가기 버튼
        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 상세보기 누른 흔적 전송
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = dateFormat.format(new Date());
                new PostUserLog(getApplicationContext())
                        .execute("", userNo.toString(), content.getNo().toString(), time);

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(content.getLink()));
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

        // 서버와 통신을 하는 doInBackground 메소드
        @Override
        protected String doInBackground(String... params) {
            Log.d("ho's activity", "DetailActivity.OkHttpGet.doInBackground");

            // 0: serverUrl , 1: userNo, 2:contentNo
            String data = "userId=" + params[1] + "&webtoonId=" + params[2];

            Log.e("------->", "data " + data);

            RequestBody body = RequestBody.create(HTML, data);

            // OkHttp 사용을 위한 문법
            Request request = new Request.Builder()
                    .url(params[0] + "detail/")
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
                        content.setAuthor(obj.getString("author").substring(0, obj.getString("author").length() - 1));
                        content.setGenre(obj.getString("genre").substring(0, obj.getString("genre").length() - 1));
                        content.setAdult(obj.getBoolean("adult"));
                        content.setThumbnailSmall(obj.getString("thumbnail_small"));
                        content.setThumbnailBig(obj.getString("thumbnail_big"));
                        if (!obj.isNull("recommendStar"))
                            content.setPrediction(Float.parseFloat(String.format("%.1f", Float.parseFloat(obj.getString("recommendStar")) / 1000000)));
                        if(!obj.isNull("like") && obj.getBoolean("like"))
                            content.setLike(obj.getBoolean("like"));
                        content.setLink(obj.getString("link"));
                        content.setSummary(obj.getString("summary"));
                        content.setMedia(obj.getString("media"));
                        content.setPublish(obj.getBoolean("publish"));
                        if(!obj.isNull("average"))
                            content.setAverage((float)obj.getInt("average")/1000);
                        if (!obj.isNull("tags"))
                            content.setTags(obj.getString("tags").substring(0, obj.getString("tags").length() - 1));
                        if (!obj.isNull("star"))
                            content.setRating((float) (obj.getInt("star") * 1.0) / 10);
                        if (!obj.isNull("isCartoon"))
                            content.setCartoon(obj.getBoolean("isCartoon"));

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            if(content == null){
                Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            // 성인물인 경우
            if(content.getAdult()){
                Picasso.with(getApplicationContext())
                        .load(R.drawable.heart_full)
                        .into(adultImg);
            }

            // 이미지 뷰 가운데 정렬 후 세로 길이 맞추기. 잘 되는지 테스트가 필요한디.
            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager windowManager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(metrics);

            Picasso.with(getApplicationContext())
                    .load(content.getThumbnailBig())
                    .resize(metrics.widthPixels, metrics.heightPixels/3)
                    .centerCrop()
                    .into(thumbnailImg);

            title.setText(content.getTitle());
            average.setText("★ "+ String.format("%.1f",content.getAverage()));
            prediction.setText(content.getPrediction().toString());

            if(content.getLike()){
                preferenceBtn.setTextColor(Color.parseColor("#F13839"));
            }

            String tags = content.getTags();
            String [] tagList = tags.split(",");
            tags = "";
            for(int i = 0; i < tagList.length; i++){
                tags += "#" + tagList[i];
                if(i != tagList.length - 1){
                    tags += " ";
                }
            }
            tag.setText(tags);

            friends.setText("");    // 친구목록 넣어야함
            title2.setText(content.getTitle());

            author.setText(content.getAuthor());

            genre.setText(content.getGenre());

            media.setText(content.getMedia());

            toolbar.setTitle(content.getTitle());

            if(content.getPublish()){
                publish.setText("연재중");
            } else {
                publish.setText("완결");
            }

            summary.setText(content.getSummary());
        }
    }

    private class GetCommentList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("ho's activity", "DetailActivity.GetCommentList.doInBackground");

            // 0: serverUrl , 1: webtoonId
            String data = "webtoonId=" + params[1];
            Log.d("------>", "getCommentList data" + data);

            RequestBody body = RequestBody.create(HTML, data);

            // OkHttp 사용을 위한 문법
            Request request = new Request.Builder()
                    .url(params[0] + "getComment/")
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("ho's activity", "DetailActivity.GetCommentList.onPostExecute" + s);

            if(s != null && s != ""){
                try{
                    // 통째로 받아들여서 하나씩 자르기 위한 json object
                    JSONObject reader = new JSONObject(s);

                    // 하나씩 잘라서 adapter에 저장해야 한다
                    JSONArray list = reader.getJSONArray("");

                    Comment comment;

                    for(int i=0;i<list.length();i++){
                        JSONObject obj = list.getJSONObject(i);

                        comment = new Comment(
                                contentNo,
                                0,
                                obj.getString("name"),
                                obj.getString("comment"),
                                obj.getString("profile"));

                        commentArrayList.add(comment);

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

//            if(commentArrayList.size() > 0){
//                commentCard.setVisibility(View.VISIBLE);
//            }
//            for(int i=0;i<commentArrayList.size();i++){
//                Log.d("------>", commentArrayList.get(i).getMessage());
//            }

            commentAdapter.notifyDataSetChanged();
        }
    }
}
