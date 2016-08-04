package kr.fugle.ranking;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kr.fugle.Item.Author;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.R;

/**
 * Created by hokyung on 16. 8. 4..
 */
public class RankingTabFragment extends Fragment {

    private ArrayList<Author> authorArrayList;
    private RecyclerView recyclerView;
    private RankingRecyclerAdapter adapter;
    private static Integer pageNo;

    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext().getApplicationContext();
        handler = new Handler();

        pageNo = 1;

        View view = inflater.inflate(R.layout.tab_ranking_fragment, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        authorArrayList = new ArrayList<>();

        // 다이얼로그 둘 것인가?



        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
