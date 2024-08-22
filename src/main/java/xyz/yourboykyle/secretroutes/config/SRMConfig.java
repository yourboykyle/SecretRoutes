package xyz.yourboykyle.secretroutes.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.InfoType;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.huds.CurrentRoomHUD;
import xyz.yourboykyle.secretroutes.config.huds.RecordingHUD;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.FileUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

import java.io.File;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;
import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendVerboseMessage;

public class SRMConfig extends Config {
    @Switch(
            name = "Render Routes",
            size = OptionSize.DUAL,
            subcategory = "General"
    )
    public static boolean modEnabled = true;

    @Dropdown(
            name = "Line Type",
            options = {"Fire Particles", "Lines", "None"},
            subcategory = "General"
    )
    public static int particleType = 0;

    @Slider(
            name = "Line width (not for particles)",
            min = 1, max = 10.1F,
            step = 1,
            subcategory = "General"
    )
    public static int width = 5;


    @Slider(
            name = "Line width (for ender pearls)",
            min = 1, max = 10.1F,
            step = 1,
            subcategory = "General"
    )
    public static int pearlLineWidth = 5;

    @Text(
            name = "Routes file name",
            placeholder = "routes.json",
            subcategory = "General"

    )
    public static String routesFileName = "routes.json";

    @Button(
            name = "Update routes",
            text = "Update routes",
            description = "Downloads the routes.json from github",
            subcategory = "General",
            size = 2
    )
    Runnable runnable = () -> {
        new Thread(() -> {
            Main.updateRoutes();
        }).start();
    };

