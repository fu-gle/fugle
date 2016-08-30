package kr.fugle.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.rating.RatingActivity;
import kr.fugle.recommend.RecommendAdapter;
import kr.fugle.webconnection.GetContentList;

/**
 * Created by 김은진 on 2016-07-26.
 * RecommendFragment
 * 추천 리스트 뷰
 */
public class TabFragment3 extends Fragment {

    private ArrayList<Content> contentArrayList;
    private ArrayList<String> tagList;

    private RecyclerView recyclerView;
    private CardView cardView;
    private RelativeLayout relativeLayout;
    private TextView zeroText;
    private Button goStar;

    private RecommendAdapter adapter;
    private User user;
    private static int pageNo;
    private TabStatusListener tabStatusListener;

    private Handler handler;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentArrayList = tabStatusListener.getContentList();
        if(tagList == null)
            tagList = new ArrayList<>();
        pageNo = tabStatusListener.getPageNo();
        user = User.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment3, container,false);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        cardView = (CardView)v.findViewById(R.id.zero_cardview);

        zeroText = (TextView)v.findViewById(R.id.zero_count_text);

        goStar = (Button)v.findViewById(R.id.go_star);

        relativeLayout = (RelativeLayout)v.findViewById(R.id.relativeLayout);

        if((user.getWebtoonStars() + user.getCartoonStars()) < 15){
            cardView.setVisibility(View.VISIBLE);

            zeroText.setText(user.getName() + "님 아직 평가가 부족해서 \n추천을 할 수 없어요ㅠㅠ"
                    + "\n15개 이상 평가를 하셔야 추천을 받을 수 있어요!");

            relativeLayout.setVisibility(View.GONE);

            goStar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), RatingActivity.class));
                }
            });
        }

        adapter = new RecommendAdapter(
                getContext(),
                contentArrayList,
                tagList,
                user.getNo(),
                recyclerView);

        handler = new Handler();

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // add null , so the adapter will check view_type and show progress bar at bottom
                contentArrayList.add(null);
                adapter.notifyItemInserted(contentArrayList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(getContext().getApplicationContext(), "rating bottom", Toast.LENGTH_SHORT).show();

                        GetContentList getContentList = new GetContentList(getContext(),
                                    contentArrayList,
                                    adapter,
                                    0,
                                    user.getNo());

                        if(tagList.size() == 0)
                            getContentList.setTagList(tagList);

                        getContentList.execute("recommend/", user.getNo().toString(), pageNo + "");

                        pageNo++;
                    }
                }, 1500);
            }
        });

        adapter.setActivityStartListener(new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {
                startActivity(intent);
            }

            @Override
            public void activityStart() {

            }

            @Override
            public void activityFinish() {

            }
        });

        recyclerView.setAdapter(adapter);

        // 위로가기 버튼 Floating Action Button
        v.findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext().getApplicationContext(), "위로가자!", Toast.LENGTH_SHORT).show();
                recyclerView.scrollToPosition(3);
                recyclerView.smoothScrollToPosition(0);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("ho's activity", "TabFragment3.onResume");

        // 초기해야하는지 확인 후 초기화
        int totalCount = user.getWebtoonStars() + user.getCartoonStars();
        if(tabStatusListener.getRefresh()  // 새로 평가하기 액티비티에서 입력한 경우
                || (totalCount >= 15 && contentArrayList.size() == 0)){ // 평가하기 이외의 액티비티에서 입력하였는데, 마침 15개가 되었을

            Log.d("ho's activity", "Recommend List Refresh");

            contentArrayList.clear();
            adapter.notifyDataSetChanged();

            pageNo = 1;

            // 로딩 시작
            loadingDialog.show();

            GetContentList getContentList = new GetContentList(getContext(),
                    contentArrayList,
                    adapter,
                    0,
                    user.getNo());

            if(tagList.size() == 0)
                getContentList.setTagList(tagList);

            getContentList.setLoadingDialog(loadingDialog);

            getContentList.execute("recommend/", user.getNo().toString(), pageNo + "");

            pageNo++;

            tabStatusListener.setRefresh(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Handler 종료
        handler.removeMessages(0);

        // 여기서 list를 메인으로 넘겨서 저장한다.
        tabStatusListener.setContentList(contentArrayList);
        tabStatusListener.setPageNo(pageNo);
    }
}
