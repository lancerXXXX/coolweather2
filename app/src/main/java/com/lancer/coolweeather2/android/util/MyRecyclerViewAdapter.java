package com.lancer.coolweeather2.android.util;



import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lancer.coolweeather2.android.R;
import com.lancer.coolweeather2.android.Weather2;
import com.lancer.coolweeather2.android.db.cityWeatherString;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.Collections;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    public List<String> data;
    public List<cityWeatherString> cityWeatherStrings;
    public List<View> viewList;
    private RecyclerViewItemListener mrecyclerViewItemListener;

    public void setMrecyclerViewItemListener(RecyclerViewItemListener recyclerViewItemListener){
        mrecyclerViewItemListener=recyclerViewItemListener;
    }

    public MyRecyclerViewAdapter(List<String> data,List<cityWeatherString> cityWeatherStrings,List<View> viewList){
        this.cityWeatherStrings=cityWeatherStrings;
        this.data=data;
        this.viewList=viewList;
    }

    @NonNull
    @Override
    public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, int i) {
        View itemLayoutView=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view,null);
        ViewHolder viewHolder=new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mrecyclerViewItemListener!=null){
                    mrecyclerViewItemListener.onClick(view,viewHolder.getLayoutPosition());
                }
            }

        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(mrecyclerViewItemListener!=null){
                    mrecyclerViewItemListener.onLongClick(view,viewHolder.getLayoutPosition());
                    return true;
                }
                return false;
            }
        });


        viewHolder.cityname.setText(data.get(i)+" ");
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return data.size();

    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView cityname;
        public ViewHolder(View itemLayoutView){
            super(itemLayoutView);
            cityname=(TextView)itemLayoutView.findViewById(R.id.city_manager_text);
        }
    }
}
