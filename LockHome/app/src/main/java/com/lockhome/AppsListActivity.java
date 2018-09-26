package com.lockhome;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lockhome.Adapter.AddAppAdapter;
import com.lockhome.Adapter.MenuAdpater;
import com.lockhome.Adapter.MenuPagerAdapter;
import com.lockhome.Others.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class AppsListActivity extends Activity {

    private PackageManager manager;
    ArrayList<AppData> apps = new ArrayList<AppData>();

    RecyclerView gdList;

    ListView list;

    Button btnCancel, btnOk;

    ArrayList<Integer> arrayOfImages = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();

    SharedPreferences pref;
    SharedPreferences.Editor editorPref;
    String savedApp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_apps_list);
        pref = getApplicationContext().getSharedPreferences("Apps", MODE_WORLD_WRITEABLE);
        savedApp = pref.getString("OffSidedata", "");

        init();
        loadApps();
        loadListView();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameData = "";

                try {
                    for (int i = 0; i < names.size(); i++) {
                        if (!names.get(i).equals("0")) {
                            nameData = nameData + names.get(i) + ", ";
                        }
                    }
                    nameData = nameData.substring(0, nameData.lastIndexOf(","));
                    editorPref = pref.edit();
                    editorPref.putString("OffSidedata", nameData.toString().trim());
                    editorPref.commit();

                    AppDrawer.activity.finish();
                    startActivity(new Intent(AppsListActivity.this, AppDrawer.class));
                    finish();
                } catch (Exception e) {
                    Toast.makeText(AppsListActivity.this, "Please Select at least on App", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    public void init() {

        list = (ListView) findViewById(R.id.list);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnOk = (Button) findViewById(R.id.btnOk);
    }


    private void loadApps() {
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

            apps.add(app);


            if (savedApp.contains(ri.activityInfo.packageName)) {
                names.add(ri.activityInfo.packageName);
                arrayOfImages.add(R.drawable.ic_check_box_black_24dp);
            } else {
                names.add("0");
                arrayOfImages.add(R.drawable.ic_check_box_outline_blank_black_24dp);
            }


        }

    }


    private void loadListView() {

//        ArrayAdapter<AppData> adapter = new ArrayAdapter<AppData>(this,
//                R.layout.list_item,
//                apps) {
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                if (convertView == null) {
//                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
//                }
//
//                ImageView appIcon = (ImageView) convertView.findViewById(R.id.item_app_icon);
//                appIcon.setImageDrawable(apps.get(position).icon);
//
//                TextView appLabel = (TextView) convertView.findViewById(R.id.item_app_label);
//                appLabel.setText(apps.get(position).label);
//
//                TextView appName = (TextView) convertView.findViewById(R.id.item_app_name);
//                appName.setText(apps.get(position).name);
//
//                return convertView;
//            }
//        };
//        list.setAdapter(adapter);

        AddAppAdapter adapter = new AddAppAdapter(this, apps, arrayOfImages, names);
        list.setAdapter(adapter);
    }


}
