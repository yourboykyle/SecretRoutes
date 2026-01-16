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

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.Room.SECRET_TYPES;
import xyz.yourboykyle.secretroutes.utils.Room.WAYPOINT_TYPES;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

public class RouteRecording {
    private static final String verboseTag = "Recording";
    public static boolean malformed = false;
    private static int tntWaypoints = 0;
    private static int interactWaypoints = 0;
    private static int minewaypoints = 0;
    private static int etherwaypoints = 0;
    private static int locationwaypoints = 0;
    private static int enderPearlWaypoints = 0;
    private static int enderPearlAngleWaypoints = 0;
    public boolean recording = false;
    public JsonObject allSecretRoutes = new JsonObject();
    public JsonArray currentSecretRoute = new JsonArray();
    public JsonObject currentSecretWaypoints = new JsonObject();
    public String recordingMessage = "Recording...";
    // Each waypoint on the locations will be added based on how far the player is from the previous waypoint, this will be used to keep track of said previous waypoint
    public BlockPos previousLocation;

    public RouteRecording() {
        // Create placeholders for the waypoints, as the coordinates are 2D arrays
        currentSecretWaypoints.add("locations", new JsonArray());
        currentSecretWaypoints.add("etherwarps", new JsonArray());
        currentSecretWaypoints.add("mines", new JsonArray());
        currentSecretWaypoints.add("interacts", new JsonArray());
        currentSecretWaypoints.add("tnts", new JsonArray());
        currentSecretWaypoints.add("enderpearls", new JsonArray());
        currentSecretWaypoints.add("enderpearlangles", new JsonArray());

        // Import all the current secret routes into the allSecretRoutes JsonObject
        importRoutes("routes.json");
    }

    public static String prettyPrint(JsonObject jsonObject) {
        // Pretty print the secret routes
        Gson gson = new GsonBuilder().create();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            stringBuilder.append("\t\"").append(entry.getKey()).append("\": ").append(gson.toJson(entry.getValue())).append(",\n");
        }

        // Remove the last comma and add a new line
        stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    public void startRecording() {
        // Start recording the secret route
        recording = true;
        sendVerboseMessage("§eRecording started...", verboseTag);

        // HUDs are now handled via Fabric Event Loop checking the 'recording' boolean
        // xyz.yourboykyle.secretroutes.Main.recordingHUD.enable(); -> Removed
        // xyz.yourboykyle.secretroutes.Main.currentRoomHUD.enable(); -> Removed
    }

    public void stopRecording() {
        // Stop recording the secret route
        recording = false;
        //Log the results of the recording
        newRoute();
        sendVerboseMessage("§eRecording stopped.", verboseTag);
        sendVerboseMessage("§6  Locations: " + locationwaypoints, verboseTag);
        sendVerboseMessage("§6  Etherwarps: " + etherwaypoints, verboseTag);
        sendVerboseMessage("§6  Mines: " + minewaypoints, verboseTag);
        sendVerboseMessage("§6  Interacts: " + interactWaypoints, verboseTag);
        sendVerboseMessage("§6  TNTs: " + tntWaypoints, verboseTag);
        sendVerboseMessage("§6  Enderpearls: " + enderPearlWaypoints, verboseTag);
        sendVerboseMessage("§6  Enderpearl Angles (should equal enderpearls): " + enderPearlAngleWaypoints, verboseTag);

        locationwaypoints = 0;
        etherwaypoints = 0;
        minewaypoints = 0;
        interactWaypoints = 0;
        tntWaypoints = 0;
        enderPearlWaypoints = 0;
        enderPearlAngleWaypoints = 0;
    }

