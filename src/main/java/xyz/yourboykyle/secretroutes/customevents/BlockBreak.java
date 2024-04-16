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

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.world.BlockEvent;
import xyz.yourboykyle.secretroutes.Main;

public class BlockBreak {
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if(Main.currentRoom.getNext() == null || Main.currentRoom.getNext().getKey() == null || Main.currentRoom.getNext().getValue() == null) {
            return;
        }

        if(Main.currentRoom.getNext().getValue().equals("stonk")) {
            EntityPlayer p = e.getPlayer();

            if ((p.getHeldItem().hasDisplayName() && p.getHeldItem().getDisplayName().contains("Stonk")) || EnchantmentHelper.getEnchantmentLevel(32, p.getHeldItem()) >= 10) {
                BlockPos stonkPos = Main.currentRoom.getNext().getKey();

                if(e.pos.getX() == stonkPos.getX() && e.pos.getY() == stonkPos.getY() && e.pos.getZ() == stonkPos.getZ()) {
                    if (Main.currentRoom.getRoute().size() < 2) {
                        Main.currentRoom.add(null, null);
                    }
                    Main.currentRoom.removeNext();
                    SecretCompleted.onSecretCompleted();
                    p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Stonked!"));
                }
            }
        }
    }
}