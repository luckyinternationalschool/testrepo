package com.lockhome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.lockhome.Others.GenerateID;
import com.lockhome.Others.NotifyMe;

public class GCMIntentService extends GCMBaseIntentService {

    SharedPreferences prefs;
    String num = "0";
    int topicId = 0;

    public GCMIntentService() {
        super(GenerateID.senderId);
    }


    @Override
    protected void onError(Context arg0, String arg1) {

        Log.v("Show Error", arg1);

    }

    @Override
    protected void onMessage(Context arg0, Intent intent) {

        try {
            String action = intent.getAction();
            Log.v("actionPush", action);
            if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
                String message = intent.getExtras().getString("message");
                Log.d("message", "" + message);
                Log.v("PushReceived", message);

                prefs = PreferenceManager.getDefaultSharedPreferences(arg0);
                if (prefs.getString("Notify", "Yes").equals("Yes")) {
                    message = Html.fromHtml(message).toString();


                    NotifyMe notify = new NotifyMe(arg0);
                    try {
                        if (!prefs.getString("UserIdSaved", "0").equals("0")) {
                            String number = message.substring(0, message.indexOf("_"));
                            Integer n = Integer.parseInt(number);
                            message = message.substring(message.indexOf("_") + 1);
                            String commentUserPhoto = message.substring(message.lastIndexOf("?") + 1);
                            Log.v("numberId", "" + n + "\n" + message);
                            Log.v("UserPhoto", commentUserPhoto);
                            notify.notifyNow(message, n + "");
                        }
                    } catch (Exception e) {
                        String strTopicId = message.substring(message.lastIndexOf("?") + 1);
                        //int topicId=Integer.parseInt(strTopicId);
                        Log.v("numberId", "" + strTopicId + "\n" + message);
                        notify.notifyNow(message, strTopicId);
                    }
                }

            }


        } catch (Exception exp) {
            Log.v("gcmException", exp.getMessage(), exp);
            exp.printStackTrace();
        }


    }

    @Override
    protected void onRegistered(Context arg0, String registrationId) {

        try {
            GenerateID.deviceId = registrationId;
            //new SendId().execute();
            Log.d("user_dev_id", GenerateID.deviceId);


        } catch (Exception exp) {
            Log.v("exceptionGCM", "" + exp);
        } catch (Error e) {
            Log.v("errorGCM", "" + e);
        }

    }

    @Override
    protected void onUnregistered(Context arg0, String arg1) {


    }
}











/* @Override
    protected void onMessage(Context arg0, Intent intent) {

        try {

            String action = intent.getAction();

            Log.d("sumit", action + " on mesage");
//			SharedPreferences alarmSettings = arg0.getSharedPreferences("NotificationClassData", Context.MODE_WORLD_READABLE);
//			String login_str = alarmSettings.getString("login", "");
            if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
                String message = intent.getStringExtra("message");
                Log.d("message", message);
                notification(message,arg0);
            }

        } catch (Exception exp) {
            Log.e("gcm", exp.getMessage(), exp);
            exp.printStackTrace();
        }

    }




    private void notification(String message,Context arg0) {
        try {
            file = getSharedPreferences("football", 1);

            edit = file.edit();

            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int icon = R.drawable.ic_launcher;
            long when = new Date().getTime();

            Notification notification = new Notification(icon, message, when);

            // notification.defaults = Notification.DEFAULT_SOUND
            // | Notification.DEFAULT_VIBRATE;

            notification.defaults |= Notification.DEFAULT_VIBRATE;
            // notification.sound= Uri.parse("android.resource://" +
            // getPackageName() + "/" + R.raw.futuresound);

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults = Notification.DEFAULT_ALL;
            PendingIntent contentIntent;

            Log.d("hii", message);
//			if(login_str.equalsIgnoreCase("login"))
//			{
//			 Intent i = new Intent(this, TeamSelectActivity.class);
//			 i.putExtra("message", message);
            contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    (int) System.currentTimeMillis(),null,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notification.setLatestEventInfo(getApplicationContext(), "Football",
                    message, contentIntent);
//                edit.putString("bid", "bid");
//                edit.commit();
            mNotificationManager.notify(1110, notification);

        } catch (Exception exp) {

            exp.printStackTrace();

        }
    }*//*



}

*/
