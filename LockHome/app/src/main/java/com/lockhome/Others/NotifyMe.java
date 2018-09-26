package com.lockhome.Others;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lockhome.MainActivity;
import com.lockhome.R;


public class NotifyMe {

    Context context;
    String heading = "Notification - School Guardian App";

    public NotifyMe(Context context) {
        this.context = context;
    }

    public void notifyNow(String text, String id) {
        int nId = Integer.parseInt(id);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context).
                setContentTitle(heading).
                setContentText(text).
                setContentIntent(pi).
                setAutoCancel(true).
                setSmallIcon(R.mipmap.ic_launcher);
        NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        n.notify(nId, noti.build());
    }

}
