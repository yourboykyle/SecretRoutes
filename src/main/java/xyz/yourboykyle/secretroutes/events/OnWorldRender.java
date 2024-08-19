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
import io.github.quantizr.dungeonrooms.dungeons.catacombs.DungeonManager;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import io.github.quantizr.dungeonrooms.utils.Utils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.SecretRoutesRenderUtils;

import java.util.ArrayList;

public class OnWorldRender {
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        // Make sure the player is actually in a dungeon
        Utils.checkForCatacombs();
        if(!Utils.inCatacombs || DungeonManager.gameStage != 2 || !SRMConfig.modEnabled) {
            return;
        }

        ArrayList<BlockPos> etherwarpPositions = new ArrayList<>();
        ArrayList<BlockPos> minesPositions = new ArrayList<>();
        ArrayList<BlockPos> interactsPositions = new ArrayList<>();
        ArrayList<BlockPos> superboomsPositions = new ArrayList<>();


        GlStateManager.disableDepth();
        GlStateManager.disableCull();

        // Render the etherwarps
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("etherwarps") != null) {
            JsonArray etherwarpLocations = Main.currentRoom.currentSecretWaypoints.get("etherwarps").getAsJsonArray();
            for (JsonElement etherwarpLocationElement : etherwarpLocations) {
                JsonArray etherwarpLocation = etherwarpLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(etherwarpLocation.get(0).getAsInt(), etherwarpLocation.get(1).getAsInt(), etherwarpLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                etherwarpPositions.add(pos);
                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), SRMConfig.etherWarp, 1, 1);
            }
        }

        // Render the mines
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("mines") != null) {
            JsonArray mineLocations = Main.currentRoom.currentSecretWaypoints.get("mines").getAsJsonArray();
            for (JsonElement mineLocationElement : mineLocations) {
                JsonArray mineLocation = mineLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(mineLocation.get(0).getAsInt(), mineLocation.get(1).getAsInt(), mineLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                minesPositions.add(pos);

                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), SRMConfig.mine, 1, 1);
            }
        }

        // Render the interacts
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("interacts") != null) {
            JsonArray interactLocations = Main.currentRoom.currentSecretWaypoints.get("interacts").getAsJsonArray();
            for (JsonElement interactLocationElement : interactLocations) {
                JsonArray interactLocation = interactLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(interactLocation.get(0).getAsInt(), interactLocation.get(1).getAsInt(), interactLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                interactsPositions.add(pos);

                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), SRMConfig.interacts, 1, 1);
            }
        }

        // Render the tnts
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("tnts") != null) {
            JsonArray tntLocations = Main.currentRoom.currentSecretWaypoints.get("tnts").getAsJsonArray();
            for (JsonElement tntLocationElement : tntLocations) {
                JsonArray tntLocation = tntLocationElement.getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(tntLocation.get(0).getAsInt(), tntLocation.get(1).getAsInt(), tntLocation.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);
                superboomsPositions.add(pos);

                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), SRMConfig.superbooms, 1, 1);
            }
        }

        // Render the secret
        if(Main.currentRoom.currentSecretWaypoints != null && Main.currentRoom.currentSecretWaypoints.get("secret") != null) {
            JsonObject secret = Main.currentRoom.currentSecretWaypoints.get("secret").getAsJsonObject();
            String type = secret.get("type").getAsString();
            JsonArray location = secret.get("location").getAsJsonArray();

            Main.checkRoomData();
            BlockPos pos = MapUtils.relativeToActual(new BlockPos(location.get(0).getAsInt(), location.get(1).getAsInt(), location.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

            GlStateManager.disableTexture2D();
            if(type.equals("interact")) {
                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), SRMConfig.secretsInteract, 1, 1);
                if(SRMConfig.interactTextToggle) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.interactWaypointColorIndex) + "Interact", SRMConfig.interactTextSize);
                }
            } else if(type.equals("item")) {
                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), SRMConfig.secretsItem, 1, 1);
                if(SRMConfig.itemTextToggle) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.itemWaypointColorIndex) + "Item", SRMConfig.itemTextSize);
                }
            } else if(type.equals("bat")) {
                SecretRoutesRenderUtils.drawBoxAtBlock(pos.getX(),  pos.getY(), pos.getZ(), SRMConfig.secretsBat, 1, 1);
                if(SRMConfig.batTextToggle) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.batWaypointColorIndex) + "Bat", SRMConfig.batTextSize);
                }
            }
            if(SRMConfig.etherwarpsTextToggle) {
                for(BlockPos etherwarpPos : etherwarpPositions) {
                    SecretRoutesRenderUtils.drawText(etherwarpPos.getX(), etherwarpPos.getY(), etherwarpPos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.etherwarpsWaypointColorIndex) + "etherwarp", SRMConfig.etherwarpsTextSize);
                }
            }
            if(SRMConfig.minesTextToggle) {
                for(BlockPos minePos : minesPositions) {
                    SecretRoutesRenderUtils.drawText(minePos.getX(), minePos.getY(), minePos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.minesWaypointColorIndex) + "stonk", SRMConfig.minesTextSize);
                }
            }
            if(SRMConfig.interactsTextToggle) {
                for(BlockPos interactPos : interactsPositions) {
                    SecretRoutesRenderUtils.drawText(interactPos.getX(), interactPos.getY(), interactPos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.interactsWaypointColorIndex) + "interact", SRMConfig.interactsTextSize);
                }
            }
            if(SRMConfig.superboomsTextToggle) {
                for(BlockPos superboomPos : superboomsPositions) {
                    SecretRoutesRenderUtils.drawText(superboomPos.getX(), superboomPos.getY(), superboomPos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.superboomsWaypointColorIndex) + "superboom", SRMConfig.superboomsTextSize);
                }
            }
            GlStateManager.enableTexture2D();
        }

        // Render the start waypoint text
        if(Main.currentRoom.currentSecretIndex == 0) {
            JsonObject waypoints = Main.currentRoom.currentSecretWaypoints;
            if (!(waypoints == null || waypoints.get("locations") == null || waypoints.get("locations").getAsJsonArray().get(0) == null)) {
                // First secret in the route (the start)
                JsonArray startCoords = Main.currentRoom.currentSecretWaypoints.get("locations").getAsJsonArray().get(0).getAsJsonArray();

                Main.checkRoomData();
                BlockPos pos = MapUtils.relativeToActual(new BlockPos(startCoords.get(0).getAsInt(), startCoords.get(1).getAsInt(), startCoords.get(2).getAsInt()), RoomDetection.roomDirection, RoomDetection.roomCorner);

                // Render the text
                GlStateManager.disableTexture2D();
                if(SRMConfig.startTextToggle) {
                    SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), SecretRoutesRenderUtils.getTextColor(SRMConfig.startWaypointColorIndex) + "Start", SRMConfig.startTextSize);
                }
                GlStateManager.enableTexture2D();
            }
        }

        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
    }
}