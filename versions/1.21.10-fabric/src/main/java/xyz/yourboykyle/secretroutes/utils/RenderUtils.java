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

import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class RenderUtils {
    public static void spawnParticleAtLocation(BlockPos loc, BlockPos offset, ParticleEffect particle) {
        World world = MinecraftClient.getInstance().world;

        if (world != null) {
            double x = loc.getX() + 0.5;
            double y = loc.getY() + 0.5;
            double z = loc.getZ() + 0.5;

            double offsetX = offset.getX();
            double offsetY = offset.getY();
            double offsetZ = offset.getZ();

            world.addParticleClient(particle, true, true, x, y, z, 0, 0, 0);
        }
    }

    public static void drawLineParticles(BlockPos loc1, BlockPos loc2, ParticleEffect particle) {
        double distanceX = loc2.getX() - loc1.getX();
        double distanceY = loc2.getY() - loc1.getY();
        double distanceZ = loc2.getZ() - loc1.getZ();

        double maxDistance = Math.max(Math.abs(distanceX), Math.abs(distanceZ));
        int maxPoints = (int) Math.ceil(maxDistance * 1);

        double deltaX = distanceX / (double) maxPoints;
        double deltaY = distanceY / (double) maxPoints;
        double deltaZ = distanceZ / (double) maxPoints;

        double x = loc1.getX();
        double y = loc1.getY();
        double z = loc1.getZ();

        for (int i = 0; i <= maxPoints; i++) {
            //double offsetRot = Math.atan2 (distanceX, distanceY);
            //double offsetX = Math.cos(offsetRot)*0.25;
            //double offsetZ = Math.sin(offsetRot)*0.25;

            spawnParticleAtLocation(new BlockPos((int) x, (int) y, (int) z), new BlockPos(0, 0, 0), particle);

            x += deltaX;
            y += deltaY;
            z += deltaZ;
        }
    }

    public static void drawLineMultipleParticles(ParticleEffect particle, List<BlockPos> locations) {
        if (locations == null) {
            return;
        }
        if (locations.size() >= 2) {
            BlockPos lastLoc = null;
            for (BlockPos loc : locations) {
                if (lastLoc == null) {
                    lastLoc = loc;
                    continue;
                }

                drawLineParticles(lastLoc, loc, particle);
                lastLoc = loc;
            }
        }
    }
}
