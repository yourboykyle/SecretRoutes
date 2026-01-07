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
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

public class OnChatReceive {
    private static final HashMap<String, String> msgMap = new HashMap<>();
    private static boolean allFound = false;

    public static void register() {
        OnChatReceive instance = new OnChatReceive();

        // Register high priority handler for secrets tracking
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            return instance.handleChatReceive(message, overlay);
        });

        // Register low priority handler for boss message hiding
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            return instance.handleBossMessages(message, overlay);
        });
    }

    public static boolean isAllFound() {
        return allFound;
    }

    private boolean handleChatReceive(Text message, boolean overlay) {
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
                SecretUtils.secretLocations.remove(BlockUtils.blockPos(SecretUtils.lastInteract));
            }).start();
            SecretUtils.renderLever = true;

            JsonObject route = Main.currentRoom.currentSecretRoute.get(Main.currentRoom.currentSecretIndex - 1)
                    .getAsJsonObject().get("secret").getAsJsonObject();
            JsonArray loc = route.get("location").getAsJsonArray();
            BlockPos pos = new BlockPos(loc.get(0).getAsInt(), loc.get(1).getAsInt(), loc.get(2).getAsInt());

            if (route.get("type").getAsString().equals("interact")) {
                if (BlockUtils.compareBlocks(
                        MapUtils.actualToRelative(SecretUtils.lastInteract, RoomDetection.roomDirection(), RoomDetection.roomCorner()),
                        pos, 0)) {
                    Main.currentRoom.lastSecretKeybind();
                }
                sendChatMessage("Distance : " + BlockUtils.blockDistance(
                        MapUtils.actualToRelative(SecretUtils.lastInteract, RoomDetection.roomDirection(), RoomDetection.roomCorner()),
                        pos));
            }
        }

        return true;
    }

    private boolean handleBossMessages(Text message, boolean overlay) {
        // Don't process overlay messages
        if (overlay) return true;

        if (SRMConfig.get().hideBossMessages) {
            if (msgMap.isEmpty()) {
                msgMap.put("§r§c[BOSS] The Watcher", "hideWatcher");
                msgMap.put("§r§c[BOSS] Bonzo", "hideBonzo");
                msgMap.put("§r§c[BOSS] Scarf", "hideScarf");
                msgMap.put("§r§c[BOSS] The Professor", "hideProfessor");
                msgMap.put("§r§c[BOSS] Thorn", "hideThorn");
                msgMap.put("§r§c[BOSS] Livid", "hideLivid");
                msgMap.put("§r§c[BOSS] Sadan", "hideSadan");
                msgMap.put("§r§4[BOSS] Maxor", "hideWitherLords");
                msgMap.put("§r§4[BOSS] Storm", "hideWitherLords");
                msgMap.put("§r§4[BOSS] Goldor", "hideWitherLords");
                msgMap.put("§r§4[BOSS] Necron", "hideWitherLords");
            }

            String formatted = getFormattedText(message);
            if (!formatted.contains("BOSS")) return true;

            if (SRMConfig.get().bloodNotif) {
                if (formatted.equals("§r§c[BOSS] The Watcher§r§f: That will be enough for now.§r")) {
                    sendChatMessage("KILL BLOOD");
                    OnGuiRender.spawnNotifTime = System.currentTimeMillis() + SRMConfig.get().bloodBannerDuration;
                }
            }

            for (Map.Entry<String, String> entry : msgMap.entrySet()) {
                try {
                    Field field = SRMConfig.class.getField(entry.getValue());
                    field.setAccessible(true);
                    if (formatted.startsWith(entry.getKey()) && field.getBoolean(null)) {
                        return false; // Cancel the message
                    }
                } catch (NoSuchFieldException ex) {
                    LogUtils.info("I (_Wyan) screwed up. No field: " + entry.getValue());
                } catch (IllegalAccessException ex) {
                    LogUtils.info("I (_Wyan) screwed up. Field not accessible: " + entry.getValue());
                }
            }
        }

        return true;
    }

    // Helper method to get formatted text from Text component
    private String getFormattedText(Text text) {
        // Convert Text to formatted string with color codes
        // This does work, I think
        return text.getString();
    }
}
