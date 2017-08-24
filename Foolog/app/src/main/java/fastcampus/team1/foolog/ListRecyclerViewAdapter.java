package fastcampus.team1.foolog;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.List;
import fastcampus.team1.foolog.model.AllList;

/**
 * Created by jhjun on 2017-08-21.
 */

public class ListRecyclerViewAdapter extends RecyclerView.Adapter<ListRecyclerViewAdapter.ViewHolder>{
    List<AllList> allLists;
    AllList.Tag[] tag;
    String imageUrl;
    Context context;

    public ListRecyclerViewAdapter(List<AllList> allLists, Context context){
        this.allLists = allLists;
        this.context = context;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtText, txtFood, txtEval;
        ImageView imgFood;

        public ViewHolder(View v) {
            super(v);
            txtText = v.findViewById(R.id.txtText);
            txtFood = v.findViewById(R.id.txtFood);
            txtEval = v.findViewById(R.id.txtEval);
            imgFood = v.findViewById(R.id.imgFood);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtText.setText(allLists.get(position).text);
        setTag(position, holder);
        setImage(position, holder);
    }

    @Override
    public int getItemCount() {
        return allLists.size();
    }

    public void setTag(int position, ViewHolder holder){
        tag = allLists.get(position).tags;
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
        imageUrl = allLists.get(position).photo;
        Glide.with(context).load(imageUrl).into(holder.imgFood);
    }

}
