package com.lockhome.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lockhome.AppData;
import com.lockhome.R;

import java.util.ArrayList;

public class AddAppAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Activity context;


    ArrayList<AppData> arrayOfData = new ArrayList<>();

    ArrayList<String> names = new ArrayList<>();
    ArrayList<Integer> arrayOfImages = new ArrayList<>();


    public AddAppAdapter(Activity context, ArrayList<AppData> arrayOfData, ArrayList<Integer> arrayOfImages, ArrayList<String> name) {
        this.context = context;
        this.arrayOfData = arrayOfData;
        this.names = name;
        this.arrayOfImages = arrayOfImages;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayOfData.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generatedString url="";

        return 0;
    }

    @Override
    public View getView(final int position, View ConvertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View ivu = inflater.inflate(R.layout.list_item, parent, false);


        RelativeLayout rlmain= (RelativeLayout) ivu.findViewById(R.id.main);
        ImageView appIcon = (ImageView) ivu.findViewById(R.id.item_app_icon);
        TextView appLabel = (TextView) ivu.findViewById(R.id.item_app_label);
        TextView appName = (TextView) ivu.findViewById(R.id.item_app_name);
        ImageView imgCheck = (ImageView) ivu.findViewById(R.id.imgCheck);

        imgCheck.setImageResource(arrayOfImages.get(position));
        appIcon.setImageDrawable(arrayOfData.get(position).getIcon());
        appLabel.setText(arrayOfData.get(position).getLabel());
        appName.setText(arrayOfData.get(position).getName());


        rlmain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayOfImages.get(position) == R.drawable.ic_check_box_outline_blank_black_24dp) {
                    arrayOfImages.set(position, R.drawable.ic_check_box_black_24dp);
                    names.set(position, arrayOfData.get(position).getName().toString().trim());
                    notifyDataSetChanged();
                } else {
                    arrayOfImages.set(position, R.drawable.ic_check_box_outline_blank_black_24dp);
                    names.set(position, "0");
                    notifyDataSetChanged();
                }
            }
        });


        return ivu;
    }


}
