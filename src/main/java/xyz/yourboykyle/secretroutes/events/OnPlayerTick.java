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

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnPlayerTick {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if(e.player.getUniqueID() != Minecraft.getMinecraft().thePlayer.getUniqueID()) {
            return;
        }

        //If all secrets in the room have been completed
        /*if(Waypoints.allFound) {
            Main.currentRoom = new Room(null);
        }*/

        // Auto Updates
        if (SRMConfig.autoUpdate) {
            Main.updateManager.checkUpdate();
        }

        // Draw Lines
        if(SRMConfig.modEnabled) {
            Main.currentRoom.renderLines();
        }

        if(Main.currentRoom.getSecretType() == Room.SECRET_TYPES.BAT) {
            BlockPos pos = e.player.getPosition();
            BlockPos batPos = Main.currentRoom.getSecretLocation();

            if (pos.getX() >= batPos.getX() - 3 && pos.getX() <= batPos.getX() + 3 && pos.getY() >= batPos.getY() - 3 && pos.getY() <= batPos.getY() + 3 && pos.getZ() >= batPos.getZ() - 3 && pos.getZ() <= batPos.getZ() + 3) {
                Main.currentRoom.nextSecret();
                LogUtils.info("Went by bat at " + batPos);
            }
        }

        // Route Recording
        if(Main.routeRecording.recording) {
            if (Main.routeRecording.previousLocation == null) {
                Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.LOCATIONS, e.player.getPosition());
                Main.routeRecording.previousLocation = e.player.getPosition();
            } else {
                BlockPos pos = e.player.getPosition();
                BlockPos prevPos = Main.routeRecording.previousLocation;

                double distance = Math.abs(Math.sqrt(Math.pow(pos.getX() - prevPos.getX(), 2) + Math.pow(pos.getY() - prevPos.getY(), 2) + Math.pow(pos.getZ() - prevPos.getZ(), 2)));

                // If the player has moved 5 blocks or more from the previous waypoint
                if (distance >= 4.9) {
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.LOCATIONS, e.player.getPosition());
                    Main.routeRecording.previousLocation = e.player.getPosition();
                }
            }
        }
    }
}