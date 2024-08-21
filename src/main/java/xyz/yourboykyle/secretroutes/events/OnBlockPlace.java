package xyz.yourboykyle.secretroutes.events;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnBlockPlace {
    private static final String verboseTag = "Recording";
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        ChatUtils.sendVerboseMessage("§d Block placed: " + e.placedBlock.getBlock().getLocalizedName(), verboseTag);
        // Route Recording
        if(e.placedBlock.getBlock() == Blocks.tnt) {
            ChatUtils.sendVerboseMessage("§d TNT placed at: " + e.pos, verboseTag);
            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, e.pos);
            Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
            return;
        }
        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if(heldItem != null) {
            if(heldItem.getItem() == Item.getItemFromBlock(Blocks.tnt)) {
                ChatUtils.sendVerboseMessage("§d TNT held", verboseTag);
                Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, e.pos);
                Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
            }
        }
    }
}
