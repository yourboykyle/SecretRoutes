package xyz.yourboykyle.secretroutes.events;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnBlockPlace {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        // Route Recording
        if(e.placedBlock.getBlock() == Blocks.tnt) {
            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, e.pos);
            Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
        }
    }
}
