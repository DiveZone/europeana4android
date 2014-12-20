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

package net.eledge.android.europeana.gui.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import net.eledge.android.europeana.R;
import net.eledge.android.europeana.search.model.enums.Language;
import net.eledge.android.toolkit.gui.GuiUtils;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SettingsActivity extends PreferenceActivity {

    private PackageInfo mInfo;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        try {
            mInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(getClass().getName(), e.getMessage(), e);
        }

        setVersionNumber();
        fillLocaleListApplication();
        fillLocaleListData();
    }

    @SuppressWarnings("deprecation")
    private void setVersionNumber() {
        if (mInfo != null) {
            Preference p = findPreference("settings_about_version");
            p.setSummary(mInfo.versionName);
            p = findPreference("settings_about_build");
            p.setSummary(String.valueOf(mInfo.versionCode));
        }
    }


    @SuppressWarnings("deprecation")
    private void fillLocaleListApplication() {
        if (mInfo != null) {
            ListPreference p = (ListPreference) findPreference("settings_locale_app");
            String[] choices = getResources().getStringArray(R.array.settings_locale_app_choices);
            setSortedLocales(p, choices);
        }
    }

    @SuppressWarnings("deprecation")
    private void fillLocaleListData() {
        if (mInfo != null) {
            ListPreference p = (ListPreference) findPreference("settings_locale_content");
            String[] choices = getResources().getStringArray(R.array.settings_locale_content_choices);
            setSortedLocales(p, choices);
        }
    }

    private void setSortedLocales(ListPreference p, String[] choices) {
        String[] entries = new String[choices.length + 1];
        String[] values = new String[choices.length + 1];
        entries[0] = GuiUtils.getString(this, R.string.settings_locale_def);
        values[0] = "def";
        SortedMap<String, String> map = new TreeMap<>();
        for (String choice : choices) {
            map.put(getLocaleResource(choice), choice);
        }
        int i = 1;
        for (Map.Entry<String, String> e : map.entrySet()) {
            entries[i] = e.getKey();
            values[i] = e.getValue();
            i++;
        }
        p.setEntries(entries);
        p.setEntryValues(values);
    }

    private String getLocaleResource(String localeCode) {
        return getResources().getString(Language.safeValueOf(localeCode).resourceId);
    }

}
