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
    DayList[] dayListBody;

    public CustomDialog(@NonNull Context context, DayList[] dayListBody) {
        super(context);
        this.context = context;
        this.dayListBody = dayListBody;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDialogWindow();
        setContentView(R.layout.activity_custom_dialog);
        init();
        setTag();
        setImage();
        setMemo();
        setDate();
    }

    private void init(){
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtFood = (TextView) findViewById(R.id.txtFood);
        txtEval = (TextView) findViewById(R.id.txtEval);
        txtMemo = (TextView) findViewById(R.id.txtMemo);
        txtPlace = (TextView) findViewById(R.id.txtTag);
        imgFood = (ImageView) findViewById(R.id.imgFood);
    }

    private void setDialogWindow(){
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
    }

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