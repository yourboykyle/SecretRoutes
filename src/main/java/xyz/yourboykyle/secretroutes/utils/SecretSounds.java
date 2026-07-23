//#if FABRIC
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

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import org.joml.Vector3d;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

public class SecretSounds {
    private static Minecraft mc = Minecraft.getInstance();
    private static long lastPlayed = System.currentTimeMillis();

    public static void secretChime(Boolean bypass) {
        SRMConfig config = SRMConfig.get();
        if (!bypass && (!config.customSecretSound || ((System.currentTimeMillis() - lastPlayed <= 10) || !LocationUtils.isInDungeons()))) {
            return;
        }

        SRMConfig.SoundType soundType = config.customSecretSoundType != null
                ? config.customSecretSoundType
                : SRMConfig.SoundType.NOTE_PLING;
        preview(soundType, config.customSecretSoundVolume, config.customSecretSoundPitch);
        lastPlayed = System.currentTimeMillis();
    }

    public static void secretChime() {
        secretChime(false);
    }

    public static void preview(SRMConfig.SoundType soundType, float volume, float pitch) {
        if (mc.player == null || soundType == null) return;

        Vector3d playerPos = new Vector3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        playLoudSound(soundType.soundId, volume, pitch, playerPos);
    }

    public static void playLoudSound(String sound, Float volume, Float pitch, Vector3d pos) {
        mc.execute(() -> {
            if (mc.level != null) {
                Identifier soundId;
                if (sound.contains(":")) {
                    String[] parts = sound.split(":");
                    soundId = Identifier.fromNamespaceAndPath(parts[0], parts[1]);
                } else {
                    soundId = Identifier.withDefaultNamespace(sound);
                }

                var soundEvent = BuiltInRegistries.SOUND_EVENT.get(soundId);

                soundEvent.ifPresent(soundEventReference -> mc.level.playLocalSound(
                        pos.x,
                        pos.y + 1.62,
                        pos.z,
                        soundEventReference.value(),
                        SoundSource.MASTER,
                        volume,
                        pitch,
                        false
                ));
            }
        });
    }
}
//#endif
