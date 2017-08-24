package fastcampus.team1.foolog.Dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fastcampus.team1.foolog.R;
import fastcampus.team1.foolog.iService;
import fastcampus.team1.foolog.model.DayList;
import fastcampus.team1.foolog.model.Delete;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private static final int TYPE_HEADER = 0;
    private static final int TYPE_FOOTER = 0;
    private static final int TYPE_ITEM = 1;

    CustomDialog dialog;
    List<DayList> dayListBody = new ArrayList<>();
    String imageUrl;
    Context context;
    DayList.Tag[] tag; // 태그 값 -> json 배열
    iService service = null;
    String send_token;
    RefreshCallback callback;

    public CustomRecyclerViewAdapter(List<DayList> dayListBody, Context context, String send_token, CustomDialog dialog, RefreshCallback callback) {
        this.dialog = dialog;
        this.dayListBody = dayListBody;
        this.context = context;
        this.send_token = send_token;
        this.callback = callback;
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
                    Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    callback.refresh();
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

    public interface RefreshCallback{
        public void refresh();
    }

}
