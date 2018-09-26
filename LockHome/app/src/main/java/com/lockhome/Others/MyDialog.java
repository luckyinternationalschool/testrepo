package com.lockhome.Others;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

/**
 * Created by wscubetech on 16/10/15.
 */
public class MyDialog {

    Context act;

    public MyDialog(Context act){
        this.act=act;
    }

    public Dialog getMyDialog(int layout) {
        Dialog d = new Dialog(act);
        d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.setContentView(layout);
        return d;
    }
}
