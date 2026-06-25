package xyz.yourboykyle.secretroutes.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.yourboykyle.secretroutes.events.OnReceivePacket;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "handleTakeItemEntity", at = @At("HEAD"))
    private void secretroutes$onItemPickupAnimation(ClientboundTakeItemEntityPacket packet, CallbackInfo ci) {
        OnReceivePacket.onItemPickup(packet);
    }

    @Inject(method = "handleBlockUpdate", at = @At("HEAD"))
    private void secretroutes$onBlockUpdate(ClientboundBlockUpdatePacket packet, CallbackInfo ci) {
        OnReceivePacket.onBlockUpdate(packet);
    }
}