package net.armooo.nightdock;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class NightDockPreferences extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
