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
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.events.PacketEvent;

public class OnSendPacket {
    @SubscribeEvent
    public void onSendPacket(PacketEvent.SendEvent e) {
        try {
            if(e.packet instanceof C08PacketPlayerBlockPlacement) {
                Minecraft mc = Minecraft.getMinecraft();
                OnPlayerInteract.onPlayerInteract(new PlayerInteractEvent(mc.thePlayer, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, ((C08PacketPlayerBlockPlacement) e.packet).getPosition(), EnumFacing.UP, mc.theWorld));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}