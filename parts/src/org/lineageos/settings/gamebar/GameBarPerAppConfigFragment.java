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
import android.widget.EditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.LinearLayout;
import android.content.Context;
import org.lineageos.settings.R;

public class GameBarPerAppConfigFragment extends PreferenceFragmentCompat {
    public static final String PREF_AUTO_APPS = "game_bar_auto_apps";

    private EditText mSearchBar;
    private PreferenceCategory mCategory;
    private List<ApplicationInfo> mAllApps;
    private PackageManager mPm;
    private Set<String> mAutoApps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        Context context = getContext();
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        mSearchBar = new EditText(context);
        mSearchBar.setId(View.generateViewId());
        mSearchBar.setHint("Search apps...");
        mSearchBar.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        mSearchBar.setBackgroundResource(R.drawable.bg_search_rounded);
        mSearchBar.setPadding(24, 24, 24, 24);
        mSearchBar.setTextColor(context.getColor(R.color.app_name_text_selector));
        mSearchBar.setHintTextColor(context.getColor(R.color.app_package_text_selector));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = (int) (context.getResources().getDisplayMetrics().density * 16); // 16dp
        params.setMargins(margin, 0, margin, 0);
        layout.addView(mSearchBar, params);
        if (root != null) {
            layout.addView(root);
        }
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                populateAppList(s.toString().toLowerCase());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        return layout;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(getContext()));
        mCategory = new PreferenceCategory(getContext());
        mCategory.setTitle("Configure Per-App GameBar");
        getPreferenceScreen().addPreference(mCategory);
        mPm = requireContext().getPackageManager();
        mAllApps = mPm.getInstalledApplications(PackageManager.GET_META_DATA);
        mAutoApps = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getStringSet(PREF_AUTO_APPS, new HashSet<>());
        populateAppList("");
    }

    private void populateAppList(String filter) {
        mCategory.removeAll();
        for (ApplicationInfo app : mAllApps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;
            if (app.packageName.equals(getContext().getPackageName())) continue;
            String label = app.loadLabel(mPm).toString().toLowerCase();
            String pkg = app.packageName.toLowerCase();
            if (!filter.isEmpty() && !(label.contains(filter) || pkg.contains(filter))) continue;
            SwitchPreferenceCompat pref = new SwitchPreferenceCompat(getContext());
            pref.setTitle(app.loadLabel(mPm));
            pref.setSummary(app.packageName);
            pref.setKey("gamebar_" + app.packageName);
            pref.setChecked(mAutoApps.contains(app.packageName));
            pref.setIcon(app.loadIcon(mPm));
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
            mCategory.addPreference(pref);
        }
    }
} 