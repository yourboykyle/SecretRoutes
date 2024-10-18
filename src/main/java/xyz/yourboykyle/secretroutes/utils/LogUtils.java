package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogUtils {
    public static void info(String msg) {
        if(!sendVerboseMessage(msg, "Info")) {
            appendToFile("====================\n[INFO] " + msg);
        }
    }

    public static void error(Exception error) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        String stackTrace = sw.toString();

        appendToFile("====================\n[ERROR] " + stackTrace);
        if(Minecraft.getMinecraft().thePlayer != null) {
            sendChatMessage(EnumChatFormatting.DARK_RED+"Error caught by Secret Routes. Check latest logs at .minecraft/logs/SecretRoutes/LATEST-{date}.log. SEND THIS FILE IN #SUPPORT IN THE DISCORD FOR HELP");
            sendChatMessage(EnumChatFormatting.DARK_RED+error.getLocalizedMessage());
        }
    }

    public static void appendToFile(String msg) {
        try {
            FileWriter fw = new FileWriter(Main.outputLogs, true);
            fw.write(msg + "\n");
            fw.close();
        } catch (Exception e) {
            System.out.println("Secret Routes Logging Exception:");
            e.printStackTrace();
        }
    }
}