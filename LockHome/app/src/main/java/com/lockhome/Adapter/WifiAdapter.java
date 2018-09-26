package com.lockhome.Adapter;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lockhome.AppData;
import com.lockhome.R;

import java.util.ArrayList;
import java.util.List;

public class WifiAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Activity context;

    String connectedNetwork = "";

    List<ScanResult> arrayOfData = new ArrayList<>();


    List<WifiConfiguration> configwifiList = new ArrayList<>();


    public WifiAdapter(Activity context, List<ScanResult> arrayOfData, List<WifiConfiguration> arrayOfConfigData, String connectedNetwork) {
        this.context = context;
        this.arrayOfData = arrayOfData;
        this.connectedNetwork = connectedNetwork;
        this.configwifiList = arrayOfConfigData;
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

        View ivu = inflater.inflate(R.layout.wifi_item, parent, false);


        ImageView imgLock = (ImageView) ivu.findViewById(R.id.imgLock);
        TextView appLabel = (TextView) ivu.findViewById(R.id.item_label);
        TextView status = (TextView) ivu.findViewById(R.id.status);


        //Toast.makeText(context, connectedNetwork, Toast.LENGTH_SHORT).show();

        appLabel.setText(arrayOfData.get(position).SSID.toString().trim());


        if (connectedNetwork.contains(arrayOfData.get(position).SSID)) {
            status.setVisibility(View.VISIBLE);
        } else {
            status.setVisibility(View.GONE);
        }


        if (arrayOfData.get(position).capabilities.contains("WEP")) {
            imgLock.setVisibility(View.VISIBLE);
        } else if (arrayOfData.get(position).capabilities.contains("PSK")) {
            imgLock.setVisibility(View.VISIBLE);
        } else if (arrayOfData.get(position).capabilities.contains("EAP")) {
            imgLock.setVisibility(View.VISIBLE);
        } else if (arrayOfData.get(position).capabilities.contains("WPA")) {
            imgLock.setVisibility(View.VISIBLE);
        } else {
            imgLock.setVisibility(View.GONE);
        }


        return ivu;
    }


}
