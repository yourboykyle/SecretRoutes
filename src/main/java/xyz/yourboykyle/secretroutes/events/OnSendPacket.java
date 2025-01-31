package xyz.yourboykyle.secretroutes.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.events.PacketEvent;

public class OnSendPacket {
    @SubscribeEvent
    public void onSendPacket(PacketEvent.SendEvent e) {
        try {
            if(e.packet instanceof C08PacketPlayerBlockPlacement) {
                Minecraft mc = Minecraft.getMinecraft();
                OnPlayerInteract.onPlayerInteract(new PlayerInteractEvent(mc.thePlayer, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, ((C08PacketPlayerBlockPlacement) e.packet).getPosition(), EnumFacing.UP, mc.theWorld));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}