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

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnBlockPlace {
    private static final String verboseTag = "Recording";
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        ChatUtils.sendVerboseMessage("§d Block placed: " + e.placedBlock.getBlock().getLocalizedName(), verboseTag);
        // Route Recording
        if(e.placedBlock.getBlock() == Blocks.tnt && Main.routeRecording.recording) {
            ChatUtils.sendVerboseMessage("§d TNT placed at: " + e.pos, verboseTag);
            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, e.pos);
            Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
            return;
        }
        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if(heldItem != null) {
            if(heldItem.getItem() == Item.getItemFromBlock(Blocks.tnt) && Main.routeRecording.recording) {
                ChatUtils.sendVerboseMessage("§d TNT held", verboseTag);
                Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, e.pos);
                Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
            }
        }
    }
}