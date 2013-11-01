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
        switch (settings) {
            case "locale":
                addPreferencesFromResource(R.xml.settings_locale);
                break;
            default:

        }

    }
}
