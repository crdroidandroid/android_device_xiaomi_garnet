/*
 * Copyright (C) 2024 The LineageOS Project
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

package org.lineageos.settings.thermal;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceScreen;

import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import com.android.settingslib.widget.SettingsBasePreferenceFragment;

import org.lineageos.settings.R;

public class ThermalProfileFragment extends SettingsBasePreferenceFragment {

    public static final String EXTRA_PACKAGE_NAME = "package_name";
    public static final String EXTRA_APP_LABEL = "app_label";

    private String mPackageName;
    private String mAppLabel;
    private ThermalUtils mThermalUtils;
    private LayoutPreference mHeader;
    private final SelectorWithWidgetPreference[] mOptions =
            new SelectorWithWidgetPreference[ThermalUtils.PROFILE_COUNT];
    private int mCurrentState;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        final Bundle args = getArguments();
        mPackageName = args != null ? args.getString(EXTRA_PACKAGE_NAME) : null;
        mAppLabel = args != null ? args.getString(EXTRA_APP_LABEL) : null;
        mThermalUtils = new ThermalUtils(getContext());
        mCurrentState = mThermalUtils.getStateForPackage(mPackageName);

        final PreferenceScreen screen =
                getPreferenceManager().createPreferenceScreen(getContext());

        mHeader = new LayoutPreference(getContext(), R.layout.thermal_app_header);
        mHeader.setSelectable(false);
        screen.addPreference(mHeader);

        for (int i = 0; i < ThermalUtils.PROFILE_COUNT; i++) {
            final int state = i;
            final SelectorWithWidgetPreference pref =
                    new SelectorWithWidgetPreference(getContext());
            pref.setTitle(ThermalUtils.getProfileNameRes(state));
            pref.setSummary(ThermalUtils.getProfileDescRes(state));
            pref.setPersistent(false);
            pref.setChecked(state == mCurrentState);
            pref.setOnPreferenceClickListener(p -> {
                selectProfile(state);
                return true;
            });
            screen.addPreference(pref);
            mOptions[state] = pref;
        }

        final FooterPreference footer = new FooterPreference(getContext());
        footer.setTitle(R.string.thermal_profile_footer);
        footer.setSelectable(false);
        screen.addPreference(footer);

        setPreferenceScreen(screen);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageView icon = mHeader.findViewById(R.id.thermal_app_icon);
        try {
            icon.setImageDrawable(
                    getContext().getPackageManager().getApplicationIcon(mPackageName));
        } catch (PackageManager.NameNotFoundException e) {
            icon.setImageResource(android.R.drawable.sym_def_app_icon);
        }
        final TextView name = mHeader.findViewById(R.id.thermal_app_name);
        name.setText(mAppLabel);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(mAppLabel);
    }

    private void selectProfile(int state) {
        if (state != mCurrentState) {
            mCurrentState = state;
            mThermalUtils.writePackage(mPackageName, state);
        }
        for (int i = 0; i < mOptions.length; i++) {
            mOptions[i].setChecked(i == mCurrentState);
        }
    }
}
