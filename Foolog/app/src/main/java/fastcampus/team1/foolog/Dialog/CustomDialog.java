package fastcampus.team1.foolog.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.DayList;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.bumptech.glide.Glide;

public class CustomDialog extends Dialog {

    TextView txtDate,txtMemo, txtPlace;  // 다이얼로그의 각 위젯
    TextView txtFood, txtEval; // 태그 표현 위젯
    ImageView imgFood;  // 다이얼로그 위젯 - 이미지뷰
    String date, memo;  // 서버로 부터 받은 날짜, 메모
    DayList.Tag[] tag; // 태그 값 -> json 배열
    String imageUrl;
    Context context;
    String send_token;  // 통신에 필요한 헤더 값 (토큰)
    String day;  // 날짜 클릭시 넘어온 해당 날짜의 정보 YYYYMMDD -> Get Day list 에 전송하는 값
    DayList[] dayListBody;

    public CustomDialog(@NonNull Context context, DayList[] dayListBody) {
        super(context);
        this.context = context;
        this.dayListBody = dayListBody;
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
        setTag();
        setImage();
        setMemo();
        setDate();
        //setNetwork();
    }

    private void init(){
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtFood = (TextView) findViewById(R.id.txtFood);
        txtEval = (TextView) findViewById(R.id.txtEval);
        txtMemo = (TextView) findViewById(R.id.txtMemo);
        txtPlace = (TextView) findViewById(R.id.txtTag);
        imgFood = (ImageView) findViewById(R.id.imgFood);

        SharedPreferences storage = context.getSharedPreferences("storage", Activity.MODE_PRIVATE);
        String shared_token = storage.getString("inputToken", " ");
        send_token = "Token " + shared_token;
    }

    /*
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
        Log.e("Dialog","Token ====================="+ send_token);
        call.enqueue(new Callback<DayList[]>() {
            @Override               // Call call..
            public void onResponse(Call<DayList[]> call, Response<DayList[]> response) {
                // 전송결과가 정상이면
                Log.e("Write","in ====== onResponse");
                if(response.isSuccessful()){
                    dayList = response.body();
                        setDate();
                        setImage();
                        setTag();
                        setMemo();
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
    */
    public void setTag(){
        tag = dayListBody[0].tags;
        String foodType = tag[0].type;
        String foodColor = tag[0].color;
        String foodText = tag[0].text;

        String evalType = tag[1].type;
        String evalColor = tag[1].color;
        String evalText = tag[1].text;

        txtFood.setText(foodText);
        txtFood.setTextColor(Color.parseColor(foodColor));

        txtEval.setText(evalText);
        txtEval.setTextColor(Color.parseColor(evalColor));
    }

    public void setDate(){
        date = dayListBody[0].date;
        String dateSplit[];
        dateSplit = date.split(" ");
        txtDate.setText(dateSplit[0]);
    }

    public void setMemo(){
        memo = dayListBody[0].text;
        txtMemo.setText(memo);
    }

    public void setImage(){
        imageUrl = dayListBody[0].photo;
        Glide.with(context).load(imageUrl).into(imgFood);
    }
}

/*
 setNetwork 후 init으로 값이 안넘어오는 이유
 다이얼로그 내린 후 다시 누르면 넘어가 있음.
 */

// TODO: 2017-08-11  아래와 같이 할 경우 왜 안되는지 & Glide.with(context) 의미
/*                    try {
                        URL url = new URL(imageUrl);
                        URLConnection con = url.openConnection();
                        con.connect();
                        BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                        Bitmap bm = BitmapFactory.decodeStream(bis);
                        bis.close();
                        imgFood.setImageBitmap(bm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
*/