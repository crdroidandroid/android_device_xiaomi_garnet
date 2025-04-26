/*
 * Copyright (C) 2025 The LineageOS Project
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

package org.lineageos.settings.charge;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import org.lineageos.settings.R;

public class ChargeSettingsFragment extends PreferenceFragment
    implements Preference.OnPreferenceChangeListener {

    private static final String KEY_BYPASS_CHARGE = "bypass_charge";
    private SwitchPreference mBypassChargePreference;
    private ChargeUtils mChargeUtils;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.charge_settings, rootKey);
        
        mChargeUtils = new ChargeUtils(getActivity());
        mBypassChargePreference = (SwitchPreference) findPreference(KEY_BYPASS_CHARGE);

        boolean bypassChargeSupported = mChargeUtils.isBypassChargeSupported();

        if (mBypassChargePreference != null) {
            mBypassChargePreference.setEnabled(bypassChargeSupported);
            if (bypassChargeSupported) {
                mBypassChargePreference.setChecked(mChargeUtils.isBypassChargeEnabled());
                mBypassChargePreference.setOnPreferenceChangeListener(this);
            } else {
                mBypassChargePreference.setSummary(R.string.charge_bypass_unavailable);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        
        if (KEY_BYPASS_CHARGE.equals(key)) {
            boolean bypassValue = (Boolean) newValue;
            if (bypassValue) {
                new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.charge_bypass_title)
                    .setMessage(R.string.charge_bypass_warning)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        mChargeUtils.enableBypassCharge(true);
                        mBypassChargePreference.setChecked(true);
                    })
                    .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                        mBypassChargePreference.setChecked(false);
                    })
                    .show();
                return false;
            } else {
                mChargeUtils.enableBypassCharge(false);
                return true;
            }
        }
        return false;
    }
}
