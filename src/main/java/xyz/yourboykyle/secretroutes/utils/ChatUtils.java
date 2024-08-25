package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.client.Minecraft;
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
        //if(!SRMConfig.verboseInfo){
           // LogUtils.info("Sent chat message: " + message);
        //}
    }
    public static void sendChatMessage(String message) {
        if(Minecraft.getMinecraft().thePlayer == null){
            return;
        }
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(message));
        //if(!SRMConfig.verboseInfo){
            LogUtils.info("Sent chat message: " + message);
        //}
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
            default:
                sendChatMessage("§d[" + TAG + "] " + message);
                return true;
        }

    }
}
