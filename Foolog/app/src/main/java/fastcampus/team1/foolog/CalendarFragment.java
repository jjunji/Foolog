package fastcampus.team1.foolog;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fastcampus.team1.foolog.Calendar.CalendarAdapter;
import fastcampus.team1.foolog.Dialog.CustomDialog;
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

    private TextView txtMonth;
    private GridView monthView;
    private CalendarAdapter adapter;
    View view;
    Typeface font;
    Context context;
    CustomDialog customDialog;
    String send_token;  // 통신에 필요한 헤더 값 (토큰)
    SharedPreferences storage;
    String shared_token;
    Button btnPrevious, btnNext;
    List<TagList> tagList = new ArrayList<>();

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

        Log.e("Dialog", "Token =====================" + send_token);
        initView();
        setAdapter();   // 어답터 연결
        setTxtMonth();  // 달력 상단 월 표시 텍스트
        setMonthViewClickListener();  // 그리드뷰의 한 아이템 클릭시 이벤트 정의
        setButton();  // 전 달, 다음 달 이동 버튼 정의

        return view;
    }

    private void initView() {
        storage = context.getSharedPreferences("storage", Activity.MODE_PRIVATE);
        shared_token = storage.getString("inputToken", " ");
        send_token = "Token " + shared_token;

        font = Typeface.createFromAsset(getActivity().getAssets(), "yaFontBold.ttf");
        adapter = new CalendarAdapter(context, send_token);

        btnPrevious = (Button) view.findViewById(R.id.btnPrevious);
        btnNext = (Button) view.findViewById(R.id.btnNext);

        txtMonth = (TextView) view.findViewById(R.id.txtMonth);

        monthView = (GridView) view.findViewById(R.id.monthView);
    }

    private void setAdapter() {
        monthView.setAdapter(adapter);
        adapter.setNowMonth();
    }

    // 전 달, 다음 달 이동 버튼
    private void setButton() {

        // 이전 월을 설정하고 그대로 표시됨.
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setPreviousMonth();
                setTxtMonth();
                //어댑터가 바뀌었으니 notifyDataSetChanged
                adapter.notifyDataSetChanged();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setNextMonth();
                setTxtMonth();
                adapter.notifyDataSetChanged();
            }
        });
    }

    // 폰트 적용 함수
    private void setTxtMonth() {
        txtMonth.setText(adapter.getCurrentYear() + "년" + adapter.getCurrentMonth() + "월");
        txtMonth.setTypeface(font);
    }

    // 달력의 한 아이템(날짜) 뷰 클릭시 이벤트 정의
    private void setMonthViewClickListener() {
        monthView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //String position = String.valueOf(i);
                //Toast.makeText(context, "position : " + position, Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, adapter.getDateList(position), Toast.LENGTH_SHORT).show();
                customDialog = new CustomDialog(context, adapter.getDateList(position));
                customDialog.show();
            }
        });
    }

    public void setNetwork(String send_token, String start, String end){
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
    }
}
