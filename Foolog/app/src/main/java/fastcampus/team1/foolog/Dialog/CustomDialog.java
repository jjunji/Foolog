package fastcampus.team1.foolog.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.model.DayList;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CustomDialog extends Dialog {

    Context context;
    TextView txtDate;
    RecyclerView recyclerView;
    List<DayList> dayListBody = new ArrayList<>();
    CustomRecyclerViewAdapter adapter;
    String date;

    public CustomDialog(@NonNull Context context, List<DayList> dayListBody) {
        super(context);
        this.context = context;
        this.dayListBody = dayListBody;
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
        adapter = new CustomRecyclerViewAdapter(dayListBody, context);
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

class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.ViewHolder>{

    List<DayList> dayListBody = new ArrayList<>();
    String imageUrl;
    Context context;
    DayList.Tag[] tag; // 태그 값 -> json 배열

    public CustomRecyclerViewAdapter(List<DayList> dayListBody, Context context){
        this.dayListBody = dayListBody;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtDate,txtText, txtPlace;  // 다이얼로그의 각 위젯
        TextView txtFood, txtEval; // 태그 표현 위젯
        ImageView imgFood;  // 다이얼로그 위젯 - 이미지뷰
        String memo;  // 서버로 부터 받은 날짜, 메모

        public ViewHolder(View v) {
            super(v);
            txtText = (TextView) v.findViewById(R.id.txtText);
            txtFood = (TextView) v.findViewById(R.id.txtFood);
            txtEval = (TextView) v.findViewById(R.id.txtEval);
            imgFood = (ImageView) v.findViewById(R.id.imgFood);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dialog_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtText.setText(dayListBody.get(position).text);
        setTag(position, holder);
        setImage(position, holder);
    }

    @Override
    public int getItemCount() {
        return dayListBody.size();
    }

    public void setTag(int position, ViewHolder holder){
        tag = dayListBody.get(position).tags;
        String foodColor = tag[0].color;
        String foodText = tag[0].text;
        String evalColor = tag[1].color;
        String evalText = tag[1].text;

        holder.txtFood.setText(foodText);
        holder.txtFood.setTextColor(Color.parseColor(foodColor));
        holder.txtEval.setText(evalText);
        holder.txtEval.setTextColor(Color.parseColor(evalColor));
    }

    public void setImage(int position, ViewHolder holder){
        imageUrl = dayListBody.get(position).photo;
        Glide.with(context).load(imageUrl).into(holder.imgFood);
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