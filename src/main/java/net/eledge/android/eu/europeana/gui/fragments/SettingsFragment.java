package net.eledge.android.eu.europeana.gui.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import net.eledge.android.eu.europeana.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String settings = getArguments().getString("settings");
        if ("locale".equals(settings)) {
            addPreferencesFromResource(R.xml.settings_locale);
//            } else if ("cache".equals(settings)) {
//                addPreferencesFromResource(R.xml.settings_cache);
        }

    }
}
