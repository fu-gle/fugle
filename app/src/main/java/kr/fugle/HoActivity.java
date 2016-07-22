package kr.fugle;

import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.fugle.Item.User;
import kr.fugle.mystar.MyStarActivity;
import kr.fugle.rating.RatingActivity;
import kr.fugle.recommend.RecommendActivity;

public class HoActivity extends AppCompatActivity{

    private User user;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ho);

        // 툴바 생성
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent data = getIntent();
        JSONObject jsonObject;

        try {
            Log.d("--->","user json " + data.getStringExtra("user"));
        Log.i("----->","user json " + data.getStringExtra("user"));
            jsonObject = new JSONObject(data.getStringExtra("user"));

            user = new User();

            user.setNo(jsonObject.getInt("id"));
            user.setMessage(jsonObject.getString("message"));
            user.setEmail(jsonObject.getString("email"));
            user.setPasswd(jsonObject.getString("password"));
            user.setName(jsonObject.getString("name"));
            user.setProfileImg(jsonObject.getString("profile"));
            user.setUserKey(jsonObject.getString("primary"));
            user.setGender(jsonObject.getString("gender"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        findViewById(R.id.ratingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HoActivity.this, RatingActivity.class);
                intent.putExtra("userNo", user.getNo());
                startActivity(intent);
            }
        });

        findViewById(R.id.recommendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HoActivity.this, RecommendActivity.class);
                intent.putExtra("userNo", user.getNo());
                startActivity(intent);
            }
        });

        findViewById(R.id.mystar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HoActivity.this, MyStarActivity.class);
                intent.putExtra("userNo", user.getNo());
                startActivity(intent);
            }
        });

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new MaterialDialog.Builder(HoActivity.this)
//                        .title("Basic Dialog")
//                        .content("contents")
//                        .positiveText("positive")
//                        .negativeText("negative")
//                        .show();
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
}
