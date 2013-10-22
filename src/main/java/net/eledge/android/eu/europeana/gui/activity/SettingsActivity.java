package net.eledge.android.eu.europeana.gui.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import net.eledge.android.eu.europeana.R;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    public static final String PREFS_LOCALE = "net.eledge.android.eu.europeana.prefs.PREFS_LOCALE";

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String action = getIntent().getAction();
        if (action != null && action.equals(PREFS_LOCALE)) {
            addPreferencesFromResource(R.xml.settings_locale);
        }
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Load the legacy preferences headers
            addPreferencesFromResource(R.xml.settings_legacy);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings, target);
    }

}
