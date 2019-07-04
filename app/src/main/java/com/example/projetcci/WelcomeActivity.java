package com.example.projetcci;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

/**
 * Welcoming activity that presents the application
 */
public class WelcomeActivity extends AppCompatActivity {

    private WebView mWebView;

    Button btnToLogin, btnToSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mWebView = (WebView) findViewById(R.id.webview_projetcci);
        btnToLogin = (Button) findViewById(R.id.btnToLogin);
        btnToSignup = (Button) findViewById(R.id.btnToSignup);

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

        //To LoginActivity
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //To SignupActivity
        btnToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
