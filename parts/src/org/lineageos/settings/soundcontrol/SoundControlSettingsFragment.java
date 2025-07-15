/*
 * Copyright (C) 2025 GuidixX
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.soundcontrol;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;
import org.lineageos.settings.R;

public class SoundControlSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.sound_control_settings, rootKey);

        SeekBarPreference micGainPref = findPreference("mic_gain");
        SeekBarPreference hpLeftGainPref = findPreference("hp_left_gain");
        SeekBarPreference hpRightGainPref = findPreference("hp_right_gain");
        SwitchPreferenceCompat enableSwitch = findPreference("sound_control_enable");

        micGainPref.setValue(SoundControlUtils.getSavedMicGain(getContext()));
        int[] hpGains = SoundControlUtils.getSavedHpGain(getContext());
        hpLeftGainPref.setValue(hpGains[0]);
        hpRightGainPref.setValue(hpGains[1]);

        boolean enabled = SoundControlUtils.isEnabled(getContext());
        enableSwitch.setChecked(enabled);
        micGainPref.setEnabled(enabled);
        hpLeftGainPref.setEnabled(enabled);
        hpRightGainPref.setEnabled(enabled);

        micGainPref.setOnPreferenceChangeListener((pref, newValue) -> {
            SoundControlUtils.saveMicGain(getContext(), (int) newValue);
            return true;
        });
        hpLeftGainPref.setOnPreferenceChangeListener((pref, newValue) -> {
            SoundControlUtils.saveHpGain(getContext(), (int) newValue, hpRightGainPref.getValue());
            return true;
        });
        hpRightGainPref.setOnPreferenceChangeListener((pref, newValue) -> {
            SoundControlUtils.saveHpGain(getContext(), hpLeftGainPref.getValue(), (int) newValue);
            return true;
        });
        enableSwitch.setOnPreferenceChangeListener((pref, newValue) -> {
            boolean isEnabled = (Boolean) newValue;
            SoundControlUtils.setEnabled(getContext(), isEnabled);
            micGainPref.setEnabled(isEnabled);
            hpLeftGainPref.setEnabled(isEnabled);
            hpRightGainPref.setEnabled(isEnabled);
            if (isEnabled) {
                SoundControlUtils.applyAll(getContext());
            }
            return true;
        });
    }
}
