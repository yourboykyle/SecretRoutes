package xyz.yourboykyle.secretroutes.events;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OnPlaySound {

    private static final ConcurrentMap<Identifier, Long> recentSounds = new ConcurrentHashMap<>();
    private static final long SOUND_COOLDOWN = 100;

    public static void handleSoundPlayed(SoundInstance sound) {
        try {
            if (sound == null || sound.getId() == null) {
                return;
            }

            Identifier soundId = sound.getId();

            // Check if we've already processed this sound recently
            long currentTime = System.currentTimeMillis();
            Long lastProcessed = recentSounds.get(soundId);
            if (lastProcessed != null && currentTime - lastProcessed < SOUND_COOLDOWN) {
                return;
            }
            recentSounds.put(soundId, currentTime);

            MinecraftClient client = MinecraftClient.getInstance();
            ClientPlayerEntity player = client.player;

            if (player == null) {
                return;
            }

            // Check for ender dragon hit sound (etherwarp)
            if (soundId.toString().equals("minecraft:entity.ender_dragon.hurt") ||
                    soundId.toString().equals("entity.ender_dragon.hurt") ||
                    soundId.toString().contains("enderdragon.hit")) {

                // Route Recording
                if (Main.routeRecording.recording &&
                        player.isSneaking() &&
                        player.getMainHandStack().getItem() == Items.DIAMOND_SHOVEL) {

                    new Thread(() -> {
                        try {
                            player.sendMessage(Text.literal("Detected etherwarp! Please wait 0.5 seconds before continuing the route..."), false);
                            Main.routeRecording.setRecordingMessage("Detected etherwarp! Please wait 0.5 seconds before continuing the route...");
                            Thread.sleep(500);

                            BlockPos playerPos = player.getBlockPos();
                            BlockPos targetPos = playerPos.add(-1, -1, -1);
                            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ETHERWARPS, targetPos);
                            Main.routeRecording.setRecordingMessage("Etherwarp recorded! You may continue the route.");
                        } catch (InterruptedException ex) {
                            LogUtils.error(ex);
                            BlockPos playerPos = player.getBlockPos();
                            BlockPos targetPos = playerPos.add(-1, -1, -1);
                            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ETHERWARPS, targetPos);
                            Main.routeRecording.setRecordingMessage("Etherwarp recorded! You may continue the route.");
                        }
                    }).start();
                }
            }

            // Check for block break sounds
            String soundPath = soundId.getPath();
            if (soundPath.contains("block") && soundPath.contains("break")) {
                BlockPos pos = new BlockPos((int) sound.getX(), (int) sound.getY(), (int) sound.getZ());
                World world = client.world;

                if (world != null) {
                    BlockState blockState = world.getBlockState(pos);
                    OnBlockBreak.handleBlockBreak(world, pos, blockState, player);
                }
            }
        } catch (Exception ex) {
            LogUtils.error(ex);
        }
    }
}