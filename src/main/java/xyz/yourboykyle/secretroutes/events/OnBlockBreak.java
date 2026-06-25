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

package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RoomDirectionUtils;
import xyz.yourboykyle.secretroutes.utils.RoomRotationUtils;

public class OnBlockBreak {
    public static void register() {
        PlayerBlockBreakEvents.AFTER.register(OnBlockBreak::onBlockBreak);
    }

    public static void handleBlockBreak(Level world, BlockPos pos, BlockState state, LocalPlayer player) {
        onBlockBreak(world, player, pos, state, null);
    }

    private static void onBlockBreak(Level world, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            // Route Recording
            if (player.getUUID().equals(mc.player.getUUID()) && Main.routeRecording.recording) {
                boolean shouldAddWaypoint = false;

                JsonArray waypoints = Main.routeRecording.currentSecretWaypoints.get("mines").getAsJsonArray();
                if (waypoints.size() > 1) {
                    for (JsonElement waypoint : waypoints) {
                        JsonArray waypointCoords = waypoint.getAsJsonArray();

                        Main.checkRoomData();
                        BlockPos relPos = RoomRotationUtils.actualToRelative(pos, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());

                        // Check if waypoints already has the broken block
                        if (!(relPos.getX() == waypointCoords.get(0).getAsInt() &&
                                relPos.getY() == waypointCoords.get(1).getAsInt() &&
                                relPos.getZ() == waypointCoords.get(2).getAsInt())) {
                            // Waypoint doesn't exist yet
                            shouldAddWaypoint = true;
                        }
                    }
                } else {
                    // Waypoint doesn't exist yet
                    shouldAddWaypoint = true;
                }

                ItemStack heldItem = player.getMainHandItem();
                if (heldItem != null && !heldItem.isEmpty()) {
                    // Check if the player is holding a pickaxe by checking the item's registry ID
                    String itemId = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).toString();
                    if (!itemId.contains("pickaxe")) {
                        // Player is not holding a pickaxe, do not add waypoint
                        shouldAddWaypoint = false;
                    }
                } else {
                    // Player is not holding anything
                    shouldAddWaypoint = false;
                }

                if (shouldAddWaypoint) {
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.MINES, pos);
                    Main.routeRecording.setRecordingMessage("Added mine waypoint.");
                }
            }
        } catch (Exception ex) {
            LogUtils.error(ex);
        }
    }
}

//#endif
