package xyz.yourboykyle.secretroutes.events;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnReceivePacket {
    public static boolean firstBlockBreakPacket = true;
    public static boolean firstBlockPlacePacket = true;

    public static void onItemPickup(ItemPickupAnimationS2CPacket packet) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;

            Entity entity = client.world.getEntityById(packet.getEntityId());

            if (entity instanceof ItemEntity) {
                ItemEntity item = (ItemEntity) entity;
                Entity collector = client.world.getEntityById(packet.getCollectorEntityId());

                if (collector == null) return;

                if (collector.getUuid().equals(client.player.getUuid())) {
                    OnItemPickedUp.handleItemPickup(client.player, item);
                }
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public static void onBlockUpdate(BlockUpdateS2CPacket packet) {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world == null || client.player == null) return;

            BlockPos pos = packet.getPos();
            World world = client.world;
            BlockState blockState = packet.getState();
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
            String blockName = Registries.BLOCK.getId(blockState.getBlock()).toString();
            ChatUtils.sendVerboseMessage("§d Block placed: " + blockName, "Recording");
            ChatUtils.sendVerboseMessage("§d TNT placed at: " + pos, "Recording");

            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, pos);
            Main.routeRecording.setRecordingMessage("Added TNT waypoint.");
        }
    }
}