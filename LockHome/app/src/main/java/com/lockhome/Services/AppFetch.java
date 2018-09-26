package com.lockhome.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.lockhome.AppController;
import com.lockhome.Others.CheckNetwork;
import com.lockhome.Others.GenerateID;
import com.lockhome.Others.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wscube on 21/4/16.
 */
public class AppFetch extends BroadcastReceiver {
    private static Timer timer = new Timer();
    private Context ctx;

    SharedPreferences pref;
    SharedPreferences.Editor editorPref;
    int result = 0;

    String savedOnlineApp = "";


    @Override
    public void onReceive(Context context, Intent intent) {


        Log.d("Service --->", "Start");

        pref = context.getSharedPreferences("Apps", context.MODE_WORLD_WRITEABLE);

        if (CheckNetwork.isConnected(context))
            AppFetch();

    }


    public void AppFetch() {
        savedOnlineApp = "";
        StringRequest request = new StringRequest(Request.Method.GET, Urls.ViewApps, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                if (s != null) {
                    s = Html.fromHtml(s).toString();
                    try {
                        JSONObject json = new JSONObject(s);
                        result = json.getInt("result");
                        if (result == 1) {
                            JSONArray jsonArray = json.getJSONArray("msg");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                String pckg = data.getString("package_name");
                                if (savedOnlineApp.equals("")) {
                                    savedOnlineApp = pckg;
                                } else {
                                    savedOnlineApp = savedOnlineApp + ", " + pckg;
                                }
                            }

                            Log.d("SavedOnlineApp  --->", savedOnlineApp);
                        }
                    } catch (Exception e) {
                        Log.d("Problem", e.toString());
                    }
                }

                editorPref = pref.edit();
                editorPref.putString("OnSidedata", savedOnlineApp.toString().trim());
                editorPref.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        AppController.getInstance().addToRequestQueue(request, "View Apps");
    }

}
