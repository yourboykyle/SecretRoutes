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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

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
        errorNoShout(error);

        if(Minecraft.getMinecraft().thePlayer != null) {
            switch(error.getClass().getTypeName()){
                case "java.io.FileNotFoundException":
                    sendChatMessage(EnumChatFormatting.DARK_RED+"The system cannot find the file specified. Please ensure that this is the correct file name §c\""+ (SRMConfig.pearls ? SRMConfig.pearlRoutesFileName : SRMConfig.routesFileName) + "\"§4 and that it exists in §c" + Main.ROUTES_PATH);
                    break;
                default:
                    sendChatMessage(EnumChatFormatting.DARK_RED+"Error caught by Secret Routes. Check latest logs at .minecraft/logs/SecretRoutes/LATEST-{date}.log. SEND THIS FILE IN #SUPPORT IN THE DISCORD FOR HELP. ("+ error.getLocalizedMessage()+")");
            }
        }
    }
    public static void errorNoShout(Exception error) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        String stackTrace = sw.toString();

        appendToFile("====================\n[ERROR] " + stackTrace);
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