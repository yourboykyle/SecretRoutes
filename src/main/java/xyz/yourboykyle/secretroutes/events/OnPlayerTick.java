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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.dungeons.Room;

public class OnPlayerTick {
    private static final long ITEM_SECRET_PROXIMITY_DELAY_NANOS = 1_500_000_000L;
    private static final ItemSecretProximityTracker ITEM_SECRET_PROXIMITY_TRACKER =
            new ItemSecretProximityTracker(ITEM_SECRET_PROXIMITY_DELAY_NANOS);

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(OnPlayerTick::onPlayerTick);
    }

    private static void onPlayerTick(Minecraft client) {
        try {
            LocalPlayer player = client.player;
            if (player == null || client.level == null) {
                ITEM_SECRET_PROXIMITY_TRACKER.reset();
                return;
            }

            Room room = Main.currentRoom;
            if (room == null || room.name == null) {
                ITEM_SECRET_PROXIMITY_TRACKER.reset();
                return;
            }

            //If all secrets in the room have been completed
        /*if(Waypoints.allFound) {
            Main.currentRoom = new Room(null);
        }*/

            // Draw Lines
            if (SRMConfig.get().modEnabled) {
                Main.currentRoom.renderLines();
            }

            if (room.getSecretType() == Room.SECRET_TYPES.BAT) {
                BlockPos batPos = room.getSecretLocation();

                if (batPos != null) {
                    BlockPos pos = player.blockPosition();

                    if (pos.getX() >= batPos.getX() - 3 && pos.getX() <= batPos.getX() + 3 && pos.getY() >= batPos.getY() - 3 && pos.getY() <= batPos.getY() + 3 && pos.getZ() >= batPos.getZ() - 3 && pos.getZ() <= batPos.getZ() + 3) {
                        room.nextSecret();
                        LogUtils.info("Went by bat at " + batPos);
                    }
                }
            }

            handleItemSecretProximity(player, room);


            // Route Recording
            if (Main.routeRecording.recording) {
                if (Main.routeRecording.previousLocation == null) {
                    BlockPos targetPos = new BlockPos((int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()));
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.LOCATIONS, targetPos); // Trust the process
                    Main.routeRecording.previousLocation = player.blockPosition();
                } else {
                    BlockPos pos = player.blockPosition();
                    BlockPos prevPos = Main.routeRecording.previousLocation;

                    double distance = Math.abs(Math.sqrt(Math.pow(pos.getX() - prevPos.getX(), 2) + Math.pow(pos.getY() - prevPos.getY(), 2) + Math.pow(pos.getZ() - prevPos.getZ(), 2)));

                    // If the player has moved 5 blocks or more from the previous waypoint
                    if (distance >= 2.4) {
                        BlockPos targetPos = new BlockPos((int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()));
                        Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.LOCATIONS, targetPos);
                        Main.routeRecording.previousLocation = player.blockPosition();
                    }
                }
            }
        } catch (Exception e) {
            ITEM_SECRET_PROXIMITY_TRACKER.reset();
            LogUtils.error(e);
            e.printStackTrace();
        }
    }

    private static void handleItemSecretProximity(LocalPlayer player, Room room) {
        boolean isItemSecret = room.getSecretType() == Room.SECRET_TYPES.ITEM;
        BlockPos itemPos = isItemSecret ? room.getSecretLocation() : null;
        boolean shouldAdvance = ITEM_SECRET_PROXIMITY_TRACKER.update(
                room,
                room.currentSecretRoute,
                room.currentSecretIndex,
                isItemSecret,
                itemPos,
                player.blockPosition(),
                System.nanoTime()
        );

        if (shouldAdvance) {
            room.nextSecret();
            LogUtils.info("Picked up item at " + itemPos + " (Auto)");
        }
    }
}
//#endif
