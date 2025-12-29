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

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent;
import org.polyfrost.oneconfig.api.event.v1.invoke.impl.Subscribe;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

public class OnReceivePacket {
    // The BlockUpdateS2CPacket packet is sent twice for each block break and place. These variables are workarounds to keep track of if it's the first time each packet is sent, and just ignore the second time
    public static boolean firstBlockBreakPacket = true;
    public static boolean firstBlockPlacePacket = true;

    @Subscribe
    public void onReceivePacket(PacketEvent.Receive event) {
        try {
            Object packet = event.getPacket();

            if (packet instanceof ItemPickupAnimationS2CPacket) {
                // Note to Hypixel: This is not manipulating packets, it is simply listening and checking for the collect item packet.
                // If that is the correct packet, it simulates creating an itempickedup event client-side
                ItemPickupAnimationS2CPacket pickupPacket = (ItemPickupAnimationS2CPacket) packet;
                MinecraftClient client = MinecraftClient.getInstance();

                if (client.world == null || client.player == null) {
                    return;
                }

                Entity entity = client.world.getEntityById(pickupPacket.getEntityId());

                if(entity instanceof ItemEntity) {
                    ItemEntity item = (ItemEntity) entity;
                    Entity collector = client.world.getEntityById(pickupPacket.getCollectorEntityId());

                    if(collector == null) {
                        LogUtils.info("Entity is null");
                        return;
                    }

                    if(!collector.getName().getString().equals(client.player.getName().getString())) {
                        // Someone else has picked up the item
                        return;
                    }

                    OnItemPickedUp.handleItemPickup(client.player, item);
                }
            } else if(packet instanceof BlockUpdateS2CPacket) {
                // Route Recording
                BlockUpdateS2CPacket blockPacket = (BlockUpdateS2CPacket) packet;
                MinecraftClient client = MinecraftClient.getInstance();

                if (client.world == null || client.player == null) {
                    return;
                }

                BlockPos pos = blockPacket.getPos();
                World world = client.world;
                BlockState blockState = blockPacket.getState();
                Block block = blockState.getBlock();

                if(block == Blocks.AIR) {
                    // Block was broken
                    if(Main.routeRecording.recording && firstBlockBreakPacket) {
                        OnBlockBreak.handleBlockBreak(world, pos, blockState, client.player);
                    }
                } else if(block == null) {
                    // Block is null.
                } else {
                    // Block was placed
                    if(Main.routeRecording.recording && firstBlockPlacePacket) {
                        BlockState placedAgainst = world.getBlockState(pos.add(1, 0, 0));
                        OnBlockPlace.handleBlockPlace(world, pos, blockState, placedAgainst, client.player);
                    }
                }

                firstBlockBreakPacket = !firstBlockBreakPacket;
                firstBlockPlacePacket = !firstBlockPlacePacket;
            }
        } catch (Exception error) {
            LogUtils.error(error);
            ChatUtils.sendChatMessage("There was an error with the " + Main.MODID + " mod.");
        }
    }
}
//#endif
