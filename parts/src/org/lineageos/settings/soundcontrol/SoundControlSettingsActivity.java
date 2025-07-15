/*
 * Copyright (C) 2025 GuidixX
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.soundcontrol;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;

public class SoundControlSettingsActivity extends CollapsingToolbarBaseActivity {
    private static final String TAG_SOUND_CONTROL = "soundcontrol";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(
            com.android.settingslib.collapsingtoolbar.R.id.content_frame,
            new SoundControlSettingsFragment(), TAG_SOUND_CONTROL).commit();
    }
}
