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

package xyz.yourboykyle.secretroutes.dungeons;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.dungeons.detection.DungeonScanner;
import xyz.yourboykyle.secretroutes.events.OnSecretComplete;
import xyz.yourboykyle.secretroutes.utils.*;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static xyz.yourboykyle.secretroutes.utils.ParticleUtils.getParticleFromType;

public class Room {
    private static final ExecutorService ROUTE_LOADER = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "SecretRoutes-RouteLoader");
        thread.setDaemon(true);
        return thread;
    });
    public enum WAYPOINT_TYPES { LOCATIONS, ETHERWARPS, MINES, INTERACTS, TNTS, ENDERPEARLS, BONZO_STAFFS }
    public enum SECRET_TYPES { INTERACT, ITEM, BAT, EXITROUTE }

    public String name;
    public JsonArray currentSecretRoute;
    public int currentSecretIndex = 0;
    public JsonObject currentSecretWaypoints;
    private List<RouteVariantSelector.RouteVariant> routeVariants = List.of();
    private int selectedRouteIndex = -1;
    private double selectedRouteDistanceSquared = Double.POSITIVE_INFINITY;
    private long loadRequestId;
    private SRMConfig.RouteType configuredRouteType;
    private RouteAttempt personalBestAttempt;
    int c = 0;

    public Room(String roomName) {
        currentSecretIndex = 0;
        try {
            name = roomName;
            if (roomName != null) {
                getData(configuredRoutePath());
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
                configuredRouteType = SRMConfig.get().effectiveRouteType(name);
                getData(filePath);
            } else {
                currentSecretRoute = null;
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public void lastSecretKeybind() {
        invalidatePersonalBest();
        if (currentSecretIndex > 0) currentSecretIndex--;
        updateWaypoints();
    }

    public void nextSecret() {
        OnSecretComplete.onSecretCompleteNoKeybind();
        currentSecretIndex++;
        updateWaypoints();
    }

    public void nextSecretKeybind() {
        invalidatePersonalBest();
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

    public boolean cycleRoute(int direction) {
        if (routeVariants.size() <= 1) return false;

        int newIndex = RouteVariantSelector.cycleIndex(selectedRouteIndex, routeVariants.size(), direction);
        return selectRoute(newIndex);
    }

    public int getSelectedRouteIndex() {
        return selectedRouteIndex;
    }

    public int getRouteVariantCount() {
        return routeVariants.size();
    }

    public String getSelectedRouteStatus() {
        if (selectedRouteIndex < 0 || selectedRouteIndex >= routeVariants.size()) return "No route selected";
        return routeVariants.get(selectedRouteIndex).jsonKey() + " (" + (selectedRouteIndex + 1) + "/" + routeVariants.size() + ")";
    }

    private boolean selectRoute(int routeIndex) {
        if (routeIndex < 0 || routeIndex >= routeVariants.size()) return false;

        RouteVariantSelector.RouteVariant route = routeVariants.get(routeIndex);
        selectedRouteIndex = routeIndex;
        currentSecretRoute = route.steps();
        currentSecretIndex = 0;
        updateWaypoints();
        SecretUtils.clearEtherwarpTargetTracking();

        if (personalBestAttempt != null) personalBestAttempt.bindRoute(currentSecretRoute);
        return true;
    }

    public void startPersonalBestVisit() {
        personalBestAttempt = name != null && !"f7boss".equals(name) && SRMConfig.get().trackPersonalBests
                ? new RouteAttempt(System::nanoTime) : null;
        if (personalBestAttempt != null) personalBestAttempt.bindRoute(currentSecretRoute);
    }

    public void invalidatePersonalBest() {
        if (personalBestAttempt != null) personalBestAttempt.invalidate();
    }

    /** Loading a custom route is not a new room entry. Keep the visit's original clock. */
    public void inheritPersonalBestVisit(Room previous) {
        if (previous != null && Objects.equals(name, previous.name)) {
            personalBestAttempt = previous.personalBestAttempt;
            previous.personalBestAttempt = null;
        }
    }

    public void recordPersonalBestStep() {
        if (personalBestAttempt == null) return;
        if (!SRMConfig.get().trackPersonalBests) {
            invalidatePersonalBest();
            return;
        }
        OptionalLong completed = personalBestAttempt.completeStep(currentSecretIndex);
        if (completed.isPresent()) {
            PBUtils.completeRoute(name, completed.getAsLong(), personalBestAttempt.splitsMillis());
        }
    }

    public SECRET_TYPES getSecretType() {
        try {
            if (currentSecretWaypoints != null && currentSecretWaypoints.has("secret") && !currentSecretWaypoints.get("secret").isJsonNull()) {
                JsonObject secretObj = currentSecretWaypoints.getAsJsonObject("secret");
                if (secretObj.has("type")) {
                    String type = secretObj.get("type").getAsString();
                    return switch (type) {
                        case "interact" -> SECRET_TYPES.INTERACT;
                        case "item" -> SECRET_TYPES.ITEM;
                        case "bat" -> SECRET_TYPES.BAT;
                        case "exitroute" -> SECRET_TYPES.EXITROUTE;
                        default -> null;
                    };
                }
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
        return null;
    }

    public BlockPos getSecretLocation() {
        if (currentSecretWaypoints == null || !currentSecretWaypoints.has("secret") || currentSecretWaypoints.get("secret").isJsonNull()) return null;

        JsonObject secretObj = currentSecretWaypoints.getAsJsonObject("secret");
        if (!secretObj.has("location")) return null;

        JsonArray location = secretObj.get("location").getAsJsonArray();

        BlockPos relative = new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt());
        if ("f7boss".equals(name)) return relative;

        return RoomRotationUtils.relativeToActual(
                relative,
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

                    BlockPos actual;
                    if ("f7boss".equals(name)) {
                        actual = relative;
                    } else {
                        actual = RoomRotationUtils.relativeToActual(relative, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
                    }
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

    private String configuredRoutePath() {
        configuredRouteType = SRMConfig.get().effectiveRouteType(name);
        boolean fow = configuredRouteType != SRMConfig.RouteType.ROUTE_3ppopka;
        String fileName = fow ? SRMConfig.get().routeFOWFileName : SRMConfig.get().route3ppopkaFileName;
        if (fileName == null || fileName.isEmpty()) fileName = fow ? "fowroutes.json" : "3ppopkaroutes.json";
        return Main.ROUTES_PATH + File.separator + fileName;
    }

    /** Called on the client thread after the provider setting is committed. */
    public void reloadIfRouteTypeChanged() {
        if (configuredRouteType != SRMConfig.get().effectiveRouteType(name)) reloadConfiguredRoute();
    }

    public void reloadConfiguredRoute() {
        if (Main.currentRoom != this || name == null || !LocationUtils.isInDungeons()) return;
        clearRoute();
        SecretUtils.clearEtherwarpTargetTracking();
        EtherwarpAimAssist.reset();
        String path = configuredRoutePath();
        RouteFileCache.SHARED.invalidate(Path.of(path));
        getData(path, true);
    }

    private void clearRoute() {
        routeVariants = List.of();
        selectedRouteIndex = -1;
        selectedRouteDistanceSquared = Double.POSITIVE_INFINITY;
        currentSecretIndex = 0;
        currentSecretRoute = null;
        currentSecretWaypoints = null;
        c = 0;
    }

    public void getData(String filePath) {
        getData(filePath, false);
    }

    private void getData(String filePath, boolean notifyMissing) {
        long requestId = ++loadRequestId;
        Minecraft client = Minecraft.getInstance();
        ClientLevel level = client.level;
        String roomName = name;
        ROUTE_LOADER.execute(() -> {
            try {
                JsonObject rawData = RouteFileCache.SHARED.roomData(Path.of(filePath), roomName);
                List<RouteVariantSelector.RouteVariant> loadedVariants =
                        RouteVariantSelector.parseVariants(rawData, roomName);
                client.execute(() -> {
                    if (!isCurrentLoad(requestId, level)) return;
                    installLoadedVariants(loadedVariants);
                    if (notifyMissing && loadedVariants.isEmpty()) {
                        ChatUtils.sendChatMessage("§eNo route for " + roomName + " in " + new File(filePath).getName() + ".");
                    }
                });
            } catch (Exception e) {
                client.execute(() -> {
                    if (!isCurrentLoad(requestId, level)) return;
                    LogUtils.error(e);
                    if (notifyMissing) {
                        ChatUtils.sendChatMessage("§cCould not load " + new File(filePath).getName() + ". Check the route file and try switching again.");
                    }
                });
            }
        });
    }

    private boolean isCurrentLoad(long requestId, ClientLevel level) {
        Minecraft client = Minecraft.getInstance();
        return requestId == loadRequestId && Main.currentRoom == this
                && level != null && client.level == level && client.player != null
                && LocationUtils.isInDungeons();
    }

    private void installLoadedVariants(List<RouteVariantSelector.RouteVariant> loadedVariants) {
        if (Main.currentRoom != this) return;
        if (!"f7boss".equals(name) && DungeonScanner.currentRoom == null) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        routeVariants = loadedVariants;
        if (routeVariants.isEmpty()) {
            clearRoute();
            return;
        }

        BlockPos playerPosition;
        if ("f7boss".equals(name)) {
            playerPosition = player.blockPosition();
        } else {
            playerPosition = RoomRotationUtils.actualToRelative(
                    player.blockPosition(),
                    RoomDirectionUtils.roomDirection(),
                    RoomDirectionUtils.roomCorner()
            );
        }

        RouteVariantSelector.Selection selection =
                RouteVariantSelector.selectClosest(routeVariants, playerPosition);
        if (selection == null) return;

        selectedRouteDistanceSquared = selection.distanceSquared();
        selectRoute(selection.index());
        LogUtils.info("Selected route " + getSelectedRouteStatus()
                + " at distance " + String.format(Locale.ROOT, "%.2f", Math.sqrt(selectedRouteDistanceSquared)));
    }
}
//#endif
