package kr.fugle.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;

import kr.fugle.R;

/**
 * Created by 김은진 on 2016-06-17.
 */
public class SuccessActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView idView;
    private TextView nameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        String imagePath = getIntent().getStringExtra("image");
        imageView = (ImageView) findViewById(R.id.user_profile_photo);

        String id = "ID = " + getIntent().getStringExtra("id") + "";
        String name = getIntent().getStringExtra("name") + " 님 \n환영합니다.";

        idView = (TextView) findViewById(R.id.login_id);
        nameView = (TextView) findViewById(R.id.login_name);

        idView.setText(id);
        nameView.setText(name);

        CircleTransform circleTransform = new CircleTransform();

        Picasso.with(getApplicationContext())
                .load(imagePath)
                .transform(circleTransform)
                .placeholder(R.drawable.profile)
                .into(imageView);

    }

    public void onLogoutButtonClicked(View v) {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Intent intent = new Intent(SuccessActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            });
    }
}