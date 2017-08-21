package fastcampus.team1.foolog;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fastcampus.team1.foolog.Calendar.CalendarAdapter;
import fastcampus.team1.foolog.Dialog.CustomDialog;
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
 * A simple {@link Fragment} subclass.
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class CalendarFragment extends Fragment {

    TextView txtMonth;
    GridView monthView;
    CalendarAdapter adapter;
    View view;
    Typeface font;
    Context context;
    CustomDialog customDialog;
    String send_token;  // 통신에 필요한 헤더 값 (토큰)
    SharedPreferences storage;
    String shared_token;
    Button btnPrevious, btnNext;
    Calendar calendar;
    ArrayList<String> dayList = new ArrayList<String>();
    ArrayList<String> dateList = new ArrayList<>(); // 포지션 값에 매칭되는 날짜를 저장하는 list(20170810)

    int lastDay; // 마지막 날
    int curYear; // 현재 년도
    int curMonth; // 현재 월
    int dayOfWeek;
    String start, end;

    iService service = null;

    public CalendarFragment() {
        // Required empty public constructor
    }

    //// TODO: 2017-08-10
    public static CalendarFragment newInstance(Context mContext) {
        Bundle args = new Bundle();
        CalendarFragment fragment = new CalendarFragment();
        fragment.context = mContext;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Log.i("CalendarFragment", "===================CalendarFragment" + "Start");
        initNetwork();
        initView();
        initDate();
        setNowMonth();
        setTxtMonth();
        setNetwork(send_token, start, end);
        setMonthViewClickListener();  // 그리드뷰의 한 아이템 클릭시 이벤트 정의
        setButton();  // 전 달, 다음 달 이동 버튼 정의

        return view;
    }

    private void initView() {
        storage = context.getSharedPreferences("storage", Activity.MODE_PRIVATE);
        shared_token = storage.getString("inputToken", " ");
        send_token = "Token " + shared_token;

        font = Typeface.createFromAsset(getActivity().getAssets(), "yaFontBold.ttf");

        btnPrevious = (Button) view.findViewById(R.id.btnPrevious);
        btnNext = (Button) view.findViewById(R.id.btnNext);

        txtMonth = (TextView) view.findViewById(R.id.txtMonth);

        monthView = (GridView) view.findViewById(R.id.monthView);
        monthView.setAdapter(adapter); // 그리드뷰(달력) 와 어댑터 연결 (안하면 뷰가 안보임)

    }

    private void initDate() {
        Date date = new Date();
        calendar = Calendar.getInstance();
        calendar.setTime(date);  // calendar 에 현재 시간 설정.
    }

    // 전 달, 다음 달 이동 버튼
    private void setButton() {
        // 이전 월을 설정하고 그대로 표시됨.
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPreviousMonth();
                setTxtMonth();
                setNetwork(send_token, start, end);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNextMonth();
                setTxtMonth();
                setNetwork(send_token, start, end);

            }
        });
    }

    // 폰트 적용 함수
    private void setTxtMonth() {
        txtMonth.setText(getCurrentYear() + "년" + getCurrentMonth() + "월");
        txtMonth.setTypeface(font);
    }

    // 달력의 한 아이템(날짜) 뷰 클릭시 이벤트 정의
    private void setMonthViewClickListener() {
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setNetwork(getDateList(position));
            }
        });
    }

    private void setNowMonth() {
        calendar.add(Calendar.MONTH, curMonth);
        recalculate(); // 해당 월의 첫날, 마지막 날 계산
        resetDayNumbers2();  // dayList & dateList 채우는 부분
        //setNetwork(send_token, start, end);
        Log.e("setNowMonth", "Start & End===========" + start + end);
    }

    private void setPreviousMonth() {
        calendar.add(Calendar.MONTH, -1); // -1 : 이전 달로 이동
        recalculate(); // 해당 월의 첫날, 마지막 날 계산
        resetDayNumbers2(); // dayList & dateList 채우는 부분
        Log.e("setPreMonth", "Start & End===========" + start + end);
    }

    private void setNextMonth() {
        calendar.add(Calendar.MONTH, +1);
        recalculate(); // 해당 월의 첫날, 마지막 날 계산
        resetDayNumbers2(); // dayList & dateList 채우는 부분
        Log.e("setNextMonth", "Start & End===========" + start + end);
    }

    private int getCurrentYear() {
        return curYear;
    }

    private int getCurrentMonth() {
        return (curMonth + 1);
    }


    // 달력을 선택한 달의 연도 & 달로 설정하고, 1일이 시작되는 요일을 재계산하는 메소드
    private void recalculate() {
        // 날짜를 현재 달의 1일로 설정.
        calendar.set(Calendar.DAY_OF_MONTH, 1);  // -> 1 ex) 현재 8월이면 8월 1일 에서 1을 가져옴.
        curYear = calendar.get(Calendar.YEAR);  // 2017
        curMonth = calendar.get(Calendar.MONTH);  // 7 (0 부터 시작) -> 8월
        lastDay = getLastDay();  // 셋팅 달의 마지막 날이 몇일인지 출력 -> 31
        //setNetwork(dateList.get(Calendar.DAY_OF_MONTH), dateList.get(dateList.size()));

        Log.i("Main", "DAY_OF_MONTH===============" + Calendar.DAY_OF_MONTH);
        Log.i("Main", "curYear===============" + curYear);
        Log.i("Main", "curMonth===============" + curMonth);
        Log.i("Main", "lastDay===============" + lastDay);
    }

    private void resetDayNumbers2() {

        dayList.clear();
        dateList.clear();

        dayList.add("일");
        dayList.add("월");
        dayList.add("화");
        dayList.add("수");
        dayList.add("목");
        dayList.add("금");
        dayList.add("토");

        for (int i = 0; i < 7; i++) {
            dateList.add("");
        }

        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // 위에서 1일로 셋팅했으므로 1일이 무슨 요일인지 확인 -> 3(화)
        Log.i("Main", "DAY_OF_WEEK===============" + dayOfWeek);

        for (int i = 1; i < dayOfWeek; i++) {
            dayList.add("");
            dateList.add("");
        }

        for (int i = 1; i <= lastDay; i++) {
            dayList.add("" + (i));

            if (i < 10) { // 10일 미만
                if ((curMonth + 1) < 10) { // 10월 미만
                    dateList.add(curYear + "0" + (curMonth + 1) + "0" + (i));
                } else {  // 10일 미만 10월 이상
                    dateList.add(curYear + "" + (curMonth + 1) + "0" + (i));
                }
            } else { // 10일 이상
                if ((curMonth + 1) < 10) {  // 10월 미만
                    dateList.add(curYear + "0" + (curMonth + 1) + "" + (i));
                } else {  // 10월 이상
                    dateList.add(curYear + "" + (curMonth + 1) + "" + (i));
                }
            }
        }

        setTagQuery(dayOfWeek);
    }

    private void setTagQuery(int dayOfWeek) {
        int startNum = dayOfWeek + 6; // 9
        int endNum = dateList.size() - 1; // 리스트 마지막 요소 -> 달의 마지막 날짜

        start = dateList.get(startNum);
        end = dateList.get(endNum);
    }

    private String getDateList(int position) {
        return dateList.get(position);
    }

    // 각 월 마다의 마지막 날 반환
    private int getLastDay() {
        switch (curMonth) {
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
                if (((curYear % 4 == 0) && (curYear % 100 != 0)) || (curYear % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
        }
    }


    private void initNetwork() {
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

        service = retrofit.create(iService.class);
    }

    /* setNetwork 오버로딩
       1. 인자로 토큰, start,end -> 날짜 범위(start ~ end) 에 작성한 글에 포함된 항목별 태그 개수
       2. 인자로 day(특정 한 날짜) -> 선택한 날짜에 해당하는 post Data
    */
    private void setNetwork(String send_token, String start, String end) {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("데이터를 불러오는 중입니다.");
        progressDialog.show();

        // 서비스 호출
        Call<List<TagList>> call = service.createTagList(send_token, start, end);

        call.enqueue(new Callback<List<TagList>>() {
            @Override
            public void onResponse(Call<List<TagList>> call, Response<List<TagList>> response) {
                progressDialog.dismiss();
                List<TagList> tagList = response.body();
                adapter = new CalendarAdapter(context, dayList, curMonth, tagList, dayOfWeek);
                monthView.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<TagList>> call, Throwable t) {
                Log.e("CalendarFragment", "error===============" + t.getMessage());
            }
        });
    }

    // day 날짜 클릭시 넘어온 해당 날짜의 정보 YYYYMMDD -> Get Day list 에 전송하는 값

    private void setNetwork(String day) {
        Call<List<DayList>> call = service.createDayList(day, send_token);
        call.enqueue(new Callback<List<DayList>>() {
            @Override
            public void onResponse(Call<List<DayList>> call, Response<List<DayList>> response) {
                // 전송결과가 정상이면
                Log.e("Write", "in ====== onResponse");
                if (response.isSuccessful()) {
                    List<DayList> dayListBody = response.body();
                    if (dayListBody.size() != 0) {
                        customDialog = new CustomDialog(context, dayListBody);
                        customDialog.show();
                    } else {
                        Toast.makeText(context, "Nothing", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    int statusCode = response.code();
                    Log.i("CalendarFragment", "응답코드 ============= " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<List<DayList>> call, Throwable t) {
                Log.e("MyTag", "error===========" + t.getMessage());
            }
        });
    }
}