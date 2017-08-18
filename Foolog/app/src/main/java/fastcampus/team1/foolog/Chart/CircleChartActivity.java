package fastcampus.team1.foolog.Chart;


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

public class CircleChartActivity extends AppCompatActivity {


    private String TAG = "CircleChartActivity";
    private PieChart pieChart;
    private RelativeLayout relativeLayout;

    private float[] yData = {25.3f, 10.2f, 50.2f, 70.2f, 8.2f};
    private String[] xData = {"한식", "일식", "중식", "양식", "기타"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_circle_chart);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        pieChart = (PieChart) findViewById(R.id.pieChart);

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


}
