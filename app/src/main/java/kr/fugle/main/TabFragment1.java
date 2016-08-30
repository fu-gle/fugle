package kr.fugle.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import kr.fugle.Item.Content;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.detail.DetailActivity;
import kr.fugle.login.CircleTransform;
import kr.fugle.webconnection.PostSingleData;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class TabFragment1 extends Fragment implements View.OnClickListener {

    // 첫번째 카드뷰 버튼
    Boolean checkBtn;   // true:취향분석, false:평가하기

    ArrayList<Content> contentArrayList1;
    ArrayList<Content> contentArrayList2;
    Content webtoon, cartoon;   // 메인 페이지에 나올 객체들
    int width, height;
    AppCompatDialog ratingDialog;
    AppCompatDialog loadingDialog;

    // 1번째 카드뷰
    static boolean firstCardview = true;

    // 웹툰 정보
    ImageView todayWebtoonImg;
    TextView todayWebtoonFirstTitle;    // 몇월 몇주차 제목
    TextView todayWebtoonTitle; // 제목
    TextView todayWebtoonPrediction;    // 예상별점
    TextView todayWebtoonText;  // 이미지 밑에 있는 텍스트
    ImageView webtoonLikeImg;
    LinearLayout webtoonLikeBtn;
    TextView webtoonLike;
    LinearLayout webtoonRatingBtn;
    ImageView webtoonRatingImg;
    TextView webtoonRating;
    LinearLayout webtoonCommentBtn;
    ImageView webtoonCommentImg;

    // 카툰 정보
    ImageView todayCartoonImg;
    TextView todayCartoonFirstTitle;    // 몇월 몇주차 제목
    TextView todayCartoonTitle; // 제목
    TextView todayCartoonPrediction;    // 예상별점
    TextView todayCartoonText;
    LinearLayout cartoonLikeBtn;
    TextView cartoonLike;
    ImageView cartoonLikeImg;
    LinearLayout cartoonRatingBtn;
    ImageView cartoonRatingImg;
    TextView cartoonRating;
    LinearLayout cartoonCommentBtn;
    ImageView cartoonCommentImg;

    CardView cardView;

    TabStatusListener tabStatusListener;

    // 날짜
    Calendar cal;

    public void setTabStatusListener(TabStatusListener tabStatusListener) {
        this.tabStatusListener = tabStatusListener;
    }

    // 서버 통신
    public final MediaType HTML = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    OkHttpClient client;
    String serverUrl;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 서버 통신용 객체
        client = new OkHttpClient();
        serverUrl = getContext().getApplicationContext().getResources().getString(R.string.server_url);

        contentArrayList1 = ((MainActivity)getActivity()).mainList1;
        contentArrayList2 = ((MainActivity)getActivity()).mainList2;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.tab_fragment1, container, false);

        // 별점 다이얼로그 객체 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true)
                .setView(R.layout.dialog_rating);

        ratingDialog = builder.create();

        WindowManager.LayoutParams params = ratingDialog.getWindow().getAttributes();
        params.width = 800;
        ratingDialog.getWindow().setAttributes(params);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        // 웹툰
        todayWebtoonImg = (ImageView) rootView.findViewById(R.id.today_webtoon_img);
        todayWebtoonFirstTitle = (TextView) rootView.findViewById(R.id.today_webtoon_first_title);
        todayWebtoonTitle = (TextView) rootView.findViewById(R.id.today_webtoon_title);
        todayWebtoonPrediction = (TextView) rootView.findViewById(R.id.today_webtoon_prediction);
        todayWebtoonText = (TextView) rootView.findViewById(R.id.today_webtoon_text);
        webtoonLikeBtn = (LinearLayout) rootView.findViewById(R.id.webtoon_like_btn);
        webtoonLike = (TextView) rootView.findViewById(R.id.webtoon_like);
        webtoonLikeImg = (ImageView) rootView.findViewById(R.id.webtoon_like_img);
        webtoonRatingBtn = (LinearLayout) rootView.findViewById(R.id.webtoon_rating_btn);
        webtoonRatingImg = (ImageView) rootView.findViewById(R.id.webtoon_rating_img);
        webtoonRating = (TextView) rootView.findViewById(R.id.webtoon_rating);
        webtoonCommentBtn = (LinearLayout) rootView.findViewById(R.id.webtoon_comment_btn);
        webtoonCommentImg = (ImageView) rootView.findViewById(R.id.webtoon_comment_img);

        // 카툰
        todayCartoonImg = (ImageView) rootView.findViewById(R.id.today_cartoon_img);
        todayCartoonFirstTitle = (TextView) rootView.findViewById(R.id.today_cartoon_first_title);
        todayCartoonTitle = (TextView) rootView.findViewById(R.id.today_cartoon_title);
        todayCartoonPrediction = (TextView) rootView.findViewById(R.id.today_cartoon_prediction);
        todayCartoonText = (TextView) rootView.findViewById(R.id.today_cartoon_text);
        cartoonLikeBtn = (LinearLayout) rootView.findViewById(R.id.cartoon_like_btn);
        cartoonLike = (TextView) rootView.findViewById(R.id.cartoon_like);
        cartoonLikeImg = (ImageView) rootView.findViewById(R.id.cartoon_like_img);
        cartoonRatingBtn = (LinearLayout) rootView.findViewById(R.id.cartoon_rating_btn);
        cartoonRatingImg = (ImageView) rootView.findViewById(R.id.cartoon_rating_img);
        cartoonRating = (TextView) rootView.findViewById(R.id.cartoon_rating);
        cartoonCommentBtn = (LinearLayout) rootView.findViewById(R.id.cartoon_comment_btn);
        cartoonCommentImg = (ImageView) rootView.findViewById(R.id.cartoon_comment_img);

        // 월, 주차 표시
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        todayWebtoonFirstTitle.setText((cal.get(Calendar.MONTH)+1)+"월 "
                +(cal.get(Calendar.WEEK_OF_MONTH))+"주차의 인기 웹툰이예요!");
        todayCartoonFirstTitle.setText((cal.get(Calendar.MONTH)+1)+"월 "
                +(cal.get(Calendar.WEEK_OF_MONTH))+"주차의 인기 만화책이예요!");

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
        String profileImagePath = User.getInstance().getProfileImg();
        if(profileImagePath != null && !profileImagePath.equals("")) {
            Context c = getActivity().getApplicationContext();
            ImageView profileView = (ImageView) rootView.findViewById(R.id.home_prof_img);
            CircleTransform circleTransform = new CircleTransform();
            Picasso.with(c).load(profileImagePath)
                    .resize(metrics.widthPixels / 3, metrics.heightPixels / 3)
                    .centerInside()
                    .transform(circleTransform)
                    .into(profileView);
        }


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
        cardView = (CardView) rootView.findViewById(R.id.tab1_first_cardview);
        TextView likeView = (TextView) rootView.findViewById(R.id.tab1_like_content);
        TextView likeBtn1 = (TextView) rootView.findViewById(R.id.tab1_like_btn1);
        TextView likeBtn2 = (TextView) rootView.findViewById(R.id.tab1_like_btn2);
        TextView moreWebtoon = (TextView) rootView.findViewById(R.id.more_today_webtoon);
        TextView moreCartoon = (TextView) rootView.findViewById(R.id.more_today_cartoon);

        if(firstCardview == false) {
            cardView.setVisibility(View.GONE);
        } else {
            cardView.setVisibility(View.VISIBLE);
        }

        // true:취향분석, false:평가하기
        String tabLikeContent;
        if(User.getInstance().getWebtoonStars() + User.getInstance().getCartoonStars() < 15) {
            tabLikeContent = User.getInstance().getName()
                    + "님 아직 취향을 입력하지 않으셨네요\n"
                    + User.getInstance().getName()
                    + "님의 취향을 더 알아야 취향분석을 할수 있어요!"
                    + "평가를 입력해주세요~";
            checkBtn = false;
        } else {
            tabLikeContent = User.getInstance().getName()
                    + "님과 맞는 웹툰, 만화책이 궁금하세요?"
                    + User.getInstance().getName()
                    + "님 취향분석 한번 보고가세요~!\n";
            likeBtn1.setText("나중에볼래요");
            likeBtn2.setText("취향분석보기");
            checkBtn = true;
        }


        likeView.setText(tabLikeContent);
        // button부분
        likeBtn1.setOnClickListener(this);
        likeBtn2.setOnClickListener(this);
        moreWebtoon.setOnClickListener(this);
        moreCartoon.setOnClickListener(this);

        // 둘 다 비었을 경우 로딩 프로그래스바를 보여준다
        if(contentArrayList1.isEmpty() && contentArrayList2.isEmpty()) {
            Log.d("ho's activity", "main lists are empty");
            loadingDialog.show();
        }

        // 오늘의 추천 리스트 가져오기
        if(contentArrayList1.isEmpty() || contentArrayList2.isEmpty()) {
            if(contentArrayList1.isEmpty()) {
                GetMainList getMainList = new GetMainList(contentArrayList1, 1);
                getMainList.setLoadingDialog(loadingDialog);
                getMainList.execute("webtoonLog/", User.getInstance().getNo() + "");
            }
            if(contentArrayList2.isEmpty()) {
                GetMainList getMainList = new GetMainList(contentArrayList2, 2);
                getMainList.setLoadingDialog(loadingDialog);
                getMainList.execute("cartoonLog/", User.getInstance().getNo() + "");
            }
        } else {
            // 웹툰 정보 불러오기
            Picasso.with(getContext())
                    .load(webtoon.getThumbnailBig())
                    .resize(width, height)
                    .centerCrop()
                    .into(todayWebtoonImg);
            todayWebtoonTitle.setText(webtoon.getTitle());
            todayWebtoonPrediction.setText(webtoon.getPrediction().toString());
            if(webtoon.getLike()){  // 보고싶어요가 눌려있는 경우
                webtoonLike.setTextColor(Color.parseColor("#F13839"));
                Picasso.with(getContext().getApplicationContext())
                        .load(R.drawable.main_heart_fill)
                        .into(webtoonLikeImg);
            }

            // 카툰 정보 불러오기
            Picasso.with(getContext())
                    .load(cartoon.getThumbnailBig())
                    .resize(width, height)
                    .centerInside()
                    .into(todayCartoonImg);
            todayCartoonTitle.setText(cartoon.getTitle());
            todayCartoonPrediction.setText(cartoon.getPrediction().toString());
            if(cartoon.getLike()){  // 보고싶어요가 눌려있는 경우
                cartoonLike.setTextColor(Color.parseColor("#F13839"));
                Picasso.with(getContext().getApplicationContext())
                        .load(R.drawable.main_heart_fill)
                        .into(cartoonLikeImg);
            }
        }

        // 버튼에 온클릭 할당
        webtoonLikeBtn.setOnClickListener(this);
        webtoonRatingBtn.setOnClickListener(this);
        webtoonCommentBtn.setOnClickListener(this);
        cartoonLikeBtn.setOnClickListener(this);
        cartoonRatingBtn.setOnClickListener(this);
        cartoonCommentBtn.setOnClickListener(this);
        todayWebtoonImg.setOnClickListener(this);
        todayCartoonImg.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("------>", "tab1 onResume");
        if(!contentArrayList1.isEmpty()) {

            final Content content = contentArrayList1.get(0);
            webtoon = content;

            // 웹툰 정보 불러오기
            Picasso.with(getContext())
                    .load(content.getThumbnailBig())
                    .resize(width, height)
                    .centerCrop()
                    .into(todayWebtoonImg);
            todayWebtoonTitle.setText(content.getTitle());
            todayWebtoonPrediction.setText(content.getPrediction().toString());

            String tags = content.getTags();
            String [] tagList = tags.split(",");
            tags = "";
            for(int i = 0; i < tagList.length; i++){
                tags += "#" + tagList[i];
                if(i != tagList.length - 1){
                    tags += " ";
                }
            }

            if(tags.equals("#"))
                tags = "";
            else
                tags += "\n";

            todayWebtoonText.setText(
                    tags    + content.getLikeCnt()
                            + "명의 분들이 관심을 가지고 계세요!");

            if(content.getLike()){
                webtoonLike.setTextColor(Color.parseColor("#F13839"));
                Picasso.with(getContext().getApplicationContext())
                        .load(R.drawable.main_heart_fill)
                        .into(webtoonLikeImg);
            }
            todayWebtoonImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("content", content);
                    intent.putExtra("contentNo", content.getNo());
                    startActivity(intent);
                }
            });

            // 별점을 준 경우
            if(content.getRating() > 0){
                // 아이콘 수정
                Picasso.with(getContext().getApplicationContext())
                        .load(R.drawable.main_star_fill)
                        .into(webtoonRatingImg);

                // 텍스트 수정
                webtoonRating.setText((content.getRating()) + "");
                webtoonRating.setTextColor(Color.parseColor("#FF4081"));
            }
        }

        if(!contentArrayList2.isEmpty()) {

            final Content content = contentArrayList2.get(0);

            cartoon = content;

            // 카툰 정보 불러오기
            Picasso.with(getContext())
                    .load(content.getThumbnailBig())
                    .resize(width, height)
                    .centerInside()
                    .into(todayCartoonImg);
            todayCartoonTitle.setText(content.getTitle());
            todayCartoonPrediction.setText(content.getPrediction().toString());

            String tags = content.getTags();
            String [] tagList = tags.split(",");
            tags = "";
            for(int i = 0; i < tagList.length; i++){
                tags += "#" + tagList[i];
                if(i != tagList.length - 1){
                    tags += " ";
                }
            }

            if(tags.equals("#"))
                tags = "";
            else
                tags += "\n";

            todayWebtoonText.setText(
                    tags    + content.getLikeCnt()
                            + "명의 분들이 관심을 가지고 계세요!");

            if(content.getLike()){
                cartoonLike.setTextColor(Color.parseColor("#F13839"));
                Picasso.with(getContext().getApplicationContext())
                        .load(R.drawable.main_heart_fill)
                        .into(cartoonLikeImg);
            }
            todayCartoonImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), DetailActivity.class);
                    intent.putExtra("content", content);
                    intent.putExtra("contentNo", content.getNo());
                    startActivity(intent);
                }
            });

            // 별점을 준 경우
            if(content.getRating() > 0){
                // 아이콘 수정
                Picasso.with(getContext().getApplicationContext())
                        .load(R.drawable.main_star_fill)
                        .into(cartoonRatingImg);

                // 텍스트 수정
                cartoonRating.setText((content.getRating()) + "");
                cartoonRating.setTextColor(Color.parseColor("#FF4081"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab1_like_btn1: {    // 1번 - 나중에하기 or 나중에보기
                Toast.makeText(getContext(), "나중에할끄양", Toast.LENGTH_SHORT).show();
                firstCardview = false;
                cardView.animate().translationY(0)
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                cardView.setVisibility(View.GONE);
                            }
                        });
//                cardView.setVisibility(View.GONE);
                break;
            }
            case R.id.tab1_like_btn2: { // 1번 - 평가하기 or 취향분석보기
                MainActivity activity = (MainActivity) getActivity();
                // true:취향분석, false:평가하기
                if(checkBtn == false) {
                    activity.onFragmentChanged(0);
                } else {
                    activity.onFragmentChanged(6);
                }
                break;
            }
            case R.id.more_today_webtoon: { // 오늘의 웹툰 더보기
                // 여기서 액티비티 갑니당
                MainActivity activity = (MainActivity) getActivity();
                activity.onFragmentChanged(3);
                break;
            }
            case R.id.more_today_cartoon: { // 오늘의 만화 더보기
                MainActivity activity = (MainActivity) getActivity();
                activity.onFragmentChanged(4);
                break;
            }
            case R.id.webtoon_like_btn: {   // 오늘의 웹툰 보고싶어요
                postLike(webtoon, webtoonLike, webtoonLikeImg);
                break;
            }
            case R.id.webtoon_rating_btn: { // 오늘의 웹툰 평가하기
                postRating(webtoon, webtoonRatingImg, webtoonRating);
                break;
            }
            case R.id.webtoon_comment_btn: {    //오늘의 웹툰 코멘트
                postComment(webtoon);
                break;
            }
            case R.id.cartoon_like_btn: {   // 오늘의 만화 보고싶어요
                postLike(cartoon, cartoonLike, cartoonLikeImg);
                break;
            }
            case R.id.cartoon_rating_btn: { // 오늘의 만화 평가하기
                postRating(cartoon, cartoonRatingImg, cartoonRating);
                break;
            }
            case R.id.cartoon_comment_btn: {    // 오늘의 만화 코멘트
                postComment(cartoon);
                break;
            }
            case R.id.today_webtoon_img: {
                break;
            }
            case R.id.today_cartoon_img: {
                break;
            }
            default:
                break;
        }
    }

    // 보고싶어요 전송하는 메소드
    private void postLike(Content content, TextView textView, ImageView imageView){
        if(content == null) {
            Toast.makeText(getContext(), "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
            return;
        }

//        Toast.makeText(getContext().getApplicationContext(), "만화 : " + content.getNo() + "'s like " + content.getLike(), Toast.LENGTH_SHORT).show();

        // 서버로 데이터 전송
        new PostSingleData(getContext().getApplicationContext())
                .execute("like/", User.getInstance().getNo().toString(), content.getNo().toString());

        if(content.getLike()){  // 이미 보고싶어요가 눌렸던 상태
            User.getInstance().setLikes(User.getInstance().getLikes() - 1);
            textView.setTextColor(Color.parseColor("#777777"));
            content.setLike(false);
            Picasso.with(getContext().getApplicationContext())
                    .load(R.drawable.main_heart_empty)
                    .into(imageView);
        }else {
            User.getInstance().setLikes(User.getInstance().getLikes() + 1);
            textView.setTextColor(Color.parseColor("#F13839"));
            content.setLike(true);
            Picasso.with(getContext().getApplicationContext())
                    .load(R.drawable.main_heart_fill)
                    .into(imageView);
        }
    }

    private void postRating(final Content content, final ImageView imageView, final TextView textView){
        Log.d("ho's activity", "DetailActivity ratingBtn clicked");

        ratingDialog.show();

        if(content == null){
            Toast.makeText(getContext(), "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
            ratingDialog.cancel();
            return;
        }

        ((TextView)ratingDialog.findViewById(R.id.title)).setText(content.getTitle());

        RatingBar ratingBar = (RatingBar)ratingDialog.findViewById(R.id.ratingBar);
        if(content != null) {
            assert ratingBar != null;
            ratingBar.setRating(content.getRating());
        }

        assert ratingBar != null;
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser){

                    // 별점 준 갯수 증가
                    if(rating == 0){
                        if(content.getCartoon())
                            User.getInstance().setCartoonStars(User.getInstance().getCartoonStars() - 1);
                        else
                            User.getInstance().setWebtoonStars(User.getInstance().getWebtoonStars() - 1);
                    }else if(content.getRating() == 0){
                        if(content.getCartoon())
                            User.getInstance().setCartoonStars(User.getInstance().getCartoonStars() + 1);
                        else
                            User.getInstance().setWebtoonStars(User.getInstance().getWebtoonStars() + 1);
                    }

                    Integer Rating = (int)(rating * 10);

                    content.setRating(rating);

//                    Toast.makeText(getContext(), "작품 번호 : " + content.getNo().toString() + ", 별점 : " + Rating.toString(), Toast.LENGTH_SHORT).show();

                    new PostSingleData(getContext()).execute("insert/", User.getInstance().getNo().toString(), content.getNo().toString(), Rating.toString());

                    // 별점을 준 경우
                    if(content.getRating() > 0){
                        // 아이콘 수정
                        Picasso.with(getContext().getApplicationContext())
                                .load(R.drawable.main_star_fill)
                                .into(imageView);

                        // 텍스트 수정
                        textView.setText((content.getRating()) + "");
                        textView.setTextColor(Color.parseColor("#FF4081"));
                    }else{  // 0점을 준 경우
                        // 아이콘 수정
                        Picasso.with(getContext().getApplicationContext())
                                .load(R.drawable.main_star_empty)
                                .into(imageView);

                        // 텍스트 수정
                        textView.setText("평가하기");
                        textView.setTextColor(Color.parseColor("#777777"));
                    }

                    ratingDialog.cancel();
                }
            }
        });
    }

    private void postComment(Content content){
        if(content == null){
            Toast.makeText(getContext(), "잠시만 기다려주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        ((MainActivity)getActivity()).gotoComment(content);
    }

    private class GetMainList extends AsyncTask<String, Void, String> {

        AppCompatDialog loadingDialog;

        ArrayList<Content> contentArrayList;
        int idx = 0;

        GetMainList(ArrayList<Content> contentArrayList, int idx) {
            this.contentArrayList = contentArrayList;
            this.idx = idx;
        }

        public void setLoadingDialog(AppCompatDialog loadingDialog) {
            this.loadingDialog = loadingDialog;
        }

        @Override
        protected String doInBackground(String... params) {

            Log.d("uwangg's activity", "GetMainList.doInBackground");

            String data = "userId=" + params[1] + "&dayOfWeek=" + (cal.get(Calendar.WEEK_OF_MONTH));
            Log.d("uwangg's activity", "GetMainList data " + data);

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

            Log.d("ho's activity", "GetMainList " + s);

            if(loadingDialog != null)
                loadingDialog.cancel();

            if (s != null && s != "") {
                try {
                    // 통째로 받아들여서 하나씩 자르기 위한 json object
                    JSONObject reader = new JSONObject(s);

                    // 하나씩 잘라서 adapter에 저장해야 한다
                    JSONArray dataList = reader.getJSONArray("");

                    for (int i = 0; i < dataList.length(); i++) {
                        JSONObject obj = dataList.getJSONObject(i);

                        Content content = new Content();
                        if (!obj.isNull("id"))
                            content.setNo(obj.getInt("id"));
                        if (!obj.isNull("title"))
                            content.setTitle(obj.getString("title"));
                        if (!obj.isNull("author")) {
                            String aut = obj.getString("author");
                            aut = aut.substring(0, aut.length() - 1);
                            content.setAuthor(aut);
                        }
                        if (!obj.isNull("average")) {
                            content.setAverage((float) obj.getInt("average") / 1000);
                        }
                        if (!obj.isNull("genre"))
                            content.setGenre(obj.getString("genre").substring(0, obj.getString("genre").length() - 1));
                        if (!obj.isNull("adult"))
                            content.setAdult(obj.getBoolean("adult"));
                        if (!obj.isNull("thumbnail_small"))
                            content.setThumbnailSmall(obj.getString("thumbnail_small"));
                        if (!obj.isNull("thumbnail_big"))
                            content.setThumbnailBig(obj.getString("thumbnail_big"));
                        if (!obj.isNull("star"))
                            content.setRating((float) (obj.getInt("star") * 1.0) / 10);
                        if (!obj.isNull("like") && obj.getBoolean("like"))
                            content.setLike(obj.getBoolean("like"));
                        if (!obj.isNull("recommendStar"))
                            content.setPrediction(Float.parseFloat(String.format("%.1f", Float.parseFloat(obj.getString("recommendStar")) / 1000000)));
                        if (!obj.isNull("link"))
                            content.setLink(obj.getString("link"));
                        if (!obj.isNull("tags"))
                            content.setTags(obj.getString("tags").substring(0, obj.getString("tags").length() - 1));
                        if (!obj.isNull("dontsee"))
                            content.setHate(obj.getBoolean("dontsee"));
                        if (!obj.isNull("is_cartoon"))
                            content.setCartoon(obj.getBoolean("is_cartoon"));
                        if (!obj.isNull("cnt"))
                            content.setLikeCnt(obj.getInt("cnt"));

                        contentArrayList.add(content);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (contentArrayList.size() == 0) {
                return;
            }

            onResume();
        }
    }
}
