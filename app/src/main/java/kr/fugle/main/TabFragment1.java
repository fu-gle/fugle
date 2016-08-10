package kr.fugle.main;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment1 extends Fragment implements View.OnClickListener {

    ArrayList<Content> contentArrayList;
    int width, height;

    ImageView todayWebtoonImg;
    ImageView todayCartoonImg;

    TabStatusListener tabStatusListener;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    // 서버 통신
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client;
    String serverUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 서버 통신용 객체
        client = new OkHttpClient();
        serverUrl = getContext().getApplicationContext().getResources().getString(R.string.server_url);

        contentArrayList = new ArrayList<>();

        View rootView = inflater.inflate(R.layout.tab_fragment1, container,false);

        todayWebtoonImg = (ImageView) rootView.findViewById(R.id.today_webtoon_img);
        todayCartoonImg = (ImageView) rootView.findViewById(R.id.today_cartoon_img);

        // 추천 이미지
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels / 3;
        Picasso.with(getContext()).load(R.drawable.kero1)
                .resize(width, height)
                .into(todayWebtoonImg);
        Picasso.with(getContext()).load(R.drawable.frog)
                .resize(width, height)
                .into(todayCartoonImg);


        // 1. 취향분석

        // 1 - (1)프로필 사진
        Context c = getActivity().getApplicationContext();
        ImageView profileView = (ImageView)rootView.findViewById(R.id.home_prof_img) ;
        String profileImagePath = User.getInstance().getProfileImg();
        CircleTransform circleTransform = new CircleTransform();
        Picasso.with(c).load(profileImagePath)
                .transform(circleTransform)
                .into(profileView);


        // 1 - (2) 취향 내용
        // 2가지 경우로 나뉠수 있다.
        //  i) 취향을 입력하지 않은 경우 (or 첫 가입 후)
        //      취향을 입력 하지 않았음을 알려줌
        //      버튼 - 나중에하기 & 평가하기
        //  ii) 취향을 입력한 후
        //      취향 분석 결과를 보여줌
        //      버튼 - 취향분석결과 & 나중에보기

        // i) 경우 코드
        // content부분
        String tabLikeContent = User.getInstance().getName()
                + "님 아직 취향을 입력하지 않으셨네요\n"
                + User.getInstance().getName()
                + "님의 취향을 더 알아야 취향분석을 할수 있어요!"
                + "평가를 입력해주세요~";
        TextView likeView = (TextView)rootView.findViewById(R.id.tab1_like_content);
        likeView.setText(tabLikeContent);
        // button부분
        TextView likeBtn1 = (TextView) rootView.findViewById(R.id.tab1_like_btn1);
        likeBtn1.setOnClickListener(this);
        TextView likeBtn2 = (TextView) rootView.findViewById(R.id.tab1_like_btn2);
        likeBtn2.setOnClickListener(this);
        TextView moreWebtoon = (TextView) rootView.findViewById(R.id.more_today_webtoon);
        moreWebtoon.setOnClickListener(this);

        // 오늘의 추천 리스트 가져오기
        new GetMainList().execute("mainPage/", User.getInstance().getNo() + "");

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case  R.id.tab1_like_btn1: {    // 1번 - 나중에하기 or 나중에보기
                Toast.makeText(getContext(), "나중에할끄양", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.tab1_like_btn2: { // 1번 - 평가하기 or 취향분석보기
                MainActivity activity = (MainActivity)getActivity();
                activity.onFragmentChanged(0);
                break;
            }
            case R.id.more_today_webtoon: { // 오늘의 웹툰 더보기
                // 여기서 액티비티 갑니당
            }
            default:
                break;
        }
    }

    private class GetMainList extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            Log.d("ho's activity", "GetMainList.doInBackground");

            String data = "userId=" + params[1];
            Log.d("ho's activity", "GetMainList data " + data);

            RequestBody body = RequestBody.create(HTML, data);

            Request request = new Request.Builder()
                    .url(serverUrl + params[0])
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

            Log.d("ho's activity", "GetMainList " + s);

            if(s != null && s != ""){
                try{
                    // 통째로 받아들여서 하나씩 자르기 위한 json object
                    JSONObject reader = new JSONObject(s);

                    // 하나씩 잘라서 adapter에 저장해야 한다
                    JSONArray dataList = reader.getJSONArray("");

                    for(int i=0;i<dataList.length();i++){
                        JSONObject obj = dataList.getJSONObject(i);

                        Content content = new Content();
                        content.setNo(obj.getInt("id"));
                        content.setTitle(obj.getString("title"));
                        content.setAuthor(obj.getString("author"));
                        content.setGenre(obj.getString("genre"));
                        content.setAdult(obj.getBoolean("adult"));
                        content.setThumbnailSmall(obj.getString("thumbnail_small"));
                        content.setThumbnailBig(obj.getString("thumbnail_big"));
                        if(!obj.isNull("star__star")) {
                            Log.d("------>", "star__star" + obj.getInt("star__star"));
                            content.setRating((float) (obj.getInt("star__star") * 1.0) / 10);
                        }
                        if(!obj.isNull("like") && obj.getBoolean("like")){
                            Log.d("----->", "like " + obj.getBoolean("like"));
                            content.setLike(obj.getBoolean("like"));
                        }
                        if(!obj.isNull("recommendStar")) {
                            Log.d("------>", "recommendStar " + obj.getString("recommendStar"));
                            content.setPrediction(Float.parseFloat(String.format("%.1f",Float.parseFloat(obj.getString("recommendStar")) / 1000000)));
                        }
                        if(!obj.isNull("link")) {
                            content.setLink(obj.getString("link"));
                        }
                        if(!obj.isNull("tags")){
                            content.setTags(obj.getString("tags"));
                        }

                        contentArrayList.add(content);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            if(contentArrayList.size() == 0)
                return;

            Picasso.with(getContext())
                    .load(contentArrayList.get(0).getThumbnailBig())
                    .resize(width, height)
                    .centerCrop()
                    .into(todayWebtoonImg);
        }
    }
}
