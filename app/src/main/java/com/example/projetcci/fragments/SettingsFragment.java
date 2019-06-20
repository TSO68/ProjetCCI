package com.example.projetcci.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.example.projetcci.MovieManager;
import com.example.projetcci.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Open DB
        final MovieManager m = new MovieManager(getActivity());
        m.open();

        Preference clearDB = findPreference("key_clear_database");
        clearDB.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference pref) {
                m.clear();
                return true;
            }
        });
    }
}
