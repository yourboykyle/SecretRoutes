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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.customevents.BlockPlace;
import xyz.yourboykyle.secretroutes.customevents.SecretCompleted;

import java.util.ArrayList;
import java.util.List;

public class PlayerInteract {
    public static List<BlockPos> clickedChests = new ArrayList<BlockPos>();//

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent e) {
        //Basic checks
        if(Main.currentRoom.getNext() == null || Main.currentRoom.getNext().getKey() == null || Main.currentRoom.getNext().getValue() == null) {
            return;
        }
        /*if(clickedChests.contains(e.pos)) {
            return;
        }*/
        if(e.action != PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        EntityPlayer p = e.entityPlayer;
        BlockPos pos = e.pos;
        Block block = e.world.getBlockState(e.pos).getBlock();

        if(block != Blocks.chest && block != Blocks.trapped_chest && block != Blocks.lever && block != Blocks.skull) {
            return;
        }

        //Chest
        if (Main.currentRoom.getNext().getValue().equals("chest")) {
            if (block == Blocks.chest) {
                BlockPos secretPos = Main.currentRoom.getNext().getKey();
                if(pos.getX() == secretPos.getX() && pos.getY() == secretPos.getY() && pos.getZ() == secretPos.getZ()) {
                    if (Main.currentRoom.getRoute().size() < 2) {
                        Main.currentRoom.add(null, null);
                    }
                    Main.currentRoom.removeNext();
                    SecretCompleted.onSecretCompleted();
                    p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Got the chest!"));
                }
            } else if (block == Blocks.trapped_chest) {
                BlockPos secretPos = Main.currentRoom.getNext().getKey();
                if(pos.getX() == secretPos.getX() && pos.getY() == secretPos.getY() && pos.getZ() == secretPos.getZ()) {
                    if (Main.currentRoom.getRoute().size() < 2) {
                        Main.currentRoom.add(null, null);
                    }
                    Main.currentRoom.removeNext();
                    SecretCompleted.onSecretCompleted();
                    Main.displayNotification(EnumChatFormatting.RED + "WARNING! Kill the mimic!");
                    p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Got the chest!"));
                }
            }
            clickedChests.add(pos);
            return;
        }

        //Lever
        if (Main.currentRoom.getNext().getValue().equals("lever") && block == Blocks.lever) {
            BlockPos secretPos = Main.currentRoom.getNext().getKey();
            if(pos.getX() == secretPos.getX() && pos.getY() == secretPos.getY() && pos.getZ() == secretPos.getZ()) {
                if (Main.currentRoom.getRoute().size() < 2) {
                    Main.currentRoom.add(null, null);
                }
                Main.currentRoom.removeNext();
                SecretCompleted.onSecretCompleted();
                p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Got the lever!"));
            }
            return;
        }

        //Wither
        if (Main.currentRoom.getNext().getValue().equals("wither") && block == Blocks.skull) {
            BlockPos secretPos = Main.currentRoom.getNext().getKey();
            if(pos.getX() == secretPos.getX() && pos.getY() == secretPos.getY() && pos.getZ() == secretPos.getZ()) {
                if (Main.currentRoom.getRoute().size() < 2) {
                    Main.currentRoom.add(null, null);
                }
                Main.currentRoom.removeNext();
                SecretCompleted.onSecretCompleted();
                p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Got the wither essence!"));
            }
            return;
        }
    }
}
