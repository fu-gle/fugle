package kr.fugle.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
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
public class TabFragment4 extends Fragment {

    final int RATING_REQUEST_CODE = 501;
    final int RATING_RESULT_CODE = 505;

    // 액티비티간 데이터 통신을 위한 코드
    TabStatusListener tabStatusListener;

    // 서버 통신
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client;
    String serverUrl;

    // 위젯 객체
    TextView like;
    TextView hate;
    Button profWebtoonBtn;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment4, container,false);

        // 서버 통신용 객체
        client = new OkHttpClient();
        serverUrl = getContext().getApplicationContext().getResources().getString(R.string.server_url);

        // 취향분석 버튼
        Button favoriteBtn = (Button)rootView.findViewById(R.id.prof_favorite_button);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                activity.onFragmentChanged(0);
            }
        });

        // 평가하기 버튼
        Button ratingBtn = (Button)rootView.findViewById(R.id.prof_rating_button);
        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity)getActivity();
                activity.onFragmentChanged(0);
            }
        });

        // 커버사진 (프로필 사진 뒤에 사진)


        // 프로필 사진
        Context c = getActivity().getApplicationContext();
        ImageView profileView = (ImageView)rootView.findViewById(R.id.user_profile_photo) ;
        String profileImagePath = User.getInstance().getProfileImg();
        CircleTransform circleTransform = new CircleTransform();
        Picasso.with(c).load(profileImagePath)
                .transform(circleTransform)
                .into(profileView);

        // 이름
        String name = User.getInstance().getName();
        TextView nameView = (TextView)rootView.findViewById(R.id.prof_name);
        nameView.setText(name);

        // 자기소개
        String message = User.getInstance().getMessage();
        if(message.equals("") || message.equals("null") || message == null)
            message = "자기소개 글을 입력해주세요";
        TextView profMessage = (TextView)rootView.findViewById(R.id.prof_message);
        profMessage.setText(message);
        profMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭시 자기소개 수정 다이얼로그
                
            }
        });

        // 보고싶어요 갯수
        like = (TextView) rootView.findViewById(R.id.like);

        // 보기싫어요 갯수
        hate = (TextView) rootView.findViewById(R.id.hate);

        // 내가 별점 준 웹툰 버튼
        profWebtoonBtn = (Button)rootView.findViewById(R.id.prof_webtoon_btn);
        profWebtoonBtn.setOnClickListener(onProfWebtoonButtonClicked);

        // 로그아웃
        rootView.findViewById(R.id.logout_btn).setOnClickListener(onProfLogoutButtonClicked);

        // 내 정보(보고싶어요 갯수, 별점준 작품 갯수) 가져오기
        new GetMyData().execute("mypage/", User.getInstance().getNo() + "");

        return rootView;
    }

    Button.OnClickListener onProfWebtoonButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("------->", "MyStarActivity로 간다");
            MainActivity activity = (MainActivity)getActivity();
            activity.onFragmentChanged(2);
        }
    };

    Button.OnClickListener onProfLogoutButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 페이스북
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
                MainActivity activity = (MainActivity)getActivity();
                activity.onFragmentChanged(1);
                Log.d("---->","페북로그아웃");
            }

            // 카카오톡
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Session.getCurrentSession().close();
                    MainActivity activity = (MainActivity)getActivity();
                    activity.onFragmentChanged(1);
                    Log.d("---->","카톡로그아웃");
                }
            });
            Log.d("---->","if밖로그아웃");
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        Log.d("ho's activity", "TabFragment4.onResume");
        User user = User.getInstance();

        like.setText(user.getLikes());
        profWebtoonBtn.setText(user.getStars());
        hate.setText(user.getHates());
    }

    private class GetMyData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.d("ho's activity", "GetMyData.doInBackground");

            String data = "userId=" + params[1];
            Log.d("ho's activity", "GetMyData data " + data);

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

            Log.d("ho's activity", "GetMyData.onPostExecute " + s);

            if(s != null && s != ""){
                try{
                    JSONObject reader = new JSONObject(s);

                    // 하나씩 잘라서 adapter에 저장해야 한다
                    JSONArray dataList = reader.getJSONArray("");

                    JSONObject object = dataList.getJSONObject(0);

                    User user = User.getInstance();

                    if(!object.isNull("likecount"))
                        user.setLikes(object.getInt("likecount"));
                    if(!object.isNull("starcount"))
                        user.setStars(object.getInt("starcount"));
                    if(!object.isNull("dontsee"))
                        user.setHates(object.getInt("dontsee"));    // 보기싫어요 받는 파라미터 정해야함
                }catch (Exception e){
                    e.printStackTrace();
                }

                like.setText(User.getInstance().getLikes().toString());
                profWebtoonBtn.setText("웹툰 " + User.getInstance().getStars());
                hate.setText(User.getInstance().getHates());

            }
        }
    }
}
