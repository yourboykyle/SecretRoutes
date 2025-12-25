//#if FORGE == 1.8.9
// TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
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

package xyz.yourboykyle.secretroutes.deps.dungeonrooms;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.DungeonManager;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.Waypoints;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class DungeonRooms
{

    Minecraft mc = Minecraft.getMinecraft();

    public static JsonObject roomsJson;
    public static JsonObject waypointsJson;
    public static HashMap<String,HashMap<String,long[]>> ROOM_DATA = new HashMap<>();

    static int tickAmount = 1;



    public void preInit(final FMLPreInitializationEvent event) {

        //initialize logger
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


    /**
     * Modified from Danker's Skyblock Mod under the GNU General Public License v3.0
     * https://github.com/bowser0000/SkyblockMod/blob/master/LICENSE
     * @author bowser0000
     */
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
}
//#endif
