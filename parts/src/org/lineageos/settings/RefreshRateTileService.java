/*
 * Copyright (C) 2021 WaveOS
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
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import org.lineageos.settings.R;
import org.lineageos.settings.utils.RefreshRateUtils;

import java.util.Arrays;

public class RefreshRateTileService extends TileService {

    private Context context;
    private Tile tile;

    private String[] refreshRates;
    private String[] refreshRateValues;
    private int currentRefreshRate;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        refreshRates = context.getResources().getStringArray(R.array.device_refresh_rates);
        refreshRateValues = context.getResources().getStringArray(R.array.device_refresh_rates_val);
    }

    private void updateCurrentRefreshRate() {
        currentRefreshRate = Arrays.asList(refreshRateValues).indexOf(Integer.toString(RefreshRateUtils.getRefreshRate(context)));
    }

    private void updateTileDescription() {
        tile.setContentDescription(refreshRates[currentRefreshRate]);
        tile.setSubtitle(refreshRates[currentRefreshRate]);
        tile.updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        tile = getQsTile();
        tile.setState(Tile.STATE_ACTIVE);
        updateCurrentRefreshRate();
        updateTileDescription();
    }

    private int getRefreshRateVal() {
        return Integer.parseInt(refreshRateValues[currentRefreshRate]);
    }

    @Override
    public void onClick() {
        super.onClick();
        updateCurrentRefreshRate();
        if (currentRefreshRate == refreshRates.length - 1) {
            currentRefreshRate = 0;
        } else {
            currentRefreshRate++;
        }
        RefreshRateUtils.setRefreshRate(context, getRefreshRateVal());
        RefreshRateUtils.setFPS(getRefreshRateVal());
        updateTileDescription();
    }
}
