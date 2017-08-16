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
// 선택위젯 활용 -> 어답터 생성
public class CalendarAdapter extends BaseAdapter{

    Calendar calendar;
    ArrayList<String> dayList = new ArrayList<String>();
    ArrayList<String> dateList = new ArrayList<>(); // 포지션 값에 매칭되는 날짜를 저장하는 list(20170810)

    int lastDay; // 마지막 날
    int curYear; // 현재 년도
    int curMonth; // 현재 월
    Context context;
    String send_token;
    static String start, end;
    //List<TagList> tagList = new ArrayList<>();

    public CalendarAdapter(Context context, String token) {

        Date date = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(date);  // calendar 에 현재 시간 설정.
        this.context = context;
        this.send_token = token;
    }

    public void setNowMonth(){
        calendar.add(Calendar.MONTH, curMonth);
        recalculate(); // 해당 월의 첫날, 마지막 날 계산
        resetDayNumbers2(); // 실제 아이템 뷰에 넣어서 뷰로 보여줌
        //setNetwork(send_token, start, end);
        Log.e("setNowMonth","Start & End==========="+ start + end);
    }

    public String getStart(){
        return start;
    }

    public String getEnd(){
        return end;
    }

    public void setPreviousMonth(){
        calendar.add(Calendar.MONTH, -1); // -1 : 이전 달로 이동
        recalculate(); // 해당 월의 첫날, 마지막 날 계산
        resetDayNumbers2(); // 실제 아이템 뷰에 넣어서 뷰로 보여줌
        Log.e("setPreMonth","Start & End==========="+ start + end);
    }

    public void setNextMonth(){
        calendar.add(Calendar.MONTH, +1);
        recalculate(); // 해당 월의 첫날, 마지막 날 계산
        resetDayNumbers2(); // 실제 아이템 뷰에 넣어서 뷰로 보여줌
        Log.e("setNextMonth","Start & End==========="+ start + end);
    }

    public int getCurrentYear(){
        return curYear;
    }

    public int getCurrentMonth(){
        return (curMonth+1);
    }

    //------------- 어답터 생성 -> 메인액티비 안에서 그리드뷰안에 설정 할 수 있게 됨.----------------

    // 달력을 선택한 달의 연도 & 달로 설정하고, 1일이 시작되는 요일을 재계산하는 메소드
    public void recalculate() {
        // 날짜를 현재 달의 1일로 설정.
        calendar.set(Calendar.DAY_OF_MONTH, 1);  // -> 1 ex) 현재 8월이면 8월 1일 에서 1을 가져옴.
        curYear = calendar.get(Calendar.YEAR);  // 2017
        curMonth = calendar.get(Calendar.MONTH);  // 7 (0 부터 시작) -> 8월
        lastDay = getLastDay();  // 셋팅 달의 마지막 날이 몇일인지 출력 -> 31
        //setNetwork(dateList.get(Calendar.DAY_OF_MONTH), dateList.get(dateList.size()));

        Log.i("Main","DAY_OF_MONTH==============="+ Calendar.DAY_OF_MONTH);
        Log.i("Main","curYear==============="+ curYear);
        Log.i("Main","curMonth==============="+ curMonth);
        Log.i("Main","lastDay==============="+ lastDay);
    }

    public void resetDayNumbers2(){

        dayList.clear();
        dateList.clear();

        dayList.add("일");
        dayList.add("월");
        dayList.add("화");
        dayList.add("수");
        dayList.add("목");
        dayList.add("금");
        dayList.add("토");

        for(int i = 0; i<7; i++){
            dateList.add("");
        }

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 위에서 1일로 셋팅했으므로 1일이 무슨 요일인지 확인 -> 3(화)
        Log.i("Main","DAY_OF_WEEK==============="+ dayOfWeek);

        for (int i = 1; i < dayOfWeek; i++) {
            dayList.add("");
            dateList.add("");
        }

        for (int i = 1; i <= lastDay; i++) {
            dayList.add("" + (i));

            if( i<10 ){ // 10일 미만
                if((curMonth+1) < 10){ // 10월 미만
                    dateList.add(curYear +"0"+ (curMonth+1) +"0"+ (i));
                }else{  // 10일 미만 10월 이상
                    dateList.add(curYear +""+ (curMonth+1) +"0"+ (i));
                }
            }else{ // 10일 이상
                if((curMonth+1) < 10){  // 10월 미만
                    dateList.add(curYear +"0"+ (curMonth+1) +""+ (i));
                }else{  // 10월 이상
                    dateList.add(curYear +""+ (curMonth+1) +""+ (i));
                }
            }
        }

        setTagQuery(dayOfWeek);
        //setNetwork(send_token, start, end);
    }

    public void setTagQuery(int dayOfWeek){
        int startNum = dayOfWeek + 6; // 9
        int endNum = dateList.size()-1; // 리스트 마지막 요소 -> 달의 마지막 날짜

        start = dateList.get(startNum);
        end = dateList.get(endNum);
    }

/*    public void Calc(){
        String kor = tagList.get(14).count.한식;  // 1
        String chin = tagList.get(0).count.중식;
    }*/




    public String getDateList(int position){
        return dateList.get(position);
    }

    // 각 월 마다의 마지막 날 반환
    public int getLastDay() {
        switch (curMonth){
            case 0: // 1월
            case 2:
            case 4:
            case 6:
            case 7:
            case 9:
            case 11:
                return 31;

            case 3:
            case 5:
            case 8:
            case 10:
                return 30;

            default:
                if(((curYear%4 == 0) && (curYear%100 != 0)) || (curYear%400==0)) {
                    return 29;
                } else{
                    return 28;
                }
        }
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.e("Adapter", "getView 호출 시점 ======================getView start!!");
        MonthItemView view = new MonthItemView(context);
        view.setWeek(position); // 주말 표시 메서드
        view.setDay(dayList.get(position), (curMonth+1)+"");  // 날짜, 오늘의 날짜 표시 메서드


        return view;
    }

}