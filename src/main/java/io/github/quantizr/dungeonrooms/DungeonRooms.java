/*
 * Dungeon Rooms Mod - Secret Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2021 Quantizr(_risk)
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

package io.github.quantizr.dungeonrooms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.DungeonManager;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.Waypoints;
import io.github.quantizr.dungeonrooms.handlers.ConfigHandler;
import io.github.quantizr.dungeonrooms.handlers.TextRenderer;
import io.github.quantizr.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DungeonRooms {
    public static final String VERSION = "1.0";
    Minecraft mc = Minecraft.getMinecraft();

    public static JsonObject roomsJson;
    public static JsonObject waypointsJson;
    public static HashMap<String,HashMap<String,long[]>> ROOM_DATA = new HashMap<>();

    public static boolean usingSBPSecrets = false;
    public static KeyBinding[] keyBindings = new KeyBinding[3];
    public static String imageHotkeyOpen = "gui";
    static int tickAmount = 1;

    public static List<String> textToDisplay = null;
    public static int textLocX = 50;
    public static int textLocY = 5;

    public static List<String> motd = null;
    public static String configDir;
    public static boolean firstLogin = false;

    @EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory().toString();
        Utils.setLogLevel(LogManager.getLogger(DungeonRooms.class), Level.INFO);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        long time1 = System.currentTimeMillis();

        //start room data loading executors first or else it will block later and slow down loading by ~200ms
        List<Path> paths = Utils.getAllPaths("catacombs");
        final ExecutorService executor = Executors.newFixedThreadPool(4); //don't need 8 threads cause it's just 1x1 that takes longest
        Future<HashMap<String, long[]>> future1x1 = executor.submit(() -> Utils.pathsToRoomData("1x1", paths));
        Future<HashMap<String, long[]>> future1x2 = executor.submit(() -> Utils.pathsToRoomData("1x2", paths));
        Future<HashMap<String, long[]>> future1x3 = executor.submit(() -> Utils.pathsToRoomData("1x3", paths));
        Future<HashMap<String, long[]>> future1x4 = executor.submit(() -> Utils.pathsToRoomData("1x4", paths));
        Future<HashMap<String, long[]>> future2x2 = executor.submit(() -> Utils.pathsToRoomData("2x2", paths));
        Future<HashMap<String, long[]>> futureLShape = executor.submit(() -> Utils.pathsToRoomData("L-shape", paths));
        Future<HashMap<String, long[]>> futurePuzzle = executor.submit(() -> Utils.pathsToRoomData("Puzzle", paths));
        Future<HashMap<String, long[]>> futureTrap = executor.submit(() -> Utils.pathsToRoomData("Trap", paths));

        //register classes
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new DungeonManager());
        MinecraftForge.EVENT_BUS.register(new RoomDetection());
        MinecraftForge.EVENT_BUS.register(new Waypoints());

        //reload config
        ConfigHandler.reloadConfig();

        //get room and waypoint info
        try (BufferedReader roomsReader = new BufferedReader(new InputStreamReader(mc.getResourceManager()
                .getResource(new ResourceLocation("roomdetection", "dungeonrooms.json")).getInputStream()));
            BufferedReader waypointsReader = new BufferedReader(new InputStreamReader(mc.getResourceManager()
                .getResource(new ResourceLocation("roomdetection", "secretlocations.json")).getInputStream()))
        ) {
            Gson gson = new Gson();
            roomsJson = gson.fromJson(roomsReader, JsonObject.class);

            waypointsJson = gson.fromJson(waypointsReader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //set RoomData to futures - this will block if the rest of init was fast
        try {
            long time2 = System.currentTimeMillis();
            ROOM_DATA.put("1x1", future1x1.get());
            long time3 = System.currentTimeMillis();
            ROOM_DATA.put("1x2", future1x2.get());
            ROOM_DATA.put("1x3", future1x3.get());
            ROOM_DATA.put("1x4", future1x4.get());
            ROOM_DATA.put("2x2", future2x2.get());
            ROOM_DATA.put("L-shape", futureLShape.get());
            ROOM_DATA.put("Puzzle", futurePuzzle.get());
            ROOM_DATA.put("Trap", futureTrap.get());
            long time4 = System.currentTimeMillis();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }

    @EventHandler
    public void postInit(final FMLPostInitializationEvent event) {
        usingSBPSecrets = false;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        EntityPlayerSP player = mc.thePlayer;

        tickAmount++;
        if (tickAmount % 20 == 0) {
            if (player != null) {
                Utils.checkForSkyblock();
                Utils.checkForCatacombs();
                tickAmount = 0;
            }
        }
    }

    @SubscribeEvent
    public void renderPlayerInfo(final RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) return;
        if (Utils.inSkyblock) {
            if (textToDisplay != null && !textToDisplay.isEmpty()) {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                int y = 0;
                for (String line:textToDisplay) {
                    int roomStringWidth = mc.fontRendererObj.getStringWidth(line);
                    TextRenderer.drawText(mc, line, ((scaledResolution.getScaledWidth() * textLocX) / 100) - (roomStringWidth / 2),
                            ((scaledResolution.getScaledHeight() * textLocY) / 100) + y, 1D, true);
                    y += mc.fontRendererObj.FONT_HEIGHT;
                }
            }
        }
    }

    /*@SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getCurrentServerData() == null) return;
        if (mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.")) {
            //Packets are used in this mod solely to detect when the player picks up an item. No packets are modified or created.
            event.manager.channel().pipeline().addBefore("packet_handler", "secretroutes_packet_handler", new PacketHandler());

            new Thread(() -> {
                try {
                    while (mc.thePlayer == null) {
                        //Yes, I'm too lazy to code something proper so I'm busy-waiting, shut up. no :) -carmel
                        //It usually waits for less than half a second
                        Thread.sleep(100);
                    }
                    Thread.sleep(3000);
                    if (mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.")) {
                        Utils.checkForConflictingHotkeys();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }*/
}
