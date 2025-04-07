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

import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

public class ChatUtils {
    public static void sendChatMessage(String message, EnumChatFormatting color) {
        if(Minecraft.getMinecraft().thePlayer == null){
            return;
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message).setChatStyle(new ChatStyle().setColor(color)));
        LogUtils.info("Sent chat message: " + message);
    }
    public static void sendChatMessage(String message) {
        if(Minecraft.getMinecraft().thePlayer == null){
            return;
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));

        LogUtils.info("Sent chat message: " + message);


    }

    public static void sendVerboseMessage(String message){
        if(SRMConfig.verboseLogging){
            sendChatMessage(message);
        }
    }
    public static boolean sendVerboseMessage(String message, String TAG){
        if(Minecraft.getMinecraft().thePlayer == null){
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
            default:
                if(SRMConfig.verboseLogging){
                    sendChatMessage("§d[" + TAG + "] " + message);
                    return true;
                }
        }

    }
    public static void sendClickableMessage(String text, String link){
        if(Minecraft.getMinecraft().thePlayer == null){
            return;
        }
        ChatComponentText component = new ChatComponentText(text);
        component.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link));
        component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to open link")));
        Minecraft.getMinecraft().thePlayer.addChatMessage(component);
    }
}
