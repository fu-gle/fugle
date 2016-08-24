package kr.fugle.main.tab2.author;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.fugle.Item.Author;
import kr.fugle.R;

/**
 * Created by 김은진 on 2016-08-17.
 */
public class CountOfAuthorRankFragment extends Fragment {

    // 리스트뷰
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab2_rank_count, container, false);

        // 레이아웃 초기화 (RecyclerView) - start
        recyclerView = (RecyclerView) rootView.findViewById(R.id.count_rank_recyclerview);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // 레이아웃 초기화 (RecyclerView) - finish

        recyclerView.setAdapter(((AuthorRankActivity)getActivity()).adapter1);

        return rootView;
    }
}

