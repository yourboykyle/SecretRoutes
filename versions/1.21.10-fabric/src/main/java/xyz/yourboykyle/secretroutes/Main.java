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

package xyz.yourboykyle.secretroutes;

import de.hysky.skyblocker.utils.scheduler.Scheduler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import org.polyfrost.oneconfig.api.event.v1.EventManager;
import org.polyfrost.oneconfig.api.hud.v1.HudManager;
import xyz.yourboykyle.secretroutes.commands.*;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.config.huds.CurrentRoomHUD;
import xyz.yourboykyle.secretroutes.config.huds.RecordingHUD;
import xyz.yourboykyle.secretroutes.events.*;
import xyz.yourboykyle.secretroutes.utils.*;
import xyz.yourboykyle.secretroutes.utils.autoupdate.UpdateManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class Main implements ClientModInitializer {
    public static final String MODID = "@MOD_ID@";
    public static final String NAME = "@MOD_NAME@";
    public static final String VERSION = "@MOD_VERSION@";
    public static final String CONFIG_FOLDER_PATH = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes";
    public static final String ROUTES_PATH = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes"+File.separator+"routes";
    public static final String COLOR_PROFILE_PATH = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes"+File.separator+"colorprofiles";
    public static final String tmpDir = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "SecretRoutes" + File.separator + "tmp";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public final static File logDir = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "logs" + File.separator + "SecretRoutes");
    public static File outputLogs;

    public static Room currentRoom = new Room(null);
    public static RouteRecording routeRecording = null;
    public static UpdateManager updateManager = new UpdateManager();

    public static Main instance;

    // HUD instances
    public static RecordingHUD recordingHUD;
    public static CurrentRoomHUD currentRoomHUD;

    @Override
    public void onInitializeClient() {
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

        // Config
        SRMConfig.INSTANCE.preload();
        recordingHUD = new RecordingHUD();
        currentRoomHUD = new CurrentRoomHUD();
        HudManager.register(recordingHUD);
        HudManager.register(currentRoomHUD);

        // Initialize stuff
        instance = this;
        routeRecording = new RouteRecording();
        checkRoutesData();
        checkProfilesData();
        checkPBData();
        PBUtils.loadPBData();

        // OneConfig Events
        EventManager.INSTANCE.register(new OnPlaySound());
        EventManager.INSTANCE.register(new OnReceivePacket());
        EventManager.INSTANCE.register(new OnSendPacket());

        // Fabric Events
        GuildEvents.register();
        OnBlockBreak.register();
        OnBlockPlace.register();
        OnChatReceive.register();
        OnGuiRender.register();
        OnItemPickedUp.register();
        OnMouseInput.register();
        OnPlayerInteract.register();
        OnPlayerTick.register();
        OnServerTick.register();

        // Skyblocker Events
        //OnEnterNewRoom.register();
        OnSkyblockerRender.register();

        // Server connection events (for this)
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            onServerConnect();
        });

        // Commands
        ChangeColorProfile.register();
        ChangeRoute.register();
        Debug.register();
        LoadRoute.register();
        Recording.register();
        SRM.register();

        // Scheduled tasks
        // Create a new scheduled cyclic task every 20 game ticks that called OnEnterNewRoom.checkForNewRoom
        Scheduler.INSTANCE.scheduleCyclic(OnEnterNewRoom::checkForNewRoom, 20);

        if(SRMConfig.autoUpdateRoutes){
            LogUtils.info("Checking for route updates...");
            new Thread(()->{
                try{
                    RouteUtils.checkRoutesFiles();
                }catch (Exception e1){
                    LogUtils.error(e1);
                }
            }).start();
        }
    }

    public static void checkRoomData() {
        // Old method, kept for compatibility
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
                RouteUtils.updateRoutes(configFile);
            }
            if(!configFilePearl.exists()){
                RouteUtils.updatePearlRoutes();
            }
        } catch(Exception e) {
            LogUtils.error(e);
        }
    }

    public static void checkPBData() {
        try {
            String filePath = CONFIG_FOLDER_PATH + File.separator + "personal_bests.json";

            // Check if the config directory exists
            File configDir = new File(CONFIG_FOLDER_PATH);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File configFile = new File(filePath);
            if (!configFile.exists()) {
                configFile.createNewFile();
                FileWriter pbWriter = new FileWriter(configFile);
                pbWriter.write("{}");
                pbWriter.close();
            }
        } catch(Exception e) {
            LogUtils.error(e);
        }
    }

    private static void onServerConnect() {
        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                // nothing needed, literally just waiting
            }

            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.getCurrentServerEntry() == null) return;
            String serverName = mc.getCurrentServerEntry().address.toLowerCase();
            if (serverName.contains("hypixel.") || serverName.contains("fakepixel.") || SRMConfig.disableServerChecking) {

                if (SRMConfig.autoCheckUpdates) {
                    new Thread(() -> {
                        try {
                            Main.updateManager.checkUpdate(false);
                        } catch (Exception e) {
                            LogUtils.error(e);
                        }
                    }).start();
                }

                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                    } catch (Exception ignored) {
                    }
                    byte res = APIUtils.addMember();
                    if (res == 1) {
                        sendChatMessage("§aFirst logon detected... things work");
                    } else if (res == 0) {
                        sendChatMessage("§aWelcome back!");
                    }
                }).start();

                if (!APIUtils.apiQueued) {
                    Runtime.getRuntime().addShutdownHook(new Thread(APIUtils::offline));
                    APIUtils.apiQueued = true;
                }

                LogUtils.info("RouteRecording json status: " + RouteRecording.malformed);
                if (RouteRecording.malformed) {
                    sendChatMessage("[ERROR] The JSON file in downloads is malformed. Check the file or delete it.", Formatting.RED);
                }
            }
        }).start();
    }

    public static void toggleSecretsKeybind(){
        if(SRMConfig.modEnabled) {
            SRMConfig.modEnabled = false;
            sendChatMessage(Formatting.RED + "Secret Routes Mod secret rendering has been disabled.");
        }else{
            SRMConfig.modEnabled = true;
            sendChatMessage(Formatting.GREEN + "Secret Routes Mod secret rendering has been enabled.");
        }
    }
}
//#endif