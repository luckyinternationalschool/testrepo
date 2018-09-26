package com.lockhome.Services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lockhome.AppController;
import com.lockhome.Others.CheckNetwork;
import com.lockhome.Others.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wscube on 21/4/16.
 */
public class MasterPasswordFetch extends BroadcastReceiver {

    Context context;

    SharedPreferences pref;
    SharedPreferences.Editor editorPref;
    int result = 0;

  String MasterPassword = "";

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;


        pref = context.getSharedPreferences("Apps", context.MODE_WORLD_WRITEABLE);

        if (CheckNetwork.isConnected(context))
            PasswordFetch();

    }


    public void PasswordFetch() {
        MasterPassword = "";
        StringRequest request = new StringRequest(Request.Method.GET, Urls.ViewMasterPassword, new Response.Listener<String>() {
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
                                String password = data.getString("master_password");
                                MasterPassword = password;
                            }

                            Log.d("MasterPassword --->", MasterPassword);
                        }
                    } catch (Exception e) {
                        Log.d("Problem", e.toString());
                    }
                }

                editorPref = pref.edit();
                editorPref.putString("MasterPassword", MasterPassword.toString().trim());
                editorPref.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        AppController.getInstance().addToRequestQueue(request, "View MasterPassword");
    }
}
