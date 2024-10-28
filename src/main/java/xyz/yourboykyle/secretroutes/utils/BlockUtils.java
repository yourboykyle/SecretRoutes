/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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

import net.minecraft.util.BlockPos;

public class BlockUtils {

    public static String blockPos(BlockPos pos) {
        return pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
    }


    public static boolean compareBlocks(BlockPos pos1, BlockPos pos2, int dist) {
        return pos1.getX() >= pos2.getX() - dist && pos1.getX() <= pos2.getX() + dist && pos1.getY() >= pos2.getY() - dist && pos1.getY() <= pos2.getY() + dist && pos1.getZ() >= pos2.getZ() - dist && pos1.getZ() <= pos2.getZ() + dist;
    }
}
