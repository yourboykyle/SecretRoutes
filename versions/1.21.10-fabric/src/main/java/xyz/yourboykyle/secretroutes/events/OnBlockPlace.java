//#if FABRIC && MC == 1.21.10
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

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnBlockPlace {
    private static final String verboseTag = "Recording";

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || !player.getUuid().equals(mc.player.getUuid())) {
                return ActionResult.PASS;
            }

            ItemStack heldItem = player.getStackInHand(hand);
            if (heldItem != null && !heldItem.isEmpty()) {
                // Check if placing TNT
                if (heldItem.getItem() == Items.TNT && Main.routeRecording.recording) {
                    String blockName = Registries.ITEM.getId(heldItem.getItem()).toString();
                    ChatUtils.sendVerboseMessage("§d Block placed: " + blockName, verboseTag);
                    ChatUtils.sendVerboseMessage("§d TNT placed at: " + hitResult.getBlockPos(), verboseTag);

                    // Add waypoint at the block position being placed on
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, hitResult.getBlockPos());
                    Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
                }
            }

            return ActionResult.PASS;
        });
    }

    // Handle block place from packet events
    public static void handleBlockPlace(World world, BlockPos pos, BlockState blockState, BlockState placedAgainst, PlayerEntity player) {
        if (blockState.getBlock() == net.minecraft.block.Blocks.TNT && Main.routeRecording.recording) {
            String blockName = Registries.BLOCK.getId(blockState.getBlock()).toString();
            ChatUtils.sendVerboseMessage("§d Block placed: " + blockName, verboseTag);
            ChatUtils.sendVerboseMessage("§d TNT placed at: " + pos, verboseTag);

            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, pos);
            Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
        }
    }
}
//#endif
