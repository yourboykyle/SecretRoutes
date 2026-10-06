//#if FABRIC
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC & christechs
 *
 * <DO NOT REMOVE THIS COPYRIGHT NOTICE>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yourboykyle.secretroutes.utils;

import xyz.yourboykyle.secretroutes.config.SRMConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.yourboykyle.secretroutes.Main.CONFIG_FOLDER_PATH;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class PBUtils {
    private static PersonalBestStore store;

    // Load the PB data from the personal_bests.json file
    public static boolean loadPBData() {
        store = new PersonalBestStore(Path.of(CONFIG_FOLDER_PATH, "personal_bests.json"));
        try {
            store.load();
            if (store.backupPath() != null) {
                LogUtils.info("Previous PB data preserved at " + store.backupPath());
                sendChatMessage("§ePrevious PB data was backed up. Room-entry timing uses fresh records.");
            }
            return true;
        } catch (IOException e) {
            LogUtils.error(e);
            sendChatMessage("§cCould not load personal bests. Existing records have been preserved.");
            return false;
        }
    }

    public static void setPersonalBest(String roomName, long timeInMs) {
        if (!SRMConfig.get().trackPersonalBests) return;
        try {
            if (store != null) store.record(roomName, timeInMs);
        } catch (IOException e) {
            LogUtils.error(e);
            sendChatMessage("§cCould not save your personal best. Existing records have been preserved.");
        }
    }

    public static void removePersonalBest(String roomName) {
        if (!SRMConfig.get().trackPersonalBests) return;
        try {
            if (store != null) store.remove(roomName);
        } catch (IOException e) {
            LogUtils.error(e);
        }
    }

    public static long getPBForRoom(String roomName) {
        return store == null ? -1L : store.get(roomName);
    }

    public static void completeRoute(String roomName, long time, List<Long> splits) {
        if (!SRMConfig.get().trackPersonalBests || store == null) return;
        try {
            if (store.record(roomName, time) && SRMConfig.get().sendChatMessages) {
                sendChatMessage("§rNew personal best for " + roomName + ": §a" + formatTime(time));
            }
        } catch (IOException e) {
            LogUtils.error(e);
            sendChatMessage("§cCould not save your personal best. Existing records have been preserved.");
        }
        ChatUtils.sendVerboseMessage("Time for " + roomName + ": §a" + formatTime(time), "Personal Bests");
        if (SRMConfig.get().showSecretSplits) {
            StringBuilder summary = new StringBuilder("§rSecret splits for " + roomName + ": ");
            long previous = 0;
            for (int i = 0; i < splits.size(); i++) {
                if (i > 0) summary.append(", ");
                summary.append(i + 1).append(": §a").append(formatTime(splits.get(i) - previous)).append("§r");
                previous = splits.get(i);
            }
            sendChatMessage(summary.toString());
        }
    }

    public static String formatTime(long millis) {
        if (millis == 0) return "0.000s";
        long days = millis / (1000 * 60 * 60 * 24);
        millis %= (1000 * 60 * 60 * 24);
        long hours = millis / (1000 * 60 * 60);
        millis %= (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis %= (1000 * 60);
        long seconds = millis / 1000;
        long milliseconds = millis % 1000;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || milliseconds > 0) {
            sb.append(seconds);
            if (milliseconds > 0) sb.append(".").append(String.format("%03d", milliseconds));
            sb.append("s");
        }

        return sb.toString().trim(); // Remove any trailing spaces
    }

    public static long parseTimeToMillis(String formattedTime) {
        ChatUtils.sendVerboseMessage("Formatted PB time: " + formattedTime, "Personal Bests");
        long totalMillis = 0;

        // Regex pattern to match time components (e.g., "364d 1h 10m 30.524s")
        Pattern pattern = Pattern.compile("(\\d+)d| *(\\d+)h| *(\\d+)m| *(\\d+)\\.(\\d{1,3})?s| *(\\d+)s");
        Matcher matcher = pattern.matcher(formattedTime);

        while (matcher.find()) {
            if (matcher.group(1) != null) totalMillis += Long.parseLong(matcher.group(1)) * 86400000; // Days to ms
            if (matcher.group(2) != null) totalMillis += Long.parseLong(matcher.group(2)) * 3600000;  // Hours to ms
            if (matcher.group(3) != null) totalMillis += Long.parseLong(matcher.group(3)) * 60000;    // Minutes to ms
            if (matcher.group(4) != null) { // Seconds with optional milliseconds
                totalMillis += Long.parseLong(matcher.group(4)) * 1000; // Seconds to ms
                if (matcher.group(5) != null) {
                    String millisStr = matcher.group(5);
                    while (millisStr.length() < 3) millisStr += "0"; // Ensure 3-digit ms
                    totalMillis += Integer.parseInt(millisStr);
                }
            }
            if (matcher.group(6) != null) totalMillis += Long.parseLong(matcher.group(6)) * 1000; // Whole seconds
        }

        return totalMillis;
    }
}
//#endif
