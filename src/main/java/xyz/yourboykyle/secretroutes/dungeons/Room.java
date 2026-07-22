//#if FABRIC
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

package xyz.yourboykyle.secretroutes.dungeons;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.OnSecretComplete;
import xyz.yourboykyle.secretroutes.dungeons.detection.DungeonScanner;
import xyz.yourboykyle.secretroutes.utils.*;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;

import java.io.File;
import java.io.FileReader;
import java.util.*;

import static xyz.yourboykyle.secretroutes.utils.ParticleUtils.getParticleFromType;

public class Room {
    public enum WAYPOINT_TYPES { LOCATIONS, ETHERWARPS, MINES, INTERACTS, TNTS, ENDERPEARLS }
    public enum SECRET_TYPES { INTERACT, ITEM, BAT, EXITROUTE }

    public String name;
    public JsonArray currentSecretRoute;
    public int currentSecretIndex = 0;
    public JsonObject currentSecretWaypoints;
    public ArrayList<JsonArray> arrays = new ArrayList<>();
    public Triple<String, Integer, Double> closest = null;
    int c = 0;

    public Room(String roomName) {
        currentSecretIndex = 0;
        try {
            name = roomName;
            if (roomName != null) {
                String filePath = "";
                if (SRMConfig.get().routeType == SRMConfig.RouteType.ROUTE_FOW) {
                    String fileName = SRMConfig.get().routeFOWFileName;
                    filePath = Main.ROUTES_PATH + File.separator + (!fileName.equals("") ? fileName : "fowroutes.json");
                } else if (SRMConfig.get().routeType == SRMConfig.RouteType.ROUTE_3ppopka) {
                    String fileName = SRMConfig.get().route3ppopkaFileName;
                    filePath = Main.ROUTES_PATH + File.separator + (!fileName.equals("") ? fileName : "3ppopkaroutes.json");
                }

                if (!filePath.isEmpty()) {
                    getData(filePath);
                }
            } else {
                currentSecretRoute = null;
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public Room(String roomName, String filePath) {
        currentSecretIndex = 0;
        try {
            name = roomName;
            if (roomName != null) {
                getData(filePath);
            } else {
                currentSecretRoute = null;
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public void lastSecretKeybind() {
        PBUtils.pbIsValid = false;
        if (currentSecretIndex > 0) currentSecretIndex--;
        updateWaypoints();
    }

    public void nextSecret() {
        OnSecretComplete.onSecretCompleteNoKeybind();
        currentSecretIndex++;
        updateWaypoints();
    }

    public void nextSecretKeybind() {
        PBUtils.pbIsValid = false;
        if (currentSecretRoute != null && currentSecretIndex < currentSecretRoute.size() - 1) {
            currentSecretIndex++;
        }
        updateWaypoints();
    }

    private void updateWaypoints() {
        if (currentSecretRoute != null && currentSecretIndex < currentSecretRoute.size()) {
            currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
        } else {
            currentSecretWaypoints = null;
        }
    }

    public SECRET_TYPES getSecretType() {
        try {
            if (currentSecretWaypoints != null && currentSecretWaypoints.has("secret")) {
                String type = currentSecretWaypoints.get("secret").getAsJsonObject().get("type").getAsString();
                return switch (type) {
                    case "interact" -> SECRET_TYPES.INTERACT;
                    case "item" -> SECRET_TYPES.ITEM;
                    case "bat" -> SECRET_TYPES.BAT;
                    case "exitroute" -> SECRET_TYPES.EXITROUTE;
                    default -> null;
                };
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
        return null;
    }

    public BlockPos getSecretLocation() {
        if (currentSecretWaypoints == null || !currentSecretWaypoints.has("secret")) return null;
        JsonArray location = currentSecretWaypoints.get("secret").getAsJsonObject().get("location").getAsJsonArray();

        return RoomRotationUtils.relativeToActual(
                new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()),
                RoomDirectionUtils.roomDirection(),
                RoomDirectionUtils.roomCorner()
        );
    }

    public void renderLines() {
        try {
            if (currentSecretWaypoints != null && currentSecretWaypoints.has("locations")) {
                List<BlockPos> lines = new LinkedList<>();
                JsonArray lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();

                for (JsonElement lineLocationElement : lineLocations) {
                    JsonArray loc = lineLocationElement.getAsJsonArray();
                    BlockPos relative = new BlockPos(loc.get(0).getAsInt(), loc.get(1).getAsInt(), loc.get(2).getAsInt());
                    BlockPos actual = RoomRotationUtils.relativeToActual(relative, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
                    lines.add(actual);
                }

                if (SRMConfig.get().lineType == SRMConfig.LineType.PARTICLES) {
                    if (c < SRMConfig.get().tickInterval) {
                        c++;
                        return;
                    }
                    c = 0;
                    try {
                        ParticleOptions particle = getParticleFromType(SRMConfig.get().particles);
                        ParticleUtils.drawLineMultipleParticles(particle, lines);
                    } catch (Exception e) {
                        LogUtils.error(e);
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public void getData(String filePath) {
        new Thread(() -> {
            try {
                if (DungeonScanner.currentRoom == null || DungeonScanner.currentRoom.getName() == null) return;

                Gson gson = new GsonBuilder().create();
                FileReader reader = new FileReader(filePath);
                JsonObject rawData = gson.fromJson(reader, JsonObject.class);
                reader.close();

                if (rawData == null || rawData.isJsonNull()) {
                    currentSecretRoute = null;
                    return;
                }

                Map<String, JsonElement> data = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : rawData.entrySet()) {
                    data.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
                }

                HashMap<String, Integer> map = new HashMap<>();

                for (int i = 0; i <= 10; i++) {
                    String path = (i == 0 ? name : name + ":" + i).toLowerCase(Locale.ROOT);
                    JsonElement element = data.get(path);
                    if (element == null || element.isJsonNull()) {
                        if (i == 0) currentSecretRoute = null;
                        continue;
                    }

                    JsonArray route = element.getAsJsonArray();
                    arrays.add(route);
                    JsonArray starPoseArray = route.get(0).getAsJsonObject().get("locations").getAsJsonArray().get(0).getAsJsonArray();
                    BlockPos startPos = new BlockPos(starPoseArray.get(0).getAsInt(), starPoseArray.get(1).getAsInt(), starPoseArray.get(2).getAsInt());
                    map.put(BlockUtils.blockPos(startPos), i);
                }

                int i = 0;
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    LocalPlayer p = Minecraft.getInstance().player;
                    if (p == null) continue;
                    BlockPos pPos = p.blockPosition();
                    if (pPos == null) continue;

                    BlockPos relPos = RoomRotationUtils.actualToRelative(pPos, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());

                    double dist1 = BlockUtils.blockDistance(relPos, entry.getKey());

                    if (closest != null) {
                        double dist2 = closest.getThree();
                        if (dist1 > dist2) continue;
                    }

                    closest = new Triple<>(entry.getKey(), i, dist1);
                    i++;
                }

                if (closest != null) {
                    currentSecretRoute = arrays.get(closest.getTwo());
                    currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
                    System.out.println("route: " + currentSecretRoute);
                    System.out.println("waypoints: " + currentSecretWaypoints);
                }
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }).start();
    }
}
//#endif
