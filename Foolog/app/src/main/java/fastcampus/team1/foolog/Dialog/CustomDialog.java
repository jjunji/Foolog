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
        adapter = new CustomRecyclerViewAdapter(dayListBody, context, send_token);
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

class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    List<DayList> dayListBody = new ArrayList<>();
    String imageUrl;
    Context context;
    DayList.Tag[] tag; // 태그 값 -> json 배열
    iService service = null;
    String send_token;

    public CustomRecyclerViewAdapter(List<DayList> dayListBody, Context context, String send_token) {
        this.dayListBody = dayListBody;
        this.context = context;
        this.send_token = send_token;
        initNetwork();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            //Inflating footer view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_footer, parent, false);
            return new FooterViewHolder(itemView);
        }else if (viewType == TYPE_ITEM) {
            //Inflating recycle view item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dialog_item, parent, false);
            return new ItemViewHolder(itemView);
        } return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof FooterViewHolder) {
            if(position == dayListBody.size()+1){
                FooterViewHolder footerHolder = (FooterViewHolder) holder;
                footerHolder.footerText.setText("Footer View");
            }
        } else if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            //itemViewHolder.itemText.setText("Recycler Item " + position);
            if(position < dayListBody.size()) {
                itemViewHolder.txtText.setText(dayListBody.get(position).text);
                setTag(position, itemViewHolder);
                setImage(position, itemViewHolder);
                itemViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public boolean onLongClick(View view) {
                        Log.e("CustomRecyclerViewAdapter", "LongClickPosition==========="+dayListBody.get(position).pk);
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Post 삭제");
                        builder.setMessage("삭제 하시겠습니까?");
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.i("Adapter", "i==================="+i);
                                setNetwork(dayListBody.get(position).pk);
                            }
                        });
                        builder.show();
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == dayListBody.size()) {
            return TYPE_FOOTER;
        }else{
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return dayListBody.size()+1;
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView footerText;

        public FooterViewHolder(View view) {
            super(view);
            footerText = (TextView) view.findViewById(R.id.footer_text);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView txtDate,txtText, txtPlace;  // 다이얼로그의 각 위젯
        TextView txtFood, txtEval; // 태그 표현 위젯
        ImageView imgFood;  // 다이얼로그 위젯 - 이미지뷰
        String memo;  // 서버로 부터 받은 날짜, 메모

        public ItemViewHolder(View v) {
            super(v);
            txtText = (TextView) v.findViewById(R.id.txtText);
            txtFood = (TextView) v.findViewById(R.id.txtFood);
            txtEval = (TextView) v.findViewById(R.id.txtEval);
            imgFood = (ImageView) v.findViewById(R.id.imgFood);
        }
    }
    public void setTag(int position, ItemViewHolder holder){
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

    public void setImage(int position, ItemViewHolder holder){
        imageUrl = dayListBody.get(position).photo;
        Glide.with(context).load(imageUrl).into(holder.imgFood);
    }

    private void initNetwork(){
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

    private void setNetwork(int pk){
        Call<Delete> call = service.deletePost(send_token, (pk+""));
        call.enqueue(new Callback<Delete>() {
            @Override
            public void onResponse(Call<Delete> call, Response<Delete> response) {
                // 전송결과가 정상이면
                Log.e("Write","in ====== onResponse");
                if(response.isSuccessful()){
                    Toast.makeText(context, "good", Toast.LENGTH_SHORT).show();
                }else{
                    int statusCode = response.code();
                    Log.i("ShowListFragment", "응답코드 ============= " + statusCode);
                }
            }

            @Override
            public void onFailure(Call<Delete> call, Throwable t) {
                Log.e("MyTag","error==========="+t.getMessage());
            }
        });
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