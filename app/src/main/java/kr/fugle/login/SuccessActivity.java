package kr.fugle.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import kr.fugle.HoActivity;
import kr.fugle.Item.User;
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
    private String imagePath;

    private UserProfile userProfile;
//    private User user;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);

        isLogOut = false;
        user_picture = (ImageView) findViewById(R.id.user_profile_photo);
        user_id = (TextView) findViewById(R.id.login_id);
        user_name = (TextView) findViewById(R.id.login_name);
        user_email = (TextView) findViewById(R.id.login_email);

        final Intent intent = getIntent();

        // 페이스북
        String jsondata = intent.getStringExtra("jsondata");
        if (jsondata != null) {
            setUserProfile(jsondata);
        }

        // 카카오
        if (userProfile != null) {
            setUserProfile(userProfile);
        }


        findViewById(R.id.tempBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tempIntent = new Intent(SuccessActivity.this, HoActivity.class);
                tempIntent.putExtra("user", intent.getStringExtra("user"));
                Log.d("in SuccessActivity", "intent.user " + intent.getStringExtra("user"));
                startActivity(tempIntent);
                finish();
            }
        });
    }

    public void onLogoutButtonClicked(View v) {
        isLogOut = true;

        if (LoginManager.getInstance() != null) {
            LoginManager.getInstance().logOut();
        }
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
            }
        });
        Intent intent = new Intent(SuccessActivity.this, LoginActivity.class);
        intent.putExtra("logout",true);
        startActivity(intent);
        finish();
    }

    // 카카오
    // 페이스북
    public void setUserProfile(UserProfile userProfile) {

        try {
//            user_email.setText(response.get("email").toString());
            user_id.setText(userProfile.getId() + "");
            user_name.setText(userProfile.getNickname());
            imagePath = userProfile.getProfileImagePath();

            CircleTransform circleTransform = new CircleTransform();
            Picasso.with(this).load(imagePath)
                    .transform(circleTransform)
                    .into(user_picture);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // 페이스북
    public void setUserProfile(String jsondata) {

        try {
            response = new JSONObject(jsondata);
            if (response.get("login_type").equals("facebook")) {
//            user_email.setText(response.get("email").toString());
                user_id.setText(response.get("id").toString());
                user_name.setText(response.get("name").toString());
                profile_pic_data = new JSONObject(response.get("picture").toString());
                profile_pic_url = new JSONObject(profile_pic_data.getString("data"));

                CircleTransform circleTransform = new CircleTransform();
                Picasso.with(this).load(profile_pic_url.getString("url"))
                        .transform(circleTransform)
                        .into(user_picture);
            } else {
                user_id.setText(response.get("id").toString());
                user_name.setText(response.get("name").toString());
                imagePath = response.get("image").toString();

                CircleTransform circleTransform = new CircleTransform();
                Picasso.with(getApplicationContext())
                        .load(imagePath)
                        .transform(circleTransform)
                        .placeholder(R.drawable.profile)
                        .into(user_picture);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
