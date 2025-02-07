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

import net.minecraft.util.BlockPos;

public class BlockUtils {

    public static String blockPos(BlockPos pos) {
        if(pos == null){return ":::";}
        return pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
    }
    public static BlockPos blockPos(String pos) {
        try{
            String[] parts = pos.split(":");
            return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }catch(NumberFormatException e){
            ChatUtils.sendChatMessage("NMumber format exception, null.");
            return null;
        }
    }


    public static boolean compareBlocks(BlockPos pos1, BlockPos pos2, int dist) {
        return pos1.getX() >= pos2.getX() - dist && pos1.getX() <= pos2.getX() + dist && pos1.getY() >= pos2.getY() - dist && pos1.getY() <= pos2.getY() + dist && pos1.getZ() >= pos2.getZ() - dist && pos1.getZ() <= pos2.getZ() + dist;
    }
    public static double blockDistance(BlockPos pos1, BlockPos pos2) {
        if(pos1 == null || pos2 == null){
            return Integer.MAX_VALUE;}
        float xdiff = Math.abs(pos1.getX() - pos2.getX());
        float ydiff = Math.abs(pos1.getY() - pos2.getY());
        float zdiff = Math.abs(pos1.getZ() - pos2.getZ());
        return Math.pow((Math.pow(xdiff, 2)+Math.pow(ydiff, 2)+Math.pow(zdiff, 2)), 0.5);
    }
    public static double blockDistance(BlockPos pos1, String pos2) {
        BlockPos pos2Block = blockPos(pos2);
        if(pos2Block == null){return Integer.MAX_VALUE;}
        return blockDistance(pos1, pos2Block);
    }
}
