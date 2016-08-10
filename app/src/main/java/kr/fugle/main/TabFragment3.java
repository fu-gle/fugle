package kr.fugle.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.recommend.RecommendAdapter;
import kr.fugle.webconnection.GetContentList;

/**
 * Created by 김은진 on 2016-07-26.
 * RecommendFragment
 * 추천 리스트 뷰
 */
public class TabFragment3 extends Fragment {

    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;
    private RecommendAdapter adapter;
    private Integer userNo;
    private static int pageNo;
    private TabStatusListener tabStatusListener;

    private Handler handler;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("ho's activity", "TabFragment3.onResume");

        // 초기해야하는지 확인 후 초기화
        if(tabStatusListener.getRefresh()){

            contentArrayList.clear();
            adapter.notifyDataSetChanged();

            pageNo = 1;

            new GetContentList(getContext(),
                    contentArrayList,
                    adapter,
                    0,
                    userNo)
                    .execute("recommend/", userNo.toString(), pageNo + "");

            pageNo++;

            tabStatusListener.setRefresh(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentArrayList = tabStatusListener.getContentList();
        pageNo = tabStatusListener.getPageNo();
        userNo = User.getInstance().getNo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_fragment3, container,false);

        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true)
                .setView(R.layout.dialog_rating);

        AppCompatDialog dialog = builder.create();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 1200;
        dialog.getWindow().setAttributes(params);

        adapter = new RecommendAdapter(
                getContext(),
//                dialog,
                contentArrayList,
                userNo,
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
                        Toast.makeText(getContext().getApplicationContext(), "rating bottom", Toast.LENGTH_SHORT).show();

                        new GetContentList(getContext(),
                                contentArrayList,
                                adapter,
                                0,
                                userNo)
                                .execute("recommend/", userNo.toString(), pageNo + "");
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
        });

        recyclerView.setAdapter(adapter);

        // 위로가기 버튼 Floating Action Button
        v.findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "위로가자!", Toast.LENGTH_SHORT).show();
                recyclerView.scrollToPosition(3);
                recyclerView.smoothScrollToPosition(0);
            }
        });

        return v;
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
