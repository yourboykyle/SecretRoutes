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

package xyz.yourboykyle.secretroutes.customevents;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.customevents.SecretCompleted;

public class BlockPlace {
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        if(Main.currentRoom.getNext() == null || Main.currentRoom.getNext().getKey() == null || Main.currentRoom.getNext().getValue() == null) {
            return;
        }

        if(Main.currentRoom.getNext().getValue().equals("superboom")) {
            if(e.placedBlock.getBlock() != Blocks.tnt) {
                return;
            }
            BlockPos superboom = Main.currentRoom.getNext().getKey();
            if (e.pos.getX() == superboom.getX() && e.pos.getY() == superboom.getY() && e.pos.getZ() == superboom.getZ() && e.player.getHeldItem().hasDisplayName() && e.player.getHeldItem().getDisplayName().contains("TNT")) {
                if (Main.currentRoom.getRoute().size() < 2) {
                    Main.currentRoom.add(null, null);
                }
                Main.currentRoom.removeNext();
                SecretCompleted.onSecretCompleted();
                e.player.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Superboomed!"));
            }
        }
    }
}
