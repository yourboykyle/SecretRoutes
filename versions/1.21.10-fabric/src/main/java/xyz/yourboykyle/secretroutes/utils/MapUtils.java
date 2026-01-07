/*
 * Dungeon Rooms Mod - Secret Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2021 Quantizr(_risk)
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

import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;

import java.awt.*;

public class MapUtils {
    /**
     * @return the actual coordinate of a block given the relative coordinate
     */
    public static BlockPos actualToRelative(BlockPos actual, String cornerDirection, Point locationOfCorner) {
        if (cornerDirection == null) {
            cornerDirection = "northwest";
        }
        if (locationOfCorner == null) {
            locationOfCorner = new Point(0, 0);
        }
        double x = 0;
        double z = 0;
        switch (cornerDirection) {
            case "northwest":
                x = actual.getX() - locationOfCorner.getX();
                z = actual.getZ() - locationOfCorner.getY(); //.getY in a point is the MC Z coord
                break;
            case "northeast":
                x = actual.getZ() - locationOfCorner.getY();
                z = -(actual.getX() - locationOfCorner.getX());
                break;
            case "southeast":
                x = -(actual.getX() - locationOfCorner.getX());
                z = -(actual.getZ() - locationOfCorner.getY());
                break;
            case "southwest":
                x = -(actual.getZ() - locationOfCorner.getY());
                z = actual.getX() - locationOfCorner.getX();
                break;
        }
        return new BlockPos((int) x, actual.getY(), (int) z);
    }

    public static Triple<Double, Double, Double> actualToRelative(double posX, double posY, double posZ, String cornerDirection, Point locationOfCorner) {
        if (cornerDirection == null) {
            cornerDirection = "northwest";
        }
        if (locationOfCorner == null) {
            locationOfCorner = new Point(0, 0);
        }
        double x = 0;
        double z = 0;
        switch (cornerDirection) {
            case "northwest":
                x = posX - locationOfCorner.getX();
                z = posZ - locationOfCorner.getY(); //.getY in a point is the MC Z coord
                break;
            case "northeast":
                x = posZ - locationOfCorner.getY();
                z = -(posX - locationOfCorner.getX());
                break;
            case "southeast":
                x = -(posX - locationOfCorner.getX());
                z = -(posZ - locationOfCorner.getY());
                break;
            case "southwest":
                x = -(posZ - locationOfCorner.getY());
                z = posX - locationOfCorner.getX();
                break;
        }
        return new Triple<>(x, posY, z);
    }

    /**
     * @return the relative coordinate of a block given the actual coordinate
     */
    public static BlockPos relativeToActual(BlockPos relative, String cornerDirection, Point locationOfCorner) {
        if (cornerDirection == null) {
            cornerDirection = "northwest";
        }
        if (locationOfCorner == null) {
            locationOfCorner = new Point(0, 0);
        }
        double x = 0;
        double z = 0;
        switch (cornerDirection) {
            case "northwest":
                x = relative.getX() + locationOfCorner.getX();
                z = relative.getZ() + locationOfCorner.getY(); //.getY in a point is the MC Z coord
                break;
            case "northeast":
                x = -(relative.getZ() - locationOfCorner.getX());
                z = relative.getX() + locationOfCorner.getY();
                break;
            case "southeast":
                x = -(relative.getX() - locationOfCorner.getX());
                z = -(relative.getZ() - locationOfCorner.getY());
                break;
            case "southwest":
                x = relative.getZ() + locationOfCorner.getX();
                z = -(relative.getX() - locationOfCorner.getY());
                break;
        }
        return new BlockPos((int) x, relative.getY(), (int) z);
    }

    public static Triple<Double, Double, Double> relativeToActual(double posX, double posY, double posZ, String cornerDirection, Point locationOfCorner) {
        if (cornerDirection == null) {
            cornerDirection = "northwest";
        }
        if (locationOfCorner == null) {
            locationOfCorner = new Point(0, 0);
        }
        double x = 0;
        double z = 0;
        switch (cornerDirection) {
            case "northwest":
                x = posX + locationOfCorner.getX();
                z = posZ + locationOfCorner.getY(); //.getY in a point is the MC Z coord
                break;
            case "northeast":
                x = -(posZ - locationOfCorner.getX());
                z = posX + locationOfCorner.getY();
                break;
            case "southeast":
                x = -(posX - locationOfCorner.getX());
                z = -(posZ - locationOfCorner.getY());
                break;
            case "southwest":
                x = posZ + locationOfCorner.getX();
                z = -(posX - locationOfCorner.getY());
                break;
        }
        return new Triple<>(x, posY, z);
    }
}
