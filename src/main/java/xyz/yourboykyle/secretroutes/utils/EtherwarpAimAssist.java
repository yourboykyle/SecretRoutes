//#if FABRIC
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC & christechs
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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

public final class EtherwarpAimAssist {
    private static final double RAY_END_PADDING = 2.0;

    private static BlockPos cachedTarget;
    private static AABB cachedTargetBox;
    private static boolean targetAcquired;
    private static long offTargetSinceNanos = -1L;

    private EtherwarpAimAssist() {
    }

    public static void update(BlockPos target) {
        SRMConfig config = SRMConfig.get();
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (!config.etherwarpAimSound || target == null || player == null || mc.level == null) {
            reset();
            return;
        }

        if (!target.equals(cachedTarget)) {
            cachedTarget = target;
            cachedTargetBox = new AABB(target);
            targetAcquired = false;
            offTargetSinceNanos = -1L;
        }

        boolean lookingAtTarget = player.isCrouching() && isLookingAtTarget(mc, player);
        long now = System.nanoTime();

        if (lookingAtTarget) {
            offTargetSinceNanos = -1L;
            if (!targetAcquired) {
                SecretSounds.preview(
                        config.etherwarpAimSoundType != null ? config.etherwarpAimSoundType : SRMConfig.SoundType.NOTE_PLING,
                        config.etherwarpAimSoundVolume,
                        config.etherwarpAimSoundPitch
                );
                targetAcquired = true;
            }
            return;
        }

        if (targetAcquired) {
            if (offTargetSinceNanos < 0L) {
                offTargetSinceNanos = now;
            } else if (now - offTargetSinceNanos >= config.etherwarpAimSoundRearmDelay * 1_000_000L) {
                targetAcquired = false;
                offTargetSinceNanos = -1L;
            }
        }
    }

    private static boolean isLookingAtTarget(Minecraft mc, LocalPlayer player) {
        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 eyePosition = player.getEyePosition(partialTick);
        Vec3 targetCenter = cachedTargetBox.getCenter();
        double rayLength = eyePosition.distanceTo(targetCenter) + RAY_END_PADDING;
        Vec3 rayEnd = eyePosition.add(player.getViewVector(partialTick).scale(rayLength));

        if (cachedTargetBox.clip(eyePosition, rayEnd).isEmpty()) {
            return false;
        }

        BlockHitResult hitResult = mc.level.clip(new ClipContext(
                eyePosition,
                rayEnd,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        return hitResult.getType() == HitResult.Type.BLOCK && hitResult.getBlockPos().equals(cachedTarget);
    }

    public static void reset() {
        cachedTarget = null;
        cachedTargetBox = null;
        targetAcquired = false;
        offTargetSinceNanos = -1L;
    }
}
//#endif
