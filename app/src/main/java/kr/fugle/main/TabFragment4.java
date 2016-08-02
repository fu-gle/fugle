package kr.fugle.main;

import android.content.Context;
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

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment4 extends Fragment {

    // 액티비티간 데이터 통신을 위한 코드

    TabStatusListener tabStatusListener;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile, container,false);

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
        if(message.equals(""))
            message = "자기소개";
        TextView profMessage = (TextView)rootView.findViewById(R.id.prof_message);
        profMessage.setText(message);
        profMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭시 자기소개 수정 다이얼로그
                
            }
        });

        // 로그아웃
        rootView.findViewById(R.id.logout_btn).setOnClickListener(onProfLogoutButtonClicked);

        return rootView;
    }

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

}
