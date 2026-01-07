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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 * Tracks server ticks using client tick xyz.yourboykyle.secretroutes.events.
 * Note: This is an approximation since we're on the client side.
 * The old Forge version used transaction packets to estimate server ticks,
 * but in Fabric we just count client ticks as a close approximation.
 */
public class OnServerTick {
    public static int ticks = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(OnServerTick::onClientTick);
    }

    private static void onClientTick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }

        ticks++;

    }
}
