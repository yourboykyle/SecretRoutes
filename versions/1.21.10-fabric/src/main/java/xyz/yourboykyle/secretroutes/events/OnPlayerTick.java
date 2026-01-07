/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
 */

package xyz.yourboykyle.secretroutes.events;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnPlayerTick {

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(OnPlayerTick::onPlayerTick);
    }

    private static void onPlayerTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null || Main.currentRoom == null) {
            return;
        }

        // Draw Lines
        if (SRMConfig.get().modEnabled) {
            Main.currentRoom.renderLines();
        }

        // Bat Secret Logic
        if (Main.currentRoom.getSecretType() == Room.SECRET_TYPES.BAT) {
            BlockPos pos = player.getBlockPos();
            BlockPos batPos = Main.currentRoom.getSecretLocation();

            if (batPos != null) {
                if (pos.getX() >= batPos.getX() - 3 && pos.getX() <= batPos.getX() + 3 &&
                        pos.getY() >= batPos.getY() - 3 && pos.getY() <= batPos.getY() + 3 &&
                        pos.getZ() >= batPos.getZ() - 3 && pos.getZ() <= batPos.getZ() + 3) {
                    Main.currentRoom.nextSecret();
                    LogUtils.info("Went by bat at " + batPos);
                }
            }
        }

        // Item Secret Logic
        if (Main.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM) {
            BlockPos pos = player.getBlockPos();
            BlockPos itemPos = Main.currentRoom.getSecretLocation();

            if (itemPos != null) {
                if (pos.getX() >= itemPos.getX() - 2 && pos.getX() <= itemPos.getX() + 2 &&
                        pos.getY() >= itemPos.getY() - 2 && pos.getY() <= itemPos.getY() + 2 &&
                        pos.getZ() >= itemPos.getZ() - 2 && pos.getZ() <= itemPos.getZ() + 2) {

                    new Thread(() -> {
                        try {
                            Thread.sleep(1500);
                            if (Main.currentRoom != null) {
                                BlockPos currentLoc = Main.currentRoom.getSecretLocation();
                                if (Main.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM &&
                                        itemPos.equals(currentLoc)) {
                                    Main.currentRoom.nextSecret();
                                    LogUtils.info("Picked up item at " + itemPos + " (Auto)");
                                }
                            }
                        } catch (InterruptedException e1) {
                            LogUtils.error(e1);
                        }
                    }).start();
                }
            }
        }

        // Route Recording
        if (Main.routeRecording != null && Main.routeRecording.recording) {
            if (Main.routeRecording.previousLocation == null) {
                BlockPos targetPos = new BlockPos((int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()));
                Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.LOCATIONS, targetPos);
                Main.routeRecording.previousLocation = player.getBlockPos();
            } else {
                BlockPos pos = player.getBlockPos();
                BlockPos prevPos = Main.routeRecording.previousLocation;

                double distance = Math.sqrt(pos.getSquaredDistance(prevPos));

                if (distance >= 2.4) {
                    BlockPos targetPos = new BlockPos((int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()));
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.LOCATIONS, targetPos);
                    Main.routeRecording.previousLocation = player.getBlockPos();
                }
            }
        }
    }
}
