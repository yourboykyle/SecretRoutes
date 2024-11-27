/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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


package xyz.yourboykyle.secretroutes;

import xyz.yourboykyle.secretroutes.deps.dungeonrooms.DungeonRooms;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.handlers.PacketHandler;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.apache.commons.io.IOUtils;
import xyz.yourboykyle.secretroutes.commands.*;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.*;
import xyz.yourboykyle.secretroutes.events.OnMouseInput;
import xyz.yourboykyle.secretroutes.utils.*;
import xyz.yourboykyle.secretroutes.utils.autoupdate.UpdateManager;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "@ID@";
    public static final String NAME = "@NAME@";
    public static final String VERSION = "@VER@";
    public static final String ROUTES_PATH = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes"+File.separator+"routes";
    public static final String COLOR_PROFILE_PATH = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes"+File.separator+"colorprofiles";
    public static final String tmpDir = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "SecretRoutes" + File.separator + "tmp";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public final static File logDir = new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + File.separator + "logs" + File.separator + "SecretRoutes");
    public static File outputLogs;

    public static Room currentRoom = new Room(null);
    public static RouteRecording routeRecording = null;
    public static UpdateManager updateManager = new UpdateManager();
    private static DungeonRooms dungeonRooms = new DungeonRooms();

    public static Main instance = new Main();
    public static SRMConfig config;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        dungeonRooms.preInit(e);
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        String jarpath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
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
        LogUtils.info("Jarpath: "+jarpath);
        if(!new File(tmpDir).exists()){
            new File(tmpDir).mkdirs();
        }

        File batchFile = new File(tmpDir + File.separator + "update.bat");
        if(batchFile.exists()){
            batchFile.delete();
        }
        File shellFile = new File(tmpDir + File.separator + "update.sh");
        if(shellFile.exists()){
            shellFile.delete();
        }
        LogUtils.info("§bSetting ssl certificate");
        SSLUtils.setSSlCertificate();


        routeRecording = new RouteRecording();


        // Set up Config
        config = new SRMConfig();

        // Auto Updates
        LogUtils.info("Checking for updates...");

        // Initialize Other Stuff
        instance = this;
        dungeonRooms.init(e);
        checkRoutesData();
        checkProfilesData();

        // Register Events
        MinecraftForge.EVENT_BUS.register(new OnBlockPlace());
        MinecraftForge.EVENT_BUS.register(new OnItemPickedUp());
        MinecraftForge.EVENT_BUS.register(new OnPlayerInteract());
        MinecraftForge.EVENT_BUS.register(new OnPlayerTick());
        MinecraftForge.EVENT_BUS.register(new OnPlaySound());
        MinecraftForge.EVENT_BUS.register(new OnRecievePacket());
        MinecraftForge.EVENT_BUS.register(new OnWorldRender());
        MinecraftForge.EVENT_BUS.register(new OnMouseInput());
        MinecraftForge.EVENT_BUS.register(new OnChatReceive());
        MinecraftForge.EVENT_BUS.register(new OnGuiRender());
        //MinecraftForge.EVENT_BUS.register(new OnServerTick());
        MinecraftForge.EVENT_BUS.register(new GuildEvents());

        MinecraftForge.EVENT_BUS.register(this);

        // Register Commands
        ClientCommandHandler.instance.registerCommand(new LoadRoute());
        ClientCommandHandler.instance.registerCommand(new Recording());
        ClientCommandHandler.instance.registerCommand(new SRM());
        ClientCommandHandler.instance.registerCommand(new ChangeRoute());
        ClientCommandHandler.instance.registerCommand(new ChangeColorProfile());
        ClientCommandHandler.instance.registerCommand(new Debug());



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
            String filePath = "default.json";

            File colorProfileDir = new File(COLOR_PROFILE_PATH);
            if(!colorProfileDir.exists()){
                colorProfileDir.mkdirs();
            }
            if(FileUtils.getFileNames(COLOR_PROFILE_PATH).isEmpty()){
                ConfigUtils.writeColorConfig(filePath);
            }
            //Implement logic for writing to file
        }catch(Exception e){
            LogUtils.error(e);
        }
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
            File configFilePearl = new File(ROUTES_PATH+File.separator+ "pearlroutes.json");
            if (!configFile.exists()) {
                updateRoutes(configFile);
            }
            if(!configFilePearl.exists()){
                updatePearlRoutes();
            }
        } catch(Exception e) {
            LogUtils.error(e);
        }
    }

    public static void updateRoutes(File configFile) {
        try {
            LogUtils.info("Downloading routes.json...");
            URL url = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json");
            downloadFile(configFile, url);

        }catch(Exception e){
            LogUtils.error(e);
        }
    }

    public static void updateRoutes() {
        File configFile = new File(ROUTES_PATH + File.separator + "routes.json");
        try {
            LogUtils.info("Downloading routes.json...");
            URL url = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json");
            downloadFile(configFile, url);

        } catch (Exception e){
            LogUtils.error(e);
        }
    }
    public static void updatePearlRoutes() {
        File configFile = new File(ROUTES_PATH + File.separator + "pearlroutes.json");
        try {
            LogUtils.info("Downloading routes.json...");
            URL url = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/pearlroutes.json");
            downloadFile(configFile, url);
        } catch (Exception e){
            LogUtils.error(e);
        }
    }

    private static void downloadFile(File outputFile, URL url) throws IOException {
        HttpsURLConnection connection =  (HttpsURLConnection) url.openConnection();

        connection.setSSLSocketFactory(SSLUtils.getSSLSocketFactory());

        InputStream inputStream = connection.getInputStream();
        OutputStream outputStream = Files.newOutputStream(outputFile.toPath());
        IOUtils.copy(inputStream, outputStream);
        outputStream.close();
        inputStream.close();
    }

    @SubscribeEvent
    public void onServerConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        try{
            Thread.sleep(3000);
        }catch(Exception e){
            //nothign needed, literally just waiting
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.getCurrentServerData() == null) return;
        if (mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.")) {

            if(SRMConfig.autoCheckUpdates) {
                new Thread(() -> {
                    try {
                        Main.updateManager.checkUpdate();
                    } catch (Exception e) {
                        LogUtils.error(e);
                    }
                }).start();
            }

            new Thread(()->{
                try{
                    Thread.sleep(3000);
                }catch (Exception ignored){
                }
                byte res = APIUtils.addMember();
                if(res == 1){
                    sendChatMessage("§aFirst logon detected... things work");
                }
            }).start();



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
            LogUtils.info("RouteRecording json status: " + RouteRecording.malformed);
            if(RouteRecording.malformed){
                sendChatMessage("[ERROR] The JSON file in downloads is malformed. Check the file or delete it.", EnumChatFormatting.RED);
            }
        }
    }

    public static void toggleSecretsKeybind(){
        if(SRMConfig.modEnabled) {
            SRMConfig.modEnabled = false;
            sendChatMessage(EnumChatFormatting.RED + "Secret Routes Mod secret rendering has been disabled.");
        }else{
            SRMConfig.modEnabled = true;
            sendChatMessage(EnumChatFormatting.GREEN + "Secret Routes Mod secret rendering has been enabled.");
        }
    }


}