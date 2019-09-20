package com.example.projetcci.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.projetcci.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Splash screen that welcomes user
 */
public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mUser= FirebaseAuth.getInstance().getCurrentUser();

        //Timer = 3 sec
        final int SPLASH_TIMEOUT = 3000;

        /*
         * Sets the time that the screen does appear before sending user
         * on MainActivity if he's connected
         * on IntroActivity the first time the app is opened
         * on LoginActivity if he's not connected
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mUser != null) {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                } else {
                    //Get shared preferences
                    SharedPreferences sp = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

                    //Check if the app has been opened before, if not open IntroActivity, else open LoginActivity
                    if (!sp.getBoolean("first", false)) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("first", true);
                        editor.apply();
                        Intent intent = new Intent(SplashScreenActivity.this, IntroActivity.class);
                        startActivity(intent);
                    }
                    else {
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    }
                }
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