    public void importRoutes(String fileName) {
        String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + fileName;

        if (new File(filePath).exists()) {
            // Import all the current secret routes into the allSecretRoutes JsonObject from a file
            allSecretRoutes = new JsonObject();

            try {
                Gson gson = new GsonBuilder().create();
                FileReader reader = new FileReader(filePath);

                allSecretRoutes = gson.fromJson(reader, JsonObject.class);
                reader.close();
            } catch (JsonSyntaxException e) {
                LogUtils.error(e);
                malformed = true;
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }
    }

    public void addWaypoint(WAYPOINT_TYPES type, BlockPos pos) {
        sendVerboseMessage("§d Adding waypoint...", verboseTag);
        // Add a non-secret waypoint to the current secret waypoints
        Main.checkRoomData();
        BlockPos relPos = RoomRotationUtils.actualToRelative(pos, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());

        JsonArray posArray = new JsonArray();

        posArray.add(new JsonPrimitive(relPos.getX()));
        posArray.add(new JsonPrimitive(relPos.getY()));
        posArray.add(new JsonPrimitive(relPos.getZ()));

        sendVerboseMessage("§d  Waypoint Type: " + type, verboseTag);
        if (type == WAYPOINT_TYPES.LOCATIONS) {
            boolean shouldAddWaypoint = true;
            int count = 0;

            JsonArray array = currentSecretWaypoints.get("locations").getAsJsonArray();
            for (JsonElement element : array) {
                JsonArray location = element.getAsJsonArray();
                if (count < array.size() && location.equals(posArray)) {
                    shouldAddWaypoint = false;
                    break;
                }

                count++;
            }

            if (shouldAddWaypoint) {
                locationwaypoints++;
                sendVerboseMessage("§d  Adding location waypoint...", verboseTag);
                currentSecretWaypoints.get("locations").getAsJsonArray().add(posArray);
            }
        } else if (type == WAYPOINT_TYPES.ETHERWARPS) {
            etherwaypoints++;
            boolean shouldAddWaypoint = true;
            int count = 0;

            JsonArray array = currentSecretWaypoints.get("etherwarps").getAsJsonArray();
            for (JsonElement element : array) {
                JsonArray location = element.getAsJsonArray();
                if (count < array.size() && location.equals(posArray)) {
                    shouldAddWaypoint = false;
                    break;
                }

                count++;
            }

            if (shouldAddWaypoint) {
                sendVerboseMessage("§d  Adding etherwarp waypoint...", verboseTag);
                currentSecretWaypoints.get("etherwarps").getAsJsonArray().add(posArray);
            }
        } else if (type == WAYPOINT_TYPES.MINES) {
            minewaypoints++;
            boolean shouldAddWaypoint = true;
            int count = 0;

            JsonArray array = currentSecretWaypoints.get("mines").getAsJsonArray();
            for (JsonElement element : array) {
                JsonArray location = element.getAsJsonArray();
                if (count < array.size() && location.equals(posArray)) {
                    shouldAddWaypoint = false;
                    break;
                }

                count++;
            }

            if (shouldAddWaypoint) {
                sendVerboseMessage("§d  Adding mine waypoint...", verboseTag);
                currentSecretWaypoints.get("mines").getAsJsonArray().add(posArray);
            }
        } else if (type == WAYPOINT_TYPES.INTERACTS) {
            interactWaypoints++;
            boolean shouldAddWaypoint = true;
            int count = 0;

            JsonArray array = currentSecretWaypoints.get("interacts").getAsJsonArray();
            for (JsonElement element : array) {
                JsonArray location = element.getAsJsonArray();
                if (count < array.size() && location.equals(posArray)) {
                    shouldAddWaypoint = false;
                    break;
                }

                count++;
            }

            if (shouldAddWaypoint) {
                sendVerboseMessage("§d  Adding interact waypoint...", verboseTag);
                currentSecretWaypoints.get("interacts").getAsJsonArray().add(posArray);
            }
        } else if (type == WAYPOINT_TYPES.TNTS) {
            tntWaypoints++;
            boolean shouldAddWaypoint = true;
            int count = 0;

            JsonArray array = currentSecretWaypoints.get("tnts").getAsJsonArray();
            for (JsonElement element : array) {
                JsonArray location = element.getAsJsonArray();
                if (count < array.size() && location.equals(posArray)) {
                    sendVerboseMessage("§5 Waypoint not added", verboseTag);
                    shouldAddWaypoint = false;
                    break;
                }

                count++;
            }

            if (shouldAddWaypoint) {
                sendVerboseMessage("§d  Adding TNT waypoint...", verboseTag);
                currentSecretWaypoints.get("tnts").getAsJsonArray().add(posArray);
            }
        }
    }

    public void addWaypoint(WAYPOINT_TYPES type, ClientPlayerEntity player) {
        sendVerboseMessage("§d Adding waypoint...", verboseTag);
        // Add a non-secret waypoint to the current secret waypoints
        Main.checkRoomData();
        JsonArray posArray = new JsonArray();

        Triple<Double, Double, Double> relativePos = RoomRotationUtils.actualToRelative(player.getX(), player.getY(), player.getZ(), RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
        posArray.add(new JsonPrimitive(relativePos.getOne()));
        posArray.add(new JsonPrimitive(relativePos.getTwo()));
        posArray.add(new JsonPrimitive(relativePos.getThree()));

        sendVerboseMessage("§d  Waypoint Type: " + type, verboseTag);
        if (type == WAYPOINT_TYPES.ENDERPEARLS) {
            enderPearlWaypoints++;
            enderPearlAngleWaypoints++;
            boolean shouldAddWaypoint = true;
            int count = 0;

            JsonArray array = currentSecretWaypoints.get("enderpearls").getAsJsonArray();
            for (JsonElement element : array) {
                JsonArray location = element.getAsJsonArray();
                if (count < array.size() && location.equals(posArray)) {
                    shouldAddWaypoint = false;
                    break;
                }

                count++;
            }

            JsonArray anglePosArray = new JsonArray();
            if (MinecraftClient.getInstance().player == null) return;
            anglePosArray.add(new JsonPrimitive(MinecraftClient.getInstance().player.getPitch()));
            anglePosArray.add(new JsonPrimitive(RotationUtils.actualToRelativeYaw(MinecraftClient.getInstance().player.getYaw() % 360, RoomDirectionUtils.roomDirection())));

            JsonArray anglesArray = currentSecretWaypoints.get("enderpearlangles").getAsJsonArray();
            for (JsonElement element : anglesArray) {
                JsonArray rotation = element.getAsJsonArray();
                if (count < array.size() && rotation.equals(anglePosArray)) {
                    shouldAddWaypoint = false;
                    break;
                }

                count++;
            }

            if (shouldAddWaypoint) {
                sendVerboseMessage("§d  Adding Ender Pearl waypoint...", verboseTag);
                currentSecretWaypoints.get("enderpearls").getAsJsonArray().add(posArray);
                currentSecretWaypoints.get("enderpearlangles").getAsJsonArray().add(anglePosArray);
            }

        }
    }

    public boolean addWaypoint(SECRET_TYPES type, BlockPos pos) {
        // Add a secret waypoint to the current secret waypoints
        Main.checkRoomData();
        BlockPos relPos = RoomRotationUtils.actualToRelative(pos, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());

        JsonArray posArray = new JsonArray();

        posArray.add(new JsonPrimitive(relPos.getX()));
        posArray.add(new JsonPrimitive(relPos.getY()));
        posArray.add(new JsonPrimitive(relPos.getZ()));

        JsonObject secret = new JsonObject();

        if (type == SECRET_TYPES.INTERACT) {
            secret.add("type", new JsonPrimitive("interact"));
        } else if (type == SECRET_TYPES.ITEM) {
            secret.add("type", new JsonPrimitive("item"));
        } else if (type == SECRET_TYPES.BAT) {
            secret.add("type", new JsonPrimitive("bat"));
        } else if (type == SECRET_TYPES.EXITROUTE) {
            secret.add("type", new JsonPrimitive("exitroute"));
        }

        // Make sure the secret hasn't already been recorded
        boolean shouldAddWaypoint = true;

        int count = 0;
        for (JsonElement element : currentSecretRoute) {
            JsonObject waypoints = element.getAsJsonObject();
            if (count < currentSecretRoute.size() && waypoints.get("secret") != null && waypoints.get("secret").getAsJsonObject().get("location") != null) {
                JsonObject secretWaypoints = waypoints.get("secret").getAsJsonObject();
                JsonArray location = secretWaypoints.get("location").getAsJsonArray();

                if (location.equals(posArray)) {
                    shouldAddWaypoint = false;
                }
            }

            count++;
        }

        secret.add("location", posArray);
        if (shouldAddWaypoint) {
            currentSecretWaypoints.add("secret", secret);
            return true;
        } else {
            return false;
        }
    }

    public void newSecret() {
        // Add current secret waypoints to the route and start a new secret waypoints list
        currentSecretRoute.add(currentSecretWaypoints);

        currentSecretWaypoints = new JsonObject();

        currentSecretWaypoints.add("locations", new JsonArray());
        currentSecretWaypoints.add("etherwarps", new JsonArray());
        currentSecretWaypoints.add("mines", new JsonArray());
        currentSecretWaypoints.add("interacts", new JsonArray());
        currentSecretWaypoints.add("tnts", new JsonArray());
        currentSecretWaypoints.add("enderpearls", new JsonArray());
        currentSecretWaypoints.add("enderpearlangles", new JsonArray());
    }

    public void newRoute() {
        // Start a new secret route recording
        int routeNumber = SRMConfig.get().routeNumber;
        allSecretRoutes.add(routeNumber != 0 ? RoomDirectionUtils.roomName() + ":" + routeNumber : RoomDirectionUtils.roomName(), currentSecretRoute);
        currentSecretRoute = new JsonArray();

        currentSecretWaypoints = new JsonObject();

        currentSecretWaypoints.add("locations", new JsonArray());
        currentSecretWaypoints.add("etherwarps", new JsonArray());
        currentSecretWaypoints.add("mines", new JsonArray());
        currentSecretWaypoints.add("interacts", new JsonArray());
        currentSecretWaypoints.add("tnts", new JsonArray());
        currentSecretWaypoints.add("enderpearls", new JsonArray());
        currentSecretWaypoints.add("enderpearlangles", new JsonArray());
    }

    public void exportAllRoutes() {
        // Export the secret route to a file
        String filePath = System.getProperty("user.home") + File.separator + "Downloads";
        String fileName = "routes.json";

        File file = new File(filePath, fileName);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(prettyPrint(allSecretRoutes));
            writer.flush();
            writer.close();
            sendChatMessage(Formatting.DARK_GREEN + "Exported routes to " + filePath + File.separator + fileName);
        } catch (IOException e) {
            LogUtils.error(e);
        }
    }

    public void setRecordingMessage(String message) {
        recordingMessage = message;
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                if (recordingMessage.equals(message)) {
                    recordingMessage = "Recording...";
                }
            } catch (InterruptedException e) {
                LogUtils.error(e);
            }
        }).start();
    }
}
