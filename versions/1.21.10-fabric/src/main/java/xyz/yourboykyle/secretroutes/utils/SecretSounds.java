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

import de.hysky.skyblocker.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import xyz.yourboykyle.secretroutes.config.SRMConfig;


public class SecretSounds {
    private static boolean shouldBypassVolume = false;
    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static long lastPlayed = System.currentTimeMillis();
    private static String[] defaultSounds = new String[]{"mob.blaze.hit", "fire.ignite", "random.orb", "random.break", "mob.guardian.land.hit", "note.pling", "secretroutesmod:zyra.meow0"};

    public static void secretChime(Boolean bypass) {
        if(!bypass &&(!SRMConfig.customSecretSound || ((System.currentTimeMillis() - lastPlayed <= 10) || !Utils.isInDungeons()))){return;}

        if (mc.player == null) return;

        Vec3d playerPos = new Vec3d(mc.player.getX(), mc.player.getY(), mc.player.getZ());

        if(SRMConfig.customSecretSoundIndex == 6){
            long test = System.currentTimeMillis()%9;
            playLoudSound("secretroutesmod:zyra.meow" + test, SRMConfig.customSecretSoundVolume, SRMConfig.customSecretSoundPitch, playerPos);
            lastPlayed = System.currentTimeMillis();
        }else{
            playLoudSound(defaultSounds[SRMConfig.customSecretSoundIndex], SRMConfig.customSecretSoundVolume, SRMConfig.customSecretSoundPitch, playerPos);
            lastPlayed = System.currentTimeMillis();
        }



    }
    public static void secretChime(){
        secretChime(false);
    }


    public static void playLoudSound(String sound, Float volume, Float pitch, Vec3d pos) {
        mc.execute(() -> {
            shouldBypassVolume = true;
            if (mc.world != null) {
                // Convert legacy sound string to Identifier
                Identifier soundId;
                if (sound.contains(":")) {
                    String[] parts = sound.split(":");
                    soundId = Identifier.of(parts[0], parts[1]);
                } else {
                    soundId = Identifier.ofVanilla(sound.replace('.', '_'));
                }

                mc.world.playSound(
                    mc.player,
                    pos.x,
                    pos.y + 1.62,
                    pos.z,
                    net.minecraft.registry.Registries.SOUND_EVENT.get(soundId),
                    SoundCategory.MASTER,
                    volume,
                    pitch
                );
            }
            shouldBypassVolume = false;
        });
    }
}
//#endif
