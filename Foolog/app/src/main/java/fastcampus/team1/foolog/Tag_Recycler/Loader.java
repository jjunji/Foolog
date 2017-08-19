package fastcampus.team1.foolog.Tag_Recycler;

import android.content.Context;

import java.util.ArrayList;

import fastcampus.team1.foolog.Calendar.MonthItemView;

/**
 * Created by jhjun on 2017-08-15.
 */

public class Loader {
    public static ArrayList<Data> getData(Context context){
        ArrayList<Data> result = new ArrayList<>();
        for(int i=1 ; i<2; i++){
            Data data = new Data();
            data.setImage("tag_circle_korea", context);
            result.add(data);
        }
        return result;
    }
}
