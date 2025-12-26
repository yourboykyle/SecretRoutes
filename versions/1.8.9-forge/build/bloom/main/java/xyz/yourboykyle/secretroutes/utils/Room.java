//#if FORGE && MC == 1.8.9
// TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
 *
 *<DO NOT REMOVE THIS COPYRIGHT NOTICE>
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.MapUtils;
import xyz.yourboykyle.secretroutes.events.OnSecretComplete;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Room {
    int c = 0;
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
        BAT,
        EXITROUTE
    };
    public String name;
    public JsonArray currentSecretRoute;
    public JsonArray currentSecretRoute2;
    public int currentSecretIndex = 0;
    public JsonObject currentSecretWaypoints;
    public JsonArray tests;
    public HashMap<String, Integer> map = new HashMap<>();
    public PrintingUtils printer = new PrintingUtils();
    public ArrayList<JsonArray> arrays = new ArrayList<>();
    public Triple<String, Integer, Double> closest = null;



    public Room(String roomName) {
        currentSecretIndex = 0;

        try {
            name = roomName;

            if (roomName != null) {
                String filePath;
                if(SRMConfig.routeTypeIndex == 1) {
                    filePath = Main.ROUTES_PATH + File.separator + (!SRMConfig.pearlRoutesFileName.equals("") ? SRMConfig.pearlRoutesFileName : "pearlroutes.json");
                }else{
                    filePath = Main.ROUTES_PATH + File.separator + (!SRMConfig.routesFileName.equals("") ? SRMConfig.routesFileName  : "pearlroutes.json");
                }
                getData(filePath);
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
                getData(filePath);
            }
        } catch(Exception e) {
            LogUtils.error(e);
        }
    }

    public void lastSecretKeybind() {
        PBUtils.pbIsValid = false;

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
        OnSecretComplete.onSecretCompleteNoKeybind();

        currentSecretIndex++;

        if(!(currentSecretIndex >= currentSecretRoute.size())) {
            currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
        } else {
            currentSecretWaypoints = null;
        }
    }

    public void nextSecretKeybind() {
        PBUtils.pbIsValid = false;

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
        try{
            if(currentSecretWaypoints != null && currentSecretWaypoints.get("secret") != null && currentSecretWaypoints.get("secret").getAsJsonObject().get("type") != null) {
                String type = currentSecretWaypoints.get("secret").getAsJsonObject().get("type").getAsString();
                switch (type) {
                    case "interact":
                        return SECRET_TYPES.INTERACT;
                    case "item":
                        return SECRET_TYPES.ITEM;
                    case "bat":
                        return SECRET_TYPES.BAT;
                    case "exitroute":
                        return SECRET_TYPES.EXITROUTE;
                }
            }
        }catch (Exception e){
            LogUtils.error(e);
        }

        return null;
    }

    public BlockPos getSecretLocation() {
        if(currentSecretWaypoints == null){return null;}
        if(currentSecretWaypoints.get("secret") != null && currentSecretWaypoints.get("secret").getAsJsonObject().get("location") != null) {
            JsonArray location = currentSecretWaypoints.get("secret").getAsJsonObject().get("location").getAsJsonArray();

            Main.checkRoomData();
            return MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
        }
        return null;
    }

    public void renderLines() {
        try {
            if (currentSecretWaypoints != null && currentSecretWaypoints.has("locations")) {
                // Render the lines
                List<BlockPos> lines = new LinkedList<>();
                JsonArray lineLocations = new JsonArray();
                try {
                    lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();

                } catch (IllegalStateException e) {
                    LogUtils.info(String.valueOf(currentSecretWaypoints.get("locations")));
                    LogUtils.info(currentSecretWaypoints.get("locations").getClass().getName());
                    LogUtils.error(e);
                    return;
                }
                for (JsonElement lineLocationElement : lineLocations) {
                    JsonArray lineLocation = lineLocationElement.getAsJsonArray();

                    Main.checkRoomData();
                    lines.add(MapUtils.relativeToActual(new BlockPos(lineLocation.get(0).getAsInt(), lineLocation.get(1).getAsInt(), lineLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner));
                }

                if (SRMConfig.lineType == 0) {
                    //Add tick delay
                    if (c < SRMConfig.tickInterval) {
                        c++;
                        return;
                    }
                    c = 0;
                    int particleType = SRMConfig.particles;
                    if (particleType >= 36) {
                        particleType += 3;
                    }
                    // Draw particles based on enum
                    try {
                        RenderUtils.drawLineMultipleParticles(EnumParticleTypes.getParticleFromId(particleType), lines);
                    } catch (Exception e) {
                        LogUtils.error(e);
                    }
                }
            }
        }catch (Exception e){
            LogUtils.error(e);
        }
    }
    public void getData(String filePath){
        new Thread( () ->{
            HashMap<String, Integer> map = new HashMap<>();
            try {
                Gson gson = new GsonBuilder().create();
                FileReader reader = new FileReader(filePath);
                JsonObject data = gson.fromJson(reader, JsonObject.class);
                for (int i = 0; i <= 10; i++) {
                    String path = name;
                    if (i == 0) {
                        if (data == null || data.isJsonNull() || data.get(name) == null || data.get(name).isJsonNull()) {
                            currentSecretRoute = null;
                        }
                    } else {
                        path = name + ":" + i;
                    }
                    if (data == null || data.isJsonNull() || data.get(path) == null || data.get(path).isJsonNull()) {
                        continue;
                    }
                    JsonArray route = data.get(path).getAsJsonArray();

                    arrays.add(route);
                    JsonArray starPoseArray = route.get(0).getAsJsonObject().get("locations").getAsJsonArray().get(0).getAsJsonArray();
                    BlockPos startPos = new BlockPos(starPoseArray.get(0).getAsInt(), starPoseArray.get(1).getAsInt(), starPoseArray.get(2).getAsInt());
                    map.put(BlockUtils.blockPos(startPos), i);
                }
                int i = 0;
                for (Map.Entry<String, Integer> entry : map.entrySet()) {
                    EntityPlayerSP p = Minecraft.getMinecraft().thePlayer;
                    BlockPos pPos = new BlockPos(p.posX, p.posY, p.posZ);
                    BlockPos relPos = MapUtils.actualToRelative(pPos, RoomDetection.roomDirection, RoomDetection.roomCorner);
                    double dist1 = BlockUtils.blockDistance(relPos, entry.getKey());

                    if(closest != null){
                        double dist2 = closest.getThree();
                        if(dist1 > dist2) {
                            continue;
                        }
                    }

                    closest = new Triple<>(entry.getKey(), i, dist1);
                    i++;
                }
                if(closest != null) {
                    currentSecretRoute = arrays.get(closest.getTwo());
                    currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
                }
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }).start();

    }
}
//#endif
