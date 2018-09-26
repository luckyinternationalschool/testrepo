package com.lockhome.Others;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gcm.GCMRegistrar;
import com.lockhome.AppController;

import org.json.JSONObject;


public class GenerateID {
    Context context;
    SharedPreferences prefs;

   // public static String senderId = "897678216457";
     public static String senderId = "897678216457";
    public static String deviceId = "";
    int result = 0;
    String userId = "";

    public GenerateID(Context context) {
        this.context = context;
    }

    public void generate() {
        try {
            GCMRegistrar.checkDevice(context);
            GCMRegistrar.checkManifest(context);
            deviceId = GCMRegistrar.getRegistrationId(context);

            if (deviceId.equals("")) {
                GCMRegistrar.register(context, senderId);
            }
            Log.v("Device Id is ", "Device id is " + deviceId);

            if (CheckNetwork.isConnected(context)) {
                send_did();
            }
        } catch (Exception e) {
            Log.v("Exception...", "" + e);
            //Toast.makeText(context, "" + e, Toast.LENGTH_LONG).show();
        }


    }

    public void send_did() {
        String url = Urls.AddDeviceId + deviceId;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.d("Response", s.toString());
                if (s != null) {
                    s = Html.fromHtml(s).toString();
                    try {
                        JSONObject json = new JSONObject(s);
                        result = json.getInt("result");
                        if (result == 1) {
                            JSONObject jsonObj = new JSONObject(s);
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
        AppController.getInstance().addToRequestQueue(request, "Add DeviceId");
    }
}




