package xyz.yourboykyle.secretroutes.mixin;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.yourboykyle.secretroutes.events.OnPlaySound;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"))
    private void onPlaySound(SoundInstance sound, int delay, CallbackInfo ci) {
        OnPlaySound.handleSoundPlayed(sound);
    }
}