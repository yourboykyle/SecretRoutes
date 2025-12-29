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

import net.minecraft.util.Formatting;
import org.polyfrost.polyui.color.PolyColor;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;

public class SecretRoutesRenderUtils {
    // TODO: Update this rendering stuff for fabric 1.21.10
    /*public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, PolyColor color) {
        drawBoxAtBlock(worldX, worldY, worldZ, color, 1, 1, 1);
    }
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, PolyColor color, int width, int height) {
        drawBoxAtBlock(worldX, worldY, worldZ, color, width, height, 1);
    }
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, PolyColor color, double width, double height) {
        drawBoxAtBlock(worldX, worldY, worldZ, color, width, height, 1);
    }
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, PolyColor color, double width, double height, int alpha) {
        Triple<Double, Double, Double> pos = BlockUtils.playerToWorld(worldX, worldY, worldZ);
        RenderUtils.drawBoxAtBlock(pos.getOne(), pos.getTwo(), pos.getThree(), color, width, height, alpha);
    }

    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, PolyColor color) {
        drawFilledBoxAtBlock(worldX, worldY, worldZ, color, 1, 1, SRMConfig.alphaMultiplier);


    }
    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, PolyColor color, float width, float height) {
        drawFilledBoxAtBlock(worldX, worldY, worldZ, color, width, height, SRMConfig.alphaMultiplier);
    }
    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, PolyColor color, float width, float height, float alpha) {
        Triple<Double, Double, Double> pos = BlockUtils.playerToWorld(worldX, worldY, worldZ);
        WaypointUtils.drawFilledBoundingBox(new AxisAlignedBB(pos.getOne() - 0.01, pos.getTwo() - 0.01, pos.getThree() - 0.01, pos.getOne() + width + 0.01, pos.getTwo() + height + 0.01, pos.getThree() + width + 0.01), color, alpha);
    }

    public static void drawBeaconBeam(double worldX, double worldY, double worldZ, int RGB, float alpha) {
        Triple<Double, Double, Double> pos = BlockUtils.playerToWorld(worldX, worldY, worldZ);
        WaypointUtils.renderBeaconBeam(pos.getOne(), pos.getTwo(), pos.getThree(), RGB, alpha, 0);
    }

    public static void drawText(double worldX, double worldY, double worldZ, String text, float size, float partialTicks) {
        BlockPos pos = new BlockPos(worldX, worldY, worldZ);
        text = Formatting.BOLD + text;
        //GlStateManager.disableTexture2D();
        //WaypointUtils.renderWaypointText(text, pos, partialTicks, size);
        RenderUtils.drawText(text, pos, partialTicks, false, Constants.shadows, size);
        //GlStateManager.enableTexture2D();
    }*/

    public static String getTextColor(int index) {
        switch (index) {
            case 0:
                return Formatting.BLACK.toString();
            case 1:
                return Formatting.DARK_BLUE.toString();
            case 2:
                return Formatting.DARK_GREEN.toString();
            case 3:
                return Formatting.DARK_AQUA.toString();
            case 4:
                return Formatting.DARK_RED.toString();
            case 5:
                return Formatting.DARK_PURPLE.toString();
            case 6:
                return Formatting.GOLD.toString();
            case 7:
                return Formatting.GRAY.toString();
            case 8:
                return Formatting.DARK_GRAY.toString();
            case 9:
                return Formatting.BLUE.toString();
            case 10:
                return Formatting.GREEN.toString();
            case 11:
                return Formatting.AQUA.toString();
            case 13:
                return Formatting.LIGHT_PURPLE.toString();
            case 14:
                return Formatting.YELLOW.toString();
            case 15:
                return Formatting.WHITE.toString();
            default:
                return Formatting.RED.toString();
        }
    }
}
//#endif
