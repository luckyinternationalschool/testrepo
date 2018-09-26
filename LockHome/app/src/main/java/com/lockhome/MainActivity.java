package com.lockhome;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lockhome.Adapter.NumberAdpater;
import com.lockhome.Others.CustomViewGroup;
import com.lockhome.Others.DeviceAdminReciever;
import com.lockhome.Others.GenerateID;
import com.lockhome.Others.MyDialog;
import com.lockhome.Others.RecyclerTouchListener;
import com.lockhome.Services.AllFetch;
import com.lockhome.Services.BlockApp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    private PackageManager manager;
    private List<AppData> apps;

    private ListView list;

    ImageView app1, app2, app3, app4, app_icon, appWeb, appLock;

    Drawable drawable1, drawable2, drawable3, drawable4;

    String package1, package2, package3, package4;

    ArrayList<String> arrayList = new ArrayList<>();

    String data = "", hardPassword = "wscubetech@123!", savedPassword = "lishome@123!", masterPassword = "";

    SharedPreferences pref;
    Animation animWromg;


    // To keep track of activity's window focus
    boolean currentFocus;

    // To keep track of activity's foreground/background status
    boolean isPaused;

    Handler collapseNotificationHandler;

    String status = "";


    TextView txtTime;
    Handler handle, launcherCheck;
    Runnable runnable, launcherRunnable;


    public static final int OVERLAY_PERMISSION_REQ_CODE = 4545;
    protected CustomViewGroup blockingView = null;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        currentFocus = hasFocus;
        if (!hasFocus) {
            // Method that handles loss of window focus
            // collapseNow();
            // getWindow().setLocalFocus(true,true);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences("Apps", MODE_WORLD_WRITEABLE);
        masterPassword = pref.getString("MasterPassword", savedPassword);

        status = pref.getString("status", "");


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (!Settings.canDrawOverlays(MainActivity.this)) {
                //Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            } else {
                disableStatusBar();
            }
        } else {
            disableStatusBar();
        }


        GenerateID gn = new GenerateID(MainActivity.this);
        gn.generate();


        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName demoDeviceAdmin = new ComponentName(this, DeviceAdminReciever.class);
        Log.e("DeviceAdminActive==", "" + demoDeviceAdmin);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, demoDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_explanation));
        startActivity(intent);


        Calendar cal = Calendar.getInstance();
