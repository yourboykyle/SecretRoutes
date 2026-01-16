/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.Formatting;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.yourboykyle.secretroutes.Main.CONFIG_FOLDER_PATH;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class PBUtils {
    public static JsonObject personalBests = new JsonObject();
    public static String filePath = CONFIG_FOLDER_PATH + File.separator + "personal_bests.json";

    // Stuff for tracking personal bests
    public static long startTime = System.currentTimeMillis();
    public static boolean pbIsValid = false; // If a person uses one of the prev or next secret keybinds, its invalid. This it to prevent people from spamming the keybind until the last secret and clicking it legit for super fast PBs.

    // Load the PB data from the personal_bests.json file
    public static boolean loadPBData() {
        if (!new File(filePath).exists()) {
            sendChatMessage(Formatting.RED + "Personal bests file not found.");
            return false;
        }
        Gson gson = new GsonBuilder().create();
        FileReader reader = null;
        try {
            reader = new FileReader(filePath);
            personalBests = gson.fromJson(reader, JsonObject.class);
        } catch (FileNotFoundException e) {
            //This should never happen...
            LogUtils.error(e);
            sendChatMessage("§4 THIS SHOULD NEVER HAVE HAPPENED... (ConfigUtils 123)");
            return false;
        }

        return true;
    }

    public static void setPersonalBest(String roomName, long timeInMs) {
        if (!SRMConfig.get().trackPersonalBests) return;
        // Add the data to the json object, in the form of 2 strings. One being the room name, the other being the time formatted as a string, like: 364d 1h 10m 30.524s
        personalBests.addProperty(roomName, formatTime(timeInMs));
        writePBData();
    }

    public static void removePersonalBest(String roomName) {
        if (!SRMConfig.get().trackPersonalBests) return;
        if (personalBests.has(roomName)) {
            personalBests.remove(roomName);
            writePBData();
        }
    }

    public static long getPBForRoom(String roomName) {
        if (personalBests.has(roomName)) {
            String formattedTime = personalBests.get(roomName).getAsString();
            return parseTimeToMillis(formattedTime); // Convert formatted time back to milliseconds
        }
        return -1; // Return -1 if no personal best exists for the given room
    }

    public static void writePBData() {
        if (!SRMConfig.get().trackPersonalBests) return;
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(personalBests.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startRoute() {
        if (!SRMConfig.get().trackPersonalBests) return;
        startTime = System.currentTimeMillis();
    }

    public static void stopRoute() {
        if (!SRMConfig.get().trackPersonalBests) return;

        if (!pbIsValid) {
            ChatUtils.sendVerboseMessage("PB is invalid, not saving.", "Personal Bests");
            return;
        }

        long time = System.currentTimeMillis() - startTime;
        startTime = 0;
        // Check if it's a PB
        long pbTime = getPBForRoom(RoomDirectionUtils.roomName());
        ChatUtils.sendVerboseMessage("PB for " + RoomDirectionUtils.roomName() + ": " + (pbTime == -1 ? "N/A" : formatTime(pbTime)), "Personal Bests");
        if (pbTime == -1 || time < pbTime) {
            // New PB
            if (SRMConfig.get().sendChatMessages) {
                sendChatMessage("§rNew personal best for " + RoomDirectionUtils.roomName() + ": §a" + formatTime(time));
            }
            setPersonalBest(RoomDirectionUtils.roomName(), time);
        }

        PBUtils.pbIsValid = false;
        ChatUtils.sendVerboseMessage("Time for " + RoomDirectionUtils.roomName() + ": §a" + formatTime(time), "Personal Bests");
    }

    public static String formatTime(long millis) {
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
