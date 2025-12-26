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

package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

import java.net.URI;
import java.net.URISyntaxException;

public class ChatUtils {
    public static void sendChatMessage(String message, Formatting color) {
        if(MinecraftClient.getInstance().player == null){
            return;
        }
        MinecraftClient.getInstance().player.sendMessage(Text.literal(message).formatted(color), false);
        LogUtils.info("Sent chat message: " + message);
    }

    public static void sendChatMessage(String message) {
        if(MinecraftClient.getInstance().player == null){
            return;
        }
        MinecraftClient.getInstance().player.sendMessage(Text.literal(message), false);

        LogUtils.info("Sent chat message: " + message);
    }

    public static void sendVerboseMessage(String message){
        if(SRMConfig.verboseLogging){
            sendChatMessage(message);
        }
    }

    public static boolean sendVerboseMessage(String message, String TAG){
        if(MinecraftClient.getInstance().player == null){
            return false;
        }
        switch(TAG){
            case "Recording":
                if(SRMConfig.verboseRecording){
                    sendVerboseMessage("§d[Recording] " + message);
                    return true;
                }
                return false;
            case "Update":
                if(SRMConfig.verboseUpdating){
                    sendVerboseMessage("§d[Update] " + message);
                    return true;
                }
                return false;
            case "Info":
                if(SRMConfig.verboseInfo && !message.contains("Sent chat message")){
                    sendVerboseMessage("§d[Info] " + message);
                    return true;
                }
                return false;
            case "Rendering":
                if(SRMConfig.verboseRendering){
                    sendVerboseMessage("§5[Rendering] " + message);
                    return true;
                }
                return false;
            case "Actionbar":
                if(SRMConfig.actionbarInfo){
                    sendVerboseMessage("§3[ActionBar] §a "+message);
                    return true;
                }
                return false;
            case "Personal Bests":
                if(SRMConfig.verbosePersonalBests){
                    sendVerboseMessage("§d[Personal Bests] " + message);
                    return true;
                }
                return false;
            default:
                if(SRMConfig.verboseLogging){
                    sendChatMessage("§d[" + TAG + "] " + message);
                    return true;
                }
                return false;
        }
    }

    /*public static void sendClickableMessage(String text, String link){
        if(MinecraftClient.getInstance().player == null){
            return;
        }
        ChatComponentText component = new ChatComponentText(text);
        component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to open link")));
        MinecraftClient.getInstance().player.sendMessage(component);
    }*/

    public static void sendClickableMessage(String text, String link){
        if(MinecraftClient.getInstance().player == null){
            return;
        }
        MinecraftClient.getInstance().player.sendMessage(
                Text.literal(text)
                        .styled(style -> {
                                    try {
                                        return style
                                                .withClickEvent(new ClickEvent.OpenUrl(new URI(link)))
                                                .withHoverEvent(new HoverEvent.ShowText(Text.literal("Click to open link")));
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                        return style;
                                    }
                                }
                        ),
                false
        );
    }
}
//#endif