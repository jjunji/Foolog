package fastcampus.team1.foolog.Chart;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.CircleChartTotal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CircleChartActivity extends AppCompatActivity {


    private String TAG = "CircleChartActivity";
    private PieChart pieChart;
    private RelativeLayout relativeLayout;

    private float[] yData;
    private String[] xData;

    private float koreaTag, japanTag, chinaTag, usaTag, etcTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_chart);
        initView();

        //todo 임시 temp값 설정
        koreaTag = 5;
        japanTag = 6;
        chinaTag = 25;
        usaTag = 10;
        etcTag = 17;


        yData = new float[]{koreaTag, japanTag, chinaTag, usaTag, etcTag};
        xData = new String[]{"한식", "일식", "중식", "양식", "기타"};


        relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));

        pieChart.setUsePercentValues(true);
//        pieChart.setDescription();

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);


        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;
                //todo 이건 switch 문으로 나눠서 작업 [1] = 한식 , [2] 중식 ...


                Log.e(TAG, "xData[0]" + xData[0]);
                Log.e(TAG, "xData[1]" + xData[1]);
                Log.e(TAG, "xData[2]" + xData[2]);
                Log.e(TAG, "xData[3]" + xData[3]);
                Log.e(TAG, "xData[4]" + xData[4]);

                Log.e(TAG, "Entry==" + e.getY());
                Log.e(TAG, "Highlight" + h);

                switch ((int) e.getY()) {
                    // todo case로 위에있는 %를 받아와서 토슽 처리해준다
                }

                if (xData[0] == "한식") {
                    Toast.makeText(CircleChartActivity.this, xData[0] + "=" + e.getY(), Toast.LENGTH_SHORT).show();
                } else if (xData[1] == "중식") {
                    Toast.makeText(CircleChartActivity.this, xData[1] + "=" + e.getY(), Toast.LENGTH_SHORT).show();
                } else if (xData[2] == "일식") {
                    Toast.makeText(CircleChartActivity.this, xData[2] + "=" + e.getY(), Toast.LENGTH_SHORT).show();
                } else if (xData[3] == "양식") {
                    Toast.makeText(CircleChartActivity.this, xData[3] + "=" + e.getY(), Toast.LENGTH_SHORT).show();
                } else if (xData[4] == "기타") {
                    Toast.makeText(CircleChartActivity.this, xData[4] + "=" + e.getY(), Toast.LENGTH_SHORT).show();
                }

//                    Toast.makeText(CircleChartActivity.this, xData[2] + "=" + e.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });


        addData();


    }

    private void initView() {
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        pieChart = (PieChart) findViewById(R.id.pieChart);
    }

    private void addData() {
        Log.d(TAG, "addDataSet Started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

//        for (int i = 0; i < yData.length; i++) {
//            yEntrys.add(new PieEntry(yData[i],i));
//        }

        yEntrys.add(new PieEntry(yData[0], "한식"));
        yEntrys.add(new PieEntry(yData[1], "중식"));
        yEntrys.add(new PieEntry(yData[2], "일식"));
        yEntrys.add(new PieEntry(yData[3], "양식"));
        yEntrys.add(new PieEntry(yData[4], "기타"));


        for (int i = 0; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }

        // 데이터 넣기
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "태그");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setSelectionShift(5);

        //색깔 넣기
        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(getResources().getColor(R.color.color_korea));
        colors.add(getResources().getColor(R.color.color_japan));
        colors.add(getResources().getColor(R.color.color_china));
        colors.add(getResources().getColor(R.color.color_usa));
        colors.add(getResources().getColor(R.color.color_etc));

        pieDataSet.setColors(colors);


        // legend 넣기
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);


        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(11f);
        pieData.setValueTextColor(Color.GRAY);

        pieChart.setData(pieData);

        pieChart.highlightValue(null);
        pieChart.invalidate();

    }


    private void setTagData() {

        SharedPreferences storage = getSharedPreferences("storage", Activity.MODE_PRIVATE);
        String shared_token = storage.getString("inputToken", " ");

        String send_token = "Token " + shared_token;

        Log.e(TAG, "shared_token==========" + shared_token);
        Log.e(TAG, "send_token==========" + send_token);


        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("데이터를 불러오는 잠시만 기다려주세요...");
        progressDialog.show();


        // 레트로핏 정의
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.foolog.xyz/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // 인터페이스 불러오기
        iService service = retrofit.create(iService.class);


        Call<CircleChartTotal> getTagTotal = service.getTagTotal(send_token);
        getTagTotal.enqueue(new Callback<CircleChartTotal>() {
            @Override
            public void onResponse(Call<CircleChartTotal> call, Response<CircleChartTotal> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {



                } else {
                    int statusCode = response.code();
                    Log.i(TAG, "image 응답코드 ============= " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<CircleChartTotal> call, Throwable t) {
                progressDialog.dismiss();
            }
        });


    }


}
