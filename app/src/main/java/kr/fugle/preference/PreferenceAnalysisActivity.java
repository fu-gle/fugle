package kr.fugle.preference;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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

    private User user;

    // 서버 통신
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private OkHttpClient client;
    private String serverUrl;

    // 취향 태그, 미디어, 장르
    public ArrayList<Tag> tagArrayList;
    public ArrayList<Media> mediaArrayList;
    public ArrayList<Genre> genreArrayList;

    Fragment preferenceKeywordFragment;
    Fragment preferenceMediaFragment;
    Fragment preferenceGenreFragment;

    // 서버 통신
    private GetPreferenceList getPreferenceList;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_analysis);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(PreferenceAnalysisActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        // 서버 통신용 객체
        client = new OkHttpClient();
        serverUrl = getApplicationContext().getResources().getString(R.string.server_url);

        user = User.getInstance();
        tagArrayList = new ArrayList<>();
        mediaArrayList = new ArrayList<>();
        genreArrayList = new ArrayList<>();

        // 로딩 시작
        loadingDialog.show();

        getPreferenceList = new GetPreferenceList();
        getPreferenceList.setLoadingDialog(loadingDialog);
        getPreferenceList.execute("userTaste/", user.getNo() + "");

        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(user.getName() + "님의 취향분석 결과예요");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 이미지 크기 조절 용
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        // 프로필사진
        ImageView profileImg = (ImageView) findViewById(R.id.profile_img);
        if(user.getProfileImg() != null && !user.getProfileImg().equals("")) {
            CircleTransform circleTransform = new CircleTransform();
            Picasso.with(getApplicationContext())
                    .load(user.getProfileImg())
                    .resize(metrics.widthPixels / 3, metrics.heightPixels / 3)
                    .centerInside()
                    .transform(circleTransform)
                    .into(profileImg);
        }

        // 사용자 이름
        TextView name = (TextView) findViewById(R.id.user_name);
        name.setText(user.getName());

        // 평가 갯수
        TextView starCount = (TextView) findViewById(R.id.star_count);
        starCount.setText(user.getCartoonStars() + user.getWebtoonStars() + "");

        CardView cardView1 = (CardView) findViewById(R.id.cardview1);
        CardView cardView2 = (CardView) findViewById(R.id.cardview2);
        CardView cardView3 = (CardView) findViewById(R.id.cardview3);

        // 평가가 없을때
        CardView cardView = (CardView) findViewById(R.id.zero_cardview);
        if((user.getCartoonStars()+user.getWebtoonStars())<15) {

            TextView zeroText = (TextView) findViewById(R.id.zero_count_text);
            zeroText.setText(user.getName() + "님 아직 평가가 부족해서 \n취향을 알 수 없어요ㅠㅠ"
                    + "\n15개 이상 평가를 하셔야 취향을 알 수 있어요!");
            // 평가하러가기
            Button button = (Button) findViewById(R.id.go_star);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(PreferenceAnalysisActivity.this, RatingActivity.class));
                    finish();
                }
            });

            cardView1.setVisibility(View.GONE);
            cardView2.setVisibility(View.GONE);
            cardView3.setVisibility(View.GONE);
            cardView.setVisibility(View.VISIBLE);
        } else {
            cardView1.setVisibility(View.VISIBLE);
            cardView2.setVisibility(View.VISIBLE);
            cardView3.setVisibility(View.VISIBLE);
            cardView.setVisibility(View.GONE);
        }

        preferenceKeywordFragment = new PreferenceKeywordFragment();
        preferenceMediaFragment = new PreferenceMediaFragment();
        preferenceGenreFragment = new PreferenceGenreFragment();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.cardview1, preferenceKeywordFragment);
        fragmentTransaction.replace(R.id.cardview2, preferenceMediaFragment);
        fragmentTransaction.replace(R.id.cardview3, preferenceGenreFragment);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getPreferenceList.cancel(true);
    }

    private class GetPreferenceList extends AsyncTask<String, Void, String> {

        private AppCompatDialog loadingDialog;

        public void setLoadingDialog(AppCompatDialog loadingDialog) {
            this.loadingDialog = loadingDialog;
        }

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
                result = response.body().string();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(loadingDialog != null)
                loadingDialog.cancel();

            if(isCancelled()){
                return;
            }

            ArrayList<Author> authorArrayList = new ArrayList<>();

            Log.d("uwangg's activity", "GetPreferenceList " + s);

            if (s != null && s != "") {
                try {
                    // 통째로 받아들여서 하나씩 자르기 위한 json object
                    JSONObject reader = new JSONObject(s);

                    JSONArray tags = reader.getJSONArray("tags");
                    for (int i = 0; i < tags.length(); i++) {
                        // tagName, tagCount
                        JSONObject obj = tags.getJSONObject(i);
                        Tag tag = new Tag();
                        if (!obj.isNull("tagName")) {
                            tag.setName(obj.getString("tagName"));
                        }
                        if (!obj.isNull("tagCount")) {
                            tag.setCount(obj.getInt("tagCount"));
                        }
                        tagArrayList.add(tag);
                    }
                    JSONArray medias = reader.getJSONArray("media");
                    Log.d("media--->", medias.toString());
                    for (int i = 0; i < medias.length(); i++) {
                        // tagName, tagCount
                        JSONObject obj = medias.getJSONObject(i);
                        Media media = new Media();
                        if (!obj.isNull("mediaName")) {
                            media.setName(obj.getString("mediaName"));
                        }
                        if (!obj.isNull("mediaAverage")) {
                            media.setAverage((float) obj.getInt("mediaAverage") / 10);
                        }
                        if (!obj.isNull("mediaCount")) {
                            media.setCount(obj.getInt("mediaCount"));
                        }
                        mediaArrayList.add(media);
                    }

                    JSONArray genres = reader.getJSONArray("genre");
                    Log.d("genre--->", genres.toString());
                    for (int i = 0; i < genres.length(); i++) {
                        // tagName, tagCount
                        JSONObject obj = genres.getJSONObject(i);
                        Genre genre = new Genre();
                        if (!obj.isNull("genreName")) {
                            genre.setName(obj.getString("genreName"));
                        }
                        if (!obj.isNull("genreAverage")) {
                            genre.setAverage((float) obj.getInt("genreAverage") / 10);
                        }
                        if (!obj.isNull("genreCount")) {
                            genre.setCount(obj.getInt("genreCount"));
                        }
                        genreArrayList.add(genre);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.d("mediaSize----->",mediaArrayList.size()+"");
            Log.d("genreSize----->",genreArrayList.size()+"");

            if (tagArrayList.size() == 0 || mediaArrayList.size() == 0 || genreArrayList.size() == 0)
                return;

            preferenceKeywordFragment.onResume();
            preferenceMediaFragment.onResume();
            preferenceGenreFragment.onResume();
        }
    }
}
