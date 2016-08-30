package kr.fugle.rating;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.Item.OnLoadMoreListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.rating.category.CategorySelectActivity;
import kr.fugle.webconnection.GetContentList;

/**
 * Created by hokyung on 16. 7. 27..
 */
public class RatingTabFragment1 extends Fragment {

    final int CATEGORY_REQUEST_CODE = 1004;
    final int CATEGORY_RESULT_CODE = 333;

    private CountChangeListener countChangeListener;

    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;
    private TextView categoryName;
    private RatingRecyclerAdapter adapter;
    private Integer userNo;
    private static Integer pageNo;
    private Integer categoryNo;

    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private Handler handler;

    // 서버 통신
    private GetContentList getContentList;
    private GetContentList getContentListWithCategory;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    public void setCountChangeListener(CountChangeListener countChangeListener){
        this.countChangeListener = countChangeListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        context = getContext().getApplicationContext();
        handler = new Handler();

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        userNo = User.getInstance().getNo();
        pageNo = 1;
        categoryNo = 0;     // 카테고리별로 받아오는 것 구현해야함. 기본이 0.

        View view = inflater.inflate(R.layout.tab_rating_fragment, container, false);

        categoryName = (TextView)view.findViewById(R.id.categoryName);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        LinearLayoutManager manager = new LinearLayoutManager(context);
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
                getContext(),
                dialog,
                contentArrayList,
                userNo,
                recyclerView);

        onLoadMoreListener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // add null , so the adapter will check view_type and show progress bar at bottom
                contentArrayList.add(null);
                adapter.notifyItemInserted(contentArrayList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        Toast.makeText(context, "rating bottom", Toast.LENGTH_SHORT).show();

                        new GetContentList(getContext(),
                                contentArrayList,
                                adapter,
                                1,
                                userNo)
                                .execute("webtoonEvaluate/", userNo + "", pageNo + "", ""); // 웹툰 표시 추가
                        pageNo++;
                    }
                }, 1500);
            }
        };

        adapter.setOnLoadMoreListener(onLoadMoreListener);

        adapter.setCountChangeListener(countChangeListener);

        recyclerView.setAdapter(adapter);

        // 로딩 시작
        loadingDialog.show();

        // 아이템 넣기
        getContentList = new GetContentList(getContext(),
                contentArrayList,
                adapter,
                1,
                userNo);
        getContentList.setLoadingDialog(loadingDialog);
        getContentList.execute("webtoonEvaluate/", userNo + "", pageNo + "", ""); // 웹툰 표시 추가

        pageNo++;

        // 위로가기 버튼 Floating Action Button
        view.findViewById(R.id.topBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext().getApplicationContext(), "위로가자!", Toast.LENGTH_SHORT).show();

                recyclerView.smoothScrollToPosition(0);
            }
        });

        // 카테고리 선택 버튼
        view.findViewById(R.id.categoryBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CategorySelectActivity.class);
                intent.putExtra("category", 0); // 만화책인경우
                startActivityForResult(intent, CATEGORY_REQUEST_CODE);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 카테고리 변경시
        if(requestCode == CATEGORY_REQUEST_CODE && resultCode == CATEGORY_RESULT_CODE){

            Log.d("------->", "카테고리 변경 " + data.getIntExtra("categoryNo", 0) + " " + data.getStringExtra("categoryName"));

            categoryNo = data.getIntExtra("categoryNo", 0);
            pageNo = 1;

            categoryName.setText(data.getStringExtra("categoryName"));

            String parameterName = "";

            if(categoryNo != 0){
                parameterName = categoryName.getText().toString();
            }

            // inner class 호출을 위한 상수화
            final String CATEGORYNAME = parameterName;

            onLoadMoreListener = new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    // add null , so the adapter will check view_type and show progress bar at bottom
                    contentArrayList.add(null);
                    adapter.notifyItemInserted(contentArrayList.size() - 1);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Toast.makeText(context, "rating bottom", Toast.LENGTH_SHORT).show();

                            new GetContentList(getContext(),
                                    contentArrayList,
                                    adapter,
                                    1,
                                    userNo)
                                    .execute("webtoonEvaluate/", userNo + "", pageNo + "", CATEGORYNAME); // 웹툰 표시 추가
                            pageNo++;
                        }
                    }, 1500);
                }
            };

            // 로딩 시작
            loadingDialog.show();

            contentArrayList.clear();
            adapter.notifyDataSetChanged();

            adapter.setOnLoadMoreListener(onLoadMoreListener);

            // 아이템 넣기
            getContentListWithCategory = new GetContentList(getContext(),
                    contentArrayList,
                    adapter,
                    1,
                    userNo);
            getContentListWithCategory.setLoadingDialog(loadingDialog);
            getContentListWithCategory.execute("webtoonEvaluate/", userNo + "", pageNo + "", CATEGORYNAME); // 웹툰 표시 추가

            pageNo++;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);

        if(getContentList != null)
            getContentList.cancel(true);

        if(getContentListWithCategory != null)
            getContentListWithCategory.cancel(true);
    }
}
