package xyz.yourboykyle.secretroutes.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnBlockBreak {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        // Route Recording
        if(e.getPlayer().getUniqueID() == Minecraft.getMinecraft().thePlayer.getUniqueID() && Main.routeRecording.recording) {
                Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.MINES, e.pos);
        }
    }
}