//#if FORGE && MC == 1.8.9
// TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
package xyz.yourboykyle.secretroutes.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * An example mixin using SpongePowered's Mixin library
 *
 * @see Inject
 * @see Mixin
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "startGame", at = @At(value = "HEAD"))
    private void onStartGame(CallbackInfo ci) {
        System.out.println("Thank you for using Secret Routes Mod!");
    }
}
//#endif
