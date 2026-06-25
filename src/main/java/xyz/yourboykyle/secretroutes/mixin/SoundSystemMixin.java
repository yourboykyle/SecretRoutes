package xyz.yourboykyle.secretroutes.mixin;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable; // <-- Changed!
import xyz.yourboykyle.secretroutes.events.OnPlaySound;

@Mixin(SoundEngine.class)
public class SoundSystemMixin {

    @Inject(method = "play", at = @At("HEAD"))
    private void secretroutes$onPlaySound(SoundInstance sound, CallbackInfoReturnable<?> cir) {
        OnPlaySound.handleSoundPlayed(sound);
    }
}