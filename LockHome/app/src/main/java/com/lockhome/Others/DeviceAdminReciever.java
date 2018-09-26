package com.lockhome.Others;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lockhome.R;

/**
 * Created by wscube on 18/4/16.
 */
public class DeviceAdminReciever extends DeviceAdminReceiver {
    static final String TAG = "DemoDeviceAdmin";

    /**
     * Called when this application is approved to be a device administrator.
     */
    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);

        Log.d(TAG, "onEnabled");
    }

    /**
     * Called when this application is no longer the device administrator.
     */
    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);

        Log.d(TAG, "onDisabled");
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        super.onPasswordChanged(context, intent);
        Log.d(TAG, "onPasswordChanged");
    }

    @Override
    public void onPasswordFailed(Context context, Intent intent) {
        super.onPasswordFailed(context, intent);
        Log.d(TAG, "onPasswordFailed");
    }

    @Override
    public void onPasswordSucceeded(Context context, Intent intent) {
        super.onPasswordSucceeded(context, intent);
        Log.d(TAG, "onPasswordSucceeded");
    }

}