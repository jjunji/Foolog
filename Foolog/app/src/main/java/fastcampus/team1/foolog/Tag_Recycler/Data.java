package fastcampus.team1.foolog.Tag_Recycler;

import android.content.Context;

import fastcampus.team1.foolog.Calendar.MonthItemView;

/**
 * Created by jhjun on 2017-08-15.
 */

public class Data {

    public String image;
    public int resId;

    public void setImage(String str, Context context){
        image = str;
        // 문자열로 리소스 아이디 가져오기
        resId = context.getResources().getIdentifier(image, "drawable", context.getPackageName());
    }
}
