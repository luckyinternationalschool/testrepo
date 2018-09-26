package com.lockhome;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class WebActivity extends Activity {

    WebView web;

    EditText edtAddress;

    Button btnSearch;

    ProgressBar pb;
    SharedPreferences pref;

    String Domains = "";

    ArrayList<String> arrayOfData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);


        pref = getApplicationContext().getSharedPreferences("Apps", MODE_WORLD_WRITEABLE);
        Domains = pref.getString("Domain", "");

        web = (WebView) findViewById(R.id.web);
        edtAddress = (EditText) findViewById(R.id.edtSearch);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        pb = (ProgressBar) findViewById(R.id.pb);


        pb.setVisibility(View.GONE);


        String[] domain = Domains.split(",");

        for (int i = 0; i < domain.length; i++) {
            arrayOfData.add(domain[i].toString().trim());
        }


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String address = edtAddress.getText().toString().trim();

//                if (Domains.contains(address)) {
                web.loadUrl("http://" + address);
//                } else {
//                    Toast.makeText(WebActivity.this, "You are not Authorised for Access this type of Domain!!", Toast.LENGTH_SHORT).show();
//                }

                WebSettings settings = web.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setJavaScriptCanOpenWindowsAutomatically(true);
                settings.setDomStorageEnabled(true);
                web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                web.setWebViewClient(new myWebClient());


            }
        });

    }


    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            pb.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            if (!Domains.equals("")) {
                edtAddress.setText(url);
                int fl = 0;
                for (int i = 0; i < arrayOfData.size(); i++) {
                    if (url.toLowerCase().contains(arrayOfData.get(i).toLowerCase())) {
                        fl = 1;
                        break;
                    }
                }
                if (fl == 0) {
                    Toast.makeText(WebActivity.this, "You are not Authorised for Access this type of Domain!!", Toast.LENGTH_SHORT).show();
                } else {
                    view.loadUrl(url);
                }
            }else {
                Toast.makeText(WebActivity.this, "You are not Authorised for Access this type of Domain!!", Toast.LENGTH_SHORT).show();
            }


            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            pb.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView vi, int errorCode, String description, String failingUrl) {
            Log.d("WEB_VIEW_TEST", "error code:" + errorCode);
            super.onReceivedError(vi, errorCode, description, failingUrl);
        }
    }

}
