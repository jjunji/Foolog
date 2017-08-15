package fastcampus.team1.foolog.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.Tag_Recycler.CustomRecycler;
import fastcampus.team1.foolog.Tag_Recycler.Data;
import fastcampus.team1.foolog.Tag_Recycler.Loader;
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
    ImageView imgTag;
    RecyclerView listView;

    public MonthItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        // LayoutInflater inflater = (LayoutInflater.from(context.getApplicationContext()));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.month_item, this, true);

        textView = (TextView) findViewById(R.id.textView);
        font = Typeface.createFromAsset(context.getAssets(), "yaFontBold.ttf");

        listView = (RecyclerView) findViewById(R.id.listView);
        ArrayList<Data> datas = Loader.getData(context);
        CustomRecycler adapter = new CustomRecycler(datas, this);
        listView.setAdapter(adapter);
        listView.setLayoutManager(new GridLayoutManager(context, 2));
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

}

