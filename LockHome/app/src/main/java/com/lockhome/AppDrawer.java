package com.lockhome;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lockhome.Adapter.MenuAdpater;
import com.lockhome.Adapter.NumberAdpater;
import com.lockhome.Adapter.WifiAdapter;
import com.lockhome.Others.CheckNetwork;
import com.lockhome.Others.MyDialog;
import com.lockhome.Others.RecyclerTouchListener;
import com.lockhome.Others.Urls;
import com.lockhome.WifiUtils.WifiActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AppDrawer extends Activity {


    EditText edtSearch;
    TextView txt;
    ListView list;
    RecyclerView gdList;
    ImageView btnAdd, btnSync, btnWifi;

    private PackageManager manager;
    ArrayList<AppData> apps = new ArrayList<AppData>();
    ArrayList<AppData> arrayOfSearchData = new ArrayList<AppData>();
    ArrayList<String> arrayList = new ArrayList<>();


    SharedPreferences pref;
    SharedPreferences.Editor editorPref;
    public static Activity activity;
    Animation animWromg;


    String savedOfflineApp = "", savedOnlineApp = "";
    String data = "", hardPassword = "wscubetech@123!", savedPassword = "lishome@123!", masterPassword = "";

    WifiManager wifiManager;
    WifiReceiver receiverWifi;
    List<ScanResult> wifiList;
    List<WifiConfiguration> configwifiList;
    StringBuilder sb = new StringBuilder();


    String MasterPassword = "";
    String newDomain = "";

    String newsavedOnlineApp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_drawer);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        pref = getSharedPreferences("Apps", Context.MODE_WORLD_WRITEABLE);
        savedOfflineApp = pref.getString("OffSidedata", "");
        savedOnlineApp = pref.getString("OnSidedata", "");
        masterPassword = pref.getString("MasterPassword", "");

        Log.d("passsssssssss", masterPassword);

        activity = AppDrawer.this;

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        btnAdd = (ImageView) findViewById(R.id.addApps);
        btnSync = (ImageView) findViewById(R.id.sync);
        btnWifi = (ImageView) findViewById(R.id.wifi);
        gdList = (RecyclerView) findViewById(R.id.list);
        gdList.setLayoutManager(new GridLayoutManager(AppDrawer.this, 5));

        loadApps();

        btnAdd.bringToFront();
        btnSync.bringToFront();
        btnWifi.bringToFront();

        animWromg = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake);


        gdList.addOnItemTouchListener(new RecyclerTouchListener(AppDrawer.this, gdList, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

//                if (position == arrayOfSearchData.size() - 1) {
//                    Intent i = new Intent(AppDrawer.this, WebActivity.class);
//                    startActivity(i);
//                } else {
                    try {
                        Intent i = manager.getLaunchIntentForPackage(arrayOfSearchData.get(position).getName().toString());
                        AppDrawer.this.startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                //}


            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckNetwork.isConnected(AppDrawer.this)) {
                    AppFetch();
                    DomainFetch();
                    PasswordFetch();
                    Toast.makeText(AppDrawer.this, "Please wait! Sync in progress", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(AppDrawer.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

            }
        });


        btnWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWifiDialog();
//                startActivity(new Intent(AppDrawer.this, WifiActivity.class));
            }
        });


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                arrayOfSearchData.clear();
                String str = editable.toString().trim().toLowerCase();
                for (int j = 0; j < apps.size(); j++) {
                    if (apps.get(j).getLabel().toString().trim().toLowerCase().contains(str)) {
                        arrayOfSearchData.add(apps.get(j));
                    }
                }
                MenuAdpater adpater = new MenuAdpater(AppDrawer.this, arrayOfSearchData);
                gdList.setAdapter(adpater);
            }
        });
    }

    private void loadApps() {

        apps.clear();

        manager = getPackageManager();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

        for (ResolveInfo ri : availableActivities) {

            AppData app = new AppData();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            Log.d("Package Name --->", ri.activityInfo.packageName);
            app.icon = ri.activityInfo.loadIcon(manager);
            //apps.add(app);

            app.setIcon(ri.activityInfo.loadIcon(manager));
            app.setLabel(ri.loadLabel(manager));
            app.setName(ri.activityInfo.packageName);


            // if (ri.activityInfo.packageName.contains(".browser") || ri.activityInfo.packageName.contains(".setting")) {
            if (savedOfflineApp.contains(ri.activityInfo.packageName) || savedOnlineApp.contains(ri.activityInfo.packageName)) {
                apps.add(app);
                arrayOfSearchData.add(app);
            }

        }

//        AppData app = new AppData();
//        app.setIcon(getResources().getDrawable(R.drawable.ic_custom_web));
//        app.setLabel("LIS Browser");
//        app.setName("LIS Browser");
//        apps.add(app);
//        arrayOfSearchData.add(app);

        MenuAdpater adpater = new MenuAdpater(AppDrawer.this, arrayOfSearchData);
        gdList.setAdapter(adpater);
    }

    public void showDialog() {
        final Dialog dialog = new MyDialog(AppDrawer.this).getMyDialog(R.layout.dialog_lock);
        dialog.setCancelable(true);

        addArray();

        final EditText edt = (EditText) dialog.findViewById(R.id.edt);
        final LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.ll);
        final ImageView i1 = (ImageView) dialog.findViewById(R.id.i1);
        final ImageView i2 = (ImageView) dialog.findViewById(R.id.i2);
        final ImageView i3 = (ImageView) dialog.findViewById(R.id.i3);
        final ImageView i4 = (ImageView) dialog.findViewById(R.id.i4);
        final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
        final TextView txtMsg = (TextView) dialog.findViewById(R.id.txtMsg);

        txtMsg.setText("Please Insert Wifi Password");


        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(AppDrawer.this, 3));
        recyclerView.setAdapter(new NumberAdpater(arrayList));

        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


