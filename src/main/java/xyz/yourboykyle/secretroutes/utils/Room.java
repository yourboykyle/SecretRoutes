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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import xyz.yourboykyle.secretroutes.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Room {
    public enum SECRET_TYPES {
        INTERACT,
        ITEM,
        BAT
    };

    private Queue<Pair<BlockPos, String>> route; // Pairs will be BlockPos to the type of secret
    public String name;
    public JsonObject data;

    public JsonArray currentSecretRoute;
    public int currentSecretIndex = 0;
    public JsonObject currentSecretWaypoints;

    public Room(String roomName) {
        currentSecretIndex = 0;

        try {
            route = new LinkedList<>();
            name = roomName;

            if (roomName != null) {
                Gson gson = new GsonBuilder().create();
                InputStream inputStream = Main.class.getResourceAsStream(Main.roomsDataPath);

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                JsonObject myData = gson.fromJson(reader, JsonObject.class);

                if(myData != null && myData.get(name) != null) {
                    data = myData.get(name).getAsJsonObject();
                }

                // New Rooms
                Gson newGson = new GsonBuilder().create();
                InputStream newInputStream = Main.class.getResourceAsStream(Main.newRoomsDataPath);

                BufferedReader newReader = new BufferedReader(new InputStreamReader(newInputStream));
                JsonObject newMyData = newGson.fromJson(newReader, JsonObject.class);

                if(newMyData != null && newMyData.get(name) != null) {
                    currentSecretRoute = newMyData.get(name).getAsJsonArray();
                    currentSecretWaypoints = currentSecretRoute.get(currentSecretIndex).getAsJsonObject();
                }

                System.out.println("Current Secret Route: " + currentSecretRoute);
                System.out.println("Current Secret (#" + (currentSecretIndex + 1) + "): " + currentSecretWaypoints);
            } else {
                data = null;
                currentSecretRoute = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
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

    public void renderLines() {
        if(currentSecretWaypoints != null) {
            // Render the lines
            List<BlockPos> lines = new LinkedList<>();

            JsonArray lineLocations = currentSecretWaypoints.get("locations").getAsJsonArray();
            for (JsonElement lineLocationElement : lineLocations) {
                JsonArray lineLocation = lineLocationElement.getAsJsonArray();
                lines.add(new BlockPos(lineLocation.get(0).getAsInt(), lineLocation.get(1).getAsInt(), lineLocation.get(2).getAsInt()));
            }

            RenderUtils.drawLineMultipleParticles(EnumParticleTypes.FLAME, lines);
        }
    }

    public SECRET_TYPES getSecretType() {
        if(currentSecretWaypoints != null) {
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
        JsonArray location = currentSecretWaypoints.get("secret").getAsJsonObject().get("location").getAsJsonArray();

        return new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt());
    }



    public Pair<BlockPos, String> getNext() {
        return route.peek();
    }

    public void add(BlockPos pos, String type) {
        if(getNext() == null || getNext().getKey() == null || getNext().getValue() == null) {
            removeNext();
        }
        route.add(new Pair(pos, type));
    }
    public void add(BlockPos pos, String type, boolean isInit) {
        route.add(new Pair(pos, type));
    }

    public void removeNext() {
        route.poll();
    }

    public Queue<Pair<BlockPos, String>> getRoute() {
        return route;
    }
}
