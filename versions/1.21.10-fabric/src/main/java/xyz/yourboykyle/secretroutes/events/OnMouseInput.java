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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnMouseInput {

    private static boolean[] previousMouseState = new boolean[8];

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player == null || mc.mouse == null) {
                    return;
                }

                // Check right click (button 1)
                boolean rightClick = mc.mouse.wasRightButtonClicked();

                if (rightClick && !previousMouseState[1]) {
                    onMouseClick(mc.player, 1);
                }

                previousMouseState[1] = rightClick;

            } catch (Exception ex) {
                LogUtils.error(ex);
            }
        });
    }

    private static void onMouseClick(ClientPlayerEntity player, int button) {
        try {
            ItemStack item = player.getMainHandStack();
            if (item.isEmpty()) {
                return;
            }

            String itemName = item.getName().getString().toLowerCase();

            if (itemName.contains("ender pearl") && button == 1) {
                LogUtils.info("§bPlayer is holding an ender pearl");
                if (Main.routeRecording.recording) {
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ENDERPEARLS, player);
                }
            }

            if (itemName.contains("boom")) {
                LogUtils.info("§bPlayer is holding a superboom");
                if (Main.routeRecording.recording) {
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, player);
                }
            }

            /*
            if (itemName.contains("aspect of the void") && button == 1 && player.isSneaking()) {
                LogUtils.info("§bPlayer is holding an aspect of the void");
                if (xyz.yourboykyle.secretroutes.Main.routeRecording.recording) {
                    new Thread(() -> {
                        try {
                            BlockPos pos = player.getBlockPos();
                            try {
                                Thread.sleep(SRMConfig.etherwarpPing);
                            } catch (InterruptedException ex) {
                                LogUtils.error(ex);
                            }
                            BlockPos pos2 = player.getBlockPos();

                            if (!pos.equals(pos2)) {
                                LogUtils.info("§bPlayer teleported and moved");
                                xyz.yourboykyle.secretroutes.Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ETHERWARPS, pos2);
                            } else {
                                LogUtils.info("§bPlayer teleported and did not move");
                            }
                        } catch (Exception ex) {
                            LogUtils.error(ex);
                        }
                    }).start();
                }
            }
            */

        } catch (Exception ex) {
            LogUtils.error(ex);
        }
    }
}
