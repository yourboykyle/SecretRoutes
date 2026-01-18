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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.*;

public class OnPlayerInteract {

    private static final long INTERACT_COOLDOWN = 110; // 50ms cooldown to prevent duplicate triggers
    private static long lastInteractTime = 0;
    private static BlockPos lastInteractPos = null;

    public static void register() {
        UseBlockCallback.EVENT.register(OnPlayerInteract::onUseBlock);
    }

    public static ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        LogUtils.info("onUseBlock event triggered");

        try {
            // Debounce: prevent duplicate calls within 50ms for the same block
            long currentTime = System.currentTimeMillis();
            BlockPos pos = hitResult.getBlockPos();

            if (lastInteractPos != null && lastInteractPos.equals(pos) &&
                    (currentTime - lastInteractTime) < INTERACT_COOLDOWN) {
                return ActionResult.PASS;
            }

            lastInteractTime = currentTime;
            lastInteractPos = pos;

            if (hand != Hand.MAIN_HAND) {
                return ActionResult.PASS;
            }

            Block block = world.getBlockState(pos).getBlock();

            if (!LocationUtils.isInDungeons()) {
                return ActionResult.PASS;
            }

            if (block != Blocks.CHEST && block != Blocks.TRAPPED_CHEST && block != Blocks.LEVER && block != Blocks.PLAYER_HEAD && block != Blocks.SKELETON_SKULL) {
                return ActionResult.PASS;
            }

            if (BlockUtils.blockPos(RoomRotationUtils.actualToRelative(pos, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner())).equals(BlockUtils.blockPos(SecretUtils.currentLeverPos))) {
                SecretUtils.resetValues();
            }
            SecretUtils.lastInteract = pos;

            if (SRMConfig.get().allSecrets) {
                if (SecretUtils.secrets != null) {
                    for (JsonElement secret : SecretUtils.secrets) {
                        try {
                            JsonObject json = secret.getAsJsonObject();
                            BlockPos spos = new BlockPos(json.get("x").getAsInt(), json.get("y").getAsInt(), json.get("z").getAsInt());
                            BlockPos rel = RoomRotationUtils.actualToRelative(pos, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner());
                            if (BlockUtils.blockPos(spos).equals(BlockUtils.blockPos(rel))) {
                                if (!SecretUtils.secretLocations.contains(BlockUtils.blockPos(spos))) {
                                    SecretUtils.secretLocations.add(BlockUtils.blockPos(spos));
                                }
                            }
                        } catch (Exception ex) {
                            LogUtils.error(ex);
                        }
                    }
                }
            }

            if (Main.currentRoom.getSecretType() == Room.SECRET_TYPES.INTERACT) {
                BlockPos interactPos = Main.currentRoom.getSecretLocation();
                SecretSounds.secretChime();
                if (pos.getX() == interactPos.getX() && pos.getY() == interactPos.getY() && pos.getZ() == interactPos.getZ()) {
                    Main.currentRoom.nextSecret();
                    LogUtils.info("Interacted with block at " + interactPos);
                }
            }

            // Route Recording
            if (Main.routeRecording.recording) {
                if (block == Blocks.LEVER) {
                    // If the block is a lever, then it is a waypoint on the route, going to a secret
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.INTERACTS, pos);
                    Main.routeRecording.setRecordingMessage("Added interact waypoint.");
                } else if (block == Blocks.PLAYER_HEAD || block == Blocks.SKELETON_SKULL || block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST) {
                    // If the block is a chest, trapped chest (mimic chest), or skull (essence), then it is a waypoint for a secret, so start a new secret waypoint list
                    boolean created = Main.routeRecording.addWaypoint(Room.SECRET_TYPES.INTERACT, pos);
                    if (created) {
                        Main.routeRecording.newSecret();
                        Main.routeRecording.setRecordingMessage("Added interact secret waypoint.");

                        // Stuff so items from chests don't count as secrets (because they're not)
                        OnItemPickedUp.itemSecretOnCooldown = true;
                        new Thread(() -> {
                            try {
                                Thread.sleep(2000);
                                OnItemPickedUp.itemSecretOnCooldown = false;
                            } catch (InterruptedException ex) {
                                LogUtils.error(ex);
                            }
                        }).start();
                    }
                }
            }
        } catch (Exception ex) {
            LogUtils.error(ex);
        }

        return ActionResult.PASS;
    }
}
