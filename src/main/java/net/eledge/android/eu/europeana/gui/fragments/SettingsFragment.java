package net.eledge.android.eu.europeana.gui.fragments;

import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import net.eledge.android.eu.europeana.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment {

    private PackageInfo mInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }

        String settings = getArguments().getString("settings");
        switch (settings) {
            case "locale":
                addPreferencesFromResource(R.xml.settings_locale);
                break;
            case "about":
                addPreferencesFromResource(R.xml.settings_about);
                setVersionNumber();
                break;
            default:

        }

    }

    private void setVersionNumber() {
        if (mInfo != null) {
            Preference p = findPreference("settings_about_version");
            p.setSummary(mInfo.versionName);
            p = findPreference("settings_about_build");
            p.setSummary(String.valueOf(mInfo.versionCode));
        }
    }

}
