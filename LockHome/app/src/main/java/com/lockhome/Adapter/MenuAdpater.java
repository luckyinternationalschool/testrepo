package com.lockhome.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lockhome.AppData;
import com.lockhome.AppsListActivity;
import com.lockhome.R;

import java.util.ArrayList;


/**
 * Created by wscube on 8/3/16.
 */
public class MenuAdpater extends RecyclerView.Adapter<MenuAdpater.DataObjectHolder> {

    Context context;

    PackageManager manager;


    ArrayList<AppData> apps = new ArrayList<AppData>();

    public static RelativeLayout main;

    public MenuAdpater(Context context, ArrayList<AppData> apps) {
        this.context = context;
        this.apps = apps;
    }


    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_menu, parent, false);


        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }


    @Override
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {


        holder.img.setImageDrawable(apps.get(position).getIcon());
        holder.txtTitle.setText(apps.get(position).getLabel());


//        main.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = manager.getLaunchIntentForPackage(apps.get(position).getName().toString());
//                context.startActivity(i);
//            }
//        });


    }


    public static class DataObjectHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView img;


        public DataObjectHolder(View itemView) {
            super(itemView);
            txtTitle = (TextView) itemView.findViewById(R.id.txtName);
            img = (ImageView) itemView.findViewById(R.id.img);

            main = (RelativeLayout) itemView.findViewById(R.id.mainlayout);

        }
    }
}
