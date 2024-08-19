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

    public static void drawText(double worldX, double worldY, double worldZ, String text, float size) {
        BlockPos pos = new BlockPos(worldX, worldY, worldZ);

        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        text = EnumChatFormatting.BOLD + text;

        WaypointUtils.renderWaypointText(text, pos, 0, size);
    }

    public static String getTextColor(int index) {
        switch (index) {
            case 0:
                return EnumChatFormatting.BLACK.toString();
            case 1:
                return EnumChatFormatting.DARK_BLUE.toString();
            case 2:
                return EnumChatFormatting.DARK_GREEN.toString();
            case 3:
                return EnumChatFormatting.DARK_AQUA.toString();
            case 4:
                return EnumChatFormatting.DARK_RED.toString();
            case 5:
                return EnumChatFormatting.DARK_PURPLE.toString();
            case 6:
                return EnumChatFormatting.GOLD.toString();
            case 7:
                return EnumChatFormatting.GRAY.toString();
            case 8:
                return EnumChatFormatting.DARK_GRAY.toString();
            case 9:
                return EnumChatFormatting.BLUE.toString();
            case 10:
                return EnumChatFormatting.GREEN.toString();
            case 11:
                return EnumChatFormatting.AQUA.toString();
            case 12:
                return EnumChatFormatting.RED.toString();
            case 13:
                return EnumChatFormatting.LIGHT_PURPLE.toString();
            case 14:
                return EnumChatFormatting.YELLOW.toString();
            case 15:
                return EnumChatFormatting.WHITE.toString();
            default:
                return EnumChatFormatting.RED.toString();
        }
    }
}