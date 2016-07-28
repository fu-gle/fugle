package kr.fugle.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment4 extends Fragment {

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

        return rootView;
    }
}
