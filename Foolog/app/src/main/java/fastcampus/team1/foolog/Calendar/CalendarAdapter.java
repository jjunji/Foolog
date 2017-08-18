package fastcampus.team1.foolog.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.DayList;
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

@RequiresApi(api = Build.VERSION_CODES.N)
public class CalendarAdapter extends BaseAdapter{

    Context context;
    List<TagList> tagList = new ArrayList<>();
    ArrayList<String> dayList = new ArrayList<String>();
    int curMonth;
    int dayOfWeek;
    int lastDay;

    public CalendarAdapter(Context context, ArrayList<String> dayList, int curMonth, List<TagList> tagList, int dayOfWeek, int lastDay) {
        this.dayList = dayList;
        this.curMonth = curMonth;
        this.context = context;
        this.tagList = tagList;
        this.dayOfWeek = dayOfWeek;
        this.lastDay = lastDay;
    }

    @Override
    public int getCount() {
        return dayList.size();
    }

    @Override
    public Object getItem(int position) {
        return dayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e("Adapter", "getView 호출 시점 ======================getView start!!");
        //MonthItemView view = new MonthItemView(context);
        EachDateView view = new EachDateView(context);
        view.setWeek(position); // 주말 표시 메서드
        view.setDay(dayList.get(position), (curMonth+1)+"");  // 날짜, 오늘의 날짜 표시 메서드
        Log.i("CalendarAdapter", "position===========" + position);  // 일욜 ~ 30..
        Log.i("CalendarAdapter", "dayList.get(position)===========" + dayList.get(position));  // 일욜 ~ 30..
        Log.i("CalendarAdapter", "dayList.size()=============" + dayList.size());  // 7+시작일+lastDay
        Log.i("CalendarAdapter", "tagList.size()=============" + tagList.size());
        Log.i("CalendarAdapter", "dayOfWeek===============" + dayOfWeek);  // 일욜 ~ 30.. // 3 -> 6
        if(position >= dayOfWeek+6 && position <= dayList.size()){
            view.setTags(tagList, position-9);
        }
        return view;
    }

}