package com.example.projetcci.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.example.projetcci.R;
import com.example.projetcci.fragments.SettingsFragment;

/**
 * Settings activity
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.menu_settings));
    }

    /**
     * Back to previous activity
     */
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}