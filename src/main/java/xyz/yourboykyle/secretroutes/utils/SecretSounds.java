/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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

import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;
import xyz.yourboykyle.secretroutes.config.SRMConfig;


public class SecretSounds {

    private static boolean shouldBypassVolume = false;
    private static Minecraft mc = Minecraft.getMinecraft();
    private static long lastPlayed = System.currentTimeMillis();
    private static String[] defaultSounds = new String[]{"mob.blaze.hit", "fire.ignite", "random.orb", "random.break", "mob.guardian.land.hit", "note.pling", "secretroutesmod:zyra.meow0"};

    public static void secretChime(Boolean bypass) {
        Utils.checkForCatacombs();
        if(!bypass &&(!SRMConfig.customSecretSound || ((System.currentTimeMillis() - lastPlayed <= 10) || !Utils.inCatacombs))){return;}

        if(SRMConfig.customSecretSoundIndex == 6){
            long test = System.currentTimeMillis()%9;
            playLoudSound("secretroutesmod:zyra.meow"+test, SRMConfig.customSecretSoundVolume, SRMConfig.customSecretSoundPitch, mc.thePlayer.getPositionVector());
            lastPlayed = System.currentTimeMillis();
        }else{
            playLoudSound(defaultSounds[SRMConfig.customSecretSoundIndex], SRMConfig.customSecretSoundVolume, SRMConfig.customSecretSoundPitch, mc.thePlayer.getPositionVector());
            lastPlayed = System.currentTimeMillis();
        }



    }
    public static void secretChime(){
        secretChime(false);
    }


    public static void playLoudSound(String sound, Float volume, Float pitch, Vec3 pos) {
        mc.addScheduledTask(() -> {
            shouldBypassVolume = true;
            mc.theWorld.playSound(pos.xCoord, pos.yCoord+1.62, pos.zCoord, sound, volume, pitch, false);
            shouldBypassVolume = false;
        });
    }
}
