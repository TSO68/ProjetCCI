package com.example.projetcci.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.example.projetcci.database.MovieManager;
import com.example.projetcci.R;

/**
 * Included in SettingsActivity
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Open DB
        final MovieManager m = new MovieManager(getActivity());
        m.open();

        //AlertDialog creation
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.clear_ask));
        builder.setCancelable(true);

        //Clear local database
        Preference clearDB = findPreference("key_clear_database");
        clearDB.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference pref) {

                //Ask user if he really want to clear the local database
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        m.clear();
                        Toast.makeText(getActivity(), getString(R.string.db_cleared), Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();

                return true;
            }
        });
    }
}
