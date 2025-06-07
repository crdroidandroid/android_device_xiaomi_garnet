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

package org.lineageos.settings.corecontrol;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import org.lineageos.settings.R;

import java.io.File;

public class CoreControlFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "CoreControlFragment";
    private static final int NUM_CORES = 8;

    private SwitchPreference[] mCorePrefs = new SwitchPreference[NUM_CORES];

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.core_control_settings);

        for (int i = 0; i < NUM_CORES; i++) {
            String key = "core_" + i;
            mCorePrefs[i] = (SwitchPreference) findPreference(key);
            if (mCorePrefs[i] != null) {
                mCorePrefs[i].setOnPreferenceChangeListener(this);
                mCorePrefs[i].setChecked(isCoreOnline(i));
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean requestedState = (Boolean) newValue;

        for (int i = 0; i < NUM_CORES; i++) {
            if (preference == mCorePrefs[i]) {
                if (!requestedState && !canOffline(i)) {
                    Toast.makeText(getContext(), "At least 2 little cores must remain online", Toast.LENGTH_SHORT).show();
                    return false;
                }
                setCoreState(i, requestedState);
                return true;
            }
        }
        return false;
    }

    private boolean isCoreOnline(int core) {
        return new File("/sys/devices/system/cpu/cpu" + core + "/online").exists() &&
               readFile("/sys/devices/system/cpu/cpu" + core + "/online").equals("1");
    }

    private void setCoreState(int core, boolean online) {
        writeFile("/sys/devices/system/cpu/cpu" + core + "/online", online ? "1" : "0");
    }

    private boolean canOffline(int core) {
        if (core >= 0 && core <= 3) {
            int onlineCount = 0;
            for (int i = 0; i <= 3; i++) {
                if (i != core && isCoreOnline(i)) onlineCount++;
            }
            return onlineCount >= 2;
        }
        return true;
    }

    private String readFile(String path) {
        try {
            return new String(java.nio.file.Files.readAllBytes(new File(path).toPath())).trim();
        } catch (Exception e) {
            Log.e(TAG, "Failed to read " + path, e);
            return "";
        }
    }

    private void writeFile(String path, String value) {
        try {
            java.nio.file.Files.write(new File(path).toPath(), value.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Failed to write " + path, e);
        }
    }
}
