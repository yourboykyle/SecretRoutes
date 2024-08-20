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

import cc.polyfrost.oneconfig.config.core.OneColor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.handlers.PacketHandler;
import io.github.quantizr.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
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
import xyz.yourboykyle.secretroutes.commands.*;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.*;
import xyz.yourboykyle.secretroutes.utils.*;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final String ROUTES_PATH = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes"+File.separator+"routes";
    public static final String COLOR_PROFILE_PATH = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes"+File.separator+"colorprofiles";
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
        checkProfilesData();

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
        ClientCommandHandler.instance.registerCommand(new ChangeColorProfile());

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
    public static void checkProfilesData(){
        try{
            String filePath = COLOR_PROFILE_PATH+File.separator+ "default.json";

            File colorProfileDir = new File(COLOR_PROFILE_PATH);
            if(!colorProfileDir.exists()){
                colorProfileDir.mkdirs();
            }
            if(FileUtils.getFileNames(COLOR_PROFILE_PATH).isEmpty()){
                writeColorConfig(filePath);
            }
            //Implement logic for writing to file
        }catch(Exception e){
            LogUtils.error(e);
        }
    }
    public static void writeColorConfig(String path){
            if(!path.endsWith(".json")){
                path += ".json";
            }
            Map<String, OneColor> defaultColors = new HashMap<>();
            defaultColors.put("lineColor", SRMConfig.lineColor);
            defaultColors.put("etherWarp", SRMConfig.etherWarp);
            defaultColors.put("mine", SRMConfig.mine);
            defaultColors.put("interacts", SRMConfig.interacts);
            defaultColors.put("superbooms", SRMConfig.superbooms);
            defaultColors.put("secretsItem", SRMConfig.secretsItem);
            defaultColors.put("secretsInteract", SRMConfig.secretsInteract);
            defaultColors.put("secretsBat", SRMConfig.secretsBat);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(defaultColors);

            try (FileWriter writer = new FileWriter(path)) {
                writer.write(json);
                sendChatMessage("Color profile created successfully.", EnumChatFormatting.DARK_GREEN);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    public static boolean loadColorConfig(String path) {
        try {
            if (!path.endsWith(".json")) {
                path += ".json";
            }
            String finalPath = COLOR_PROFILE_PATH + File.separator + path;
            if(!new File(finalPath).exists()){
                sendChatMessage("Color profile not found, please select different one or create it.", EnumChatFormatting.RED);
                return false;
            }
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(OneColor.class, new OneColorDeserializer())
                    .create();
            FileReader reader = new FileReader(finalPath);
            Type type = new TypeToken<Map<String, OneColor>>() {}.getType();
            Map<String, OneColor> data = gson.fromJson(reader, type);

            SRMConfig.lineColor = data.get("lineColor");
            SRMConfig.etherWarp = data.get("etherWarp");
            SRMConfig.mine = data.get("mine");
            SRMConfig.interacts = data.get("interacts");
            SRMConfig.superbooms = data.get("superbooms");
            SRMConfig.secretsItem = data.get("secretsItem");
            SRMConfig.secretsInteract = data.get("secretsInteract");
            SRMConfig.secretsBat = data.get("secretsBat");
            return true;
        } catch (Exception e) {
            LogUtils.error(e);
        }
        return false;
    }


    public static void checkRoutesData() {
        try {
            String filePath = ROUTES_PATH+File.separator+ "routes.json";

            // Check if the config directory exists
            File configDir = new File(ROUTES_PATH);
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
        File configFile = new File(ROUTES_PATH+File.separator + "routes.json");
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