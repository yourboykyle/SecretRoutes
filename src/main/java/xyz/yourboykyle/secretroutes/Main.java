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
import io.github.quantizr.dungeonrooms.handlers.PacketHandler;
import io.github.quantizr.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import xyz.yourboykyle.secretroutes.commands.ChangeRoute;
import xyz.yourboykyle.secretroutes.commands.LoadRoute;
import xyz.yourboykyle.secretroutes.commands.Recording;
import xyz.yourboykyle.secretroutes.commands.SRM;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.*;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RouteRecording;
import xyz.yourboykyle.secretroutes.utils.SSLUtils;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final String ROUTES_PATH = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public final static File logDir = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "logs" + File.separator + "SecretRoutes");
    public static File outputLogs;

    public static Room currentRoom = new Room(null);
    public static RouteRecording routeRecording = new RouteRecording();
    private static DungeonRooms dungeonRooms = new DungeonRooms();

    public static Main instance = new Main();
    public static SRMConfig config;

    public static String logFilePath = "";

    // Key Binds
    public static KeyBinding lastSecret = new KeyBinding("Last Secret", Keyboard.KEY_LBRACKET, "Secret Routes");
    public static KeyBinding nextSecret = new KeyBinding("Next Secret", Keyboard.KEY_RBRACKET, "Secret Routes");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        dungeonRooms.preInit(e);
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        // Set up logging system
        String date = sdf.format(System.currentTimeMillis());
        outputLogs = new File(logDir + File.separator + "LATEST-" + date + ".log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        } else {
            File[] files = logDir.listFiles((dir, name) -> name.startsWith("LATEST"));
            for(File file : files) {
                File[] logFiles = logDir.listFiles((dir, name) -> name.contains(date));
                int logsNo = logFiles == null ? 1 : logFiles.length;
                String newName = file.getName().replaceFirst("LATEST-", "").split("\\.")[0] + "-" + logsNo + ".log";
                File renamedFile = new File(logDir + File.separator + newName);
                file.renameTo(renamedFile);
            }

        }
        try {
            outputLogs.createNewFile();
        } catch (IOException e1) {
            System.out.println("Secret Routes Mod logging file creation failed :(");
            e1.printStackTrace();
        }

        // Set up Config
        config = new SRMConfig();

        // Initialize Other Stuff
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

        MinecraftForge.EVENT_BUS.register(this);

        // Register Commands
        ClientCommandHandler.instance.registerCommand(new LoadRoute());
        ClientCommandHandler.instance.registerCommand(new Recording());
        ClientCommandHandler.instance.registerCommand(new SRM());
        ClientCommandHandler.instance.registerCommand(new ChangeRoute());

        // Register Keybinds
        ClientRegistry.registerKeyBinding(lastSecret);
        ClientRegistry.registerKeyBinding(nextSecret);

        // Make sure room data isn't null
        RoomDetection.roomName = "undefined";
        RoomDetection.roomCorner = new Point(0, 0);
        RoomDetection.roomDirection = "NW";
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
                updateRoutes(configFile);
            }
        } catch(Exception e) {
            LogUtils.error(e);
        }
    }

    public static void updateRoutes(File configFile) {
        try {
            LogUtils.info("Downloading routes.json...");
            SSLUtils.disableSSLCertificateChecking();

            InputStream inputStream = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json").openStream();
            OutputStream outputStream = new FileOutputStream(configFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();

            SSLUtils.enableSSLCertificateChecking();
        }catch(Exception e){
            LogUtils.error(e);
        }
    }

    public static void updateRoutes() {
        File configFile = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes" + File.separator + "routes.json");
        try {
            LogUtils.info("Downloading routes.json...");
            SSLUtils.disableSSLCertificateChecking();

            InputStream inputStream = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json").openStream();
            OutputStream outputStream = new FileOutputStream(configFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();

            SSLUtils.enableSSLCertificateChecking();
        } catch (Exception e){
            LogUtils.error(e);
        }
    }

    @SubscribeEvent
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
    }
}