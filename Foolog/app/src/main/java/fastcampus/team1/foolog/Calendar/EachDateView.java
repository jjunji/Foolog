package fastcampus.team1.foolog.Calendar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.model.TagList;

/**
 * Created by jhjun on 2017-08-17.
 */

public class EachDateView extends FrameLayout {

    public static final int KOREA = 0;
    public static final int CHINA = 1;
    public static final int JAPAN = 2;
    public static final int WESTERN = 3;
    public static final int ETC = 4;

    private TextView date;
    private LinearLayout marks;
    Typeface font;

    public EachDateView(Context context) {
        this(context, null);
    }

    public EachDateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.month_item,this,true);
        date = view.findViewById(R.id.date);
        marks = view.findViewById(R.id.marks);
        font = Typeface.createFromAsset(context.getAssets(), "yaFontBold.ttf");
    }

    public void setDate(String dateStr){
        date.setText(dateStr);
    }

    // 달력(그리드 뷰)에 주말 표시
    public void setWeek(int position){
        if( position % 7 == 0 ){
            date.setTextColor(Color.RED);
        }else if( position % 7 == 6 ){
            date.setTextColor(Color.BLUE);
        } else{
            date.setTextColor(Color.BLACK);
        }
    }

    // 날짜, 오늘 날짜 표시
    public void setDay(String day, String month){
        Boolean isToday = getDateString(day, month);

        if(isToday == true){
            date.setBackgroundColor(getResources().getColor(R.color.colorToday));
            date.setText(day);
            date.setTypeface(font);
        }else{
            date.setText(day);
            date.setTypeface(font);
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
        Log.i("MonthItemView","today_month : ==============="+ today_month);
        Log.i("MonthItemView","month : ==============="+ month);
        String today_day = date_today[1]; // 13

        if(today_month.equals(month) && today_day.equals(day)){
            flag = true;
        }else{
            flag = false;
        }

        return flag;
    }

    //
    public void setTags(List<TagList> tagList, int position){
        int korea = tagList.get(position).count.한식;
        int china = tagList.get(position).count.중식;
        int japan = tagList.get(position).count.일식;
        int western = tagList.get(position).count.양식;
        int etc = tagList.get(position).count.기타;

        for(int i=0; i<korea; i++){
            addDot(KOREA);
        }
        for(int i=0; i<china; i++){
            addDot(CHINA);
        }
        for(int i=0; i<japan; i++){
            addDot(JAPAN);
        }
        for(int i=0; i<western; i++){
            addDot(WESTERN);
        }
        for(int i=0; i<etc; i++){
            addDot(ETC);
        }
    }

    public void addDot(int color){
        View view = new View(this.getContext());
        switch (color){
            case KOREA:
                view.setBackgroundResource(R.drawable.tag_circle_korea);
                break;
            case CHINA:
                view.setBackgroundResource(R.drawable.tag_circle_china);
                break;
            case WESTERN:
                view.setBackgroundResource(R.drawable.tag_circle_usa);
                break;
            case JAPAN:
                view.setBackgroundResource(R.drawable.tag_circle_japan);
                break;
            case ETC:
                view.setBackgroundResource(R.drawable.tag_circle_etc);
                break;
        }
        marks.addView(view,new LayoutParams(20,20));
    }
}
