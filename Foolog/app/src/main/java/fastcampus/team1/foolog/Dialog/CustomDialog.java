package fastcampus.team1.foolog.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fastcampus.team1.foolog.ListRecyclerViewAdapter;
import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.AllList;
import fastcampus.team1.foolog.model.DayList;
import fastcampus.team1.foolog.model.Delete;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CustomDialog extends Dialog {

    Context context;
    TextView txtDate;
    RecyclerView recyclerView;
    List<DayList> dayListBody = new ArrayList<>();
    CustomRecyclerViewAdapter adapter;
    String date, send_token;

    public CustomDialog(@NonNull Context context, List<DayList> dayListBody, String send_token) {
        super(context);
        this.context = context;
        this.dayListBody = dayListBody;
        this.send_token = send_token;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDialogWindow();
        setContentView(R.layout.activity_custom_dialog);
        init();
        setDate();
    }

    private void init(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new CustomRecyclerViewAdapter(dayListBody, context, send_token, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        txtDate = (TextView) findViewById(R.id.txtDate);
    }

    private void setDialogWindow(){
        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
    }

    public void setDate(){
        date = dayListBody.get(0).date;
        String dateSplit[];
        dateSplit = date.split(" ");
        txtDate.setText(dateSplit[0]);
    }

}


// TODO: 2017-08-11  아래와 같이 할 경우 왜 안되는지 & Glide.with(context) 의미
/*                    try {
                        URL url = new URL(imageUrl);
                        URLConnection con = url.openConnection();
                        con.connect();
                        BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                        Bitmap bm = BitmapFactory.decodeStream(bis);
                        bis.close();
                        imgFood.setImageBitmap(bm);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
*/