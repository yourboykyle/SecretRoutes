//#if FABRIC && MC == 1.21.10
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
 *
 * <DO NOT REMOVE THIS COPYRIGHT NOTICE>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
import org.polyfrost.oneconfig.api.event.v1.events.SoundPlayedEvent;
import org.polyfrost.oneconfig.api.event.v1.invoke.impl.Subscribe;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class OnPlaySound {

    private static final ConcurrentMap<Identifier, Long> recentSounds = new ConcurrentHashMap<>();
    private static final long SOUND_COOLDOWN = 100; // milliseconds


    @Subscribe
    public void handleSoundPlayed(SoundPlayedEvent event) {
        try {
            // Get the sound instance from the event
            SoundInstance sound = event.getSound();

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
                            BlockPos targetPos = playerPos.add(-1, -1, -1); // Block under the player, the -1 on X and Z have to be like that, trust the process
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
                // Route Recording - handle block break sound
                BlockPos pos = new BlockPos((int)sound.getX(), (int)sound.getY(), (int)sound.getZ());
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
//#endif
