package kr.fugle.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.Content;
import kr.fugle.Item.SearchData;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.commonlist.CommonRecyclerAdapter;
import kr.fugle.detail.DetailActivity;
import kr.fugle.webconnection.GetContentList;
/**
 * Created by 김은진 on 2016-08-03.
 */
public class SearchActivity extends AppCompatActivity {

    // 작가명, 작품명만 들어있는 리스트
    private ArrayList<String> searchItem = SearchData.getInstance().getList();

    // 작가명, 작품명에 맞는 작품 정보 리스트
    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;

//    AutoCompleteTextView edit;
    ClearableAutoCompleteTextView edit;
    private CommonRecyclerAdapter adapter;
    private Integer userNo;

    // 서버 통신
    private GetContentList getContentList;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(SearchActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        userNo = User.getInstance().getNo();

        Toolbar toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        toolbar.setTitle("검색하기");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        edit = (AutoCompleteTextView) findViewById(R.id.edit);
        edit = (ClearableAutoCompleteTextView) findViewById(R.id.edit);

        // 레이아웃 초기화 (RecyclerView) - start
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // 레이아웃 초기화 (RecyclerView) - finish

        edit.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, searchItem));

        contentArrayList = new ArrayList<>();

        adapter = new CommonRecyclerAdapter(
                getApplicationContext(),
                contentArrayList,
                userNo,
                recyclerView);

        ActivityStartListener activityStartListener = new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {
                startActivity(intent);
            }

            @Override
            public void activityStart() {
                startActivity(new Intent(SearchActivity.this, DetailActivity.class));
            }

            @Override
            public void activityFinish() {

            }
        };

        adapter.setActivityStartListener(activityStartListener);

        recyclerView.setAdapter(adapter);

        // 자동 완성 된 것중 선택했을 때
        edit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                performSearch();
//                Toast.makeText(SearchActivity.this, "name:"+edit.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // 키보드로 actionSearch를 이용할때
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    // action_search버튼 클릭시 자동완성 기능 사라지게 함
                    edit.dismissDropDown();
                    return true;
                }
                    return false;
            }
        });
    }

    // 작가명, 작품명 입력받았을때 서버로 보냄
    // 파라미터에 맞는 리스트 받아옴
    public void performSearch() {

        // 로딩 시작
        loadingDialog.show();

        contentArrayList.clear();

        getContentList = new GetContentList(getApplicationContext(),
                contentArrayList,
                adapter,
                3,
                userNo);
        getContentList.setLoadingDialog(loadingDialog);

        getContentList.execute("search/", edit.getText().toString());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(getContentList != null)
            getContentList.cancel(true);
    }
}
