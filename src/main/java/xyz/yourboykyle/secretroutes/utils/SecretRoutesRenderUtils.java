/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
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

import cc.polyfrost.oneconfig.config.core.OneColor;
import io.github.quantizr.dungeonrooms.utils.WaypointUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.config.pages.ColorsPage;

import java.awt.*;

public class SecretRoutesRenderUtils {
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, OneColor color) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        RenderUtils.drawBoxAtBlock(x, y, z, color, 1, 1, 1);
    }
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, OneColor color, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        RenderUtils.drawBoxAtBlock(x, y, z, color, width, height, 1);
    }
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, OneColor color, int width, int height, int alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        RenderUtils.drawBoxAtBlock(x, y, z,color, width, height, alpha);
    }

    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, OneColor color) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        int width = 1;
        int height = 1;

        WaypointUtils.drawFilledBoundingBox(new AxisAlignedBB(x - 0.01, y - 0.01, z - 0.01, x + width + 0.01, y + height + 0.01, z + width + 0.01), color, 1);
    }
    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, OneColor color, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        WaypointUtils.drawFilledBoundingBox(new AxisAlignedBB(x - 0.01, y - 0.01, z - 0.01, x + width + 0.01, y + height + 0.01, z + width + 0.01), color, 0);
    }
    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, OneColor color, int width, int height, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        WaypointUtils.drawFilledBoundingBox(new AxisAlignedBB(x - 0.01, y - 0.01, z - 0.01, x + width + 0.01, y + height + 0.01, z + width + 0.01), color, alpha);
    }

    public static void drawBeaconBeam(double worldX, double worldY, double worldZ, int RGB, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        WaypointUtils.renderBeaconBeam(x, y, z, RGB, alpha, 0);
    }

    public static void drawText(double worldX, double worldY, double worldZ, String text) {
        BlockPos pos = new BlockPos(worldX, worldY, worldZ);

        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        switch(ColorsPage.startWaypointIndex){
            case 0:
                text = EnumChatFormatting.BLACK + text;
                break;
            case 1:
                text = EnumChatFormatting.DARK_BLUE + text;
                break;
            case 2:
                text = EnumChatFormatting.DARK_GREEN + text;
                break;
            case 3:
                text = EnumChatFormatting.DARK_AQUA + text;
                break;
            case 4:
                text = EnumChatFormatting.DARK_RED + text;
                break;
            case 5:
                text = EnumChatFormatting.DARK_PURPLE + text;
                break;
            case 6:
                text = EnumChatFormatting.GOLD + text;
                break;
            case 7:
                text = EnumChatFormatting.GRAY + text;
                break;
            case 8:
                text = EnumChatFormatting.DARK_GRAY + text;
                break;
            case 9:
                text = EnumChatFormatting.BLUE + text;
                break;
            case 10:
                text = EnumChatFormatting.GREEN + text;
                break;
            case 11:
                text = EnumChatFormatting.AQUA + text;
                break;
            case 12:
                text = EnumChatFormatting.RED + text;
                break;
            case 13:
                text = EnumChatFormatting.LIGHT_PURPLE + text;
                break;
            case 14:
                text = EnumChatFormatting.YELLOW + text;
                break;
            case 15:
                text = EnumChatFormatting.WHITE + text;
                break;
            default:
                text = EnumChatFormatting.RED + text;
                break;
        }

        WaypointUtils.renderWaypointText(text, pos, 0);
    }
}