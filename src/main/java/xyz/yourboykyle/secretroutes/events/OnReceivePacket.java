//#if FABRIC
package xyz.yourboykyle.secretroutes.events;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.dungeons.Room;

public class OnReceivePacket {
    public static boolean firstBlockBreakPacket = true;
    public static boolean firstBlockPlacePacket = true;

    public static void onItemPickup(ClientboundTakeItemEntityPacket packet) {
        try {
            Minecraft client = Minecraft.getInstance();
            if (client.level == null || client.player == null) return;

            Entity entity = client.level.getEntity(packet.getItemId());

            if (entity instanceof ItemEntity item) {
                Entity collector = client.level.getEntity(packet.getPlayerId());

                if (collector == null) return;

                if (collector.getUUID().equals(client.player.getUUID())) {
                    OnItemPickedUp.handleItemPickup(client.player, item);
                }
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public static void onBlockUpdate(ClientboundBlockUpdatePacket packet) {
        try {
            Minecraft client = Minecraft.getInstance();
            if (client.level == null || client.player == null) return;

            BlockPos pos = packet.getPos();
            Level world = client.level;
            BlockState blockState = packet.getBlockState();
            Block block = blockState.getBlock();

            if (block == Blocks.AIR) {
                // Block was broken
                if (Main.routeRecording.recording && firstBlockBreakPacket) {
                    OnBlockBreak.handleBlockBreak(world, pos, blockState, client.player);
                }
            } else {
                // Block was placed
                if (Main.routeRecording.recording && firstBlockPlacePacket) {
                    handleBlockPlace(pos, blockState);
                }
            }

            firstBlockBreakPacket = !firstBlockBreakPacket;
            firstBlockPlacePacket = !firstBlockPlacePacket;
        } catch (Exception error) {
            LogUtils.error(error);
            ChatUtils.sendChatMessage("There was an error with the " + Main.MODID + " mod.");
        }
    }

    public static void handleBlockPlace(BlockPos pos, BlockState blockState) {
        if (blockState.getBlock() == Blocks.TNT && Main.routeRecording.recording) {
            String blockName = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
            ChatUtils.sendVerboseMessage("§d Block placed: " + blockName, "Recording");
            ChatUtils.sendVerboseMessage("§d TNT placed at: " + pos, "Recording");

            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, pos);
            Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
        }
    }
}
//#endif