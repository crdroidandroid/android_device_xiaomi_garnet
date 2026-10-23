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

import android.os.Bundle;
import android.view.MenuItem;

import com.android.settingslib.collapsingtoolbar.CollapsingToolbarBaseActivity;
import com.android.settingslib.widget.ExpressiveDesignEnabledProvider;

public class ThermalProfileActivity extends CollapsingToolbarBaseActivity
        implements ExpressiveDesignEnabledProvider {

    private static final String TAG_THERMAL_PROFILE = "thermal_profile";

    @Override
    public boolean isExpressiveDesignEnabled() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            ThermalProfileFragment fragment = new ThermalProfileFragment();
            Bundle args = new Bundle();
            args.putString(ThermalProfileFragment.EXTRA_PACKAGE_NAME,
                    getIntent().getStringExtra(ThermalProfileFragment.EXTRA_PACKAGE_NAME));
            args.putString(ThermalProfileFragment.EXTRA_APP_LABEL,
                    getIntent().getStringExtra(ThermalProfileFragment.EXTRA_APP_LABEL));
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(
                    com.android.settingslib.collapsingtoolbar.R.id.content_frame,
                    fragment, TAG_THERMAL_PROFILE).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }
}
