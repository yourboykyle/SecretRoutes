package xyz.yourboykyle.secretroutes.customevents;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.customevents.SecretCompleted;

public class ItemPickedUp {
    public void onPickupItem(PlayerEvent.ItemPickupEvent e) {
        if(Main.currentRoom.getNext() == null || Main.currentRoom.getNext().getKey() == null || Main.currentRoom.getNext().getValue() == null) {
            return;
        }

        if(Main.currentRoom.getNext().getValue().equals("item")) {
            EntityPlayer p = e.player;

            BlockPos pos = e.player.getPosition();
            BlockPos itemPos = Main.currentRoom.getNext().getKey();
            if (pos.getX() >= itemPos.getX() - 3 && pos.getX() <= itemPos.getX() + 3 && pos.getY() >= itemPos.getY() - 3 && pos.getY() <= itemPos.getY() + 3 && pos.getZ() >= itemPos.getZ() - 3 && pos.getZ() <= itemPos.getZ() + 3) {
                if (Main.currentRoom.getRoute().size() < 2) {
                    Main.currentRoom.add(null, null);
                }
                Main.currentRoom.removeNext();
                SecretCompleted.onSecretCompleted();
                p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Picked up item!"));
            }
        }
    }
}
