package com.lockhome.Services;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.lockhome.LockScreen;
import com.lockhome.Others.CustomViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by wscube on 21/4/16.
 */
public class BlockApp extends BroadcastReceiver {

    String CURRENT_PACKAGE_NAME = "lockhome";
    String lastAppPN = "";
    boolean noDelay = false;

    Context context;

    SharedPreferences pref;
    SharedPreferences.Editor editorPref;

    String mPackageName = "";
    String AppList = "";

    String status = "";


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;


        pref = context.getSharedPreferences("Apps", context.MODE_WORLD_WRITEABLE);

        status = pref.getString("status", "");

        AppList = pref.getString("OnSidedata", "");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        printForegroundTask();
        // Do something for lollipop and above versions
//        } else {
//            // do something for phones running an SDK before lollipop
//            checkRunningApps();
//        }

        CURRENT_PACKAGE_NAME = context.getPackageName();

    }

    private void printForegroundTask() {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        ActivityManager mActivityManager2 = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
        } else {
            mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }


        if (mPackageName.contains("com.wscubetech.lishome")) {
            editorPref = pref.edit();
            editorPref.putString("status", "1");
            editorPref.commit();
        } else if (mPackageName.contains("com.android.launcher")) {
            editorPref = pref.edit();
            editorPref.putString("status", "0");
            editorPref.commit();
        } else if (mPackageName.contains("android")) {
            editorPref = pref.edit();
            editorPref.putString("status", "0");
            editorPref.commit();
        }

        //&& !mPackageName.contains("setting")

        Log.e("TAGGGGG", "Current App in foreground is: " + mPackageName);

        if (!AppList.contains(mPackageName) && !mPackageName.contains(context.getPackageName()) && !mPackageName.contains("launcher") && !mPackageName.contains("system:ui") && !mPackageName.equals("android")) {
            if (status.equals("1")) {
                Intent intent = new Intent(context, LockScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                mActivityManager.killBackgroundProcesses(mPackageName);
            }
        }
    }


}
