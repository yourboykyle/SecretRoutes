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

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;

public class GuildEvents {
    private static final String[] formats = {"§a", "§b", "§c", "§d", "§e", "§f", "§1", "§2", "§3", "§4", "§5", "§6", "§7", "§8", "§9", "§0", "§k", "§l", "§m", "§n", "§o", "§r"};
    private static final String SEARCH = "§2Guild > §a[VIP§6+§a] SRMBridge";

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            // overlay = true means it's an overlay/actionbar message
            if (overlay || !SRMConfig.get().bridge) return true;

            String messageText = message.getString();
            if (messageText.contains(SEARCH)) {
                String tmp = messageText.split(":")[1];
                String sec1 = tmp.substring(0, tmp.indexOf("»") - 1).replaceFirst("»", ":");
                String sec2 = tmp.substring(tmp.indexOf("»") + 1);
                ChatUtils.sendChatMessage("§2Bridge >§b" + sec1 + "§r:" + sec2);
                return false; // Cancel the original message
            }
            return true; // Allow the message
        });
    }

    public static String cleanMessage(String message) {
        for (String format : formats) {
            message = message.replace(format, "");
        }
        return message;
    }
}
