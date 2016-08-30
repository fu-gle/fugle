package kr.fugle.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.kakao.auth.Session;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.prefs.PreferencesManager;
import com.wooplr.spotlight.utils.SpotlightListener;

import java.util.ArrayList;

import kr.fugle.Intro.MenualActivity;
import kr.fugle.Intro.TutorialActivity;
import kr.fugle.Item.Content;
import kr.fugle.Item.SearchData;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.comment.CommentActivity;
import kr.fugle.main.tab1.MoreWebtoonActivity;
import kr.fugle.main.tab4.LikeHateActivity;
import kr.fugle.mystar.MyStarActivity;
import kr.fugle.preference.PreferenceAnalysisActivity;
import kr.fugle.rating.RatingActivity;
import kr.fugle.search.SearchActivity;
import kr.fugle.splash.SplashActivity;
import kr.fugle.suggestion.SuggestionActivity;
import kr.fugle.webconnection.GetContentList;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class MainActivity extends AppCompatActivity implements SpotlightListener {

    final int CHECK_FINISH = 1234;

    // tutorial
    private static int order = 0;
    private boolean isRevealEnabled = false;
    PreferencesManager mPreferencesManager;
    private View v;
    private int right;
    private int bottom;
    private int top;
    private int left;

    // 갤러리에서 사진가져오기
    final int REQ_PROFILE_PICK_CODE = 100;
    final int REQ_BACKGROUND_PICK_CODE = 101;

    final int RATING_REQUEST_CODE = 501;
    final int RATING_RESULT_CODE = 505;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public int[] tabIcons = {
            R.drawable.ic_home_white,
            R.drawable.ic_trending_up_white,
            R.drawable.ic_favorite_white,
            R.drawable.ic_person_white
    };
    TabFragment1 tabFragment1;
    TabFragment2 tabFragment2;
    TabFragment3 tabFragment3;
    TabFragment4 tabFragment4;

    ArrayList<Content> contentArrayList;
    ArrayList<Content> mainList1, mainList2;
    int pageNo;
    TabStatusListener tabStatusListener;
    boolean refresh;
    User user = User.getInstance();

    // 작가명, 작품명만 들어있는 리스트
    private ArrayList<String> searchItem = SearchData.getInstance().getList();

    private GetContentList getContentList;

    private Handler handler;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        // tutorial
//        mPreferencesManager = new PreferencesManager(MainActivity.this);
//        startActivityForResult(new Intent(MainActivity.this, TutorialActivity.class), CHECK_FINISH);
//        mPreferencesManager.resetAll();
        v = findViewById(R.id.view);

        if(searchItem.isEmpty()) {
            getContentList = new GetContentList(getApplicationContext());
            getContentList.execute("searchName/");
        }

        // 추천 뷰 탭을 초기화 해야하는지 판별하는 값
        refresh = true;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        // 탭 클릭시 indicator color
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));
        // 탭 클릭시 indicator height
        //tabLayout.setSelectedTabIndicatorHeight((int) (2 * getResources().getDisplayMetrics().density));
        tabLayout.setSelectedTabIndicatorHeight(10);
        setupTabIcons();

        TypedValue tv = new TypedValue();
        getApplicationContext().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);

        // 추천 뷰용으로 arraylist 생성
        contentArrayList = new ArrayList<>();
        pageNo = 1;

        // tab1에서 사용될 arraylist 생성
        mainList1 = new ArrayList<>();
        mainList2 = new ArrayList<>();

        Log.d("logCount--->", User.getInstance().getLogCount()+"");
        if(User.getInstance().getLogCount() == 0) {
            startActivity(new Intent(MainActivity.this, MenualActivity.class));
        }

    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        tabStatusListener = new TabStatusListener() {
            @Override
            public void setContentList(ArrayList<Content> list) {
                contentArrayList = list;
            }

            @Override
            public ArrayList<Content> getContentList() {
                return contentArrayList;
            }

            @Override
            public void setPageNo(int pageNum) {
                pageNo = pageNum;
            }

            @Override
            public int getPageNo() {
                return pageNo;
            }

            @Override
            public boolean getRefresh() {
                return refresh;
            }

            @Override
            public void setRefresh(boolean re) {
                refresh = re;
            }
        };

        tabFragment1 = new TabFragment1();
        tabFragment1.setTabStatusListener(tabStatusListener);

        tabFragment2 = new TabFragment2();
        tabFragment2.setTabStatusListener(tabStatusListener);

        tabFragment3 = new TabFragment3();
        tabFragment3.setTabStatusListener(tabStatusListener);

        tabFragment4 = new TabFragment4();
        tabFragment4.setTabStatusListener(tabStatusListener);

        // 홈, 순위, 추천, 마이페이지
        adapter.addFragment(tabFragment1, "");
        adapter.addFragment(tabFragment2, "");
        adapter.addFragment(tabFragment3, "");
        adapter.addFragment(tabFragment4, "");
        viewPager.setAdapter(adapter);
    }

    public void onFragmentChanged(int index) {
        if (index == 0) {
            Intent intent = new Intent(MainActivity.this, RatingActivity.class);
            startActivityForResult(intent, RATING_REQUEST_CODE);
        } else if(index == 1) { // 로그아웃 버튼 눌렀을시
            // 프로그래스바 종료
            if(loadingDialog != null)
                loadingDialog.cancel();

            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
            System.gc();
        } else if (index == 2) {    // 내 웹툰 별점 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, MyStarActivity.class);
            intent.putExtra("category", "webtoon");
            startActivity(intent);
        } else if (index == 3) {    // 오늘의 웹툰 더보기 눌렀을시
            Intent intent = new Intent(MainActivity.this, MoreWebtoonActivity.class);
            intent.putExtra("contentArrayList", tabFragment1.contentArrayList1);
            startActivity(intent);
        } else if (index == 4) {    // 오늘의 만화 더보기 눌렀을시
            Intent intent = new Intent(MainActivity.this, MoreWebtoonActivity.class);
            intent.putExtra("contentArrayList", tabFragment1.contentArrayList2);
            startActivity(intent);
        } else if (index == 5) {    // 내 만화 별점 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, MyStarActivity.class);
            intent.putExtra("category", "cartoon");
            startActivity(intent);
        } else if (index == 6) {    // 취향 분석
            Intent intent = new Intent(MainActivity.this, PreferenceAnalysisActivity.class);
            startActivity(intent);
        } else if (index == 7) {    // 보고싶어요 목록 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, LikeHateActivity.class);
            intent.putExtra("category", 0);
            startActivity(intent);
        } else if (index == 8) {    // 보기싫어요 목록 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, LikeHateActivity.class);
            intent.putExtra("category", 1);
            startActivity(intent);
        } else if (index == 9) {    // 코멘트 목록 버튼 눌렀을시
            Intent intent = new Intent(MainActivity.this, LikeHateActivity.class);
            intent.putExtra("category", 2);
            startActivity(intent);
        }
    }

    public void gotoComment(Content content){
        Intent intent = new Intent(MainActivity.this, CommentActivity.class);

        intent.putExtra("contentNo", content.getNo());
        intent.putExtra("title", content.getTitle());
        intent.putExtra("star", content.getRating());
        intent.putExtra("isCartoon", content.getCartoon());

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_search: {  // 검색 버튼 클릭시
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            }
            case R.id.action_suggestion: {    // 건의사항 버튼 클릭시
                startActivity(new Intent(MainActivity.this, SuggestionActivity.class));
                break;
            }
            case R.id.action_logout: {    // 로그아웃 버튼 클릭시

                // 로딩 시작
                loadingDialog.show();

                // 프로필 사진과 배경 사진 날리기
                Picasso.with(getApplicationContext())
                        .invalidate(user.getProfileImg());
                Picasso.with(getApplicationContext())
                        .invalidate(user.getProfileBackground());

                // User 객체 초기화
                User.destroy();

                // 이메일
                SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                // 페이스북
                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut();
                    onFragmentChanged(1);
                    Log.d("---->", "페북로그아웃");
                }

                // 카카오톡
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Session.getCurrentSession().close();
                        onFragmentChanged(1);
                        Log.d("---->", "카톡로그아웃");
                    }
                });
                Log.d("---->", "if밖로그아웃");

                break;
            }
            default: {

            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 평점을 매겼으면 추천 목록 새로고침
        if(requestCode == RATING_REQUEST_CODE && resultCode == RATING_RESULT_CODE) {
            refresh = true;
            return;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(getContentList != null)
            getContentList.cancel(true);

        if(handler != null)
            handler.removeMessages(0);
    }

    public void showIntro(View view, String usageId, String title, String text) {
        new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevalAnimation(isRevealEnabled)
                .performClick(true)
                .fadeinTextDuration(400)
                //.setTypeface(FontUtil.get(this, "RemachineScript_Personal_Use"))
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText(title)
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText(text)
                .maskColor(Color.parseColor("#dc000000"))
                .target(view)
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId(usageId) //UNIQUE ID
                .setListener(this)
                .show();
    }

    @Override
    public void onUserClicked(String s) {

        Log.d("onUserClickd--->", s);
        switch (s) {

//            case "0": { // 랭킹부분
//                right = tabLayout.getWidth()/4*2;
//                bottom = tabLayout.getBottom();
//                top = tabLayout.getTop();
//                left = tabLayout.getLeft()+tabLayout.getWidth()/4;
//                v.setRight(right);
//                v.setBottom(bottom);
//                v.setTop(top);
//                v.setLeft(left);
//                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showIntro(v, "1", "웹툰, 만화책, 작가 랭킹",
//                                "웹툰, 만화책, 작가의 평균별점순 평가갯수순 랭킹을 확인해볼수있어요!");
//                    }
//                }, 100);
//                break;
//            }
//            case "1": { // 취향 분석
//                right = tabLayout.getWidth()/4*3;
//                bottom = tabLayout.getBottom();
//                top = tabLayout.getTop();
//                left = tabLayout.getLeft()+tabLayout.getWidth()/2;
//                v.setRight(right);
//                v.setBottom(bottom);
//                v.setTop(top);
//                v.setLeft(left);
//                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showIntro(v, "2", user.getName() + "님에 맞는 웹툰, 만화책 추천",
//                                "평가하기를 완료하셨다면"
//                                        + user.getName() +
//                                        "님에 맞는 웹툰과 만화책 취향을 볼수있어요!");
//                    }
//                }, 100);
//                break;
//            }
//            case "2": {
//                right = tabLayout.getWidth();
//                bottom = tabLayout.getBottom();
//                top = tabLayout.getTop();
//                left = tabLayout.getLeft()+tabLayout.getWidth()/4*3;
//                v.setRight(right);
//                v.setBottom(bottom);
//                v.setTop(top);
//                v.setLeft(left);
//                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showIntro(v, "3", "마이페이지",
//                                "지금까지" + user.getName()
//                                        + "님이 툰데레에서 활동하신 내역을 볼 수있어요 ♡");
//                    }
//                }, 100);
//                break;
//            }
//            case "3": {
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showIntro(findViewById(R.id.action_search), "4", "검색기능",
//                                "원하시는 작가, 작품명을 검색할수 있어요");
//                    }
//                }, 100);
//                break;
//            }
//            case "4": {
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showIntro(findViewById(R.id.tab1_like_btn2), "5", "평가하기",
//                                "만약에 평가를 하지 않으셨다면 평가를 하실 수 있어요!");
//                    }
//                }, 100);
//                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
//                break;
//            }
//            case "5": {
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showIntro(findViewById(R.id.more_today_webtoon), "6", "더 보기 기능",
//                                "더 많은 웹툰과 만화책들을 볼 수 있어요.");
//                    }
//                }, 100);
//                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
//                break;
//            }
//            case "6": {
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showIntro(findViewById(R.id.today_webtoon_img), "7", "세부사항 보기",
//                                "이 작품에 세부사항을 보고 싶으시면 작품 이미지를 클릭해주세요!");
//                    }
//                }, 100);
//                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
//                break;
//            }
//            case "7": {
//                startActivity(new Intent(MainActivity.this, TutorialActivity.class));
//                break;
//            }

            //            case "0": { // 랭킹부분
//                right = tabLayout.getWidth()/4*2;
//                bottom = tabLayout.getBottom();
//                top = tabLayout.getTop();
//                left = tabLayout.getLeft()+tabLayout.getWidth()/4;
//                v.setRight(right);
//                v.setBottom(bottom);
//                v.setTop(top);
//                v.setLeft(left);
//                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
//                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        showIntro(v, "1", "웹툰, 만화책, 작가 랭킹",
//                                "웹툰, 만화책, 작가의 평균별점순 평가갯수순 랭킹을 확인해볼수있어요!");
//                    }
//                }, 100);
//                break;
//            }
            case "0": { // 취향 분석
                right = tabLayout.getWidth()/4*3;
                bottom = tabLayout.getBottom();
                top = tabLayout.getTop();
                left = tabLayout.getLeft()+tabLayout.getWidth()/2;
                v.setRight(right);
                v.setBottom(bottom);
                v.setTop(top);
                v.setLeft(left);
                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showIntro(v, "1", user.getName() + "님에 맞는 웹툰, 만화책 추천",
                                "평가하기를 완료하셨다면"
                                        + user.getName() +
                                        "님에 맞는 웹툰과 만화책 취향을 볼수있어요!");
                    }
                }, 100);
                break;
            }
            case "1": {
                right = tabLayout.getWidth();
                bottom = tabLayout.getBottom();
                top = tabLayout.getTop();
                left = tabLayout.getLeft()+tabLayout.getWidth()/4*3;
                v.setRight(right);
                v.setBottom(bottom);
                v.setTop(top);
                v.setLeft(left);
                Log.d("position---->","case"+s+":"+right+" "+bottom+" "+top+" "+left);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showIntro(v, "2", "마이페이지",
                                "지금까지" + user.getName()
                                        + "님이 툰데레에서 활동하신 내역을 볼 수있어요 ♡");
                    }
                }, 100);
                break;
            }
            case "2": {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showIntro(findViewById(R.id.action_search), "3", "검색기능",
                                "원하시는 작가, 작품명을 검색할수 있어요");
                    }
                }, 100);
                break;
            }
            case "3": {
                startActivity(new Intent(MainActivity.this, TutorialActivity.class));
                break;
            }
         }
    }
}
