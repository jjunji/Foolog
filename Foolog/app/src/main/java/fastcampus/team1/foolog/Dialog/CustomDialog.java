package fastcampus.team1.foolog.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fastcampus.team1.foolog.LoginActivity;
import fastcampus.team1.foolog.MainActivity;
import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.DayList;
import fastcampus.team1.foolog.model.LoginResult;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.content.SharedPreferences;

public class CustomDialog extends Dialog {

    TextView txtDate, txtTag, txtMemo, txtPlace;
    String date;
    String[] arr = new String[2];
    String memo;
    String place;
    ImageView imgFood;
    DayList dayList;
    Context context;
    String send_token;
    String day;

    public CustomDialog(@NonNull Context context, String day) {
        super(context);
        this.context = context;
        this.day = day;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_custom_dialog);
        init();
        setNetwork();

        txtDate.setText(day);
        txtMemo.setText(memo);
        //txtTag.setText(arr[0]);
    }

    private void init(){
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTag = (TextView) findViewById(R.id.txtTag);
        txtMemo = (TextView) findViewById(R.id.txtMemo);
        txtPlace = (TextView) findViewById(R.id.txtPlace);
        imgFood = (ImageView) findViewById(R.id.imgFood);

        SharedPreferences storage = context.getSharedPreferences("storage", Activity.MODE_PRIVATE);
        String shared_token = storage.getString("inputToken", " ");
        send_token = "Token " + shared_token;
        Log.e("Dialog","Token ====================="+ send_token);
    }

    private void setNetwork(){
        // okhttp log interceptor 사용
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();
        // 레트로핏 객체 정의
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.foolog.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        // 실제 서비스 인터페이스 생성.
        iService service = retrofit.create(iService.class);
        // 서비스 호출
        Call<DayList[]> call = service.createDayList(day,send_token);
        //Call call = service.createDayList("day",day);
        call.enqueue(new Callback<DayList[]>() {
            @Override               // Call call..
            public void onResponse(Call<DayList[]> call, Response<DayList[]> response) {
                // 전송결과가 정상이면
                Log.e("Write","in ====== onResponse");
                if(response.isSuccessful()){
                    DayList[] dayList = response.body();
                    date = dayList[0].date;
                    Log.e("CustomDialog","date =============="+ date);
                    //arr[0] = dayList.tags[0].toString();
                    memo = dayList[0].text;
                    Log.e("CustomDialog","memo =============="+ memo);
                    // 이미지
                }else{
                    int statusCode = response.code();
                    Log.i("CustomDialog", "image 응답코드 ============= " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<DayList[]> call, Throwable t) {
                Log.e("MyTag","error==========="+t.getMessage());
            }
        });
    }


}
