package net.eledge.android.eu.europeana.gui.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import net.eledge.android.eu.europeana.R;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    public static final String PREFS_LOCALE = "net.eledge.android.eu.europeana.prefs.PREFS_LOCALE";
    public static final String PREFS_ABOUT = "net.eledge.android.eu.europeana.prefs.PREFS_ABOUT";

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            String action = getIntent().getAction();
            if (StringUtils.isNotBlank(action)) {
                switch (action) {
                    case PREFS_LOCALE:
                        addPreferencesFromResource(R.xml.settings_locale);
                        break;
                    case PREFS_ABOUT:

                        break;
                    default:
                        addPreferencesFromResource(R.xml.settings_legacy);
                        break;
                }
            } else {
                addPreferencesFromResource(R.xml.settings_legacy);
            }
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.settings, target);
    }

}
