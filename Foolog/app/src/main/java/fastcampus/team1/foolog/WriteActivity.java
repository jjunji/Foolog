package fastcampus.team1.foolog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import fastcampus.team1.foolog.Map.MapsActivity;
import fastcampus.team1.foolog.model.WriteCreate;
import fastcampus.team1.foolog.model.WriteListResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static fastcampus.team1.foolog.LoginActivity.token;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private ImageButton btnPost;
    private ImageView WriteImage;
    private RadioButton radioBtnKorea;
    private RadioButton radioBtnJapan;
    private RadioButton radioBtnChina;
    private RadioButton radioBtnUsa;
    private RadioButton radioBtnEtc;
    private RadioButton radioBtnGood;
    private RadioButton radioBtnSoso;
    private RadioButton radioBtnBad;
    private TextView txtFood;
    private RadioGroup rgFood;
    private RadioGroup rgTaste;
    private TextView txtTaste;
    private TextView txtMap;
    private EditText editContent;

    private WriteCreate writeCreate;




    Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        initView();
        setListener();
        setRadioGroup();

    }

    /**
     * 리스너를 달아주는 함수
     */
    private void setListener() {
        btnBack.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        WriteImage.setOnClickListener(this);
        txtMap.setOnClickListener(this);
    }

    /**
     * FindViewbyMe 를 사용한 선언
     * xml에서 LinearLayout에 focus를 줌으로써 자동으로 입력되는걸 방지했다.
     */
    private void initView() {
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnPost = (ImageButton) findViewById(R.id.btnPost);
        WriteImage = (ImageView) findViewById(R.id.WriteImage);
        radioBtnKorea = (RadioButton) findViewById(R.id.radioBtnKorea);
        radioBtnJapan = (RadioButton) findViewById(R.id.radioBtnJapan);
        radioBtnChina = (RadioButton) findViewById(R.id.radioBtnChina);
        radioBtnUsa = (RadioButton) findViewById(R.id.radioBtnUsa);
        radioBtnEtc = (RadioButton) findViewById(R.id.radioBtnEtc);
        radioBtnGood = (RadioButton) findViewById(R.id.radioBtnGood);
        radioBtnSoso = (RadioButton) findViewById(R.id.radioBtnSoso);
        radioBtnBad = (RadioButton) findViewById(R.id.radioBtnBad);
        txtFood = (TextView) findViewById(R.id.txtFood);
        rgFood = (RadioGroup) findViewById(R.id.rgFood);
        rgTaste = (RadioGroup) findViewById(R.id.rgTaste);
        txtTaste = (TextView) findViewById(R.id.txtTaste);
        txtMap = (TextView) findViewById(R.id.txtMap);
        editContent = (EditText) findViewById(R.id.editContent);
    }

    /**
     * 버튼 onClick
     * 각각의 onClick 이벤트를 나타내 주었다. btnBack, btnPost은 우선 뒤로가기로 해놨다.
     * WriteImage에 클릭이 가능하게끔 Clickable을 주었다.  + txtMap에도
     * WriteImage는 선택한 이미지를 돌려받기 위해서 starActivityforResult를 쓴다
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnBack:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            // todo if문을 넣어서 아래쪽에 태그나 내용이 없으면 post가 안되게끔 해주자
            case R.id.btnPost:
                setData();
                setNetwork();
                break;
            case R.id.WriteImage:
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "앱을 선택하세요"), 100);
                break;
            case R.id.txtMap:
                intent = new Intent(this, MapsActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * startActivityForResult 를 닫아주는 함수이다.
     * Glide 사용
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Log.e("Gallery", "imageUri====" + imageUri.getPath());
            Glide.with(getBaseContext())
                    .load(imageUri)
                    .into(WriteImage);
        }
    }

    /**
     * RadioGroup 에서 라디오 버튼 선택시 text값이 변하게 해주는 리스너 함수
     * todo 임시로 text로 하고 customview Tag를 만들면 바꾸자(gone, invisible)
     */
    public void setRadioGroup() {

        rgFood.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnKorea:
                        txtFood.setText(radioBtnKorea.getText().toString());
                        break;
                    case R.id.radioBtnJapan:
                        txtFood.setText(radioBtnJapan.getText().toString());
                        break;
                    case R.id.radioBtnChina:
                        txtFood.setText(radioBtnChina.getText().toString());
                        break;
                    case R.id.radioBtnUsa:
                        txtFood.setText(radioBtnUsa.getText().toString());
                        break;
                    case R.id.radioBtnEtc:
                        txtFood.setText(radioBtnEtc.getText().toString());
                        break;
                }
            }
        });


        rgTaste.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i) {
                    case R.id.radioBtnGood:
                        txtTaste.setText(radioBtnGood.getText().toString());
                        break;
                    case R.id.radioBtnSoso:
                        txtTaste.setText(radioBtnSoso.getText().toString());
                        break;
                    case R.id.radioBtnBad:
                        txtTaste.setText(radioBtnBad.getText().toString());
                        break;
                }
            }
        });


    }

    private void setData(){
        String text = editContent.getText().toString();

        writeCreate = new WriteCreate();
        writeCreate.text = text;

//        writeCreate = temp.pk;

    }


    private void setNetwork() {
        // 레트로핏 정의
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://foolog.jos-project.xyz/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 인터페이스 불러오기
        iService service = retrofit.create(iService.class);


        String real_token = "Token "+token;
        Log.e("Token result","====="+token);
        // 서비스 호출
        Call<WriteListResult> call = service.createPost(writeCreate, real_token);
        call.enqueue(new Callback<WriteListResult>() {
            @Override
            public void onResponse(Call<WriteListResult> call, Response<WriteListResult> response) {
                if (response.isSuccessful()) {

//                        String body = response.body().string();
//                        Log.e("body",""+body);

                    intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(getBaseContext(),"Success", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    int statusCode = response.code();
                    Log.i("MyTag", "응답코드 ============= "+statusCode);
                }

            }

            @Override
            public void onFailure(Call<WriteListResult> call, Throwable t) {
                Log.e("Fail", "Fail network===" + t.getMessage());
            }
        });
    }


}
