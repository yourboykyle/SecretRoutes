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

package xyz.yourboykyle.secretroutes.customevents;

import com.google.gson.*;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnterNewRoom {
    public static void onEnterNewRoom(Room room) {
        try {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Entered new room \"" + RoomDetection.roomName + "\"."));

            Main.currentRoom = room;
            Main.clearPath();

            if(room.data == null) {
                return;
            }

            for (Map.Entry<String, JsonElement> entry : room.data.entrySet()) {
                String key = entry.getKey(); // 1, 2, 3, etc.
                JsonObject value = entry.getValue().getAsJsonObject(); // JsonObject contains coords[] and "type"

                String type = value.get("type").getAsString();
                if (type.equals("move")) {
                    processMove(json2DArrayToBlockPosList(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created path particles."));
                } else if (type.equals("stonk")) {
                    processStonk(jsonArrayToBlockPos(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created stonk waypoint."));
                } else if (type.equals("aotv")) {
                    processAotv(jsonArrayToBlockPos(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created AOTV waypoint."));
                } else if (type.equals("chest")) {
                    processChest(jsonArrayToBlockPos(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created chest waypoint."));
                } else if (type.equals("item")) {
                    processItem(jsonArrayToBlockPos(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created item waypoint."));
                } else if (type.equals("superboom")) {
                    processSuperboom(jsonArrayToBlockPos(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created superboom waypoint."));
                } else if (type.equals("bat")) {
                    processBat(jsonArrayToBlockPos(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created bat waypoint."));
                } else if (type.equals("lever")) {
                    processLever(jsonArrayToBlockPos(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created lever waypoint."));
                } else if (type.equals("wither")) {
                    processWither(jsonArrayToBlockPos(value.getAsJsonArray("coords")));
                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created wither waypoint."));
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static BlockPos jsonArrayToBlockPos(JsonArray jsonArray) {
        int[] array = new int[jsonArray.size()];
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement element = jsonArray.get(i);
            if (element.isJsonPrimitive()) {
                array[i] = element.getAsInt();
            } else {
                // Handle non-integer elements if needed
            }
        }
        return new BlockPos(array[0], array[1], array[2]);
    }
    public static List<BlockPos> json2DArrayToBlockPosList(JsonArray jsonArray) {
        int[][] result = new int[jsonArray.size()][];
        List<BlockPos> locations = new ArrayList<BlockPos>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonArray innerArray;
            try {
                innerArray = jsonArray.get(i).getAsJsonArray();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

            int[] innerArrayInt = new int[innerArray.size()];
            for (int j = 0; j < innerArray.size(); j++) {
                innerArrayInt[j] = innerArray.get(j).getAsInt();
            }
            result[i] = innerArrayInt;
        }

        for(int i = 0; i < result.length; i++) {
            locations.add(new BlockPos(result[i][0], result[i][1], result[i][2]));
        }
        return locations;
    }

    public static void processMove(List<BlockPos> relativeCoords) {
        List<BlockPos> coords = new ArrayList<>();
        for(BlockPos pos : relativeCoords) {
            coords.add(MapUtils.relativeToActual(pos, RoomDetection.roomDirection, RoomDetection.roomCorner));
        }
        Main.addToPath(coords);
    }
    public static void processStonk(BlockPos coords) {
        Main.currentRoom.add(MapUtils.relativeToActual(coords, RoomDetection.roomDirection, RoomDetection.roomCorner), "stonk");
    }
    public static void processAotv(BlockPos coords) {
        Main.currentRoom.add(MapUtils.relativeToActual(coords, RoomDetection.roomDirection, RoomDetection.roomCorner), "aotv");
    }
    public static void processChest(BlockPos coords) {
        Main.currentRoom.add(MapUtils.relativeToActual(coords, RoomDetection.roomDirection, RoomDetection.roomCorner), "chest");
    }
    public static void processItem(BlockPos coords) {
        Main.currentRoom.add(MapUtils.relativeToActual(coords, RoomDetection.roomDirection, RoomDetection.roomCorner), "item");
    }
    public static void processSuperboom(BlockPos coords) {
        Main.currentRoom.add(MapUtils.relativeToActual(coords, RoomDetection.roomDirection, RoomDetection.roomCorner), "superboom");
    }

    public static void processBat(BlockPos coords) {
        Main.currentRoom.add(MapUtils.relativeToActual(coords, RoomDetection.roomDirection, RoomDetection.roomCorner), "bat");
    }

    public static void processLever(BlockPos coords) {
        Main.currentRoom.add(MapUtils.relativeToActual(coords, RoomDetection.roomDirection, RoomDetection.roomCorner), "lever");
    }

    public static void processWither(BlockPos coords) {
        Main.currentRoom.add(MapUtils.relativeToActual(coords, RoomDetection.roomDirection, RoomDetection.roomCorner), "wither");
    }
}
