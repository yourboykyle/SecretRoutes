package xyz.yourboykyle.secretroutes.customevents;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.customevents.SecretCompleted;

public class BlockPlace {
    public void onBlockPlace(BlockEvent.PlaceEvent e) {
        if(Main.currentRoom.getNext() == null || Main.currentRoom.getNext().getKey() == null || Main.currentRoom.getNext().getValue() == null) {
            return;
        }

        if(Main.currentRoom.getNext().getValue().equals("superboom")) {
            if(e.placedBlock.getBlock() != Blocks.tnt) {
                return;
            }
            BlockPos superboom = Main.currentRoom.getNext().getKey();
            if (e.pos.getX() == superboom.getX() && e.pos.getY() == superboom.getY() && e.pos.getZ() == superboom.getZ() && e.player.getHeldItem().hasDisplayName() && e.player.getHeldItem().getDisplayName().contains("TNT")) {
                if (Main.currentRoom.getRoute().size() < 2) {
                    Main.currentRoom.add(null, null);
                }
                Main.currentRoom.removeNext();
                SecretCompleted.onSecretCompleted();
                e.player.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Superboomed!"));
            }
        }
    }
}
