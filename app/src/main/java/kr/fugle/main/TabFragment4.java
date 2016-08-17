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

    // 위젯 객체
    TextView like;
    TextView hate;
    Button profWebtoonBtn;
    Button profCartoonBtn;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment4, container,false);

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
        profWebtoonBtn.setOnClickListener(onToonClicked);

        // 내가 별점 준 만화 버튼
        profCartoonBtn = (Button)rootView.findViewById(R.id.prof_cartoon_btn);
        profCartoonBtn.setOnClickListener(onToonClicked);

        // 로그아웃
        rootView.findViewById(R.id.logout_btn).setOnClickListener(onProfLogoutButtonClicked);

        // 내 정보(보고싶어요 갯수, 별점준 작품 갯수) 가져오기
//        new GetMyData().execute("mypage/", User.getInstance().getNo() + "");

        return rootView;
    }

    Button.OnClickListener onToonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.prof_webtoon_btn: {   // 내가 별점 준 웹툰 목록
                    MainActivity activity = (MainActivity)getActivity();
                    activity.onFragmentChanged(2);
                    break;
                }
                case R.id.prof_cartoon_btn: {   // 내가 별점 준 만화 목록
                    MainActivity activity = (MainActivity)getActivity();
                    activity.onFragmentChanged(4);
                    break;
                }
            }
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

        // 유저의 정보 적용
        like.setText(user.getLikes().toString());
        hate.setText(user.getHates().toString());
        profWebtoonBtn.setText("웹툰 " + user.getStars().toString());
    }
}
