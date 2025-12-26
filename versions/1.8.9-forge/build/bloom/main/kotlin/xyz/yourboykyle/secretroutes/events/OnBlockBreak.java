//#if FORGE && MC == 1.8.9
// TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
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

package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.MapUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnBlockBreak {
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        try {
            // Route Recording
            if(e.getPlayer().getUniqueID() == Minecraft.getMinecraft().thePlayer.getUniqueID() && Main.routeRecording.recording) {
                boolean shouldAddWaypoint = false;

                JsonArray waypoints = Main.routeRecording.currentSecretWaypoints.get("mines").getAsJsonArray();
                if(waypoints.size() > 1) {
                    for (JsonElement waypoint : waypoints) {
                        JsonArray waypointCoords = waypoint.getAsJsonArray();

                        Main.checkRoomData();
                        BlockPos relPos = MapUtils.actualToRelative(e.pos, RoomDetection.roomDirection, RoomDetection.roomCorner);

                        // Check if waypoints already has the broken block
                        if (!(relPos.getX() == waypointCoords.get(0).getAsInt() && relPos.getY() == waypointCoords.get(1).getAsInt() && relPos.getZ() == waypointCoords.get(2).getAsInt())) {
                            // Waypoint doesn't exist yet
                            shouldAddWaypoint = true;
                        }
                    }
                } else {
                    // Waypoint doesn't exist yet
                    shouldAddWaypoint = true;
                }
                ItemStack heldItem = e.getPlayer().getHeldItem();
                if(heldItem != null) {
                    // Check if the player is holding a pickaxe
                    if(!(heldItem.getItem() instanceof ItemPickaxe)) {
                        // Player is not holding a pickaxe, do not add waypoint
                        shouldAddWaypoint = false;
                    }
                } else {
                    // Player is not holding anything
                    shouldAddWaypoint = false;
                }

                if(shouldAddWaypoint) {
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.MINES, e.pos);
                    Main.routeRecording.setRecordingMessage("Added mine waypoint.");
                }
            }
        } catch (Exception ex) {
            LogUtils.error(ex);
        }
    }
}
//#endif
