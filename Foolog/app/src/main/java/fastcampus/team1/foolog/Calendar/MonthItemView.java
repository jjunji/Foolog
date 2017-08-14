package fastcampus.team1.foolog.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.TagList;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jhjun on 2017-08-04.
 */

public class MonthItemView extends RelativeLayout{

    TextView textView;
    TextView txtTag;
    Typeface font;
    //TagList[] tagList;
    List<TagList> tagList = new ArrayList<>();
    //ArrayList<TagList> info = new ArrayList<>();
    TagList.tagInfo info;
    TagList.tagInfo info2;

    public MonthItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        // LayoutInflater inflater = (LayoutInflater.from(context.getApplicationContext()));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.month_item, this, true);

        textView = (TextView) findViewById(R.id.textView);
        txtTag = (TextView) findViewById(R.id.txtTag);
        font = Typeface.createFromAsset(context.getAssets(), "yaFontBold.ttf");
    }

    /*
     달력의 각 뷰는 0 부터 시작해 ++ 된다는 것을 확인 -> 일요일에 해당하는 뷰들은 position 0 7 14 21 로 진행.
     position 값을 7로 나누어 나머지가 0일 경우는 일요일을 뜻하므로 조건문으로 판단하여 텍스트를 붉게 표시함.
    */

    // 달력(그리드 뷰)에 주말 표시
    public void setWeek(int position){
        if( position % 7 == 0 ){
            textView.setTextColor(Color.RED);
        }else if( position % 7 == 6 ){
            textView.setTextColor(Color.BLUE);
        } else{
            textView.setTextColor(Color.BLACK);
        }
    }

    // 날짜, 오늘 날짜 표시
    public void setDay(String day, String month){
        //Log.i("setToday", "day==============" + day);
        //Log.i("setToday", "month============" + month);

        Boolean isToday = getDateString(day, month);
        //Log.i("setToday", "flag=============" + isToday);
        if(isToday == true){
            textView.setBackgroundColor(getResources().getColor(R.color.colorToday));
            textView.setText(day);
            textView.setTypeface(font);
        }else{
            textView.setText(day);
            textView.setTypeface(font);
        }
    }

    // 오늘의 날짜 판별
    public boolean getDateString(String day, String month)
    {
        Boolean flag;

        SimpleDateFormat sdf = new SimpleDateFormat("M-dd", Locale.KOREA);
        String date_temp = sdf.format(new Date());
        String[] date_today = date_temp.split("-");
        String today_month = date_today[0]; // 8
        String today_day = date_today[1]; // 13

        if(today_month.equals(month) && today_day.equals(day)){
            flag = true;
        }else{
            flag = false;
        }

        return flag;
    }

/*    public void setPostTag(int start, int end){


    }*/

   /* public void setNetwork(String send_token, String start, String end){
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
        Call<List<TagList>> call = service.createTagList(send_token, start, end);
        call.enqueue(new Callback<List<TagList>>() {
            @Override
            public void onResponse(Call<List<TagList>> call, Response<List<TagList>> response) {
                // 전송결과가 정상이면
                Log.e("Write","in ====== onResponse");
                if(response.isSuccessful()){
                    tagList = response.body();  // TODO: 2017-08-14
                    //String a = tagList[0].date;
                    //String b = tagList[1].date;
                    //Log.i("CalendarAdapter","date=============="+a+"  &  "+b);
                    String a = tagList.get(0).date;
                    String b = tagList.get(1).date;
                    String c = tagList.get(2).date;
                    String d = tagList.get(3).count.한식;
                    String f = tagList.get(12).count.양식;
                    Log.i("CalendarAdapter","info=============="+d + "&" +f);
                    Log.i("CalendarAdapter","date=============="+a+"  &  "+b +" & " + c);
                }else{
                    int statusCode = response.code();
                    Log.i("CustomDialog", "image 응답코드 ============= " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<List<TagList>> call, Throwable t) {
                Log.e("MyTag","error==========="+t.getMessage());
            }
        });
    }*/
}

