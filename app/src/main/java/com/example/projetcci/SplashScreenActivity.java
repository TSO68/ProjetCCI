package com.example.projetcci;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Splash screen that welcomes user
 */
public class SplashScreenActivity extends AppCompatActivity {

    //Timer = 3 sec
    private static int SPLASH_TIMEOUT = 3000;

    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mUser= FirebaseAuth.getInstance().getCurrentUser();

        /*
         * Sets the time that the screen does appear before sending user
         * on MainActivity if he's connected
         * on LoginActivity if he's not connected
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mUser != null) {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
