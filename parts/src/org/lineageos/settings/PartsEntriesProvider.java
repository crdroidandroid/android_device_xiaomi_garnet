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

package org.lineageos.settings;

import com.android.settingslib.drawer.EntriesProvider;
import com.android.settingslib.drawer.EntryController;

import java.util.List;

/**
 * Provides the group headers ("Parts") shown in the Settings app dashboard.
 *
 * Tiles that declare the group key of one of these entries in their
 * meta-data (com.android.settings.group_key) are nested under a
 * PreferenceCategory with the entry's title in the category page.
 */
public class PartsEntriesProvider extends EntriesProvider {

    public static final String GROUP_KEY_DISPLAY = "parts_display_group";
    public static final String GROUP_KEY_SOUND = "parts_sound_group";

    private static final String CATEGORY_DISPLAY =
            "com.android.settings.category.ia.display";
    private static final String CATEGORY_SOUND =
            "com.android.settings.category.ia.sound";

    @Override
    protected List<? extends EntryController> createEntryControllers() {
        return List.of(
                new EntryController() {
                    @Override
                    public String getKey() {
                        return GROUP_KEY_DISPLAY;
                    }

                    @Override
                    protected MetaData getMetaData() {
                        return new MetaData(CATEGORY_DISPLAY)
                                .setTitle(R.string.parts_group_title)
                                .setOrder(10000);
                    }
                },
                new EntryController() {
                    @Override
                    public String getKey() {
                        return GROUP_KEY_SOUND;
                    }

                    @Override
                    protected MetaData getMetaData() {
                        return new MetaData(CATEGORY_SOUND)
                                .setTitle(R.string.others_group_title)
                                .setOrder(10000);
                    }
                });
    }
}
