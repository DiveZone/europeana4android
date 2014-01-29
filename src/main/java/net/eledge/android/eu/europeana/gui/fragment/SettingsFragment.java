/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.eledge.android.eu.europeana.gui.fragment;

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