    @Button(
            name = "Import routes",
            text = "Import routes",
            description = "Select a routes.json file to import, this will be copied to .minecraft/config/SecretRoutes/routes.json",
            size = 2,
            subcategory = "General"
    )
    Runnable runnable9 = () -> {
        new Thread(() -> {
            try {
                File file = FileUtils.promptUserForFile();
                if(file != null) {
                    FileUtils.copyFileToDirectory(file, Main.ROUTES_PATH);
                }
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }).start();
    };

    @Switch(
            name = "Auto updates",
            description = "Automatically checks for updates on startup",
            subcategory = "Updates"
    )
    public static boolean autoUpdate = true;
/*
    @Info(
            text = "This goes to github to check for updates. It will prompt for an update, unless \"Don't confirm before updating\" is enabled",
            subcategory = "Updates",
            type = InfoType.INFO,
            size = OptionSize.DUAL
    )
    public static boolean a;

 */

    @Button(
            name = "Check for updates",
            text = "Check for updates",
            description = "Manually check for an update if you wish to make sure",
            subcategory = "Updates",
            size = 2
    )
    Runnable runnable14 = () -> {
        new Thread(() -> {
            ChatUtils.sendChatMessage("Checking for updates, please wait a few seconds...");
            Main.updateManager.checkUpdate(true);
        }).start();
    };

    // Recording

    @Button(
            name = "Set Bat Waypoint",
            text = "Set Bat Waypoint",
            description = "Since bat waypoints aren't automatically detected, sadly we must manually set a bat waypoint",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable3 = () -> {
        new Thread(() -> {
            if(Main.routeRecording.recording) {
                BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                targetPos = targetPos.add(-1, 2, -1); // Block above the player, the -1 on X and Z have to be like that, trust the process

                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.BAT, targetPos);
                Main.routeRecording.newSecret();
                Main.routeRecording.setRecordingMessage(EnumChatFormatting.YELLOW+"Added bat secret waypoint.");
            } else {
                sendChatMessage(EnumChatFormatting.RED+"Route recording is not enabled. Press the start recording button to begin.");
            }
        }).start();
    };

    @Button(
            name = "Start recording",
            text = "Start recording",
            description = "Start recording a custom secret route",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable2 = () -> {
        new Thread(() -> {
            Main.routeRecording.startRecording();
        }).start();
    };

    @Button(
            name = "Stop recording",
            text = "Stop recording",
            description = "Stop recording your secret route",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable4 = () -> {
        new Thread(() -> {
            Main.routeRecording.stopRecording();
        }).start();
    };

    @Button(
            name = "Export routes",
            text = "Export routes",
            description = "Export your current secret routes to your downloads folder as routes.json",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable5 = () -> {
        new Thread(() -> {
            Main.routeRecording.exportAllRoutes();
        }).start();
    };

    @Button(
            name = "Import routes",
            text = "Import routes",
            description = "Import routes to recording from a routes.json in your downloads folder",
            size = 2,
            category = "Route Recording"
    )
    Runnable runnable6 = () -> {
        new Thread(() -> {
            Main.routeRecording.importRoutes("routes.json");
        }).start();
    };

    @HUD(
            name = "Recording info",
            category = "HUD"
    )
    public static RecordingHUD recordingHUD = new RecordingHUD();

    @HUD(
            name = "Current room",
            category = "HUD"
    )
    public static CurrentRoomHUD currentRoomHUD = new CurrentRoomHUD();


    //Color profile saving and loading
    @Text(
            name = "Color Profile Name",
            description = "The name of the color profile to save or load",
            subcategory = "Profiles",
            category = "Rendering",
            placeholder = "default.json"
    )
    public static String colorProfileName = "default.json";

    @Info(
            text = "Will auto append the .json extension if not provided",
            subcategory = "Profiles",
            category = "Rendering",
            type = InfoType.INFO
    )
    public static boolean b;

    @Button(
            name = "Save Color Profile",
            text = "Save",
            description = "Write the current color profile, excluding waypoints, to a file",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable10 = () -> {
        new Thread(() -> {
            Main.writeColorConfig(colorProfileName);
        }).start();
    };

    @Button(
            name = "Load Color Profile",
            text = "Load",
            description = "Reads the color profile from a file",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable11 = () -> {
        new Thread(() -> {
            if(Main.loadColorConfig(colorProfileName.isEmpty() ? "default.json" : colorProfileName)){
                sendChatMessage(EnumChatFormatting.DARK_GREEN + "Loaded "+ EnumChatFormatting.GREEN + colorProfileName + EnumChatFormatting.DARK_GREEN + " as color profile");
            }
        }).start();
    };

    @Button(
            name = "List all Color Profiles",
            text = "List",
            description = "Lists all the color profiles in the color profile directory",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable12 = () -> {
        new Thread(() -> {
            sendChatMessage("Color Profiles:", EnumChatFormatting.DARK_AQUA);
            for(String name :FileUtils.getFileNames(Main.COLOR_PROFILE_PATH)){
                sendChatMessage(" - "+name, EnumChatFormatting.AQUA);
            }
        }).start();
    };

    @Button(
            name = "Import Color Profile",
            text = "Import",
            description = "Select a color profile to import, this will be copied to .minecraft/config/SecretRoutes/colorprofiles",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable13 = () -> {
        new Thread(() -> {
            try {
                File file = FileUtils.promptUserForFile();
                if(file != null) {
                    FileUtils.copyFileToDirectory(file, Main.COLOR_PROFILE_PATH);
                }
            } catch (Exception e) {
                LogUtils.error(e);
            }
        }).start();
    };

    // Rendering
    @Color(
            name="Line color",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor lineColor = new OneColor(255, 0, 0);

    @Color(
            name = "Pearl line color",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor pearlLineColor = new OneColor(0, 255, 255);

    @Color(
            name="EtherWarp",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor etherWarp = new OneColor(128, 0, 128);

    @Color(
            name = "Mine",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor mine = new OneColor(255, 255, 0);

    @Color(
            name ="Interacts",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor interacts = new OneColor(0, 0, 255);

    @Color(
            name="superbooms",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor superbooms = new OneColor(255, 0, 0);

    @Color(
            name="enderpearls",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor enderpearls = new OneColor(0, 255, 255);

    @Color(
            name = "Secrets - item",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor secretsItem = new OneColor(0, 255, 255);

    @Color(
            name = "Secrets - interact",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor secretsInteract = new OneColor(0, 0, 255);

    @Color(
            name = "Secrets - bat",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor secretsBat = new OneColor(0, 255, 0);

    @Button(
            name = "Reset to default colors",
            text = "Reset",
            size = OptionSize.DUAL,
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    Runnable runnable7 = () -> {
        lineColor = new OneColor(255, 0, 0);
        etherWarp = new OneColor(128, 0, 128);
        mine = new OneColor(255, 255, 0);
        interacts = new OneColor(0, 0, 255);
        superbooms = new OneColor(255, 0, 0);
        secretsItem = new OneColor(0, 255, 255);
        secretsInteract = new OneColor(0, 0, 255);
        secretsBat = new OneColor(0, 255, 0);
    };



    // Start waypoints
    @Switch(
            name = "Start text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean startTextToggle = true;

    @Dropdown(
            name = "Start waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int startWaypointColorIndex = 12;

    @Slider(
            name = "Start waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float startTextSize = 3;

    // Interact waypoints
    @Switch(
            name = "Interact text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean interactTextToggle = true;

    @Dropdown(
            name = "Interact waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int interactWaypointColorIndex = 9;

    @Slider(
            name = "Interact waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float interactTextSize = 3;

    // Item waypoints
    @Switch(
            name = "Item text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean itemTextToggle = true;

    @Dropdown(
            name = "Item waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int itemWaypointColorIndex = 11;

    @Slider(
            name = "Item waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float itemTextSize = 3;

    // Bat waypoints
    @Switch(
            name = "Bat text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean batTextToggle = true;

    @Dropdown(
            name = "Bat waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int batWaypointColorIndex = 10;

    @Slider(
            name = "Bat waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float batTextSize = 3;

    // Etherwarp waypoints
    @Switch(
            name = "Etherwarp text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean etherwarpsTextToggle = false;

    @Dropdown(
            name = "Etherwarp waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int etherwarpsWaypointColorIndex = 5;

    @Slider(
            name = "Etherwarp waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float etherwarpsTextSize = 3;

    // Mines waypoints
    @Switch(
            name = "Stonk text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean minesTextToggle = false;

    @Dropdown(
            name = "Stonk waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int minesWaypointColorIndex = 14;

    @Slider(
            name = "Stonk waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float minesTextSize = 3;

    // Interacts waypoints
    @Switch(
            name = "Interact text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean interactsTextToggle = false;

    @Dropdown(
            name = "Interact waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int interactsWaypointColorIndex = 9;

    @Slider(
            name = "Interact waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float interactsTextSize = 3;

    // Superboom waypoints
    @Switch(
            name = "Superboom text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean superboomsTextToggle = false;

    @Dropdown(
            name = "Superboom waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int superboomsWaypointColorIndex = 12;

    @Slider(
            name = "Superboom waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float superboomsTextSize = 3;

    // Superboom waypoints
    @Switch(
            name = "Ender Pearl text toggle",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean enderpearlTextToggle = true;

    @Dropdown(
            name = "Ender Pearl waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int enderpearlWaypointColorIndex = 11;

    @Slider(
            name = "Ender Pearl waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float enderpearlTextSize = 3;

    // Reset to text defaults
    @Button(
            name = "Reset to text defaults",
            text = "Reset",
            description = "Resets all the text options to their default values",
            size =  OptionSize.DUAL,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    Runnable runnable8 = () -> {
        startTextToggle = true;
        startWaypointColorIndex = 12;
        startTextSize = 3;
        interactTextToggle = true;
        interactTextSize = 3;
        interactWaypointColorIndex = 10;
        itemTextToggle = true;
        itemWaypointColorIndex = 11;
        itemTextSize = 3;
        batTextToggle = true;
        batWaypointColorIndex = 10;
        batTextSize = 3;
        etherwarpsTextToggle = false;
        etherwarpsWaypointColorIndex = 5;
        etherwarpsTextSize = 3;
        minesTextToggle = false;
        minesWaypointColorIndex = 14;
        minesTextSize = 3;
        interactsTextToggle = false;
        interactsWaypointColorIndex = 9;
        interactsTextSize = 3;
        superboomsTextToggle = false;
        superboomsWaypointColorIndex = 12;
        superboomsTextSize = 3;
        enderpearlTextToggle = true;
        enderpearlWaypointColorIndex = 11;
        enderpearlTextSize = 3;
    };

    @Text(
            name = "Dev password",
            description = "The password to access the dev options",
            subcategory = "General",
            category = "Dev",
            size = 2
    )
    public static String devPassword = "";

    @Switch(
            name= "Verbose logging",
            description = "Adds more detailed logging, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev",
            size = OptionSize.DUAL
    )
    public static boolean verboseLogging = false;

    @Switch(
            name= "Better recording",
            description = "Adds more detailed logging for recording, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseRecording = true;
    //More verbose logging options will come in future releases
    @Switch(
            name= "Better updating",
            description = "adds more detailed logging for updating, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseUpdating = true;

    @Switch(
            name= "Better info",
            description = "adds more detailed logging for info, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseInfo = false;

    @Switch(
            name= "Force outdated",
            description = "Forces the version to be outdated, useful for testing the auto updater",
            subcategory = "General",
            category = "Dev"
    )
    public static boolean forceUpdateDEBUG = false;

    @Info(
            text = "Do not turn this on unless you know exactly what you are doing",
            type = InfoType.ERROR,
            category = "Dev",
            subcategory = "General"
    )
    public static boolean c;






    public Boolean lambda(String dependentOption) {
        try {
            return (boolean) optionNames.get(dependentOption).get();
        } catch (IllegalAccessException ignored) {
            sendVerboseMessage("Error in lambda function");
            return true;
        }
    }

    public SRMConfig() {
        super(new Mod(Main.MODID, ModType.SKYBLOCK), Main.MODID + ".json");
        initialize();

        try {
            optionNames.get("particleType").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("width").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("routesFileName").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("runnable").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("runnable9").addHideCondition(() -> !lambda("modEnabled"));
            optionNames.get("runnable14").addHideCondition(() -> !lambda("modEnabled"));

            optionNames.get("startWaypointColorIndex").addHideCondition(() -> !lambda("startTextToggle"));
            optionNames.get("startTextSize").addHideCondition(() -> !lambda("startTextToggle"));
            optionNames.get("interactWaypointColorIndex").addHideCondition(() -> !lambda("interactTextToggle"));
            optionNames.get("interactTextSize").addHideCondition(() -> !lambda("interactTextToggle"));
            optionNames.get("itemWaypointColorIndex").addHideCondition(() -> !lambda("itemTextToggle"));
            optionNames.get("itemTextSize").addHideCondition(() -> !lambda("itemTextToggle"));
            optionNames.get("batWaypointColorIndex").addHideCondition(() -> !lambda("batTextToggle"));
            optionNames.get("batTextSize").addHideCondition(() -> !lambda("batTextToggle"));

            optionNames.get("etherwarpsWaypointColorIndex").addHideCondition(() -> !lambda("etherwarpsTextToggle"));
            optionNames.get("etherwarpsTextSize").addHideCondition(() -> !lambda("etherwarpsTextToggle"));
            optionNames.get("minesWaypointColorIndex").addHideCondition(() -> !lambda("minesTextToggle"));
            optionNames.get("minesTextSize").addHideCondition(() -> !lambda("minesTextToggle"));
            optionNames.get("interactsWaypointColorIndex").addHideCondition(() -> !lambda("interactsTextToggle"));
            optionNames.get("interactsTextSize").addHideCondition(() -> !lambda("interactsTextToggle"));
            optionNames.get("superboomsWaypointColorIndex").addHideCondition(() -> !lambda("superboomsTextToggle"));
            optionNames.get("superboomsTextSize").addHideCondition(() -> !lambda("superboomsTextToggle"));
            optionNames.get("enderpearlWaypointColorIndex").addHideCondition(() -> !lambda("enderpearlTextToggle"));
            optionNames.get("enderpearlTextSize").addHideCondition(() -> !lambda("enderpearlTextToggle"));

            optionNames.get("forceUpdateDEBUG").addHideCondition(() -> isDevPasswordNotCorrect());
            optionNames.get("verboseLogging").addHideCondition(() -> isDevPasswordNotCorrect());
            optionNames.get("c").addHideCondition(() -> isDevPasswordNotCorrect());
            optionNames.get("verboseRecording").addHideCondition(() -> !lambda("verboseLogging"));
            optionNames.get("verboseUpdating").addHideCondition(() -> !lambda("verboseLogging"));
            optionNames.get("verboseInfo").addHideCondition(() -> !lambda("verboseLogging"));
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }
    public boolean isDevPasswordNotCorrect(){
        if(devPassword.equals("KyleIsMyDaddy")) {
            return false;
        }
        verboseLogging = false;
        forceUpdateDEBUG = false;
        return true;
    }
}