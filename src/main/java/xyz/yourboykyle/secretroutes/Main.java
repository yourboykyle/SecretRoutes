/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
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

package xyz.yourboykyle.secretroutes;

import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import xyz.yourboykyle.secretroutes.commands.LoadRoute;
import xyz.yourboykyle.secretroutes.commands.Recording;
import xyz.yourboykyle.secretroutes.commands.SRM;
import xyz.yourboykyle.secretroutes.events.*;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RouteRecording;
import xyz.yourboykyle.secretroutes.utils.SSLUtils;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
    public static final String MODID = "SecretRoutes";
    public static final String VERSION = "1.0";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    public static Room currentRoom = new Room(null);
    public static RouteRecording routeRecording = new RouteRecording();
    private static DungeonRooms dungeonRooms = new DungeonRooms();

    public static Main instance = new Main();

    // Key Binds
    public static KeyBinding lastSecret = new KeyBinding("Last Secret", Keyboard.KEY_LBRACKET, "Secret Routes");
    public static KeyBinding nextSecret = new KeyBinding("Next Secret", Keyboard.KEY_RBRACKET, "Secret Routes");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        dungeonRooms.preInit(e);
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        instance = this;
        dungeonRooms.init(e);
        checkRoutesData();

        // Register Events
        MinecraftForge.EVENT_BUS.register(new OnBlockPlace());
        MinecraftForge.EVENT_BUS.register(new OnItemPickedUp());
        MinecraftForge.EVENT_BUS.register(new OnKeyInput());
        MinecraftForge.EVENT_BUS.register(new OnPlayerInteract());
        MinecraftForge.EVENT_BUS.register(new OnPlayerTick());
        MinecraftForge.EVENT_BUS.register(new OnPlaySound());
        MinecraftForge.EVENT_BUS.register(new OnRecievePacket());
        MinecraftForge.EVENT_BUS.register(new OnWorldRender());

        // Register Commands
        ClientCommandHandler.instance.registerCommand(new LoadRoute());
        ClientCommandHandler.instance.registerCommand(new Recording());
        ClientCommandHandler.instance.registerCommand(new SRM());

        // Register Keybinds
        ClientRegistry.registerKeyBinding(lastSecret);
        ClientRegistry.registerKeyBinding(nextSecret);

        // Make sure room data isn't null
        RoomDetection.roomName = "undefined";
        RoomDetection.roomCorner = new Point(0, 0);
        RoomDetection.roomDirection = "NW";
        //Check if file is LATEST_*
        //If yes, rename to remove LATEST_

        String time = sdf.format(System.currentTimeMillis());
        File logDir = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath()+File.separator+"logs" + File.separator + "SecretRoutes");
        if(!logDir.exists()) {
            logDir.mkdirs();
        }
        File outputLogs = new File(logDir+File.separator+"LATEST_"+time+"_secretroutes.log");
        try {
            System.out.println(outputLogs);
            outputLogs.createNewFile();
        }catch(IOException e1) {
            e1.printStackTrace();
            throw new RuntimeException(e1);
        }
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        dungeonRooms.postInit(e);
    }

    public static void checkRoomData() {
        if(RoomDetection.roomName == null) {
            RoomDetection.roomName = "undefined";
        }
        if(RoomDetection.roomCorner == null) {
            RoomDetection.roomCorner = new Point(0, 0);
        }
        if(RoomDetection.roomDirection == null) {
            RoomDetection.roomDirection = "NW";
        }
    }

    public static void checkRoutesData() {
        try {
            String filePath = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes" + File.separator + "routes.json";

            // Check if the config directory exists
            File configDir = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes");
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File configFile = new File(filePath);
            if (!configFile.exists()) {
                // Download the default routes file from the GitHub repository
                System.out.println("Downloading routes.json...");
                SSLUtils.disableSSLCertificateChecking();

                InputStream inputStream = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json").openStream();
                OutputStream outputStream = new FileOutputStream(configFile);
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
                inputStream.close();

                SSLUtils.enableSSLCertificateChecking();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}