package com.lockhome.Services;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.text.Html;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lockhome.AppController;
import com.lockhome.MainActivity;
import com.lockhome.Others.CheckNetwork;
import com.lockhome.Others.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;

/**
 * Created by wscube on 21/4/16.
 */
public class TimeCheck extends BroadcastReceiver {
    private static Timer timer = new Timer();
    private Context context;

    int result = 0;


    private PackageManager manager;

    @Override
    public void onReceive(Context context, Intent intent) {


        this.context = context;

        Log.d("Service --->", "Check Start");


        if (CheckNetwork.isConnected(context))
            CheckTime();

    }


    public void CheckTime() {
        StringRequest request = new StringRequest(Request.Method.GET, Urls.ViewTime, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                if (s != null) {
                    s = Html.fromHtml(s).toString();
                    try {
                        JSONObject json = new JSONObject(s);
                        result = json.getInt("result");
                        if (result == 1) {
                            String time = json.getString("time2");

                            if (time.contains("8") && time.contains("pm")) {
                                context.getPackageManager().clearPackagePreferredActivities(context.getPackageName());
                                MainActivity.resetPreferredLauncherAndOpenChooser(context);
                            } else  if (time.contains("8") && time.contains("am")){
                                context.getPackageManager().clearPackagePreferredActivities(context.getPackageName());
                                Intent i = manager.getLaunchIntentForPackage("com.lockhome");
                                context.startActivity(i);
                            }

                        }
                    } catch (Exception e) {
                        Log.d("Problem", e.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        AppController.getInstance().addToRequestQueue(request, "View Apps");
    }


}
