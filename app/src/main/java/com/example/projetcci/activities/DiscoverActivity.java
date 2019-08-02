package com.example.projetcci.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.projetcci.network.MyWebViewClient;
import com.example.projetcci.R;

/**
 * Welcoming activity that presents the application
 */
public class DiscoverActivity extends AppCompatActivity {

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mWebView = (WebView) findViewById(R.id.webview_projetcci);

        //Configure the Webview
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        mWebView.loadUrl("https://projetcci.tk/webview");

        mWebView.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                findViewById(R.id.webview_projetcci).setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Sends user on LoginActivity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Back to LoginActivity
     */
    @Override
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
}
