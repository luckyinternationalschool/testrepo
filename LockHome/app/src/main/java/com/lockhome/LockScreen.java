package com.lockhome;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lockhome.Adapter.NumberAdpater;
import com.lockhome.Others.RecyclerTouchListener;

import java.util.ArrayList;

public class LockScreen extends Activity {

    LinearLayout ll;
    ImageView i1, i2, i3, i4;
    RecyclerView recyclerView;
    EditText edt;
    ArrayList<String> arrayList = new ArrayList<>();

    Animation animWromg;
    String data = "", savedPassword = "1234", masterPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock_screen);

        addArray();


        edt = (EditText) findViewById(R.id.edt);
        ll = (LinearLayout) findViewById(R.id.ll);
        i1 = (ImageView) findViewById(R.id.i1);
        i2 = (ImageView) findViewById(R.id.i2);
        i3 = (ImageView) findViewById(R.id.i3);
        i4 = (ImageView) findViewById(R.id.i4);


        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(LockScreen.this, 3));
        recyclerView.setAdapter(new NumberAdpater(arrayList));

        animWromg = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake);


        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    i1.setImageResource(R.drawable.dot);
                    i2.setImageResource(R.drawable.dot);
                    i3.setImageResource(R.drawable.dot);
                    i4.setImageResource(R.drawable.dot);
                } else if (s.length() == 1) {
                    i1.setImageResource(R.drawable.dot_selected);
                    i2.setImageResource(R.drawable.dot);
                    i3.setImageResource(R.drawable.dot);
                    i4.setImageResource(R.drawable.dot);
                } else if (s.length() == 2) {
                    i1.setImageResource(R.drawable.dot_selected);
                    i2.setImageResource(R.drawable.dot_selected);
                    i3.setImageResource(R.drawable.dot);
                    i4.setImageResource(R.drawable.dot);
                } else if (s.length() == 3) {
                    i1.setImageResource(R.drawable.dot_selected);
                    i2.setImageResource(R.drawable.dot_selected);
                    i3.setImageResource(R.drawable.dot_selected);
                    i4.setImageResource(R.drawable.dot);
                } else if (s.length() == 4) {
                    i1.setImageResource(R.drawable.dot_selected);
                    i2.setImageResource(R.drawable.dot_selected);
                    i3.setImageResource(R.drawable.dot_selected);
                    i4.setImageResource(R.drawable.dot_selected);

                    if (savedPassword.equals(s.toString()) || masterPassword.equals(s.toString())) {

                        data = "";
                        getPackageManager().clearPackagePreferredActivities(getPackageName());


                        finish();

                    } else {
                        edt.setText("");
                        data = "";
                        ll.startAnimation(animWromg);
                    }
                }
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(LockScreen.this, recyclerView, new RecyclerTouchListener.ClickListener() {
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

    }


    @Override
    public void onBackPressed() {

        startActivity(new Intent(LockScreen.this, MainActivity.class));
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


}
