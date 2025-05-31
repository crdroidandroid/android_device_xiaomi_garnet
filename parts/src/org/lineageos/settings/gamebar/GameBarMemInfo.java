/*
 * Copyright (C) 2025 kenway214
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

package org.lineageos.settings.gamebar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class GameBarMemInfo {

    public static String getRamUsage() {
        long memTotal = 0;
        long memAvailable = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("MemTotal:")) {
                    memTotal = parseMemValue(line);
                } else if (line.startsWith("MemAvailable:")) {
                    memAvailable = parseMemValue(line);
                }
                if (memTotal > 0 && memAvailable > 0) {
                    break;
                }
            }
        } catch (IOException e) {
            return "N/A";
        }

        if (memTotal == 0) {
            return "N/A";
        }

        long usedKb = (memTotal - memAvailable);
        long usedMb = usedKb / 1024;
        return String.valueOf(usedMb);
    }

    private static long parseMemValue(String line) {
        String[] parts = line.split("\\s+");
        if (parts.length < 3) {
            return 0;
        }
        try {
            return Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String getRamSpeed() {
        String path = "/sys/devices/system/cpu/bus_dcvs/DDR/cur_freq";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            if (line != null && !line.isEmpty()) {
                try {
                    int khz = Integer.parseInt(line.trim());
                    float mhz = khz / 1000f;
                    if (mhz >= 1000) {
                        return String.format("%.3f GHz", mhz / 1000f);
                    } else {
                        return String.format("%.0f MHz", mhz);
                    }
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            // ignore
        }
        return "N/A";
    }

    public static String getRamTemp() {
        String path = "/sys/class/thermal/thermal_zone27/temp";
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            if (line != null && !line.isEmpty()) {
                try {
                    int raw = Integer.parseInt(line.trim());
                    float c = raw / 1000f;
                    return String.format("%.1f°C", c);
                } catch (NumberFormatException ignored) {}
            }
        } catch (IOException e) {
            // ignore
        }
        return "N/A";
    }
}
