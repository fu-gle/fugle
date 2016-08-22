package kr.fugle.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    // 위젯 객체
    ImageView backgroundImg;    // 커버사진
    ImageView profileView;  // 프로필 사진
    LinearLayout profLike;  // 보고싶어요 목록 가는 버튼
    LinearLayout profHate;  // 보기싫어요 목록 가는 버튼
    TextView like;
    TextView hate;
    Button profWebtoonBtn;
    Button profCartoonBtn;

    User user = User.getInstance();

    // 프로필 사진 갤러리에서 사진가져오기
    private int REQ_PICK_CODE = 100;
    // 프로필 사진 이미지 주소
    private String imgPath;

    // 배경 사진 갤러리에서 사진가져오기
    private int BACK_PICK_CODE = 101;
    private int width;
    private int height;

    public void setTabStatusListener(TabStatusListener tabStatusListener) {
        this.tabStatusListener = tabStatusListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fragment4, container, false);

        // 취향분석 버튼
        Button favoriteBtn = (Button) rootView.findViewById(R.id.prof_favorite_button);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.onFragmentChanged(0);
            }
        });

        // 평가하기 버튼
        Button ratingBtn = (Button) rootView.findViewById(R.id.prof_rating_button);
        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.onFragmentChanged(0);
            }
        });

        // 커버사진 (프로필 사진 뒤에 사진)
        backgroundImg = (ImageView) rootView.findViewById(R.id.header_cover_image);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext()
                .getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        width = metrics.widthPixels;
        height = metrics.heightPixels/3;
        if (!user.getProfileBackground().equals("")) {    // 배경사진이 있다면
            Picasso.with(getContext().getApplicationContext())
                    .load(user.getProfileBackground())
                    .resize(width, height)
                    .centerCrop()
                    .into(backgroundImg);
        }

        // 커버 사진 변경
        backgroundImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickerIntent = new Intent(Intent.ACTION_PICK);
                pickerIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                pickerIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(pickerIntent, BACK_PICK_CODE);
            }
        });

        // 프로필 사진
        profileView = (ImageView) rootView.findViewById(R.id.user_profile_photo);
        String profileImagePath = user.getProfileImg();
        CircleTransform circleTransform = new CircleTransform();
        Picasso.with(getActivity().getApplicationContext())
                .load(profileImagePath)
                .transform(circleTransform)
                .into(profileView);

        // 프로필 사진 변경
//        profileView.setOnClickListener(onClicked);
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickerIntent = new Intent(Intent.ACTION_PICK);
                pickerIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                pickerIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(pickerIntent, REQ_PICK_CODE);
            }
        });

        // 이름
        String name = user.getName();
        TextView nameView = (TextView) rootView.findViewById(R.id.prof_name);
        nameView.setText(name);

        // 자기소개
        String message = user.getMessage();
        if (message.equals("") || message.equals("null") || message == null)
            message = "자기소개 글을 입력해주세요";
        TextView profMessage = (TextView) rootView.findViewById(R.id.prof_message);
        profMessage.setText(message);
        profMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭시 자기소개 수정 다이얼로그

            }
        });

        // 보고싶어요 목록 버튼
        profLike = (LinearLayout) rootView.findViewById(R.id.prof_like);
        profLike.setOnClickListener(onClicked);

        // 보기싫어요 목록 버튼
        profHate = (LinearLayout) rootView.findViewById(R.id.prof_hate);
        profHate.setOnClickListener(onClicked);

        // 보고싶어요 갯수
        like = (TextView) rootView.findViewById(R.id.like);

        // 보기싫어요 갯수
        hate = (TextView) rootView.findViewById(R.id.hate);

        // 내가 별점 준 웹툰 버튼
        profWebtoonBtn = (Button) rootView.findViewById(R.id.prof_webtoon_btn);
        profWebtoonBtn.setOnClickListener(onClicked);

        // 내가 별점 준 만화 버튼
        profCartoonBtn = (Button) rootView.findViewById(R.id.prof_cartoon_btn);
        profCartoonBtn.setOnClickListener(onClicked);

        // 로그아웃
        rootView.findViewById(R.id.logout_btn).setOnClickListener(onProfLogoutButtonClicked);

        // 내 정보(보고싶어요 갯수, 별점준 작품 갯수) 가져오기
//        new GetMyData().execute("mypage/", User.getInstance().getNo() + "");

        return rootView;
    }

    Button.OnClickListener onClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            MainActivity activity = (MainActivity) getActivity();

            switch (v.getId()) {
                case R.id.header_cover_image: { // 커버 사진 변경
                    Intent pickerIntent = new Intent(Intent.ACTION_PICK);
                    pickerIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    pickerIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(pickerIntent, ((MainActivity) getActivity()).REQ_BACKGROUND_PICK_CODE);
                    break;
                }
                case R.id.user_profile_photo: { // 프로필 사진 변경
                    Intent pickerIntent = new Intent(Intent.ACTION_PICK);
                    pickerIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    pickerIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(pickerIntent, ((MainActivity) getActivity()).REQ_PROFILE_PICK_CODE);
                    break;
                }
                case R.id.prof_webtoon_btn: {   // 내가 별점 준 웹툰 목록
                    activity.onFragmentChanged(2);
                    break;
                }
                case R.id.prof_cartoon_btn: {   // 내가 별점 준 만화 목록
                    activity.onFragmentChanged(5);
                    break;
                }
                case R.id.prof_like: {  // 보고싶어요 목록
                    activity.onFragmentChanged(7);
                    break;
                }
                case R.id.prof_hate: {  // 보기싫어요 목록
                    activity.onFragmentChanged(8);
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
                MainActivity activity = (MainActivity) getActivity();
                activity.onFragmentChanged(1);
                Log.d("---->", "페북로그아웃");
            }

            // 카카오톡
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Session.getCurrentSession().close();
                    MainActivity activity = (MainActivity) getActivity();
                    activity.onFragmentChanged(1);
                    Log.d("---->", "카톡로그아웃");
                }
            });
            Log.d("---->", "if밖로그아웃");
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        Log.d("ho's activity", "TabFragment4.onResume");

        // 유저의 정보 적용
        CircleTransform circleTransform = new CircleTransform();
        Picasso.with(getContext().getApplicationContext())
                .load(user.getProfileImg())
                .transform(circleTransform)
                .into(profileView);

        Log.d("uwangg's user back : ",user.getProfileBackground());

        if (!user.getProfileBackground().equals("")) {
            Picasso.with(getContext().getApplicationContext())
                    .load(user.getProfileBackground())
                    .resize(width, height)
                    .centerCrop()
                    .into(backgroundImg);
        }
        like.setText(user.getLikes().toString());
        hate.setText(user.getHates().toString());
        profWebtoonBtn.setText("웹툰 " + user.getWebtoonStars().toString());
        profCartoonBtn.setText("만화 " + user.getCartoonStars().toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_PICK_CODE) {
            imgPath = data.getData().toString();
            Log.d("uwangg's camera data : ", data.getData().toString());
            user.setProfileImg(imgPath);
        }
        if (requestCode == BACK_PICK_CODE) {
            imgPath = data.getData().toString();
            Log.d("uwangg's back data : ", data.getData().toString());
            user.setProfileBackground(imgPath);
        }
    }
}
