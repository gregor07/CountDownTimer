package com.example.mate.teamapp;

//import android.app.Activity;
import android.content.SharedPreferences;
//import android.opengl.EGLExt;
import android.os.Bundle;
//import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    public static final String KEY_PREF_ALERT_SWITCH = "alert_switch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        //SharedPreferences sharedPref = android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this);
        // SharedPreferences sharedPref = PreferenceManager .getDefaultSharedPreferences(this);
        // Boolean switchPref = sharedPref.getBoolean(SettingsActivity.KEY_PREF_ALERT_SWITCH, false);

        //Toast.makeText(this, switchPref.toString(), Toast.LENGTH_SHORT).show();
    }
}
