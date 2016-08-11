package kr.fugle.comment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.webconnection.PostSingleData;

public class CommentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RatingBar ratingBar;
    private TextView doneBtn;
    private EditText commentInput;

    private int contentNo;
    private String title;
    private Float star;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        // 이전 액티비티에서 작품 번호, 작품 이름, 내 별점 받아옴
        Intent data = getIntent();
        contentNo = data.getIntExtra("contentNo", 0);
        title = data.getStringExtra("title");
        star = data.getFloatExtra("star", 0.0f);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        doneBtn = (TextView) findViewById(R.id.doneBtn);
        commentInput = (EditText) findViewById(R.id.comment_input);

        ratingBar.setRating(star);
        ratingBar.setOnRatingBarChangeListener(onRatingBarChangeListener);

        doneBtn.setOnClickListener(onClickListener);

        commentInput.setHint(title + "에 대한 생각을 자유롭게 표현해주세요.");
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

    private RatingBar.OnRatingBarChangeListener onRatingBarChangeListener =
            new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if(!fromUser)
                        return;

                    // 별점 준 갯수 증가
                    if(rating == 0){
                        User.getInstance().setStars(User.getInstance().getStars() - 1);
                    }else if(star == 0){
                        User.getInstance().setStars(User.getInstance().getStars() + 1);
                    }

                    Integer Rating = (int)(rating * 10);

                    star = rating;

                    Toast.makeText(getApplicationContext(), "작품 번호 : " + contentNo + ", 별점 : " + Rating.toString(), Toast.LENGTH_SHORT).show();

                    new PostSingleData(CommentActivity.this)
                            .execute("insert/",
                                    User.getInstance().getNo() + "",
                                    contentNo + "",
                                    Rating.toString());
                }
            };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String comment = commentInput.getText().toString();

            if(star == 0){
                Toast.makeText(CommentActivity.this, "별점을 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            if(comment.equals("")){
                Toast.makeText(CommentActivity.this, "코멘트를 입력해 주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            new PostSingleData(CommentActivity.this)
                    .execute("comment/",
                            User.getInstance().getNo() + "",
                            contentNo + "",
                            comment);
        }
    };
}
