package com.fidotechnologies.ultitrack90;

import com.fidotechnologies.ultitrack90.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
 
public class AppPreferences extends PreferenceActivity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
 
}
