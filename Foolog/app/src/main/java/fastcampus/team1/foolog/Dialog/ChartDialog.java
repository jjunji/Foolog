package fastcampus.team1.foolog.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;

import java.text.ParseException;
import java.util.List;

import fastcampus.team1.foolog.Chart.CircleChartActivity;
import fastcampus.team1.foolog.Chart.StringUtil;
import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.TagList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

/**
 * Created by SeungHoShin on 2017. 8. 22..
 */

public class ChartDialog extends Dialog implements View.OnClickListener {

    private Button btnCheckin;
    private Button btnCheckout;
    private CalendarView calendarView;
    private Button btnOk;


    String send_token;
    String shared_token;
    SharedPreferences storage;
    Context context;
    static int start, end;
    StringUtil stringUtil = new StringUtil();


    private static final int START_IN = 10;
    private static final int END_OUT = 20;
    private int checkStatus = START_IN;

    int startYear, startMonth, startDay;
    int endYear, endMonth, endDay;

    TagList tagList;
    CircleChartActivity circle;

    public static float temp_korea, temp_japan, temp_china, temp_usa, temp_etc;


    public ChartDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_date_pick);
        circle = new CircleChartActivity();
        storage = getContext().getSharedPreferences("storage", Activity.MODE_PRIVATE);
        shared_token = storage.getString("inputToken", " ");
        send_token = "Token " + shared_token;

        initView();
        setCalendarButtonText();
        setListener();

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);


    }

    private void initView() {
        btnCheckin = findViewById(R.id.btnCheckin);
        btnCheckout = findViewById(R.id.btnCheckout);
        calendarView = findViewById(R.id.calendarView);
        btnOk = findViewById(R.id.btnOk);
    }


    private void setListener() {
        btnCheckin.setOnClickListener(this);
        btnCheckout.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        calendarView.setOnDateChangeListener(dateChangeListener);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCheckin:
                checkStatus = START_IN;
                setButtonText(btnCheckin, context.getString(R.string.hint_start_date), context.getString(R.string.hint_select_date));
                setButtonText(btnCheckin, context.getString(R.string.hint_end_date), stringUtil.startDate);
                break;

            case R.id.btnCheckout:
                checkStatus = END_OUT;
                setButtonText(btnCheckout, context.getString(R.string.hint_end_date), context.getString(R.string.hint_select_date));
                setButtonText(btnCheckout, context.getString(R.string.hint_start_date), stringUtil.endDate);
                break;
            case R.id.btnOk:
                //todo 네트워크 통신
                try {
                    setNetwork(send_token, start, end);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dismiss();
        }
    }


    /**
     * 버튼 내부의 속성값들을 정의
     *
     * @param btn      : 버튼을 불러온다
     * @param upText   : Check in 에 해당하는 버튼 내부에서 위쪽 글씨
     * @param downText : 날짜에 해당하는 아래쪽 글씨
     */
    private void setButtonText(Button btn, String upText, String downText) {

        // 버튼에 다양한 색깔의 폰트 적용하기
        // 위젯의 android:textAllCaps="false" 적용 필요
        String inText = "<font color='#888888'>" + upText
                + "</font> <br> <font color=\"#fd5a5f\">" + downText + "</font>";
        StringUtil.setHtmlText(btn, inText);
    }


    /**
     * 버튼들 가져오는 걸 정의한다.
     */
    private void setCalendarButtonText() {

        setButtonText(btnCheckin, context.getString(R.string.hint_start_date), context.getString(R.string.hint_select_date));
        setButtonText(btnCheckout, context.getString(R.string.hint_end_date), "-");

    }


    CalendarView.OnDateChangeListener dateChangeListener = new CalendarView.OnDateChangeListener() {
        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            month = month + 1;
            Log.i("Calendar", "year:" + year + ", month:" + month + ", dayOfMonth:" + dayOfMonth);
            String theDay = String.format("%d-%02d-%02d", year, month, dayOfMonth);

            Log.d(TAG, "theDay==" + theDay);
            switch (checkStatus) {
                case START_IN:
                    stringUtil.startDate = theDay;
                    startYear = year;
                    startMonth = month;
                    startDay = dayOfMonth;
                    Log.d(TAG, "START==year&month&day==" + startYear + "|" + startMonth + "|" + startDay);
                    setButtonText(btnCheckin, context.getString(R.string.hint_start_date), stringUtil.startDate);
                    break;
                case END_OUT:
                    stringUtil.endDate = theDay;
                    endYear = year;
                    endMonth = month;
                    endDay = dayOfMonth;
                    Log.d(TAG, "END==year&month&day==" + endYear + "|" + endMonth + "|" + endDay);
                    setButtonText(btnCheckout, context.getString(R.string.hint_end_date), stringUtil.endDate);
                    break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setNetwork(String send_token, int start, int end) throws ParseException {


        // 레트로핏 정의
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.foolog.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 인터페이스 불러오기
        iService service = retrofit.create(iService.class);

        // 날짜 형식이 2017-01-01 이런식으로 날라와서 -를 제외시켜주었다.
        start = Integer.parseInt(stringUtil.startDate.replaceAll("-", ""));
        end = Integer.parseInt(stringUtil.endDate.replaceAll("-", ""));


        Log.d(TAG, "start==" + start);
        Log.d(TAG, "end==" + end);

        Call<List<TagList>> call = service.createDatePick(send_token, start, end);
        Log.e("ChartDialog", "origin url ==" + call.request().url());

        call.enqueue(new Callback<List<TagList>>() {
            @Override
            public void onResponse(Call<List<TagList>> call, Response<List<TagList>> response) {
                List<TagList> tagList = response.body();
                Log.d(TAG, "taglList==" + tagList);

                for (int i = 0; i < tagList.size(); i++) {
                    temp_korea = tagList.get(i).count.한식 + temp_korea;
                    temp_china = tagList.get(i).count.중식 + temp_china;
                    temp_japan = tagList.get(i).count.일식 + temp_japan;
                    temp_usa = tagList.get(i).count.양식 + temp_usa;
                    temp_etc = tagList.get(i).count.기타 + temp_etc;
                }



                Log.d(TAG, "temp_korea==" + temp_korea);
                Log.d(TAG, "temp_china==" + temp_china);
                Log.d(TAG, "temp_japan==" + temp_japan);
                Log.d(TAG, "temp_usa==" + temp_usa);
                Log.d(TAG, "temp_etc==" + temp_etc);



            }

            @Override
            public void onFailure(Call<List<TagList>> call, Throwable t) {

            }
        });
    }



}
