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

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.polyfrost.oneconfig.api.event.v1.events.PacketEvent;
import org.polyfrost.oneconfig.api.event.v1.invoke.impl.Subscribe;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

public class OnSendPacket {
    @Subscribe
    public void onSendPacket(PacketEvent.Send event) {
        // On fabric, oneuseblock is triggered on multiplayer servers, so this is useless

        /*try {
            Object packet = event.getPacket();

            if(packet instanceof PlayerInteractBlockC2SPacket) {
                LogUtils.info("OnSendPacket event triggered (INTERACT)");
                MinecraftClient mc = MinecraftClient.getInstance();
                if (mc.player == null || mc.world == null) {
                    return;
                }

                PlayerInteractBlockC2SPacket interactPacket = (PlayerInteractBlockC2SPacket) packet;
                BlockHitResult hitResult = interactPacket.getBlockHitResult();
                Hand hand = interactPacket.getHand();

                // Manually trigger the player interact handler since the native event
                // doesn't work reliably on multiplayer servers like Hypixel
                OnPlayerInteract.onUseBlock(mc.player, mc.world, hand, hitResult);
            }
        } catch (Exception ex) {
            LogUtils.error(ex);
        }*/
    }
}
//#endif
