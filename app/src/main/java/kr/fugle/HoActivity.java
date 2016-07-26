package kr.fugle;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Rating;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(true)
//                .setMessage("메세지")
                .setView(getLayoutInflater().inflate(R.layout.dialog_rating_option, null));
//                .setPositiveButton("네", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
        final AppCompatDialog alert = builder.create();

        WindowManager.LayoutParams params = alert.getWindow().getAttributes();
        params.width = 1200;
        alert.getWindow().setAttributes(params);
//        alert.findViewById(R.id.comment).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(HoActivity.this, " sdfsdf", Toast.LENGTH_SHORT).show();
//            }
//        });

//        alert.setTitle("제목");

        findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.show();

                alert.findViewById(R.id.detail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(HoActivity.this, "asdf", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void createDialog(){
//        ratingBar = (RatingBar)dialog.findViewById(R.id.ratingBar);
    }
}
