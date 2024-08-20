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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.huds.CurrentRoomHUD;
import xyz.yourboykyle.secretroutes.config.huds.RecordingHUD;
import xyz.yourboykyle.secretroutes.utils.FileUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

import java.io.File;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class SRMConfig extends Config {
    @Switch(
            name = "Render Routes",
            size = OptionSize.DUAL,
            subcategory = "General"
    )
    public static boolean modEnabled = true;

    @Dropdown(
            name = "Line Type",
            options = {"Fire Particles", "Lines"},
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
                FileUtils.copyFileToDirectory(FileUtils.promptUserForFile(), Main.ROUTES_PATH);
            } catch (Exception e) {
                LogUtils.error(e);
            }
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
                Main.routeRecording.setRecordingMessage("Added bat secret waypoint.");
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Route recording is not enabled. Press the start recording button to begin."));
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
    public static boolean a;
    @Button(
            name = "Save Color Profile",
            text = "Save",
            description = "Write the current color profile, excluding waypoints, to a file",
            subcategory = "Profiles",
            category = "Rendering"
    )
    Runnable runnable10 = () -> {
        new Thread(() -> {
            Main.writeColorConfig(Main.COLOR_PROFILE_PATH + File.separator + colorProfileName);
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
                sendChatMessage("Loaded "+colorProfileName+" as color profile", EnumChatFormatting.DARK_GREEN);
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

    // Rendering
    @Color(
            name="Line color",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static OneColor lineColor = new OneColor(255, 0, 0);

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
    };







    public SRMConfig() {
        super(new Mod(Main.MODID, ModType.SKYBLOCK), Main.MODID + ".json");
        initialize();

        addDependency("particleType", "modEnabled");
        addDependency("width", "modEnabled");
        addDependency("routesFileName", "modEnabled");
        addDependency("runnable", "modEnabled");
        addDependency("runnable9", "modEnabled");

        addDependency("startWaypointColorIndex", "startTextToggle");
        addDependency("startTextSize", "startTextToggle");
        addDependency("interactWaypointColorIndex", "interactTextToggle");
        addDependency("interactTextSize", "interactTextToggle");
        addDependency("itemWaypointColorIndex", "itemTextToggle");
        addDependency("itemTextSize", "itemTextToggle");
        addDependency("batWaypointColorIndex", "batTextToggle");
        addDependency("batTextSize", "batTextToggle");

        addDependency("etherwarpsWaypointColorIndex", "etherwarpsTextToggle");
        addDependency("etherwarpsTextSize", "etherwarpsTextToggle");
        addDependency("minesWaypointColorIndex", "minesTextToggle");
        addDependency("minesTextSize", "minesTextToggle");
        addDependency("interactsWaypointColorIndex", "interactsTextToggle");
        addDependency("interactsTextSize", "interactsTextToggle");
        addDependency("superboomsWaypointColorIndex", "superboomsTextToggle");
        addDependency("superboomsTextSize", "superboomsTextToggle");
    }
}