package fastcampus.team1.foolog.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;

import java.util.List;

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

public class ChartDialog extends Dialog implements View.OnClickListener{

    private Button btnCheckin;
    private Button btnCheckout;
    private CalendarView calendarView;
    private Button btnOk;


    String send_token;
    String shared_token;
    SharedPreferences storage;
    Context context;

    StringUtil stringUtil = new StringUtil();


    private static final int CHECK_IN = 10;
    private static final int CHECK_OUT = 20;
    private int checkStatus = CHECK_IN;

    public ChartDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_date_pick);

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
        btnCheckin = (Button) findViewById(R.id.btnCheckin);
        btnCheckout = (Button) findViewById(R.id.btnCheckout);
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        btnOk = (Button) findViewById(R.id.btnOk);
    }


    private void setListener() {
        btnCheckin.setOnClickListener(this);
        btnCheckout.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        calendarView.setOnDateChangeListener(dateChangeListener);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCheckin:
                checkStatus = CHECK_IN;
                setButtonText(btnCheckin, context.getString(R.string.hint_start_date), context.getString(R.string.hint_select_date));
                setButtonText(btnCheckin, context.getString(R.string.hint_end_date), stringUtil.checkinDate);
                break;

            case R.id.btnCheckout:
                checkStatus = CHECK_OUT;
                setButtonText(btnCheckout, context.getString(R.string.hint_end_date), context.getString(R.string.hint_select_date));
                setButtonText(btnCheckout, context.getString(R.string.hint_start_date), stringUtil.checkoutDate);
                break;
            case R.id.btnOk:
                //todo 네트워크 통신
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
            //String theDay = year+"-"+month+"-"+dayOfMonth;
            Log.d(TAG,"theDay=="+theDay);
            switch (checkStatus) {
                case CHECK_IN:
                    stringUtil.checkinDate = theDay;
                    setButtonText(btnCheckin, context.getString(R.string.hint_start_date), stringUtil.checkinDate);
                    break;
                case CHECK_OUT:
                    stringUtil.checkoutDate = theDay;
                    setButtonText(btnCheckout, context.getString(R.string.hint_end_date), stringUtil.checkoutDate);
                    break;
            }
        }
    };


    public void setNetwork(String send_token, String start, String end,Context context){


        // 레트로핏 정의
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.foolog.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 인터페이스 불러오기
        iService service = retrofit.create(iService.class);

        Call<List<TagList>> call = service.createDatePick(send_token,start,end);
        call.enqueue(new Callback<List<TagList>>() {
            @Override
            public void onResponse(Call<List<TagList>> call, Response<List<TagList>> response) {
                List<TagList> tagList = response.body();
                Log.d(TAG,"taglList=="+tagList);
            }

            @Override
            public void onFailure(Call<List<TagList>> call, Throwable t) {

            }
        });
    }

}
