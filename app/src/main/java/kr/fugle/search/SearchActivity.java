package kr.fugle.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import kr.fugle.R;

public class SearchActivity extends AppCompatActivity {

    String[] items = { "SM3", "SM5", "SM7", "SONATA", "AVANTE", "SOUL", "K5",
            "K7" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        toolbar.setTitle("검색하기");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AutoCompleteTextView edit = (AutoCompleteTextView) findViewById(R.id.edit);

        edit.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, items));
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
