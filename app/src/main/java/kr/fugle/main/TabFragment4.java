package kr.fugle.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;
import kr.fugle.webconnection.PostSingleData;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment4 extends Fragment {

    // 액티비티간 데이터 통신을 위한 코드
    TabStatusListener tabStatusListener;

    // 위젯 객체
    ImageView backgroundImg;    // 커버사진
    ImageView profileView;  // 프로필 사진
    LinearLayout profComment;   // 코멘트 목록 가는 버튼
    LinearLayout profLike;  // 보고싶어요 목록 가는 버튼
    LinearLayout profHate;  // 보기싫어요 목록 가는 버튼
    TextView comment;
    TextView like;
    TextView hate;
    Button profWebtoonBtn;
    Button profCartoonBtn;

    User user = User.getInstance();

    // 프로필 사진 갤러리에서 사진가져오기
    private final int REQ_PICK_CODE = 100;
    // 프로필 사진 이미지 주소
    private String imgPath;

    // 배경 사진 갤러리에서 사진가져오기
    private final int BACK_PICK_CODE = 101;
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
                activity.onFragmentChanged(6);
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
//        String profileImagePath = User.getInstance().getProfileImg();
//        if(profileImagePath != null && !profileImagePath.equals("")) {
//            Context c = getActivity().getApplicationContext();
//            CircleTransform circleTransform = new CircleTransform();
//            Picasso.with(c).load(profileImagePath)
//                    .transform(circleTransform)
//                    .into(profileView);
//        }

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
        if(message.equals("") || message.equals("null") || message == null) {
            user.setMessage("");
            message = "자기소개 글을 입력해주세요";
        }
        final TextView profMessage = (TextView)rootView.findViewById(R.id.prof_message);
        profMessage.setText(message);
        profMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭시 자기소개 수정 다이얼로그

                // 다이얼로그 객체 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
                builder.setCancelable(true)
                        .setView(R.layout.dialog_message);

                final AppCompatDialog dialog = builder.create();

                dialog.show();

                DisplayMetrics metrics = new DisplayMetrics();
                WindowManager windowManager = (WindowManager) getContext()
                        .getApplicationContext()
                        .getSystemService(Context.WINDOW_SERVICE);
                windowManager.getDefaultDisplay().getMetrics(metrics);

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = (int)(metrics.widthPixels * 0.9);
//                params.height = (int)(metrics.heightPixels * 0.5);
                dialog.getWindow().setAttributes(params);

                final EditText message = (EditText)dialog.findViewById(R.id.message);
                Button doneBtn = (Button) dialog.findViewById(R.id.doneBtn);

                if(!user.getMessage().equals(""));
                    message.setText(user.getMessage());

                assert doneBtn != null;
                doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String data = message.getText().toString();

                        if(data.equals("")){
                            Toast.makeText(getContext(), "자기소개를 입력해주세요", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new PostSingleData(getContext())
                                .execute("message/",
                                        User.getInstance().getNo() + "",
                                        data);

                        // 변경사항 로컬에 저장
                        user.setMessage(data);
                        profMessage.setText(data);

                        Toast.makeText(getContext(), "입력되었습니다", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
            }
        });

        // 코멘트 목록 버튼
        profComment = (LinearLayout) rootView.findViewById(R.id.prof_comment);
        profComment.setOnClickListener(onClicked);

        // 보고싶어요 목록 버튼
        profLike = (LinearLayout) rootView.findViewById(R.id.prof_like);
        profLike.setOnClickListener(onClicked);

        // 보기싫어요 목록 버튼
        profHate = (LinearLayout) rootView.findViewById(R.id.prof_hate);
        profHate.setOnClickListener(onClicked);

        // 코멘트 갯수
        comment = (TextView) rootView.findViewById(R.id.comment);

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
                case R.id.prof_comment: {   // 코멘트 목록
                    activity.onFragmentChanged(9);
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
                case R.id.prof_webtoon_btn: {   // 내가 별점 준 웹툰 목록
                    activity.onFragmentChanged(2);
                    break;
                }
                case R.id.prof_cartoon_btn: {   // 내가 별점 준 만화 목록
                    activity.onFragmentChanged(5);
                    break;
                }
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();

        Log.d("ho's activity", "TabFragment4.onResume");

        // 유저의 정보 적용
        if(user.getProfileImg() != null && user.getProfileImg().equals("")) {
            CircleTransform circleTransform = new CircleTransform();
            Picasso.with(getContext().getApplicationContext())
                    .load(user.getProfileImg())
                    .transform(circleTransform)
                    .into(profileView);
        }

        Log.d("uwangg's user back : ",user.getProfileBackground());

        if (!user.getProfileBackground().equals("")) {
            Picasso.with(getContext().getApplicationContext())
                    .load(user.getProfileBackground())
                    .resize(width, height)
                    .centerCrop()
                    .into(backgroundImg);
        }
        comment.setText(user.getComments().toString());
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
            new PostSingleData(getContext())
                    .execute("userProfileImg/",
                            User.getInstance().getNo()+"",
                            imgPath);
        }
        if (requestCode == BACK_PICK_CODE) {
            imgPath = data.getData().toString();
            Log.d("uwangg's back data : ", data.getData().toString());
            user.setProfileBackground(imgPath);
            new PostSingleData(getContext())
                    .execute("userProfileBackground/",
                            User.getInstance().getNo()+"",
                            imgPath);
        }
    }
}
