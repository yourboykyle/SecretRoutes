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
import xyz.yourboykyle.secretroutes.config.SRMConfig;

public class SecretRoutesRenderUtils {

    public static String getTextColor(SRMConfig.TextColor color) {
        if (color == null) return Formatting.RED.toString();
        return color.formatting.toString();
    }

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