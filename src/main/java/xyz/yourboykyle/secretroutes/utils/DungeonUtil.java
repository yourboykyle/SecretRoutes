/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC & christechs
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

package xyz.yourboykyle.secretroutes.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class DungeonUtil {
    private static boolean inF7 = false;
    private static int tickCounter = 0;

    public enum F7Phase {
        NONE, MAXOR, STORM, GOLDOR, NECRON
    }
    private static F7Phase currentPhase = F7Phase.NONE;

    public static void init() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> reset());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> reset());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!LocationUtils.isInDungeons() || client.level == null) {
                inF7 = false;
                currentPhase = F7Phase.NONE;
                return;
            }

            tickCounter++;
            if (tickCounter >= 20) {
                tickCounter = 0;
                checkFloor(client);
            }
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!inF7) return;

            String cleanText = ChatFormatting.stripFormatting(message.getString());

            switch (cleanText) {
                case "[BOSS] Maxor: WELL WELL WELL LOOK WHO'S HERE!" -> currentPhase = F7Phase.MAXOR;
                case "[BOSS] Storm: Pathetic Maxor, just like expected." -> currentPhase = F7Phase.STORM;
                case "[BOSS] Goldor: Who dares trespass into my domain?" -> currentPhase = F7Phase.GOLDOR;
                case "The Core entrance is opening!" -> currentPhase = F7Phase.NECRON;
            }
        });
    }

    private static void checkFloor(Minecraft client) {
        Scoreboard scoreboard = client.level.getScoreboard();
        boolean foundF7 = false;

        for (PlayerTeam team : scoreboard.getPlayerTeams()) {
            String rawText = team.getPlayerPrefix().getString() + team.getPlayerSuffix().getString();

            String cleanText = ChatFormatting.stripFormatting(rawText);

            if (cleanText.contains("The Catacombs (F7)") || cleanText.contains("The Catacombs (M7)")) {
                foundF7 = true;
                break;
            }
        }

        inF7 = foundF7;
    }

    public static boolean isF7() {
        return inF7;
    }

    public static F7Phase getCurrentPhase() {
        return currentPhase;
    }

    public static void reset() {
        inF7 = false;
        tickCounter = 0;
        currentPhase = F7Phase.NONE;
    }
}