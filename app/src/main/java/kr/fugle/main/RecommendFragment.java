package kr.fugle.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.R;
import kr.fugle.recommend.RecommendAdapter;

/**
 * Created by 김은진 on 2016-07-26.
 */
public class RecommendFragment extends Fragment {

    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;
    private RecommendAdapter adapter;
    private Integer userNo;
    private static int pageNo;
    private TabStatusListener tabStatusListener;

    public void setTabStatusListener(TabStatusListener tabStatusListener){
        this.tabStatusListener = tabStatusListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contentArrayList = tabStatusListener.getContentList();
        pageNo = tabStatusListener.getPageNo();
    }

    public void setArguments(Bundle args) {
        super.setArguments(args);
        // userNo 받아와야함
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_recommend, container,false);

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 여기서 list를 메인으로 넘겨서 저장한다.
        tabStatusListener.setContentList(contentArrayList);
        tabStatusListener.setPageNo(pageNo);
    }
}
