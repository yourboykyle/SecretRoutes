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
import com.google.gson.*;
import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.handlers.PacketHandler;
import io.github.quantizr.dungeonrooms.utils.Utils;
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

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

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
    private static final ResourceLocation SOUND_EVENT = new ResourceLocation("secretroutesmod", "custom_sound");
    public static SoundEvent customSound;

    public static Room currentRoom = new Room(null);
    public static RouteRecording routeRecording = null;
    public static UpdateManager updateManager = new UpdateManager();
    private static DungeonRooms dungeonRooms = new DungeonRooms();

    public static Main instance = new Main();
    public static SRMConfig config;

    public static String logFilePath = "";

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

        MinecraftForge.EVENT_BUS.register(this);

        // Register Commands
        ClientCommandHandler.instance.registerCommand(new LoadRoute());
        ClientCommandHandler.instance.registerCommand(new Recording());
        ClientCommandHandler.instance.registerCommand(new SRM());
        ClientCommandHandler.instance.registerCommand(new ChangeRoute());
        ClientCommandHandler.instance.registerCommand(new ChangeColorProfile());



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
                writeColorConfig(filePath);
            }
            //Implement logic for writing to file
        }catch(Exception e){
            LogUtils.error(e);
        }
    }
    public static void writeColorConfig(String path) {
            if(!path.endsWith(".json")){
                path += ".json";
            }
            Map<String, Object> defaultColors = new HashMap<>();
            defaultColors.put("alpha", SRMConfig.alphaMultiplier);
            defaultColors.put("lineColor", SRMConfig.lineColor);
            defaultColors.put("pearlLineColor", SRMConfig.pearlLineColor);
            defaultColors.put("etherWarp", SRMConfig.etherWarp);
            defaultColors.put("etherwarpFullBlock", SRMConfig.etherwarpFullBlock);
            defaultColors.put("mine", SRMConfig.mine);
            defaultColors.put("mineFullBlock", SRMConfig.mineFullBlock);
            defaultColors.put("interacts", SRMConfig.interacts);
            defaultColors.put("interactsFullBlock", SRMConfig.interactsFullBlock);
            defaultColors.put("superbooms", SRMConfig.superbooms);
            defaultColors.put("superboomsFullBlock", SRMConfig.superboomsFullBlock);
            defaultColors.put("enderpearls", SRMConfig.enderpearls);
            defaultColors.put("enderpearlFullBlock", SRMConfig.enderPearlFullBlock);
            defaultColors.put("secretsItem", SRMConfig.secretsItem);
            defaultColors.put("secretsItemFullBlock", SRMConfig.secretsItemFullBlock);
            defaultColors.put("secretsInteract", SRMConfig.secretsInteract);
            defaultColors.put("secretsInteractFullBlock", SRMConfig.secretsInteractFullBlock);
            defaultColors.put("secretsBat", SRMConfig.secretsBat);
            defaultColors.put("secretsBatFullBlock", SRMConfig.secretsBatFullBlock);

            defaultColors.put("startTextToggle", SRMConfig.startTextToggle);
            defaultColors.put("startWaypointColorIndex", SRMConfig.startWaypointColorIndex);
            defaultColors.put("startTextSize", SRMConfig.startTextSize);
            defaultColors.put("exitTextToggle", SRMConfig.exitTextToggle);
            defaultColors.put("exitWaypointColorIndex", SRMConfig.exitWaypointColorIndex);
            defaultColors.put("exitTextSize", SRMConfig.exitTextSize);
            defaultColors.put("interactTextToggle", SRMConfig.interactTextToggle);
            defaultColors.put("interactTextSize", SRMConfig.interactTextSize);
            defaultColors.put("interactWaypointColorIndex", SRMConfig.interactWaypointColorIndex);
            defaultColors.put("itemTextToggle", SRMConfig.itemTextToggle);
            defaultColors.put("itemWaypointColorIndex", SRMConfig.itemWaypointColorIndex);
            defaultColors.put("itemTextSize", SRMConfig.itemTextSize);
            defaultColors.put("batTextToggle", SRMConfig.batTextToggle);
            defaultColors.put("batWaypointColorIndex", SRMConfig.batWaypointColorIndex);
            defaultColors.put("batTextSize", SRMConfig.batTextSize);
            defaultColors.put("etherwarpsTextToggle", SRMConfig.etherwarpsTextToggle);
            defaultColors.put("etherwarpsEnumToggle", SRMConfig.etherwarpsEnumToggle);
            defaultColors.put("etherwarpsWaypointColorIndex", SRMConfig.etherwarpsWaypointColorIndex);
            defaultColors.put("etherwarpsTextSize", SRMConfig.etherwarpsTextSize);
            defaultColors.put("minesTextToggle", SRMConfig.minesTextToggle);
            defaultColors.put("minesEnumToggle", SRMConfig.minesEnumToggle);
            defaultColors.put("minesWaypointColorIndex", SRMConfig.minesWaypointColorIndex);
            defaultColors.put("minesTextSize", SRMConfig.minesTextSize);
            defaultColors.put("interactsTextToggle", SRMConfig.interactsTextToggle);
            defaultColors.put("interactsEnumToggle", SRMConfig.interactsEnumToggle);
            defaultColors.put("interactsWaypointColorIndex", SRMConfig.interactsWaypointColorIndex);
            defaultColors.put("interactsTextSize", SRMConfig.interactsTextSize);
            defaultColors.put("superboomsTextToggle", SRMConfig.superboomsTextToggle);
            defaultColors.put("superboomsEnumToggle", SRMConfig.superboomsEnumToggle);
            defaultColors.put("superboomsWaypointColorIndex", SRMConfig.superboomsWaypointColorIndex);
            defaultColors.put("superboomsTextSize", SRMConfig.superboomsTextSize);
            defaultColors.put("enderpearlTextToggle", SRMConfig.enderpearlTextToggle);
            defaultColors.put("enderpearlEnumToggle", SRMConfig.enderpearlEnumToggle);
            defaultColors.put("enderpearlWaypointColorIndex", SRMConfig.enderpearlWaypointColorIndex);
            defaultColors.put("enderpearlTextSize", SRMConfig.enderpearlTextSize);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(defaultColors);

            try (FileWriter writer = new FileWriter(COLOR_PROFILE_PATH+File.separator+path)) {
                writer.write(json);
                sendChatMessage(EnumChatFormatting.GREEN+path+EnumChatFormatting.DARK_GREEN+" color profile created successfully.");
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
                sendChatMessage(EnumChatFormatting.RED+"Color profile not found, please select different one or create it.");
                return false;
            }
            Gson gson = new GsonBuilder().create();
            FileReader reader = new FileReader(finalPath);
            JsonObject data = gson.fromJson(reader, JsonObject.class);

            SRMConfig.alphaMultiplier = data.get("alphaMultiplier").getAsFloat();
            SRMConfig.lineColor = parseOneColor(data.get("lineColor"));
            SRMConfig.pearlLineColor = parseOneColor(data.get("pearlLineColor"));
            SRMConfig.etherWarp = parseOneColor(data.get("etherWarp"));
            SRMConfig.etherwarpFullBlock = data.get("etherwarpFullBlock").getAsBoolean();
            SRMConfig.mine = parseOneColor(data.get("mine"));
            SRMConfig.mineFullBlock = data.get("mineFullBlock").getAsBoolean();
            SRMConfig.interacts = parseOneColor(data.get("interacts"));
            SRMConfig.interactsFullBlock = data.get("interactsFullBlock").getAsBoolean();
            SRMConfig.superbooms = parseOneColor(data.get("superbooms"));
            SRMConfig.superboomsFullBlock = data.get("superboomsFullBlock").getAsBoolean();
            SRMConfig.enderpearls = parseOneColor(data.get("enderpearls"));
            SRMConfig.enderPearlFullBlock = data.get("enderpearlFullBlock").getAsBoolean();
            SRMConfig.secretsItem = parseOneColor(data.get("secretsItem"));
            SRMConfig.secretsItemFullBlock = data.get("secretsItemFullBlock").getAsBoolean();
            SRMConfig.secretsInteract = parseOneColor(data.get("secretsInteract"));
            SRMConfig.secretsInteractFullBlock = data.get("secretsInteractFullBlock").getAsBoolean();
            SRMConfig.secretsBat = parseOneColor(data.get("secretsBat"));
            SRMConfig.secretsBatFullBlock = data.get("secretsBatFullBlock").getAsBoolean();

            SRMConfig.startTextToggle = data.get("startTextToggle").getAsBoolean();
            SRMConfig.startWaypointColorIndex = data.get("startWaypointColorIndex").getAsInt();
            SRMConfig.startTextSize = data.get("startTextSize").getAsFloat();
            SRMConfig.exitTextToggle = data.get("exitTextToggle").getAsBoolean();
            SRMConfig.exitWaypointColorIndex = data.get("exitWaypointColorIndex").getAsInt();
            SRMConfig.exitTextSize = data.get("exitTextSize").getAsFloat();
            SRMConfig.interactTextToggle =  data.get("interactTextToggle").getAsBoolean();
            SRMConfig.interactWaypointColorIndex = data.get("interactWaypointColorIndex").getAsInt();
            SRMConfig.interactTextSize = data.get("interactTextSize").getAsFloat();
            SRMConfig.itemTextToggle =  data.get("itemTextToggle").getAsBoolean();
            SRMConfig.itemWaypointColorIndex = data.get("itemWaypointColorIndex").getAsInt();
            SRMConfig.itemTextSize = data.get("itemTextSize").getAsFloat();
            SRMConfig.batTextToggle =  data.get("batTextToggle").getAsBoolean();
            SRMConfig.batWaypointColorIndex = data.get("batWaypointColorIndex").getAsInt();
            SRMConfig.batTextSize = data.get("batTextSize").getAsFloat();
            SRMConfig.etherwarpsTextToggle =  data.get("etherwarpsTextToggle").getAsBoolean();
            SRMConfig.etherwarpsEnumToggle =  data.get("etherwarpsEnumToggle").getAsBoolean();
            SRMConfig.etherwarpsWaypointColorIndex = data.get("etherwarpsWaypointColorIndex").getAsInt();
            SRMConfig.etherwarpsTextSize = data.get("etherwarpsTextSize").getAsFloat();
            SRMConfig.minesTextToggle =  data.get("minesTextToggle").getAsBoolean();
            SRMConfig.minesEnumToggle =  data.get("minesEnumToggle").getAsBoolean();
            SRMConfig.minesWaypointColorIndex = data.get("minesWaypointColorIndex").getAsInt();
            SRMConfig.minesTextSize = data.get("minesTextSize").getAsFloat();
            SRMConfig.interactsTextToggle =  data.get("interactsTextToggle").getAsBoolean();
            SRMConfig.interactsEnumToggle =  data.get("interactsEnumToggle").getAsBoolean();
            SRMConfig.interactsWaypointColorIndex = data.get("interactsWaypointColorIndex").getAsInt();
            SRMConfig.interactsTextSize = data.get("interactsTextSize").getAsFloat();
            SRMConfig.superboomsTextToggle =  data.get("superboomsTextToggle").getAsBoolean();
            SRMConfig.superboomsEnumToggle =  data.get("superboomsEnumToggle").getAsBoolean();
            SRMConfig.superboomsWaypointColorIndex = data.get("superboomsWaypointColorIndex").getAsInt();
            SRMConfig.superboomsTextSize = data.get("superboomsTextSize").getAsFloat();
            SRMConfig.enderpearlTextToggle =  data.get("enderpearlTextToggle").getAsBoolean();
            SRMConfig.enderpearlEnumToggle =  data.get("enderpearlEnumToggle").getAsBoolean();
            SRMConfig.enderpearlWaypointColorIndex = data.get("enderpearlWaypointColorIndex").getAsInt();
            SRMConfig.enderpearlTextSize = data.get("enderpearlTextSize").getAsFloat();

            return true;
        } catch (Exception e) {
            sendChatMessage("[ERROR] Invalid color profile format.", EnumChatFormatting.RED);
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

            InputStream inputStream = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json").openStream();
            OutputStream outputStream = new FileOutputStream(configFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();

        }catch(Exception e){
            LogUtils.error(e);
        }
    }

    public static void updateRoutes() {
        File configFile = new File(ROUTES_PATH + File.separator + "routes.json");
        try {
            LogUtils.info("Downloading routes.json...");

            InputStream inputStream = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/routes.json").openStream();
            OutputStream outputStream = Files.newOutputStream(configFile.toPath());
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();

        } catch (Exception e){
            LogUtils.error(e);
        }
    }
    public static void updatePearlRoutes() {
        File configFile = new File(ROUTES_PATH + File.separator + "pearlroutes.json");
        try {
            LogUtils.info("Downloading routes.json...");
            InputStream inputStream = new URL("https://raw.githubusercontent.com/yourboykyle/SecretRoutes/main/pearlroutes.json").openStream();
            OutputStream outputStream = new FileOutputStream(configFile);
            IOUtils.copy(inputStream, outputStream);
            outputStream.close();
            inputStream.close();
        } catch (Exception e){
            LogUtils.error(e);
        }
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
            LogUtils.info("§bSetting ssl certificate");
            SSLUtils.setSSlCertificate();


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
    public static OneColor parseOneColor(JsonElement json){
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray hsba = jsonObject.getAsJsonArray("hsba");
        int hue = hsba.get(0).getAsInt();
        int saturation = hsba.get(1).getAsInt();
        int brightness = hsba.get(2).getAsInt();
        int alpha = hsba.get(3).getAsInt();
        int chromaSpeed = jsonObject.get("dataBit").getAsInt();
        return new OneColor(hue, saturation, brightness, alpha, chromaSpeed);
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