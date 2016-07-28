package kr.fugle.rating;

import android.content.Context;
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

import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.webconnection.GetContentList;

/**
 * Created by hokyung on 16. 7. 27..
 */
public class RatingTabFragment1 extends Fragment {

    private CountChangeListener countChangeListener;

    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;
    private RatingRecyclerAdapter adapter;
    private Integer userNo;
    static Integer pageNo;

    private Context context;
    Handler handler;

    public void setCountChangeListener(CountChangeListener countChangeListener){
        this.countChangeListener = countChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext().getApplicationContext();
        handler = new Handler();

        userNo = User.getInstance().getNo();
        pageNo = 1;

        View view = inflater.inflate(R.layout.tab_rating_fragment, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        contentArrayList = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true)
                .setView(R.layout.dialog_rating_option);

        AppCompatDialog dialog = builder.create();

        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = 1000;
        dialog.getWindow().setAttributes(params);

        adapter = new RatingRecyclerAdapter(
                getContext().getApplicationContext(),
                getContext(),
                dialog,
                contentArrayList,
                userNo,
                recyclerView);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // add null , so the adapter will check view_type and show progress bar at bottom
                contentArrayList.add(null);
                adapter.notifyItemInserted(contentArrayList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "rating bottom", Toast.LENGTH_SHORT).show();

                        new GetContentList(
                                contentArrayList,
                                adapter,
                                1,
                                userNo)
                                .execute("", userNo + "", pageNo + ""); // 웹툰 표시 추가
                        pageNo++;
                    }
                }, 1500);
            }
        });

        recyclerView.setAdapter(adapter);

        // 아이템 넣기
        new GetContentList(
                contentArrayList,
                adapter,
                1,
                userNo)
                .execute("", userNo + "", pageNo + ""); // 웹툰 표시 추가

        pageNo++;

        // 위로가기 버튼 Floating Action Button
        view.findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "위로가자!", Toast.LENGTH_SHORT).show();

                recyclerView.smoothScrollToPosition(0);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
    }
}
