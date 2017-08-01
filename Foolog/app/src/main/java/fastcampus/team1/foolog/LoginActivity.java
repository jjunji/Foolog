package fastcampus.team1.foolog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import fastcampus.team1.foolog.model.Login;
import fastcampus.team1.foolog.model.LoginResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
- model -
Login : 로그인시 서버로 email, password 전송
LoginResult : 로그인 -> 토큰 생성 -> 토큰 저장용
User : 회원가입시 사용

 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText txtEmail, txtPassword;
    Button btnLogin, btnSignUp;
    Intent intent;
    String token;
    String loginId, loginPwd;  // SharedPreferences 사용을 위한 id, pwd 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword1);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);

        SharedPreferences storage = getSharedPreferences("storage", Activity.MODE_PRIVATE);
        // 처음에는 SharedPreferences에 아무런 정보도 없으므로 값을 저장할 키를 생성한다.
        // getString 의 첫번째 인자는 저장될 키, 두번째 인자는 값.
        // 처음에는 값이 없으므로 키 값은 원하는 것으로 하고, 값은 null 을 준다.
        loginId = storage.getString("inputId",null);
        loginPwd = storage.getString("inputPwd",null);

        checkStorage();
    }

    private void checkStorage() {
        if(loginId != null && loginPwd != null) {
            Toast.makeText(LoginActivity.this, loginId +"님 자동로그인 입니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        /*else if (loginId == null && loginPwd == null){
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if (id.getText().toString().equals("부르곰") && pwd.getText().toString().equals("네이버")) {

                        //아이디가 '부르곰'이고 비밀번호가 '네이버'일 경우 SharedPreferences.Editor를 통해
                        //auto의 loginId와 loginPwd에 값을 저장.



                        Toast.makeText(LoginActivity.this, txtEmail.getText().toString()+"님 환영합니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
            });
        }*/
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // 로그인
            case R.id.btnLogin :
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                // Login 클래스는 로그인용으로만 사용됨.
                final Login login = new Login();
                login.email = email;
                login.password = password;

                // 레트로핏 객체 정의
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://foolog.jos-project.xyz/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                // 실제 서비스 인터페이스 생성.
                iService service = retrofit.create(iService.class);
                // 서비스 호출
                Call<LoginResult> call = service.createLogin(login);  // 응답 body는 LoginResult.class를 이용.
                call.enqueue(new Callback<LoginResult>() {
                    @Override
                    public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                        // 전송결과가 정상이면
                        Log.e("Write","in ====== onResponse");
                        if(response.isSuccessful()){
                            LoginResult loginResult = response.body(); // 로그인시 생성되는 객체는 loginResult 형태.
                            Log.e("LoginActivity", "response body =====================" + loginResult);
                            token = loginResult.key;
                            int pk = loginResult.user.pk;
                            Log.e("LoginActivity", "token =====================" +token);
                            Log.e("LoginActivity", "pk=========================" + pk);
                            Toast.makeText(getBaseContext(),"Success", Toast.LENGTH_SHORT).show();

                            // ----------------------

                            SharedPreferences storage = getSharedPreferences("storage", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = storage.edit();

                            autoLogin.putString("inputId", txtEmail.getText().toString());
                            autoLogin.putString("inputPwd", txtPassword.getText().toString());

                            // commit() : 저장
                            autoLogin.commit();

                            intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            int statusCode = response.code();
                            Log.i("MyTag", "응답코드 ============= "+statusCode);
                            //Log.i("MyTag", "응답코드 ============= "+response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResult> call, Throwable t) {
                        Log.e("MyTag","error==========="+t.getMessage());
                    }
                });
                break;

            // 회원가입 페이지로 이동
            case R.id.btnSignUp :
                intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }
}
