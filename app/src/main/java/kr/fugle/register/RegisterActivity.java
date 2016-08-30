package kr.fugle.register;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import kr.fugle.Item.ActivityStartListener;
import kr.fugle.Item.User;
import kr.fugle.R;
import kr.fugle.login.CircleTransform;
import kr.fugle.login.OkHttpLogin;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 김은진 on 2016-07-15.
 */
public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText inputName, inputEmail, inputPassword, inputMessage;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword,
            inputLayoutMessage, inputLayoutImage;
    private Button btnSignUp;
    private ImageView inputImage;

    // 갤러리에서 사진가져오기
    private int REQ_PICK_CODE = 100;

    // 프로필 사진 이미지 주소
    private String imgPath;

    private ActivityStartListener activityStartListener;

    // 로딩 다이얼로그
    private AppCompatDialog loadingDialog;

    // 서버 통신
    private PostRegister postRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 로딩 다이얼로그
        AlertDialog.Builder loadingDialogBuilder = new AlertDialog.Builder(RegisterActivity.this, R.style.AppCompatAlertDialogStyle);
        loadingDialogBuilder.setCancelable(false)
                .setView(R.layout.dialog_progressbar);

        loadingDialog = loadingDialogBuilder.create();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 기본 이미지 주소
        imgPath = "";

        inputLayoutImage = (TextInputLayout) findViewById(R.id.input_layout_profileimg);
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        inputLayoutMessage = (TextInputLayout) findViewById(R.id.input_layout_message);

        inputImage = (ImageView) findViewById(R.id.input_profileimg);
        inputName = (EditText) findViewById(R.id.input_name);
        inputEmail = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        inputMessage = (EditText) findViewById(R.id.input_message);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputPassword.addTextChangedListener(new MyTextWatcher(inputPassword));
        inputMessage.addTextChangedListener(new MyTextWatcher(inputMessage));

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });

        // 갤러리에서 사진가져오기
        inputImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickerIntent = new Intent(Intent.ACTION_PICK);
                pickerIntent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                pickerIntent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(pickerIntent, REQ_PICK_CODE);
            }
        });

        activityStartListener = new ActivityStartListener() {
            @Override
            public void activityStart(Intent intent) {

            }

            @Override
            public void activityStart() {
                Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void activityFinish() {

            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null)
            return;
        super.onActivityResult(requestCode, resultCode, data);

        imgPath = getRealPathFromURI(data.getData());

        CircleTransform circleTransform = new CircleTransform();
        Picasso.with(this)
                .load(data.getData().toString())
                .resize(800, 600)
                .centerCrop()
                .transform(circleTransform)
                .into(inputImage);
    }

    private String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(RegisterActivity.this, contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int colum_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(colum_index);
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

        if(postRegister != null)
            postRegister.cancel(true);
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        // 로딩 시작
        loadingDialog.show();

        // name, email, password, message, image
        postRegister = new PostRegister();
        postRegister.setActivityStartListener(activityStartListener);
        postRegister.setLoadingDialog(loadingDialog);
        postRegister.execute(
                "emailRegi/",
                inputEmail.getText().toString(),
                inputName.getText().toString(),
                inputPassword.getText().toString(),
                inputMessage.getText().toString(),
                imgPath);
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(inputEmail);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (inputPassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(inputPassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_name:
                    validateName();
                    break;
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    private class PostRegister extends AsyncTask<String, Void, String>{

        private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        private String serverUrl = getResources().getString(R.string.server_url);

        private final OkHttpClient client = new OkHttpClient();

        private ActivityStartListener activityStartListener;

        private AppCompatDialog loadingDialog;

        public void setActivityStartListener(ActivityStartListener activityStartListener) {
            this.activityStartListener = activityStartListener;
        }

        public void setLoadingDialog(AppCompatDialog loadingDialog) {
            this.loadingDialog = loadingDialog;
        }

        @Override
        protected String doInBackground(String... params) {

            Log.d("ho's activity", "PostRegister.doInBackground");

            RequestBody body;
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("primary", params[1])
                    .addFormDataPart("name", params[2])
                    .addFormDataPart("password", params[3])
                    .addFormDataPart("message", params[4]);

            // 사진 유무에 따라
            if(params[5].equals("")){
                Log.d("----->", "img is null");
                body = builder.build();
            }else{
                // 사진 파일
                File file = new File(params[5]);

                body = builder.addFormDataPart("file", "profile.png", RequestBody.create(MEDIA_TYPE_PNG, file))
                        .build();
            }

            Request request = new Request.Builder()
                    .url(serverUrl + params[0])
                    .post(body)
                    .build();

            String result = "";

            try {
                Response response = client.newCall(request).execute();
                result = response.body().string();
            }catch (Exception e){
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("ho's activity", "RegisterActivity.PostRegister.onPostExecute " + s);

            if(loadingDialog != null)
                loadingDialog.cancel();

            if(isCancelled()){
                Log.d("ho's activity", "RegisterActivity.PostRegister is canceled");
                return;
            }

            if(s.equals("SocketTimeoutException")){
                Toast.makeText(RegisterActivity.this, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();

                if(activityStartListener != null)
                    activityStartListener.activityFinish();

                return;
            }

            if(s.equals("result:2")) {  // 회원가입 실패시
                Toast.makeText(RegisterActivity.this, "존재하는 이메일입니다", Toast.LENGTH_SHORT).show();
                return;
            }else if(s.equals("result:3")) {  // 회원가입 성공시
                activityStartListener.activityStart();
                return;
            }else{
                Toast.makeText(RegisterActivity.this, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}

