package xyz.yourboykyle.secretroutes.utils;

import io.github.quantizr.dungeonrooms.utils.Utils;
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
        if(!bypass && SRMConfig.customSecretSound && ((System.currentTimeMillis() - lastPlayed <= 10) || !Utils.inCatacombs)){return;}

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
