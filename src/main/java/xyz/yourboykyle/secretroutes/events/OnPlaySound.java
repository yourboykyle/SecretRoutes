//#if FORGE == 1.8.9
// TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.init.Items;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RouteRecording;

public class OnPlaySound {
    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    public void onPlaySound(PlaySoundEvent e) {
        ISound sound = e.sound;
        if(sound != null && sound.getSoundLocation() != null) {
            if(sound.getSoundLocation().equals(new ResourceLocation("mob.enderdragon.hit"))) {
                // Route Recording
                if(Main.routeRecording.recording && Minecraft.getMinecraft().thePlayer.isSneaking() && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() == Items.diamond_shovel) {
                    new Thread(() -> {
                        try {
                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Detected etherwarp! Please wait 0.5 seconds before continuing the route..."));
                            Main.routeRecording.setRecordingMessage("Detected etherwarp! Please wait 0.5 seconds before continuing the route...");
                            Thread.sleep(500);
                            BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                            BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                            targetPos = targetPos.add(-1, -1, -1); // Block under the player, the -1 on X and Z have to be like that, trust the process
                            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ETHERWARPS, targetPos);
                            Main.routeRecording.setRecordingMessage("Etherwarp recorded! You may continue the route.");
                        } catch (InterruptedException ex) {
                            LogUtils.error(ex);
                            BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                            BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                            targetPos = targetPos.add(-1, -1, -1);
                            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ETHERWARPS, targetPos);
                            Main.routeRecording.setRecordingMessage("Etherwarp recorded! You may continue the route.");
                        }
                    }).start();
                }
            }
            String[] parts = sound.getSoundLocation().toString().split(":", 2);
            if (parts.length > 1) {
                String afterColon = parts[1];
                String[] subparts = afterColon.split("\\.", 2);
                if (subparts.length > 0) {
                    String soundType = subparts[0];
                    if(soundType.equals("dig")) {
                        // Route Recording
                        BlockPos pos = new BlockPos(sound.getXPosF(), sound.getYPosF(), sound.getZPosF());
                        World world = Minecraft.getMinecraft().theWorld;
                        IBlockState blockState = world.getBlockState(pos);

                        new OnBlockBreak().onBlockBreak(new BlockEvent.BreakEvent(world, pos, blockState, Minecraft.getMinecraft().thePlayer));
                    }
                }
            }
        }
    }
}
//#endif
