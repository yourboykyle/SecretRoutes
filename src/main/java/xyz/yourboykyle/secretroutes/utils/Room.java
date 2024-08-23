/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
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

import com.google.gson.*;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

public class Room {
    public enum WAYPOINT_TYPES {
        LOCATIONS,
        ETHERWARPS,
        MINES,
        INTERACTS,
        TNTS,
        ENDERPEARLS,
    };
    public enum SECRET_TYPES {
        INTERACT,
        ITEM,
        BAT
    };
    public String name;
    public JsonArray currentSecretRoute;
    public int currentSecretIndex = 0;
    public JsonObject currentSecretWaypoints;

    public Room(String roomName) {
        currentSecretIndex = 0;

        try {
            name = roomName;

            if (roomName != null) {
                String filePath = Main.ROUTES_PATH + File.separator + SRMConfig.routesFileName;

                Gson gson = new GsonBuilder().create();
                FileReader reader = new FileReader(filePath);

                JsonObject data = gson.fromJson(reader, JsonObject.class);

                if(data != null && data.get(name) != null) {
                    currentSecretRoute = data.get(name).getAsJsonArray();
                    currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
                }
            } else {
                currentSecretRoute = null;
            }
        } catch(Exception e) {
           LogUtils.error(e);
        }
    }

    public Room(String roomName, String filePath) {
        currentSecretIndex = 0;

        try {
            name = roomName;

            if (roomName != null) {
                Gson gson = new GsonBuilder().create();
                FileReader reader = new FileReader(filePath);

                JsonObject data = gson.fromJson(reader, JsonObject.class);

                if(data != null && data.get(name) != null) {
                    currentSecretRoute = data.get(name).getAsJsonArray();
                    currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
                }
            } else {
                currentSecretRoute = null;
            }
        } catch(Exception e) {
            LogUtils.error(e);
        }
    }

    public void lastSecretKeybind() {
        if(currentSecretIndex > 0) {
            currentSecretIndex--;
        }

        if(!(currentSecretIndex >= currentSecretRoute.size())) {
            currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
        } else {
            currentSecretWaypoints = null;
        }
    }

    public void nextSecret() {
        currentSecretIndex++;

        if(!(currentSecretIndex >= currentSecretRoute.size())) {
            currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
        } else {
            currentSecretWaypoints = null;
        }
    }

    public void nextSecretKeybind() {
        if(currentSecretRoute != null) {
            if(currentSecretIndex < currentSecretRoute.size() - 1) {
                currentSecretIndex++;
            }

            if (!(currentSecretIndex >= currentSecretRoute.size())) {
                currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
            } else {
                currentSecretWaypoints = null;
            }
        }
    }

    public SECRET_TYPES getSecretType() {
        if(currentSecretWaypoints != null && currentSecretWaypoints.get("secret") != null && currentSecretWaypoints.get("secret").getAsJsonObject().get("type") != null) {
            String type = currentSecretWaypoints.get("secret").getAsJsonObject().get("type").getAsString();
            if(type.equals("interact")) {
                return SECRET_TYPES.INTERACT;
            } else if(type.equals("item")) {
                return SECRET_TYPES.ITEM;
            } else if(type.equals("bat")) {
                return SECRET_TYPES.BAT;
            }
        }

        return null;
    }

    public BlockPos getSecretLocation() {
        if(currentSecretWaypoints.get("secret") != null && currentSecretWaypoints.get("secret").getAsJsonObject().get("location") != null) {
            JsonArray location = currentSecretWaypoints.get("secret").getAsJsonObject().get("location").getAsJsonArray();

            Main.checkRoomData();
            return MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
        }
        return null;
    }

    public void renderLines() {
        if(currentSecretWaypoints != null && currentSecretWaypoints.get("locations") != null) {
            // Render the lines
            List<BlockPos> lines = new LinkedList<>();

            JsonArray lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();
            for (JsonElement lineLocationElement : lineLocations) {
                JsonArray lineLocation = lineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                lines.add(MapUtils.relativeToActual(new BlockPos(lineLocation.get(0).getAsInt(), lineLocation.get(1).getAsInt(), lineLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner));
            }


            if(SRMConfig.lineType == 0) {
                // Draw flame particles
                RenderUtils.drawLineMultipleParticles(EnumParticleTypes.FLAME, lines);
            }
        }
    }
}