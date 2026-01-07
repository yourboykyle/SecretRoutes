package xyz.yourboykyle.secretroutes.utils;
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

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.OnSecretComplete;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;
import xyz.yourboykyle.secretroutes.utils.skyblocker.DungeonMapUtils;
import xyz.yourboykyle.secretroutes.utils.skyblocker.DungeonScanner;

import java.io.File;
import java.io.FileReader;
import java.util.*;

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
                String filePath;
                if (SRMConfig.get().routeType == SRMConfig.RouteType.PEARLS) {
                    String fileName = SRMConfig.get().pearlRoutesFileName;
                    filePath = Main.ROUTES_PATH + File.separator + (!fileName.equals("") ? fileName : "pearlroutes.json");
                } else {
                    String fileName = SRMConfig.get().routesFileName;
                    filePath = Main.ROUTES_PATH + File.separator + (!fileName.equals("") ? fileName : "routes.json");
                }
                getData(filePath);
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
        if (currentSecretWaypoints == null) return null;
        if (currentSecretWaypoints.has("secret")) {
            JsonArray location = currentSecretWaypoints.get("secret").getAsJsonObject().get("location").getAsJsonArray();

            if (DungeonScanner.getCurrentRoom() != null && DungeonScanner.getCurrentRoom().getDirection() != null && DungeonScanner.getCurrentRoom().getPhysicalCornerPos() != null) {
                return DungeonMapUtils.relativeToActual(
                        DungeonScanner.getCurrentRoom().getDirection(),
                        DungeonScanner.getCurrentRoom().getPhysicalCornerPos(),
                        new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt())
                );
            }
        }
        return null;
    }

    private ParticleEffect getParticleFromType(SRMConfig.ParticleType type) {
        return switch (type) {
            case EXPLOSION_NORMAL -> ParticleTypes.EXPLOSION;
            case EXPLOSION_LARGE, EXPLOSION_HUGE -> ParticleTypes.EXPLOSION_EMITTER;
            case FIREWORKS_SPARK -> ParticleTypes.FIREWORK;
            case BUBBLE -> ParticleTypes.BUBBLE;
            case WATER_SPLASH -> ParticleTypes.SPLASH;
            case WATER_WAKE -> ParticleTypes.FISHING;
            case SUSPENDED_DEPTH -> ParticleTypes.UNDERWATER;
            case CRIT -> ParticleTypes.CRIT;
            case MAGIC_CRIT -> ParticleTypes.ENCHANTED_HIT;
            case SMOKE_NORMAL -> ParticleTypes.SMOKE;
            case SMOKE_LARGE -> ParticleTypes.LARGE_SMOKE;
            case WITCH_MAGIC -> ParticleTypes.WITCH;
            case DRIP_WATER -> ParticleTypes.DRIPPING_WATER;
            case DRIP_LAVA -> ParticleTypes.DRIPPING_LAVA;
            case VILLAGER_ANGRY -> ParticleTypes.ANGRY_VILLAGER;
            case VILLAGER_HAPPY -> ParticleTypes.HAPPY_VILLAGER;
            case TOWN_AURA -> ParticleTypes.MYCELIUM;
            case NOTE -> ParticleTypes.NOTE;
            case PORTAL -> ParticleTypes.PORTAL;
            case ENCHANTMENT_TABLE -> ParticleTypes.ENCHANT;
            case FLAME -> ParticleTypes.FLAME;
            case LAVA -> ParticleTypes.LAVA;
            case FOOTSTEP, CLOUD -> ParticleTypes.CLOUD;
            case SNOWBALL, SNOW_SHOVEL -> ParticleTypes.ITEM_SNOWBALL;
            case SLIME -> ParticleTypes.ITEM_SLIME;
            case HEART -> ParticleTypes.HEART;
            case WATER_DROP -> ParticleTypes.RAIN;
            case ITEM_TAKE -> ParticleTypes.POOF;
            case MOB_APPEARANCE -> ParticleTypes.ELDER_GUARDIAN;
            default -> ParticleTypes.FLAME;
        };
    }

    public void renderLines() {
        try {
            if (currentSecretWaypoints != null && currentSecretWaypoints.has("locations")) {
                List<BlockPos> lines = new LinkedList<>();
                JsonArray lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();

                for (JsonElement lineLocationElement : lineLocations) {
                    JsonArray lineLocation = lineLocationElement.getAsJsonArray();

                    if (DungeonScanner.getCurrentRoom() != null && DungeonScanner.getCurrentRoom().getPhysicalCornerPos() != null) {
                        lines.add(DungeonMapUtils.relativeToActual(
                                DungeonScanner.getCurrentRoom().getDirection(),
                                DungeonScanner.getCurrentRoom().getPhysicalCornerPos(),
                                new BlockPos(lineLocation.get(0).getAsInt(), lineLocation.get(1).getAsInt(), lineLocation.get(2).getAsInt())
                        ));
                    }
                }

                if (SRMConfig.get().lineType == SRMConfig.LineType.PARTICLES) {
                    if (c < SRMConfig.get().tickInterval) {
                        c++;
                        return;
                    }
                    c = 0;
                    try {
                        ParticleEffect particle = getParticleFromType(SRMConfig.get().particles);
                        RenderUtils.drawLineMultipleParticles(particle, lines);
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
                Gson gson = new GsonBuilder().create();
                FileReader reader = new FileReader(filePath);
                JsonObject rawData = gson.fromJson(reader, JsonObject.class);
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
                    ClientPlayerEntity p = MinecraftClient.getInstance().player;
                    if (p == null) continue;
                    BlockPos pPos = p.getBlockPos();
                    if (pPos == null) continue;

                    if (DungeonScanner.getCurrentRoom() == null || !DungeonScanner.getCurrentRoom().isMatched())
                        continue;

                    BlockPos relPos = DungeonMapUtils.actualToRelative(
                            DungeonScanner.getCurrentRoom().getDirection(),
                            DungeonScanner.getCurrentRoom().getPhysicalCornerPos(),
                            pPos
                    );
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
