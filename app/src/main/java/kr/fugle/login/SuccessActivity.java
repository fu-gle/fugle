package kr.fugle.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import kr.fugle.HoActivity;
import kr.fugle.R;

/**
 * Created by 김은진 on 2016-06-17.
 */
public class SuccessActivity extends AppCompatActivity {

    // 페이스북
    JSONObject response, profile_pic_data, profile_pic_url;

    boolean isLogOut;

    private ImageView user_picture;
    private TextView user_id, user_name, user_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        isLogOut = false;
        user_picture = (ImageView) findViewById(R.id.user_profile_photo);
        user_id = (TextView) findViewById(R.id.login_id);
        user_name = (TextView) findViewById(R.id.login_name);
        user_email = (TextView)findViewById(R.id.login_email);

        // 페이스북
        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsondata");
        setUserProfile(jsondata);


        findViewById(R.id.tempBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SuccessActivity.this, HoActivity.class));
                finish();
            }
        });
    }

    public void onLogoutButtonClicked(View v) {
        isLogOut = true;

        if(LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
            }
        });
        Intent intent = new Intent(SuccessActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void setUserProfile(String jsondata){

        try {
            response = new JSONObject(jsondata);
            user_email.setText(response.get("email").toString());
            user_id.setText(response.get("id").toString());
            user_name.setText(response.get("name").toString());
            profile_pic_data = new JSONObject(response.get("picture").toString());
            profile_pic_url = new JSONObject(profile_pic_data.getString("data"));

            CircleTransform circleTransform = new CircleTransform();
            Picasso.with(this).load(profile_pic_url.getString("url"))
                    .transform(circleTransform)
                    .into(user_picture);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
