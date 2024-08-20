package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

public class ChatUtils {
    public static void sendChatMessage(String message, EnumChatFormatting color) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message).setChatStyle(new ChatStyle().setColor(color)));
        LogUtils.info("Sent chat message: " + message);
    }
    public static void sendChatMessage(String message) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
        LogUtils.info("Sent chat message: " + message);
    }

    public static void sendVerboseMessage(String message){
        if(SRMConfig.verboseLogging){
            sendChatMessage(message);
        }
    }
}
