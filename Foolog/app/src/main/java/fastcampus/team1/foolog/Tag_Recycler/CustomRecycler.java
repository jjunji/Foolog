package fastcampus.team1.foolog.Tag_Recycler;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fastcampus.team1.foolog.Calendar.MonthItemView;
import fastcampus.team1.foolog.R;

/**
 * Created by jhjun on 2017-08-15.
 */

public class CustomRecycler extends RecyclerView.Adapter<CustomRecycler.Holder>{
    ArrayList<Data> datas;
    MonthItemView context;

    public CustomRecycler(ArrayList<Data> datas, MonthItemView context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag,parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Data data = datas.get(position);
        holder.setImage(data.resId);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        ImageView imgTag;

        public Holder(View itemView) {
            super(itemView);
            imgTag = (ImageView) itemView.findViewById(R.id.imgTag);
        }

        public void setImage(int resId){
            imgTag.setImageResource(resId);
        }
    }
}
