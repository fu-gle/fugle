package kr.fugle.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.util.ArrayList;

import kr.fugle.Item.Content;
import kr.fugle.Item.SearchData;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.commonlist.CommonRecyclerAdapter;
import kr.fugle.webconnection.GetContentList;

public class SearchActivity extends AppCompatActivity {

    // 작가명, 작품명만 들어있는 리스트
    private ArrayList<String> searchItem = SearchData.getInstance().getList();

    // 작가명, 작품명에 맞는 작품 정보 리스트
    private ArrayList<Content> contentArrayList;
    private RecyclerView recyclerView;

    private CommonRecyclerAdapter adapter;
    private Integer userNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        userNo = User.getInstance().getNo();

        Toolbar toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        toolbar.setTitle("검색하기");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final AutoCompleteTextView edit = (AutoCompleteTextView) findViewById(R.id.edit);

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

        // 자동 완성 된 것중 선택했을 때
        edit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                new GetContentList(getApplicationContext(),
                        contentArrayList,
                        adapter,
                        3,
                        userNo)
                        .execute("search/", edit.getText().toString());

                recyclerView.setAdapter(adapter);
                
                Toast.makeText(SearchActivity.this, "name:"+edit.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search, menu);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getApplicationContext(), SearchActivity.class)));
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//        MenuItemCompat.expandActionView(menu.findItem(R.id.action_search));
//        searchView.setIconifiedByDefault(true);
//        searchView.setIconified(false);
//
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//
//        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                // TODO do your stuff when back button is pressed
////                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
////                startActivity(intent);
//                finish();
//                return true;
//            }
//        });
//
//        return super.onCreateOptionsMenu(menu);
//    }
}
