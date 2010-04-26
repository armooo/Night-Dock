package net.armooo.nightdock;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.nullwire.trace.ExceptionHandler;

public class NightDockPreferences extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        ExceptionHandler.register(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
