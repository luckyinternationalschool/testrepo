package com.lockhome.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
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
import com.lockhome.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;

/**
 * Created by wscube on 21/4/16.
 */
public class AllFetch extends BroadcastReceiver {
    private static Timer timer = new Timer();
    private Context context;

    SharedPreferences pref;
    SharedPreferences.Editor editorPref;
    int result = 0;

    String savedOnlineApp = "",newOnlineApp="";


    String MasterPassword = "";


    PackageManager manager;
    String Domain = "";


    MediaPlayer fetchSound;


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("Service --->", "Start");
        pref = context.getSharedPreferences("Apps", context.MODE_WORLD_WRITEABLE);


        fetchSound = MediaPlayer.create(context, R.raw.coin);

        try {
            fetchSound.prepare();
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (CheckNetwork.isConnected(context))
            AppFetch();

        if (CheckNetwork.isConnected(context))
            PasswordFetch();

        if (CheckNetwork.isConnected(context))
            DomainFetch();

//        if (CheckNetwork.isConnected(context))
//            CheckTime();

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


    public void DomainFetch() {
        Domain = "";
        StringRequest request = new StringRequest(Request.Method.GET, Urls.ViewDomains, new Response.Listener<String>() {
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
                                String pckg = data.getString("domain_name");
                                if (Domain.equals("")) {
                                    Domain = pckg;
                                } else {
                                    Domain = Domain + ", " + pckg;
                                }
                            }

                            Log.d("Saved Domain --->", Domain);
                        }
                        else {

                        }
                    } catch (Exception e) {
                        Log.d("Problem", e.toString());
                    }
                }

                editorPref = pref.edit();
                editorPref.putString("Domain", Domain.toString().trim());
                editorPref.commit();

                //fetchSound.start();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        AppController.getInstance().addToRequestQueue(request, "View Domains");
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
                                // MainActivity.resetPreferredLauncherAndOpenChooser(context);
                            } else if (time.contains("8") && time.contains("am")) {
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
        AppController.getInstance().addToRequestQueue(request, "Check Time");
    }
}
