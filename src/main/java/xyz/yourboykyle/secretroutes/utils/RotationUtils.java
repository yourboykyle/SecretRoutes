// TODO: update this file for multi versioning (1.8.9 -> 1.21.8)
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

public class RotationUtils {
    public static float actualToRelativeYaw(float yaw, String direction) {
        switch(direction) {
            case "SW":
                return yaw;
            case "NW":
                return yaw - 90;
            case "NE":
                return yaw - 180;
            case "SE":
                return yaw - 270;
            default:
                return yaw;
        }
    }

    public static float relativeToActualYaw(float yaw, String direction) {
        switch(direction) {
            case "SW":
                return yaw;
            case "NW":
                return yaw + 90;
            case "NE":
                return yaw + 180;
            case "SE":
                return yaw + 270;
            default:
                return yaw;
        }
    }
}
