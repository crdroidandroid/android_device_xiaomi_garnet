/*
 * Copyright (C) 2025 kenway214
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.gamebar;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameBarPerAppConfigFragment extends PreferenceFragmentCompat {
    public static final String PREF_AUTO_APPS = "game_bar_auto_apps";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
        PreferenceCategory category = new PreferenceCategory(getContext());
        category.setTitle("Configure Per-App GameBar");
        getPreferenceScreen().addPreference(category);

        PackageManager pm = requireContext().getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Set<String> autoApps = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getStringSet(PREF_AUTO_APPS, new HashSet<>());

        for (ApplicationInfo app : apps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;
            if (app.packageName.equals(getContext().getPackageName())) continue;
            SwitchPreferenceCompat pref = new SwitchPreferenceCompat(getContext());
            pref.setTitle(app.loadLabel(pm));
            pref.setSummary(app.packageName);
            pref.setKey("gamebar_" + app.packageName);
            pref.setChecked(autoApps.contains(app.packageName));
            pref.setIcon(app.loadIcon(pm));
            pref.setOnPreferenceChangeListener((Preference p, Object newValue) -> {
                Set<String> updated = new HashSet<>(PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getStringSet(PREF_AUTO_APPS, new HashSet<>()));
                if ((Boolean) newValue) {
                    updated.add(app.packageName);
                } else {
                    updated.remove(app.packageName);
                }
                PreferenceManager.getDefaultSharedPreferences(getContext())
                        .edit().putStringSet(PREF_AUTO_APPS, updated).apply();
                return true;
            });
            category.addPreference(pref);
        }
    }
} 