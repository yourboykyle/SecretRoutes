package xyz.yourboykyle.secretroutes.events;

import io.github.quantizr.dungeonrooms.events.PacketEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnRecievePacket {
    // The S23PacketBlockChange packet is sent twice, even when checking if the block is air. This is a workaround to keep track of if it's the first time the packet is sent, and just ignore the second time
    public static boolean firstPacket = true;

    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent e) {
        try {
            if (e.packet instanceof S0DPacketCollectItem) { // Note to Hypixel: This is not manipulating packets, it is simply listening and checking for the collect item packet. If that is the correct packet, it simulates creating an itempickedup event client-side
                S0DPacketCollectItem packet = (S0DPacketCollectItem) e.packet;
                Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.getCollectedItemEntityID());

                if(entity instanceof EntityItem) {
                    EntityItem item = (EntityItem) entity;
                    entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.getEntityID());
                    if(entity == null) {
                        System.out.println("Entity is null.");
                        return;
                    }
                    if(!entity.getCommandSenderEntity().getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
                        // Someone else has picked up the item
                        return;
                    }

                    PlayerEvent.ItemPickupEvent itemPickupEvent = new PlayerEvent.ItemPickupEvent(Minecraft.getMinecraft().thePlayer, item);
                    new ItemPickedUp().onPickupItem(itemPickupEvent);
                }
            } else if(e.packet instanceof S08PacketPlayerPosLook) {
                // Route Recording
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.packet;

                if(Main.routeRecording.recording && Minecraft.getMinecraft().thePlayer.isSneaking()) {
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ETHERWARPS, Minecraft.getMinecraft().thePlayer.getPosition());
                }
            } else if(e.packet instanceof S23PacketBlockChange) {
                // Route Recording
                S23PacketBlockChange packet = (S23PacketBlockChange) e.packet;

                BlockPos pos = packet.getBlockPosition();
                World world = Minecraft.getMinecraft().theWorld;
                IBlockState blockState = world.getBlockState(pos);
                Block block = blockState.getBlock();

                if(block == Blocks.air) {
                    // Block was broken
                    if(Main.routeRecording.recording && firstPacket) {
                        new OnBlockBreak().onBlockBreak(new BlockEvent.BreakEvent(world, pos, blockState, Minecraft.getMinecraft().thePlayer));
                    }
                } else if(block == null) {
                    // Block is null.
                } else {
                    // Block was placed
                    if(Main.routeRecording.recording) {
                        IBlockState placedAgainst = world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
                        new OnBlockPlace().onBlockPlace(new BlockEvent.PlaceEvent(new BlockSnapshot(world, pos, blockState), placedAgainst, Minecraft.getMinecraft().thePlayer));
                    }
                }


                firstPacket = !firstPacket;

            }
        } catch (Exception error) {
            error.printStackTrace();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("There was an error with the " + Main.MODID + " mod."));
        }
    }
}