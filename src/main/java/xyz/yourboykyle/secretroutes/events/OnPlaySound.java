//#if FABRIC
package xyz.yourboykyle.secretroutes.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.dungeons.Room;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OnPlaySound {

    private static final ConcurrentMap<Identifier, Long> recentSounds = new ConcurrentHashMap<>();
    private static final long SOUND_COOLDOWN = 100;

    public static void handleSoundPlayed(SoundInstance sound) {
        try {
            if (sound == null || sound.getIdentifier() == null) {
                return;
            }

            Identifier soundId = sound.getIdentifier();

            // Check if we've already processed this sound recently
            long currentTime = System.currentTimeMillis();
            Long lastProcessed = recentSounds.get(soundId);
            if (lastProcessed != null && currentTime - lastProcessed < SOUND_COOLDOWN) {
                return;
            }
            recentSounds.put(soundId, currentTime);

            Minecraft client = Minecraft.getInstance();
            LocalPlayer player = client.player;

            if (player == null) {
                return;
            }

            // Check for ender dragon hit sound (etherwarp)
            if (soundId.toString().equals("minecraft:entity.ender_dragon.hurt") ||
                    soundId.toString().equals("entity.ender_dragon.hurt") ||
                    soundId.toString().contains("enderdragon.hit")) {

                // Route Recording
                if (Main.routeRecording.recording &&
                        player.isCrouching() &&
                        player.getMainHandItem().getItem() == Items.DIAMOND_SHOVEL) {

                    new Thread(() -> {
                        try {
                            player.sendSystemMessage(Component.literal("Detected etherwarp! Please wait 0.5 seconds before continuing the route..."));
                            Main.routeRecording.setRecordingMessage("Detected etherwarp! Please wait 0.5 seconds before continuing the route...");
                            Thread.sleep(500);

                            BlockPos playerPos = player.blockPosition();
                            BlockPos targetPos = playerPos.offset(-1, -1, -1);
                            Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ETHERWARPS, targetPos);
                            Main.routeRecording.setRecordingMessage("Etherwarp recorded! You may continue the route.");
                        } catch (InterruptedException ex) {
                            LogUtils.error(ex);
                            BlockPos playerPos = player.blockPosition();
                            BlockPos targetPos = playerPos.offset(-1, -1, -1);
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
                Level world = client.level;

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
//#endif
