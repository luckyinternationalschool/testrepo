package com.lockhome.Adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lockhome.AppData;
import com.lockhome.R;

import java.util.ArrayList;


/**
 * Created by wscube on 8/3/16.
 */
public class NumberAdpater extends RecyclerView.Adapter<NumberAdpater.DataObjectHolder> {

    // Context context;

    PackageManager manager;


    ArrayList<String> array = new ArrayList<String>();

    public static RelativeLayout main;

    public NumberAdpater(ArrayList<String> apps) {
        // this.context = context;
        this.array = apps;
    }


    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_lock, parent, false);


        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public int getItemCount() {
        return array.size();
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {


        holder.txtTitle.setText(array.get(position));

        if (position == 9 || position == 11 ) {
            main.setVisibility(View.GONE);
        }


        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }


    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;


        public DataObjectHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txt);

            main = (RelativeLayout) itemView.findViewById(R.id.mainlayout);

        }
    }
}
