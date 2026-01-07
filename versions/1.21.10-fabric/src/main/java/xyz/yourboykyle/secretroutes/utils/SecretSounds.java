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
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.LocationUtils;

public class SecretSounds {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static long lastPlayed = System.currentTimeMillis();

    public static void secretChime(Boolean bypass) {
        SRMConfig config = SRMConfig.get();
        if (!bypass && (!config.customSecretSound || ((System.currentTimeMillis() - lastPlayed <= 10) || !LocationUtils.isInDungeons()))) {
            return;
        }

        if (mc.player == null) return;

        Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());

        if (config.customSecretSoundType == SRMConfig.SoundType.ZYRA_MEOW) {
            long test = System.currentTimeMillis() % 9;
            playLoudSound("secretroutesmod:zyra.meow" + test, config.customSecretSoundVolume, config.customSecretSoundPitch, playerPos);
            lastPlayed = System.currentTimeMillis();
        } else {
            String soundName = config.customSecretSoundType.getDisplayName().getString();
            playLoudSound(soundName, config.customSecretSoundVolume, config.customSecretSoundPitch, playerPos);
            lastPlayed = System.currentTimeMillis();
        }
    }

    public static void secretChime() {
        secretChime(false);
    }

    public static void playLoudSound(String sound, Float volume, Float pitch, Vec3d pos) {
        mc.execute(() -> {
            if (mc.world != null) {
                Identifier soundId;
                if (sound.contains(":")) {
                    String[] parts = sound.split(":");
                    soundId = Identifier.of(parts[0], parts[1]);
                } else {
                    soundId = Identifier.ofVanilla(sound);
                }

                var soundEvent = net.minecraft.registry.Registries.SOUND_EVENT.get(soundId);

                if (soundEvent != null) {
                    mc.world.playSound(
                            mc.player,
                            pos.x,
                            pos.y + 1.62,
                            pos.z,
                            soundEvent,
                            SoundCategory.MASTER,
                            volume,
                            pitch
                    );
                }
            }
        });
    }
}
