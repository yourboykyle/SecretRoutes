package xyz.yourboykyle.secretroutes;
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

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import xyz.yourboykyle.secretroutes.commands.*;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.config.huds.CurrentRoomHUD;
import xyz.yourboykyle.secretroutes.config.huds.RecordingHUD;
import xyz.yourboykyle.secretroutes.events.*;
import xyz.yourboykyle.secretroutes.utils.*;
import xyz.yourboykyle.secretroutes.utils.autoupdate.UpdateManager;
import xyz.yourboykyle.secretroutes.utils.skyblocker.DungeonScanner;

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
    public static final String ROUTES_PATH = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes" + File.separator + "routes";
    public static final String COLOR_PROFILE_PATH = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes" + File.separator + "colorprofiles";
    public static final String tmpDir = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "SecretRoutes" + File.separator + "tmp";
    public final static File logDir = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "logs" + File.separator + "SecretRoutes");
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static File outputLogs;

    public static Room currentRoom = new Room(null);
    public static RouteRecording routeRecording = null;
    public static UpdateManager updateManager = new UpdateManager();

    public static Main instance;

    // HUD instances
    public static RecordingHUD recordingHUD;
    public static CurrentRoomHUD currentRoomHUD;

    // Keybinds
    public static KeyBinding nextSecretKey;
    public static KeyBinding lastSecretKey;
    public static KeyBinding toggleSecretsKey;

    public static void checkRoomData() {
        // Old method, kept for compatibility
    }

    public static void checkProfilesData() {
        try {
            String filePath = "default.json";

            File colorProfileDir = new File(COLOR_PROFILE_PATH);
            if (!colorProfileDir.exists()) {
                colorProfileDir.mkdirs();
            }
            if (FileUtils.getFileNames(COLOR_PROFILE_PATH).isEmpty()) {
                ConfigUtils.writeColorConfig(filePath);
            }
            //Implement logic for writing to file
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public static void checkRoutesData() {
        try {
            String filePath = ROUTES_PATH + File.separator + "routes.json";

            // Check if the xyz.yourboykyle.secretroutes.config directory exists
            File configDir = new File(ROUTES_PATH);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            File configFile = new File(filePath);
            File configFilePearl = new File(ROUTES_PATH + File.separator + "pearlroutes.json");
            if (!configFile.exists()) {
                RouteUtils.updateRoutes(configFile);
            }
            if (!configFilePearl.exists()) {
                RouteUtils.updatePearlRoutes();
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public static void checkPBData() {
        try {
            String filePath = CONFIG_FOLDER_PATH + File.separator + "personal_bests.json";

            // Check if the xyz.yourboykyle.secretroutes.config directory exists
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
        } catch (Exception e) {
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
            if (serverName.contains("hypixel.") || serverName.contains("fakepixel.") || SRMConfig.get().disableServerChecking) {

                if (SRMConfig.get().autoCheckUpdates) {
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

    public static void toggleSecretsKeybind() {
        if (SRMConfig.get().modEnabled) {
            SRMConfig.get().modEnabled = false;
            sendChatMessage(Formatting.RED + "Secret Routes Mod secret rendering has been disabled.");
        } else {
            SRMConfig.get().modEnabled = true;
            sendChatMessage(Formatting.GREEN + "Secret Routes Mod secret rendering has been enabled.");
        }
    }

    @Override
    public void onInitializeClient() {
        instance = this;

        String jarpath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        // Set up logging system
        String date = sdf.format(System.currentTimeMillis());
        outputLogs = new File(logDir + File.separator + "LATEST-" + date + ".log");
        if (!logDir.exists()) {
            logDir.mkdirs();
        } else {
            File[] files = logDir.listFiles((dir, name) -> name.startsWith("LATEST"));
            for (File file : files) {
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
        LogUtils.info("Jarpath: " + jarpath);
        if (!new File(tmpDir).exists()) {
            new File(tmpDir).mkdirs();
        }

        File batchFile = new File(tmpDir + File.separator + "update.bat");
        if (batchFile.exists()) {
            batchFile.delete();
        }
        File shellFile = new File(tmpDir + File.separator + "update.sh");
        if (shellFile.exists()) {
            shellFile.delete();
        }
        LogUtils.info("§bSetting ssl certificate");
        SSLUtils.setSSlCertificate();

        // Load YACL Config
        SRMConfig.HANDLER.load();

        // Initialize HUDs
        recordingHUD = new RecordingHUD();
        currentRoomHUD = new CurrentRoomHUD();

        // Register HUD Rendering
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            recordingHUD.render(drawContext);
            currentRoomHUD.render(drawContext);
        });

        // Initialize stuff
        routeRecording = new RouteRecording();
        checkRoutesData();
        checkProfilesData();
        checkPBData();
        PBUtils.loadPBData();

        KeyBinding.Category c = KeyBinding.Category.create(Identifier.of(MODID, "category.secretroutes.main"));

        // Register Fabric Keybinds
        nextSecretKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.secretroutes.nextSecret", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, c
        ));
        lastSecretKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.secretroutes.lastSecret", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, c
        ));
        toggleSecretsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.secretroutes.toggleSecrets", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_BACKSPACE, c
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (nextSecretKey.wasPressed()) {
                if (LocationUtils.isInDungeons()) Main.currentRoom.nextSecretKeybind();
            }
            if (lastSecretKey.wasPressed()) {
                if (LocationUtils.isInDungeons()) Main.currentRoom.lastSecretKeybind();
            }
            if (toggleSecretsKey.wasPressed()) {
                toggleSecretsKeybind();
            }
        });

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
        AnotherRenderingUtil.register();

        // Server connection xyz.yourboykyle.secretroutes.events (for this)
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

        LocationUtils.init();

        DungeonScanner.init();

        if (SRMConfig.get().autoUpdateRoutes) {
            LogUtils.info("Checking for route updates...");
            new Thread(() -> {
                try {
                    RouteUtils.checkRoutesFiles();
                } catch (Exception e1) {
                    LogUtils.error(e1);
                }
            }).start();
        }
    }
}