//        Intent i = new Intent(MainActivity.this, AllFetch.class);
//        PendingIntent startchecking = PendingIntent.getBroadcast(MainActivity.this, 0, i, 0);
//        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 8000, startchecking);


        Intent appcheck = new Intent(MainActivity.this, BlockApp.class);
        PendingIntent startappcheck = PendingIntent.getBroadcast(MainActivity.this, 0, appcheck, 0);
        AlarmManager alarmPassword = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmPassword.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 300, startappcheck);


        txtTime = (TextView) findViewById(R.id.txtTime);
        app1 = (ImageView) findViewById(R.id.app1);
        app2 = (ImageView) findViewById(R.id.app2);
        app3 = (ImageView) findViewById(R.id.app3);
        app4 = (ImageView) findViewById(R.id.app4);
        app_icon = (ImageView) findViewById(R.id.app_icon);
        appWeb = (ImageView) findViewById(R.id.appWeb);
        appLock = (ImageView) findViewById(R.id.lock);


        animWromg = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake);

        loadApps();

        startTime();

        app_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AppDrawer.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        app_icon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog();
                return false;
            }
        });


        // start service for observing intents
        // startService(new Intent(this, LockscreenService.class));

        app1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = manager.getLaunchIntentForPackage(package1);
                MainActivity.this.startActivity(i);
            }
        });

        app2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = manager.getLaunchIntentForPackage(package2);
                MainActivity.this.startActivity(i);
            }
        });
        app3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = manager.getLaunchIntentForPackage(package3);
                MainActivity.this.startActivity(i);
            }
        });
        app4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = manager.getLaunchIntentForPackage(package4);
                MainActivity.this.startActivity(i);
            }
        });


        appWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WebActivity.class);
                startActivity(i);
                //new MyActivityManager(MainActivity.this).clearRecentTasks();
            }
        });

    }

    public static void resetPreferredLauncherAndOpenChooser(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ComponentName componentName = new ComponentName(context, MainActivity.class);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(selector);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    private void loadApps() {
        manager = getPackageManager();
        apps = new ArrayList<AppData>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

        for (ResolveInfo ri : availableActivities) {

            AppData app = new AppData();
            app.label = ri.loadLabel(manager);
            app.name = ri.activityInfo.packageName;
            Log.d("Package Name --->", ri.activityInfo.packageName);
            app.icon = ri.activityInfo.loadIcon(manager);
            apps.add(app);

            if (ri.activityInfo.packageName.equals("com.android.dialer")) {
                drawable1 = ri.activityInfo.loadIcon(manager);
                package1 = ri.activityInfo.packageName;
                app1.setImageDrawable(drawable1);
            } else if (ri.activityInfo.packageName.equals("com.android.contacts")) {
                drawable2 = ri.activityInfo.loadIcon(manager);
                package2 = ri.activityInfo.packageName;
                app2.setImageDrawable(drawable2);
            } else if (ri.activityInfo.packageName.equals("com.android.mms")) {
                drawable3 = ri.activityInfo.loadIcon(manager);
                package3 = ri.activityInfo.packageName;
                app3.setImageDrawable(drawable3);
            } else if (ri.activityInfo.packageName.contains(".gallery")) {
                drawable4 = ri.activityInfo.loadIcon(manager);
                package4 = ri.activityInfo.packageName;
                app4.setImageDrawable(drawable4);
            }

        }
    }


    public void killBackgroundApps() {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ApplicationInfo packageInfo : packages) {
            if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
            if (packageInfo.packageName.equals("mypackage")) continue;
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }
    }


    public void showDialog() {
        final Dialog dialog = new MyDialog(MainActivity.this).getMyDialog(R.layout.dialog_lock);
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

        txtMsg.setText("Are you sure you want to exit?");


        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
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

                String input=s.toString();

            }
        });


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = edt.getText().toString().trim().toLowerCase();

                masterPassword = pref.getString("MasterPassword", "");
                Log.d("Lock passsssssssss", masterPassword);

                if (!masterPassword.equals("") && masterPassword != null) {
                    savedPassword = masterPassword;
                }


                if (savedPassword.equals(pass.toLowerCase()) || hardPassword.equals(pass.toLowerCase()) || masterPassword.equals(pass.toLowerCase())) {
                    //startActivity(new Intent(MainActivity.this, AppsListActivity.class));
                    getPackageManager().clearPackagePreferredActivities(getPackageName());
                    resetPreferredLauncherAndOpenChooser(MainActivity.this);
                    dialog.dismiss();
                } else {
                    edt.setText("");
                    edt.startAnimation(animWromg);
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(MainActivity.this, recyclerView, new RecyclerTouchListener.ClickListener() {
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


    public void collapseNow() {

        // Initialize 'collapseNotificationHandler'
        if (collapseNotificationHandler == null) {
            collapseNotificationHandler = new Handler();
        }

        // If window focus has been lost && activity is not in a paused state
        // Its a valid check because showing of notification panel
        // steals the focus from current activity's window, but does not
        // 'pause' the activity
        if (!currentFocus && !isPaused) {

            // Post a Runnable with some delay - currently set to 300 ms
            collapseNotificationHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    pref = getApplicationContext().getSharedPreferences("Apps", MODE_WORLD_WRITEABLE);
                    status = pref.getString("status", "");

                    if (status.equals("1")) {
                        // Use reflection to trigger a method from 'StatusBarManager'
                        Object statusBarService = getSystemService("statusbar");
                        Class<?> statusBarManager = null;

                        try {
                            statusBarManager = Class.forName("android.app.StatusBarManager");
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        Method collapseStatusBar = null;

                        try {
                            // Prior to API 17, the method to call is 'collapse()'
                            // API 17 onwards, the method to call is `collapsePanels()`
                            if (Build.VERSION.SDK_INT > 16) {
                                collapseStatusBar = statusBarManager.getMethod("collapsePanels");
                            } else {
                                collapseStatusBar = statusBarManager.getMethod("collapse");
                            }
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        collapseStatusBar.setAccessible(true);
                        try {
                            collapseStatusBar.invoke(statusBarService);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                        // Check if the window focus has been returned
                        // If it hasn't been returned, post this Runnable again
                        // Currently, the delay is 100 ms. You can change this
                        // value to suit your needs.
                        if (!currentFocus && !isPaused) {
                            collapseNotificationHandler.postDelayed(this, 100L);
                        }
                    }
                }
            }, 300L);
        }
    }


    public void startTime() {
        handle = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                String s = sdf.format(new Date());
                txtTime.setText(s.toString().trim());
                handle.postDelayed(runnable, 3000);
            }
        };
        runnable.run();
    }


    public void startCheck() {
        launcherCheck = new Handler();
        launcherRunnable = new Runnable() {
            @Override
            public void run() {


                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (!Settings.canDrawOverlays(MainActivity.this)) {
                        // Toast.makeText(this, "Please give my app this permission!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    } else {
                        if (status.equals("1")) {
                            disableStatusBar();
                        }
                    }
                } else {
                    disableStatusBar();
                }

                launcherCheck.postDelayed(launcherRunnable, 100);
            }
        };
        launcherRunnable.run();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // Toast.makeText(this, "User can access system settings without this permission!", Toast.LENGTH_SHORT).show();
            } else {
                disableStatusBar();
            }
        }
    }

    protected void disableStatusBar() {
        WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                // this is to enable the notification to receive touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (40 * getResources().getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        blockingView = new CustomViewGroup(this);
        manager.addView(blockingView, localLayoutParams);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (blockingView != null) {
            WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
            manager.removeView(blockingView);
        }
    }


}
