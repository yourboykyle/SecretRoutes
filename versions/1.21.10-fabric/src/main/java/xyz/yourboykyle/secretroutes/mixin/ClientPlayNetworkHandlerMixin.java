package xyz.yourboykyle.secretroutes.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.yourboykyle.secretroutes.events.OnReceivePacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onItemPickupAnimation", at = @At("HEAD"))
    private void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
        OnReceivePacket.onItemPickup(packet);
    }

    @Inject(method = "onBlockUpdate", at = @At("HEAD"))
    private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        OnReceivePacket.onBlockUpdate(packet);
    }
}