package fastcampus.team1.foolog;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import fastcampus.team1.foolog.Calendar.CalendarAdapter;
import fastcampus.team1.foolog.Dialog.CustomDialog;


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
        initView();

        return view;
    }

    private void initView() {
        font = Typeface.createFromAsset(getActivity().getAssets(), "yaFontBold.ttf");

        txtMonth = (TextView) view.findViewById(R.id.txtMonth);
        monthView = (GridView) view.findViewById(R.id.monthView);

        adapter = new CalendarAdapter(context);
        monthView.setAdapter(adapter);
        adapter.setNowMonth();
        setTxtMonth();

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


        Button btnPrevious = (Button) view.findViewById(R.id.btnPrevious);
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
        Button btnNext = (Button) view.findViewById(R.id.btnNext);
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
    private void setTxtMonth(){
        txtMonth.setText(adapter.getCurrentYear() + "년" + adapter.getCurrentMonth() + "월");
        txtMonth.setTypeface(font);
    }


}
