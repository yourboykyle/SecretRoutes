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

package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.SecretRoutesRenderUtils;

public class OnWorldRender {
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        GlStateManager.disableDepth();
        GlStateManager.disableCull();

        // Render the etherwarps
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("etherwarps") != null) {
            JsonArray etherwarpLocations = Main.currentRoom.currentSecretWaypoints.get("etherwarps").getAsJsonArray();
            for (JsonElement etherwarpLocationElement : etherwarpLocations) {
                JsonArray etherwarpLocation = etherwarpLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(etherwarpLocation.get(0).getAsInt(), etherwarpLocation.get(1).getAsInt(), etherwarpLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), 128, 0, 128, 1, 1); // Color is purple
            }
        }

        // Render the mines
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("mines") != null) {
            JsonArray mineLocations = Main.currentRoom.currentSecretWaypoints.get("mines").getAsJsonArray();
            for (JsonElement mineLocationElement : mineLocations) {
                JsonArray mineLocation = mineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(mineLocation.get(0).getAsInt(), mineLocation.get(1).getAsInt(), mineLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), 255, 255, 0, 1, 1); // Color is yellow
            }
        }

        // Render the interacts
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("interacts") != null) {
            JsonArray interactLocations = Main.currentRoom.currentSecretWaypoints.get("interacts").getAsJsonArray();
            for (JsonElement interactLocationElement : interactLocations) {
                JsonArray interactLocation = interactLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(interactLocation.get(0).getAsInt(), interactLocation.get(1).getAsInt(), interactLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), 0, 0, 255, 1, 1); // Color is blue
            }
        }

        // Render the tnts
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("tnts") != null) {
            JsonArray tntLocations = Main.currentRoom.currentSecretWaypoints.get("tnts").getAsJsonArray();
            for (JsonElement tntLocationElement : tntLocations) {
                JsonArray tntLocation = tntLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(tntLocation.get(0).getAsInt(), tntLocation.get(1).getAsInt(), tntLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), 255, 0, 0, 1, 1); // Color is red
            }
        }

        // Render the secret
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("secret") != null) {
            JsonObject secret = Main.currentRoom.currentSecretWaypoints.get("secret").getAsJsonObject();
            String type = secret.get("type").getAsString();
            JsonArray location = secret.get("location").getAsJsonArray();

            Main.checkRoomData();
            BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

            if(type.equals("interact")) {
                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), 0, 0, 255, 1, 1); // Color is blue
            } else if(type.equals("item")) {
                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), 0, 255, 255, 1, 1); // Color is turqoise
            } else if(type.equals("bat")) {
                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), 0, 255, 0, 1, 1); // Color is green
            }
        }



        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
    }
}