/*
 * Copyright (C) 2021 WaveOS
 * Copyright (C) 2021 Chaldeaprjkt
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

package org.lineageos.settings;

import android.content.Context;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.view.Display;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RefreshRateTileService extends TileService {
    private static final String KEY_MIN_REFRESH_RATE = "min_refresh_rate";
    private static final String KEY_PEAK_REFRESH_RATE = "peak_refresh_rate";

    private Context context;
    private Tile tile;

    private final List<String> entries = new ArrayList<>();
    private final List<Float> values = new ArrayList<>();
    private int active;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Display.Mode mode = context.getDisplay().getMode();
        Display.Mode[] modes = context.getDisplay().getSupportedModes();
        for (Display.Mode m : modes) {
            int rate = (int) Math.round(m.getRefreshRate());
            if (rate % 30 != 0 || m.getPhysicalWidth() != mode.getPhysicalWidth() ||
                m.getPhysicalHeight() != mode.getPhysicalHeight())
                continue;

            entries.add(String.format(Locale.US,"%d Hz", rate));
            values.add(m.getRefreshRate());
        }
        syncFromSettings();
    }

    private void syncFromSettings() {
        float rate = Settings.System.getFloat(context.getContentResolver(),
                KEY_MIN_REFRESH_RATE, 60);
        active = values.indexOf(rate);
        if (active < 0)
            active = 0;
    }


    private void cycleRefreshRate() {
        if (active < values.size() - 1) {
            active++;
        } else {
            active = 0;
        }

        float rate = values.get(active);
        Settings.System.putFloat(context.getContentResolver(), KEY_MIN_REFRESH_RATE, rate);
        Settings.System.putFloat(context.getContentResolver(), KEY_PEAK_REFRESH_RATE, rate);
    }

    private void updateTileView() {
        tile.setContentDescription(entries.get(active));
        tile.setSubtitle(entries.get(active));
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        syncFromSettings();
        updateTileView();
    }

    @Override
    public void onClick() {
        super.onClick();
        cycleRefreshRate();
        syncFromSettings();
        updateTileView();
    }
}
