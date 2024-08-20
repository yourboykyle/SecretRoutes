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
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        ChatUtils.sendVerboseMessage("§d Block placed: " + e.placedBlock.getBlock().getLocalizedName());
        // Route Recording
        if(e.placedBlock.getBlock() == Blocks.tnt) {
            ChatUtils.sendVerboseMessage("§d TNT placed at: " + e.pos);
            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, e.pos);
            Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
            return;
        }
        ChatUtils.sendVerboseMessage("§d Held Item: " + Minecraft.getMinecraft().thePlayer.getHeldItem().getItem());
        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();
        if(heldItem != null) {
            ChatUtils.sendVerboseMessage("§d Held Item: " + heldItem.getItem());
            if(heldItem.getItem() == Item.getItemFromBlock(Blocks.tnt)) {
                ChatUtils.sendVerboseMessage("§d TNT held");
                Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, e.pos);
                Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
            }
        }
    }
}
