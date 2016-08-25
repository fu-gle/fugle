package kr.fugle.main.tab2.cartoon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.commonlist.CommonRecyclerAdapter;
import kr.fugle.webconnection.GetContentList;

/**
 * Created by 김은진 on 2016-08-16.
 */
public class CountOfCartoonRankFragment extends Fragment {

    // 리스트뷰
    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;
    private CommonRecyclerAdapter adapter;

    // 서버 통신
    private GetContentList getContentList;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_rank_count, container, false);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        contentArrayList = new ArrayList<>();

        // 레이아웃 초기화 (RecyclerView) - start
        recyclerView = (RecyclerView) rootView.findViewById(R.id.count_rank_recyclerview);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // 레이아웃 초기화 (RecyclerView) - finish

        adapter = new CommonRecyclerAdapter(
                getContext(),
                contentArrayList,
                User.getInstance().getNo(),
                recyclerView);

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

        // 랭킹 가져오기
        performSearch();

        recyclerView.setAdapter(adapter);


        return rootView;
    }

    // 작가명, 작품명 입력받았을때 서버로 보냄
    // 파라미터에 맞는 리스트 받아옴
    public void performSearch() {

        // 로딩 시작
        loadingDialog.show();

        contentArrayList.clear();
        getContentList = new GetContentList(getContext(),
                contentArrayList,
                adapter,
                4,
                User.getInstance().getNo());
        getContentList.setLoadingDialog(loadingDialog);
        getContentList.execute("countOfCartoonRank/", User.getInstance().getNo() + "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(getContentList != null)
            getContentList.cancel(true);
    }
}
