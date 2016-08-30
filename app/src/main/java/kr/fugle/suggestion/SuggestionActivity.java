package kr.fugle.suggestion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.webconnection.PostSingleData;

public class SuggestionActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText suggestionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // 별점 주는 부분을 없앤다
        findViewById(R.id.comment_relative).setVisibility(View.GONE);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("건의사항");

        // 건의사항 입력부분
        suggestionInput = (EditText) findViewById(R.id.comment_input);
        suggestionInput.setHint("건의사항을 자유롭게 작성해주세요");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.doneBtn:  // 완료버튼
                done();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void done(){
        String suggestion = suggestionInput.getText().toString();

        if("".equals(suggestion)){
            Toast.makeText(SuggestionActivity.this, "내용을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        new PostSingleData(SuggestionActivity.this)
                .execute("suggestion/",
                        User.getInstance().getNo() + "",
                        suggestion);

//        Toast.makeText(SuggestionActivity.this, "입력되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }
}
