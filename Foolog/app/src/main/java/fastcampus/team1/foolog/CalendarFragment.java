package fastcampus.team1.foolog;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import fastcampus.team1.foolog.Calendar.CalendarAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private TextView txtMonth;
    private GridView monthView;
    private CalendarAdapter adapter;
    View view;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        initView();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initView() {
        txtMonth = (TextView) view.findViewById(R.id.txtMonth);
        monthView = (GridView) view.findViewById(R.id.monthView);
        adapter = new CalendarAdapter(view.getContext());
        monthView.setAdapter(adapter);
        adapter.setNowMonth();
        txtMonth.setText(adapter.getCurrentYear() + "년" + adapter.getCurrentMonth() + "월");

        Button btnPrevious = (Button) view.findViewById(R.id.btnPrevious);

        // 이전 월을 설정하고 그대로 표시됨.
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setPreviousMonth();
                txtMonth.setText(adapter.getCurrentYear() + "년" + adapter.getCurrentMonth() + "월");
                //어댑터가 바뀌었으니 notifyDataSetChanged
                adapter.notifyDataSetChanged();
            }
        });
        Button btnNext = (Button) view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.setNextMonth();
                txtMonth.setText(adapter.getCurrentYear() + "년" + adapter.getCurrentMonth() + "월");
                adapter.notifyDataSetChanged();
            }
        });

    }

}
