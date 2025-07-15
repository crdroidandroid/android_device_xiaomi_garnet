/*
 * Copyright (C) 2025 GuidixX
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.soundcontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.lineageos.settings.utils.FileUtils;

public class SoundControlUtils {
    private static final String PREF_MIC_GAIN = "sound_mic_gain";
    private static final String PREF_HP_LEFT_GAIN = "sound_hp_left_gain";
    private static final String PREF_HP_RIGHT_GAIN = "sound_hp_right_gain";
    private static final String MIC_GAIN_PATH = "/sys/kernel/sound_control/mic_gain";
    private static final String HP_GAIN_PATH = "/sys/kernel/sound_control/headphone_gain";
    private static final String PREF_ENABLE = "sound_control_enable";

    public static void saveMicGain(Context ctx, int value) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putInt(PREF_MIC_GAIN, value).apply();
        writeInt(MIC_GAIN_PATH, value);
    }
    public static void saveHpGain(Context ctx, int left, int right) {
        PreferenceManager.getDefaultSharedPreferences(ctx)
            .edit().putInt(PREF_HP_LEFT_GAIN, left).putInt(PREF_HP_RIGHT_GAIN, right).apply();
        writeHeadphoneGain(HP_GAIN_PATH, left, right);
    }
    public static int getSavedMicGain(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getInt(PREF_MIC_GAIN, 0);
    }
    public static int[] getSavedHpGain(Context ctx) {
        return new int[] {
            PreferenceManager.getDefaultSharedPreferences(ctx).getInt(PREF_HP_LEFT_GAIN, 0),
            PreferenceManager.getDefaultSharedPreferences(ctx).getInt(PREF_HP_RIGHT_GAIN, 0)
        };
    }
    public static void applyAll(Context ctx) {
        writeInt(MIC_GAIN_PATH, getSavedMicGain(ctx));
        int[] hp = getSavedHpGain(ctx);
        writeHeadphoneGain(HP_GAIN_PATH, hp[0], hp[1]);
    }
    public static int readInt(String path, int def) {
        try {
            return Integer.parseInt(FileUtils.readOneLine(path));
        } catch (Exception e) {
            return def;
        }
    }
    public static void writeInt(String path, int value) {
        FileUtils.writeLine(path, String.valueOf(value));
    }
    public static int[] readHeadphoneGain(String path) {
        try {
            String[] vals = FileUtils.readOneLine(path).split(" ");
            return new int[]{Integer.parseInt(vals[0]), Integer.parseInt(vals[1])};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }
    public static void writeHeadphoneGain(String path, int left, int right) {
        FileUtils.writeLine(path, left + " " + right);
    }

    public static boolean isEnabled(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(PREF_ENABLE, true);
    }
    public static void setEnabled(Context ctx, boolean enabled) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putBoolean(PREF_ENABLE, enabled).apply();
    }
}
