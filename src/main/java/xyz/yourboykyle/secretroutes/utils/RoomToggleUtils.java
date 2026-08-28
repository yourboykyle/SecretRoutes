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

import com.google.gson.stream.JsonReader;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

/**
 * Per room route toggles. Rooms are enabled by default, so only the disabled ones are stored in the config.
 */
public class RoomToggleUtils {

    // The F7 boss route has its own toggle in the Predev Routes group
    private static final String BOSS_ROOM = "f7boss";

    private RoomToggleUtils() {
    }

    /**
     * Every room that has a route in either route file, sorted alphabetically. Variants of the same room
     * ("Withermancers-4:1") share the toggle of their base room.
     */
    public static List<String> listKnownRooms() {
        if (Main.ROUTES_PATH == null) return List.of();

        TreeSet<String> rooms = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (String fileName : new String[]{"fowroutes.json", "3ppopkaroutes.json"}) {
            for (String key : readRouteKeys(new File(Main.ROUTES_PATH, fileName))) {
                if (key.startsWith("#") || key.equals("Version")) continue;

                String roomName = baseRoomName(key);
                if (!roomName.isEmpty() && !roomName.equalsIgnoreCase(BOSS_ROOM)) rooms.add(roomName);
            }
        }

        return List.copyOf(rooms);
    }

    public static boolean isRoomEnabled(String roomName) {
        if (roomName == null) return true;

        List<String> disabledRooms = SRMConfig.get().disabledRooms;
        return disabledRooms == null || !disabledRooms.contains(normalize(roomName));
    }

    public static void setRoomEnabled(String roomName, boolean enabled) {
        if (roomName == null) return;

        SRMConfig config = SRMConfig.get();
        if (config.disabledRooms == null) config.disabledRooms = new ArrayList<>();

        String normalized = normalize(roomName);
        if (enabled) {
            config.disabledRooms.remove(normalized);
        } else if (!config.disabledRooms.contains(normalized)) {
            config.disabledRooms.add(normalized);
        }
    }

    private static List<String> readRouteKeys(File file) {
        if (!file.exists()) return List.of();

        List<String> keys = new ArrayList<>();
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            reader.beginObject();
            while (reader.hasNext()) {
                keys.add(reader.nextName());
                reader.skipValue();
            }
        } catch (Exception e) {
            LogUtils.info("Failed to read room names from " + file.getName() + ": " + e.getMessage());
            return List.of();
        }
        return keys;
    }

    private static String baseRoomName(String jsonKey) {
        int variantSeparator = jsonKey.indexOf(':');
        return variantSeparator < 0 ? jsonKey : jsonKey.substring(0, variantSeparator);
    }

    private static String normalize(String roomName) {
        return baseRoomName(roomName).toLowerCase(Locale.ROOT);
    }
}
//#endif
