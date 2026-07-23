//#if FABRIC
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.dungeons.SecretUtils;
import xyz.yourboykyle.secretroutes.utils.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

public class OnChatReceive {
    private static boolean allFound = false;

    public static void register() {
        OnChatReceive instance = new OnChatReceive();

        // Register high priority handler for secrets tracking
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            return instance.handleChatReceive(message, overlay);
        });
    }

    public static boolean isAllFound() {
        return allFound;
    }

    private boolean handleChatReceive(Component message, boolean overlay) {
        if (!LocationUtils.isOnSkyblock()) {
            return true;
        }

        String unformatted = message.getString();

        // Handle overlay/actionbar messages
        if (overlay) {
            Matcher matcher = Pattern.compile("§7(?<roomCollectedSecrets>\\d+)/(?<roomTotalSecrets>\\d+) Secrets").matcher(unformatted);
            if (matcher.find()) {
                int roomSecrets = Integer.parseInt(matcher.group("roomTotalSecrets"));
                int secretsFound = Integer.parseInt(matcher.group("roomCollectedSecrets"));
                if (roomSecrets == secretsFound) {
                    sendVerboseMessage("§aAll secrets found!", "Actionbar");
                    allFound = true;
                } else {
                    sendVerboseMessage("§9(" + secretsFound + "/" + roomSecrets + ")", "Actionbar");
                    allFound = false;
                }
            }
            return true;
        }

        // Handle regular chat messages
        String formatted = getFormattedText(message);
        if (formatted.startsWith("§r§cThat chest is locked")) {
            LogUtils.info("§aLocked chest detected!");
            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
                if (SecretUtils.lastInteract != null) {
                    SecretUtils.secretLocations.remove(BlockUtils.blockPos(SecretUtils.lastInteract));
                }
            }).start();
            SecretUtils.renderLever = true;

            if (Main.currentRoom.currentSecretRoute != null && Main.currentRoom.currentSecretIndex > 0) {
                JsonObject route = Main.currentRoom.currentSecretRoute.get(Main.currentRoom.currentSecretIndex - 1)
                        .getAsJsonObject().get("secret").getAsJsonObject();
                JsonArray loc = route.get("location").getAsJsonArray();
                BlockPos pos = new BlockPos(loc.get(0).getAsInt(), loc.get(1).getAsInt(), loc.get(2).getAsInt());

                if (route.get("type").getAsString().equals("interact")) {
                    if (SecretUtils.lastInteract != null && BlockUtils.compareBlocks(
                            RoomRotationUtils.actualToRelative(SecretUtils.lastInteract, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner()),
                            pos, 0)) {
                        Main.currentRoom.lastSecretKeybind();
                    }
                    if (SecretUtils.lastInteract != null) {
                        sendChatMessage("Distance : " + BlockUtils.blockDistance(
                                RoomRotationUtils.actualToRelative(SecretUtils.lastInteract, RoomDirectionUtils.roomDirection(), RoomDirectionUtils.roomCorner()),
                                pos));
                    }
                }
            }
        }

        return true;
    }

    // Helper method to get formatted text from Text component
    private String getFormattedText(Component text) {
        // Convert Text to formatted string with color codes
        // This does work, I think
        return text.getString();
    }
}
//#endif
