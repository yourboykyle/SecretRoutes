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
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.*;
import xyz.yourboykyle.secretroutes.utils.skyblocker.DungeonScanner;

import java.util.HashMap;
import java.util.Map;

public class OnItemPickedUp {
    public static final String[] validItems = {
            "Decoy",
            "Defuse Kit",
            "Dungeon Chest Key",
            "Healing VIII",
            "Inflatable Jerry",
            "Spirit Leap",
            "Training Weights",
            "Trap",
            "Treasure Talisman"
    };
    private static final Map<String, Integer> previousInventory = new HashMap<>();
    public static boolean itemSecretOnCooldown = false;
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(OnItemPickedUp::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        // Only check every 5 ticks to reduce overhead
        if (++tickCounter % 5 != 0) return;

        if (!LocationUtils.isInDungeons()) return;

        // Track inventory changes to detect item pickups
        Map<String, Integer> currentInventory = new HashMap<>();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack != null && !stack.isEmpty()) {
                String itemName = stack.getName().getString();
                currentInventory.put(itemName, currentInventory.getOrDefault(itemName, 0) + stack.getCount());
            }
        }

        // Check for newly added items
        for (Map.Entry<String, Integer> entry : currentInventory.entrySet()) {
            String itemName = entry.getKey();
            int currentCount = entry.getValue();
            int previousCount = previousInventory.getOrDefault(itemName, 0);

            if (currentCount > previousCount && isSecretItem(itemName)) {
                handleItemPickup(player, itemName);
            }
        }

        previousInventory.clear();
        previousInventory.putAll(currentInventory);
    }

    // Overload for packet-based item pickup detection
    public static void handleItemPickup(ClientPlayerEntity player, ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getStack();
        if (stack == null || stack.isEmpty()) return;

        String itemName = stack.getName().getString();
        handleItemPickup(player, itemName);
    }

    private static void handleItemPickup(ClientPlayerEntity player, String itemName) {
        BlockPos playerPos = player.getBlockPos();

        if (SRMConfig.get().allSecrets) {
            if (SecretUtils.secrets != null) {
                BlockPos relPos = MapUtils.actualToRelative(playerPos, RoomDetection.roomDirection(), RoomDetection.roomCorner());

                for (DungeonScanner.SecretWaypoint secret : SecretUtils.secrets) {
                    try {
                        int x = secret.x();
                        int y = secret.y();
                        int z = secret.z();

                        if (relPos.getX() >= x - 10 && relPos.getX() <= x + 10 &&
                                relPos.getY() >= y - 10 && relPos.getY() <= y + 10 &&
                                relPos.getZ() >= z - 10 && relPos.getZ() <= z + 10) {

                            String locStr = BlockUtils.blockPos(new BlockPos(x, y, z));
                            if (!SecretUtils.secretLocations.contains(locStr)) {
                                SecretUtils.secretLocations.add(locStr);
                            }
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (Main.currentRoom != null && Main.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM) {
            BlockPos itemPos = Main.currentRoom.getSecretLocation();

            if (playerPos.getX() >= itemPos.getX() - 10 && playerPos.getX() <= itemPos.getX() + 10 &&
                    playerPos.getY() >= itemPos.getY() - 10 && playerPos.getY() <= itemPos.getY() + 10 &&
                    playerPos.getZ() >= itemPos.getZ() - 10 && playerPos.getZ() <= itemPos.getZ() + 10) {
                Main.currentRoom.nextSecret();
                SecretSounds.secretChime();
                LogUtils.info("Picked up item at " + itemPos);
            }
        }

        // Route Recording
        if (Main.routeRecording.recording) {
            if (!itemSecretOnCooldown && isSecretItem(itemName)) {
                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.ITEM, playerPos);
                Main.routeRecording.newSecret();
                Main.routeRecording.setRecordingMessage("Added item secret waypoint.");
            }
        }
    }

    public static boolean isSecretItem(String itemName) {
        for (String item : validItems) {
            if (itemName.contains(item)) {
                return true;
            }
        }
        return false;
    }
}