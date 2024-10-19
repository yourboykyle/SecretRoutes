package xyz.yourboykyle.secretroutes.events;

import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ServerTickEvent extends Event {
    public String modid;

    public ServerTickEvent(S32PacketConfirmTransaction packet, String modid) {
        this.modid = modid;
    }


}
