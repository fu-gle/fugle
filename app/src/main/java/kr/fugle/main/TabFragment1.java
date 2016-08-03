package kr.fugle.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment1 extends Fragment implements View.OnClickListener {

    TabStatusListener tabStatusListener;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment1, container,false);

        // 추천 이미지
        int width, height;
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = metrics.heightPixels;
        Picasso.with(getContext()).load(R.drawable.kero1)
                .resize(width, height)
                .into((ImageView) rootView.findViewById(R.id.today_webtoon_img));
        Picasso.with(getContext()).load(R.drawable.frog)
                .resize(width, height)
                .into((ImageView) rootView.findViewById(R.id.today_cartoon_img));


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
            default:
                break;
        }
    }
}
