package xyz.yourboykyle.secretroutes.customevents;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.customevents.SecretCompleted;

public class BlockBreak {
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        if(Main.currentRoom.getNext() == null || Main.currentRoom.getNext().getKey() == null || Main.currentRoom.getNext().getValue() == null) {
            return;
        }

        if(Main.currentRoom.getNext().getValue().equals("stonk")) {
            EntityPlayer p = e.getPlayer();

            if ((p.getHeldItem().hasDisplayName() && p.getHeldItem().getDisplayName().contains("Stonk")) || EnchantmentHelper.getEnchantmentLevel(32, p.getHeldItem()) >= 10) {
                BlockPos stonkPos = Main.currentRoom.getNext().getKey();

                if(e.pos.getX() == stonkPos.getX() && e.pos.getY() == stonkPos.getY() && e.pos.getZ() == stonkPos.getZ()) {
                    if (Main.currentRoom.getRoute().size() < 2) {
                        Main.currentRoom.add(null, null);
                    }
                    Main.currentRoom.removeNext();
                    SecretCompleted.onSecretCompleted();
                    p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Stonked!"));
                }
            }
        }
    }
}