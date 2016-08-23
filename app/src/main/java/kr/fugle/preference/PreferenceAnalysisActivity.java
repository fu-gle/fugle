package kr.fugle.preference;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.Author;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;
import kr.fugle.preference.item.Genre;
import kr.fugle.preference.item.Media;
import kr.fugle.preference.item.Tag;
import kr.fugle.rating.RatingActivity;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 김은진 on 2016-08-22.
 */
// 취향분석
public class PreferenceAnalysisActivity extends AppCompatActivity {

    User user;

    // 서버 통신
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client;
    String serverUrl;

    // 취향 태그, 미디어, 장르
    public ArrayList<Tag> tagArrayList;
    public ArrayList<Media> mediaArrayList;
    public ArrayList<Genre> genreArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_analysis);

        // 서버 통신용 객체
        client = new OkHttpClient();
        serverUrl = getApplicationContext().getResources().getString(R.string.server_url);

        user = User.getInstance();
        tagArrayList = new ArrayList<>();
        mediaArrayList = new ArrayList<>();
        genreArrayList = new ArrayList<>();

        new GetPreferenceList().execute("userTaste/", user.getNo()+"");

        // 툴바 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(user.getName() + "의 취향분석");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 프로필사진
        ImageView profileImg = (ImageView) findViewById(R.id.profile_img);
        CircleTransform circleTransform = new CircleTransform();
        Picasso.with(getApplicationContext())
                .load(user.getProfileImg())
                .transform(circleTransform)
                .into(profileImg);

        // 사용자 이름
        TextView name = (TextView) findViewById(R.id.user_name);
        name.setText(user.getName());

        // 평가 갯수
        TextView starCount = (TextView) findViewById(R.id.star_count);
        starCount.setText(user.getCartoonStars()+user.getWebtoonStars()+"");

        // 평가가 없을때
        CardView cardView = (CardView) findViewById(R.id.zero_cardview);
        if(user.getWebtoonStars()+user.getCartoonStars() != 0) {
            cardView.setVisibility(View.GONE);
            TextView zeroText = (TextView) findViewById(R.id.zero_count_text);
            zeroText.setText(user.getName() + "님 아직 평가가 없어서 \n취향을 알수없어요ㅠㅠ"
                    + "\n평가를 하셔야 취향을 알 수 있어요!");
            // 평가하러가기
            Button button = (Button) findViewById(R.id.go_star);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PreferenceAnalysisActivity.this, RatingActivity.class));
                    finish();
                }
            });
        }

        Fragment preferenceKeywordFragment = new PreferenceKeywordFragment();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.cardview1, preferenceKeywordFragment);
        fragmentTransaction.commit();
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

    private class GetPreferenceList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.d("uwangg's activity", "GetPreferenceList.doInBackground");

            String data = "userId=" + params[1];
            Log.d("uwangg's activity", "GetPreferenceList data " + data);

            RequestBody body = RequestBody.create(HTML, data);

            Request request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .post(body)
                    .build();

            // json 데이터가 담길 변수
            String result = "";

            try {
                // 서버 통신 실행
                Response response = client.newCall(request).execute();

                // json 형태로의 변환을 위해 { "" :  } 추가
                result = "{\"\":" + response.body().string() + "}";
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayList<Author> authorArrayList = new ArrayList<>();

            Log.d("uwangg's activity", "GetPreferenceList " + s);

            if (s != null && s != "") {
                try {
                    // 통째로 받아들여서 하나씩 자르기 위한 json object
                    JSONObject reader = new JSONObject(s);

                    // 하나씩 잘라서 adapter에 저장해야 한다
                    JSONArray dataList = reader.getJSONArray("");

                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject obj = dataList.getJSONObject(i);

//                        media:{mediaName:naver,mediaAverage:40,mediaCount:23}

                        // 태그를 보낼경우
                        if (!obj.isNull("tag")) {
                            // tagName, tagCount
                            Tag tag = new Tag();
                            if (!obj.isNull("tagName")) {
                                tag.setName(obj.getString("tagName"));
                            }
                            if (!obj.isNull("tagCount")) {
                                tag.setCount(obj.getInt("tagCount"));
                            }
                            tagArrayList.add(tag);
                        }

                        // 미디어를 보낼경우
                        if (!obj.isNull("media")) {
                            Media media = new Media();
                            if (!obj.isNull("mediaName")) {
                                media.setName(obj.getString("mediaName"));
                            }
                            if (!obj.isNull("mediaAverage")) {
                                media.setAverage((float) obj.getInt("mediaAverage") / 1000);
                            }
                            if (!obj.isNull("mediaCount")) {
                                media.setCount(obj.getInt("mediaCount"));
                            }
                            mediaArrayList.add(media);
                        }

                        // 장르를 보낼경우
                        if (!obj.isNull("genre")) {
                            Genre genre = new Genre();
                            if (!obj.isNull("genreName")) {
                                genre.setName(obj.getString("genreName"));
                            }
                            if (!obj.isNull("genreAverage")) {
                                genre.setAverage((float) obj.getInt("genreAverage") / 1000);
                            }
                            if (!obj.isNull("genreCount")) {
                                genre.setCount(obj.getInt("genreCount"));
                            }
                            genreArrayList.add(genre);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (tagArrayList.size() == 0 || mediaArrayList.size() == 0 || genreArrayList.size() == 0)
                return;


        }
    }
}
