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
        try {
            ClientPlayerEntity player = client.player;
            if (player == null) {
                return;
            }

            //If all secrets in the room have been completed
        /*if(Waypoints.allFound) {
            Main.currentRoom = new Room(null);
        }*/

            // Draw Lines
            if (SRMConfig.modEnabled) {
                Main.currentRoom.renderLines();
            }

            if (Main.currentRoom.getSecretType() == Room.SECRET_TYPES.BAT) {
                BlockPos batPos = Main.currentRoom.getSecretLocation();

                if (batPos != null) {
                    BlockPos pos = player.getBlockPos();

                    if (pos.getX() >= batPos.getX() - 3 && pos.getX() <= batPos.getX() + 3 && pos.getY() >= batPos.getY() - 3 && pos.getY() <= batPos.getY() + 3 && pos.getZ() >= batPos.getZ() - 3 && pos.getZ() <= batPos.getZ() + 3) {
                        Main.currentRoom.nextSecret();
                        LogUtils.info("Went by bat at " + batPos);
                    }
                }
            }

        /* This has been commented out because it is causing it to log the secret multiple times if there are 2 secrets in a row in the route.
        This was originally added because in a specific room, you cannot get the item secret if it spawns and the velocity pushes the item away from you.
        But you can still get the secret if you walk over to the item secret, or just press the next secret keybind if you're lazy.
        */
            if (Main.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM) {
                BlockPos itemPos = Main.currentRoom.getSecretLocation();

                if (itemPos != null) {
                    BlockPos pos = player.getBlockPos();

                    if (pos.getX() >= itemPos.getX() - 2 && pos.getX() <= itemPos.getX() + 2 && pos.getY() >= itemPos.getY() - 2 && pos.getY() <= itemPos.getY() + 2 && pos.getZ() >= itemPos.getZ() - 2 && pos.getZ() <= itemPos.getZ() + 2) {
                        new Thread(() -> {
                            try {

                                Thread.sleep(1500);
                                BlockPos currentItemPos = Main.currentRoom.getSecretLocation();
                                if (Main.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM && currentItemPos != null && itemPos.getX() == currentItemPos.getX() && itemPos.getY() == currentItemPos.getY() && itemPos.getZ() == currentItemPos.getZ()) {

                                    Main.currentRoom.nextSecret();
                                    LogUtils.info("Picked up item at " + itemPos + " (Auto)");
                                }
                            } catch (InterruptedException e1) {
                                LogUtils.error(e1);
                            }
                        }).start();
                    }
                }
            }


            // Route Recording
            if (Main.routeRecording.recording) {
                if (Main.routeRecording.previousLocation == null) {
                    BlockPos targetPos = new BlockPos((int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()));
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.LOCATIONS, targetPos); // Trust the process
                    Main.routeRecording.previousLocation = player.getBlockPos();
                } else {
                    BlockPos pos = player.getBlockPos();
                    BlockPos prevPos = Main.routeRecording.previousLocation;

                    double distance = Math.abs(Math.sqrt(Math.pow(pos.getX() - prevPos.getX(), 2) + Math.pow(pos.getY() - prevPos.getY(), 2) + Math.pow(pos.getZ() - prevPos.getZ(), 2)));

                    // If the player has moved 5 blocks or more from the previous waypoint
                    if (distance >= 2.4) {
                        BlockPos targetPos = new BlockPos((int) Math.floor(player.getX()), (int) Math.floor(player.getY()), (int) Math.floor(player.getZ()));
                        Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.LOCATIONS, targetPos);
                        Main.routeRecording.previousLocation = player.getBlockPos();
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.error(e);
            e.printStackTrace();
        }
    }
}