//                if (s.length() == 0) {
//                    i1.setImageResource(R.drawable.dot);
//                    i2.setImageResource(R.drawable.dot);
//                    i3.setImageResource(R.drawable.dot);
//                    i4.setImageResource(R.drawable.dot);
//                } else if (s.length() == 1) {
//                    i1.setImageResource(R.drawable.dot_selected);
//                    i2.setImageResource(R.drawable.dot);
//                    i3.setImageResource(R.drawable.dot);
//                    i4.setImageResource(R.drawable.dot);
//                } else if (s.length() == 2) {
//                    i1.setImageResource(R.drawable.dot_selected);
//                    i2.setImageResource(R.drawable.dot_selected);
//                    i3.setImageResource(R.drawable.dot);
//                    i4.setImageResource(R.drawable.dot);
//                } else if (s.length() == 3) {
//                    i1.setImageResource(R.drawable.dot_selected);
//                    i2.setImageResource(R.drawable.dot_selected);
//                    i3.setImageResource(R.drawable.dot_selected);
//                    i4.setImageResource(R.drawable.dot);
//                } else if (s.length() == 4) {
//                    i1.setImageResource(R.drawable.dot_selected);
//                    i2.setImageResource(R.drawable.dot_selected);
//                    i3.setImageResource(R.drawable.dot_selected);
//                    i4.setImageResource(R.drawable.dot_selected);
//
//                    Log.d("Entered PAsss", s.toString());
//                    Log.d("Saved PAsss", savedPassword.toString());
//
//
//                    if (savedPassword.equals(s.toString()) || masterPassword.equals(s.toString())) {
//                        data = "";
//                        startActivity(new Intent(AppDrawer.this, AppsListActivity.class));
//                        dialog.dismiss();
//                    } else {
//                        edt.setText("");
//                        data = "";
//                        ll.startAnimation(animWromg);
//                    }
//                }
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edt.getText().toString().trim().toLowerCase();

                if (savedPassword.equals(pass.toLowerCase()) || hardPassword.equals(pass.toLowerCase()) || masterPassword.equals(pass.toLowerCase())) {
                    startActivity(new Intent(AppDrawer.this, AppsListActivity.class));
                    dialog.dismiss();
                } else {
                    edt.setText("");
                    edt.startAnimation(animWromg);
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(AppDrawer.this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (position < 9) {
                    int total = position + 1;
                    data = data + total;
                    edt.setText(data);
                } else if (position == 10) {
                    int total = 0;
                    data = data + total;
                    edt.setText(data);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        dialog.show();
    }


    public void addArray() {

        arrayList.clear();

        for (int i = 0; i < 10; i++) {
            if (i != 0)
                arrayList.add(i + "");
        }

        arrayList.add(" ");
        arrayList.add("0");
        arrayList.add("Ok");
    }


    public void showWifiDialog() {

        final Dialog dialog = new MyDialog(AppDrawer.this).getMyDialog(R.layout.dialog_wifi);

        final ImageView btnOnOff = (ImageView) dialog.findViewById(R.id.btn);
        final TextView btnscan = (TextView) dialog.findViewById(R.id.btnscan);
        txt = (TextView) dialog.findViewById(R.id.txt);
        list = (ListView) dialog.findViewById(R.id.list);

        wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        list.bringToFront();

        if (wifiManager.isWifiEnabled()) {
            btnOnOff.setImageResource(R.drawable.ic_active);
            wifiManager.startScan();
            receiverWifi = new WifiReceiver();
            registerReceiver(receiverWifi, new IntentFilter(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
            txt.setText("\nStarting Scan...\n");
            btnscan.setVisibility(View.VISIBLE);
            list.setVisibility(View.VISIBLE);
        } else {
            btnOnOff.setImageResource(R.drawable.ic_deactive);
            btnscan.setVisibility(View.GONE);
            list.setVisibility(View.GONE);
        }


        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (wifiManager.isWifiEnabled()) {
                    btnOnOff.setImageResource(R.drawable.ic_deactive);
                    wifiManager.setWifiEnabled(false);
                    btnscan.setVisibility(View.GONE);
                    txt.setText("");
                    list.setVisibility(View.GONE);
                } else {
                    btnOnOff.setImageResource(R.drawable.ic_active);
                    wifiManager.setWifiEnabled(true);
                    btnscan.setVisibility(View.VISIBLE);

                    wifiManager.startScan();
                    receiverWifi = new WifiReceiver();
                    registerReceiver(receiverWifi, new IntentFilter(
                            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                    wifiManager.startScan();
                    txt.setText("\nStarting Scan...\n");

                    list.setVisibility(View.VISIBLE);

                }

            }
        });

        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifiManager.startScan();
                receiverWifi = new WifiReceiver();
                registerReceiver(receiverWifi, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();
                txt.setText("\nStarting Scan...\n");

            }
        });


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (wifiList.get(position).capabilities.contains("WEP")) {
                    showConnectionDialog(wifiList.get(position).SSID);
                } else if (wifiList.get(position).capabilities.contains("PSK")) {
                    showConnectionDialog(wifiList.get(position).SSID);
                } else if (wifiList.get(position).capabilities.contains("EAP")) {
                    showConnectionDialog(wifiList.get(position).SSID);
                } else if (wifiList.get(position).capabilities.contains("WPA")) {
                    showConnectionDialog(wifiList.get(position).SSID);
                } else {
                    WifiConfiguration wifiConfiguration = new
                            WifiConfiguration();
                    wifiConfiguration.SSID = String.format("\"%s\"", wifiList.get(position).SSID);
                    //wifiConfiguration.preSharedKey = String.format("\"%s\"", pass);

                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                    WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                    int netId = wifiManager.addNetwork(wifiConfiguration);

                    if (wifiManager.isWifiEnabled()) { //---wifi is turned on---
                        //---disconnect it first---
                        wifiManager.disconnect();
                    } else { //---wifi is turned off---
                        //---turn on wifi---
                        wifiManager.setWifiEnabled(true);
                    }
                    wifiManager.enableNetwork(netId, true);
                    wifiManager.reconnect();
                }


                wifiManager.startScan();
                receiverWifi = new WifiReceiver();
                registerReceiver(receiverWifi, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();


            }
        });

        dialog.show();
    }

    public void showConnectionDialog(final String SSID) {
        final Dialog Connectiondialog = new MyDialog(AppDrawer.this).getMyDialog(R.layout.dialog_lock);
        Connectiondialog.setCancelable(true);

        final EditText edt = (EditText) Connectiondialog.findViewById(R.id.edt);
        final Button btnOk = (Button) Connectiondialog.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edt.getText().toString().trim().toLowerCase();

                WifiConfiguration wifiConfiguration = new
                        WifiConfiguration();
                wifiConfiguration.SSID = String.format("\"%s\"", SSID);
                wifiConfiguration.preSharedKey = String.format("\"%s\"", pass);

                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                int netId = wifiManager.addNetwork(wifiConfiguration);

                if (wifiManager.isWifiEnabled()) { //---wifi is turned on---
                    //---disconnect it first---
                    wifiManager.disconnect();
                } else { //---wifi is turned off---
                    //---turn on wifi---
                    wifiManager.setWifiEnabled(true);
                }
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();


                wifiManager.startScan();
                receiverWifi = new WifiReceiver();
                registerReceiver(receiverWifi, new IntentFilter(
                        WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();

                Connectiondialog.dismiss();

            }
        });
        Connectiondialog.show();
    }


    class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            sb = new StringBuilder();

            wifiList = wifiManager.getScanResults();


            configwifiList = wifiManager.getConfiguredNetworks();

            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            String connectedNetwork = info.getSSID();


            WifiAdapter adapter = new WifiAdapter(AppDrawer.this, wifiList, configwifiList, connectedNetwork);
            list.setAdapter(adapter);

            for (int i = 0; i < wifiList.size(); i++) {
                sb.append(new Integer(i + 1).toString() + ".");
                sb.append((wifiList.get(i)).toString());
                sb.append("\n");
            }
            txt.setText("");
        }
    }


    public void AppFetch() {
        newsavedOnlineApp = "";
        StringRequest request = new StringRequest(Request.Method.GET, Urls.ViewApps, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                if (s != null) {
                    s = Html.fromHtml(s).toString();
                    try {
                        JSONObject json = new JSONObject(s);
                        int result = json.getInt("result");
                        if (result == 1) {
                            JSONArray jsonArray = json.getJSONArray("msg");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                String pckg = data.getString("package_name");


                                if (newsavedOnlineApp.equals("")) {
                                    newsavedOnlineApp = pckg;
                                } else {
                                    newsavedOnlineApp = newsavedOnlineApp + ", " + pckg;
                                }
                            }

                            Log.d("SavedOnlineApp  --->", newsavedOnlineApp);
                        }
                    } catch (Exception e) {
                        Log.d("Problem", e.toString());
                    }
                }

                editorPref = pref.edit();
                editorPref.putString("OnSidedata", newsavedOnlineApp.toString().trim());
                editorPref.commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        AppController.getInstance().addToRequestQueue(request, "View Apps");
    }


    public void DomainFetch() {
        newDomain = "";
        StringRequest request = new StringRequest(Request.Method.GET, Urls.ViewDomains, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                if (s != null) {
                    s = Html.fromHtml(s).toString();
                    try {
                        JSONObject json = new JSONObject(s);
                        int result = json.getInt("result");
                        if (result == 1) {
                            JSONArray jsonArray = json.getJSONArray("msg");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                String pckg = data.getString("domain_name");
                                if (newDomain.equals("")) {
                                    newDomain = pckg;
                                } else {
                                    newDomain = newDomain + ", " + pckg;
                                }
                            }

                            Log.d("Saved Domain --->", newDomain);
                        } else {

                        }
                    } catch (Exception e) {
                        Log.d("Problem", e.toString());
                    }
                }

                editorPref = pref.edit();
                editorPref.putString("Domain", newDomain.toString().trim());
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
                        int result = json.getInt("result");
                        if (result == 1) {
                            JSONArray jsonArray = json.getJSONArray("msg");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);
                                String password = data.getString("master_password");
                                MasterPassword = password;
                                masterPassword = password;
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
