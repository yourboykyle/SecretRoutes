// TODO: update this file for multi versioning (1.8.9 -> 1.21.8)
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

package xyz.yourboykyle.secretroutes.config;

import dev.deftu.omnicore.api.client.input.OmniKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.*;
import org.polyfrost.oneconfig.api.config.v1.annotations.Number;
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager;
import org.polyfrost.polyui.color.ColorUtils;
import org.polyfrost.polyui.color.PolyColor;
import org.polyfrost.polyui.input.KeybindHelper;
import org.polyfrost.polyui.input.PolyBind;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.huds.CurrentRoomHUD;
import xyz.yourboykyle.secretroutes.config.huds.RecordingHUD;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import xyz.yourboykyle.secretroutes.utils.*;

import java.io.File;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class SRMConfig extends Config {
    // Do not touch this ever (probably)
    public static final SRMConfig INSTANCE = new SRMConfig();

    @Switch(
            title = "Render Routes",
            description = "Main toggle",
            subcategory = "General"
    )
    public static boolean modEnabled = true;

    @Switch(
            title = "Render secrets when room is completed",
            description = "Renders secrets even if the room is completed",
            subcategory = "General"
    )
    public static boolean renderComplete = false;

    @Switch(
            title = "Full route",
            description = "Render all secrets in the route",
            subcategory = "General"
    )
    public static boolean wholeRoute = false;

    @Switch(
            title = "Render steps",
            description = "Renders the entire path to the secret instead of just the secret",
            subcategory = "General"
    )
    public static boolean allSteps = false;

    @Info(
            title = "Full Route - Renders the Entire route (only secrets unless All Steps is enabled)",
            description = "Full Route - Renders the Entire route (only secrets unless All Steps is enabled)",
            subcategory = "General"
    )
    public static boolean ignored;

    @Switch(
            title = "All secrets",
            description = "Renders all secrets in the room (DOES NOT RENDER STEPS) - NO LINES",
            subcategory = "General"
    )
    public static boolean allSecrets = false;
    @Info(
            title = "All secrets displays all secrets, but not the route... (no lines)",
            description = "All secrets displays all secrets, but not the route... (no lines)",
            subcategory = "General"
    )
    public static boolean ignored2;

    @Dropdown(
            title = "Line Type",
            options = {"Particles", "Lines", "None"},
            subcategory = "General"
    )
    public static int lineType = 1;

    @Dropdown(
            title = "Particle Type",
            options = {"Explosion Normal", "Explosion Large", "Explosion Huge", "Fireworks Spark", "Bubble", "Water Splash", "Water Wake", "Suspended", "Suspended Depth", "Crit", "Magic Crit", "Smoke Normal", "Smoke Large", "Spell", "Instant Spell", "Mob Spell", "Mob Spell Ambient", "Witch Magic", "Drip Water", "Drip Lava", "Villager Angry", "Villager Happy", "Town Aura", "Note", "Portal", "Enchantment Table", "Flame", "Lava", "Footstep", "Cloud", "Redstone", "Snowball", "Snow Shovel", "Slime", "Heart", "Barrier", "Water Drop", "Item Take", "Mob Appearance"},
            subcategory = "General"
    )
    public static int particles = 26;

    @Slider(
            title = "Tick inverval",
            description = "The interval between when the game renders the particles. Higher values will reduce lag, but may cause the particles to be less smooth",
            min = 0, max = 20.1F,
            step = 1,
            subcategory = "General"
    )
    public static int tickInterval = 1;


    @Slider(
            title = "Line width (not for particles)",
            min = 1, max = 10.1F,
            step = 1,
            subcategory = "General"
    )
    public static int width = 5;


    @Slider(
            title = "Line width (for ender pearls)",
            min = 1, max = 10.1F,
            step = 1,
            subcategory = "General"
    )
    public static int pearlLineWidth = 5;

    @Dropdown(
            title = "Type of routes",
            options = {"No pearls", "Pearls"},
            description = "Select which routes to use (pearls is better + default)",
            subcategory = "General"
    )
    public static int routeTypeIndex = 1; // Pearls by default

    @Text(
            title = "Routes file name",
            description = "The file name used when No pearls is selected",
            placeholder = "routes.json",
            subcategory = "General"

    )
    public static String routesFileName = "routes.json";

    @Text(
            title = "Pearl routes file name",
            description = "The file name used when Pearls is selected",
            placeholder = "pearlroutes.json",
            subcategory = "General"

    )
    public static String pearlRoutesFileName = "pearlroutes.json";

    @Button(
            title = "Update routes",
            description = "Downloads the routes.json from github",
            subcategory = "General"
    )
    private void updateRoutesButton() {
        if (routeTypeIndex == 1) {
            RouteUtils.updatePearlRoutes();
        } else {
            RouteUtils.updateRoutes();
        }
    }

    @Button(
            title = "Import routes",
            description = "Select a routes.json file to import, this will be copied to .minecraft/config/SecretRoutes/routes.json",
            subcategory = "General"
    )
    private void importRoutesButton() {
        try {
            File file = FileUtils.promptUserForFile();
            if (file != null) {
                FileUtils.copyFileToDirectory(file, Main.ROUTES_PATH);
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    @Text(
            title = "Copy file name",
            description = "This is the name of the file to copy the routes.json in your downloads to.",
            subcategory = "General"
    )
    public static String copyFileName = "";


    @Button(
            title = "Copy routes",
            description = "Copies the Downloads/routes.json to the routes directory under the name specified in the Copy file name field",
            subcategory = "General"
    )
    private void copyRoutesButton() {
        try{
            FileUtils.copyFileToRoutesDirectory();
        }catch (Exception e){
            LogUtils.error(e);
        }
    }

    @Switch(
            title = "Personal Best Tracking",
            description = "Tracks your personal best time for completing the secrets in each room",
            subcategory = "Personal Bests"
    )
    public static boolean trackPersonalBests = true;

    @Switch(
            title = "Send Chat Messages For New PBs",
            description = "Sends chat messages when you beat your personal best",
            subcategory = "Personal Bests"
    )
    public static boolean sendChatMessages = true;

    @Button(
            title = "Get personal best (current room)",
            description = "Gets your personal best time for the current room",
            subcategory = "Personal Bests"
    )
    private void getPersonalBestButton() {
        long pb = PBUtils.getPBForRoom(RoomDetection.roomName);
        if (pb != -1) {
            sendChatMessage("Personal best for " + RoomDetection.roomName + ": " + EnumChatFormatting.GREEN + PBUtils.formatTime(pb));
        } else {
            sendChatMessage(EnumChatFormatting.DARK_RED + "No personal best found for " + RoomDetection.roomName);
        }
    }

    @Button(
            title = "Reset personal best (current room)",
            description = "Reset your personal best time for the current room",
            subcategory = "Personal Bests"
    )
    private void resetPersonalBestButton() {
        // Remove the PB from the JSON
        PBUtils.removePersonalBest(RoomDetection.roomName);
        sendChatMessage("Reset personal best for " + RoomDetection.roomName);
    }

    @Switch(
            title = "Notify for new updates",
            description = "Automatically checks for updates on startup. WILL NOT AUTO UPDATE",
            subcategory = "Updates"
    )
    public static boolean autoCheckUpdates = true;


    @Switch(
            title = "Auto download new updates",
            description = "Automatically downloads updates when they are available",
            subcategory = "Updates"
    )
    public static boolean autoDownload = false;


    @Button(
            title = "Check for updates",
            description = "Manually check for an update if you wish to make sure",
            subcategory = "Updates"
    )
    private void checkForUpdatesButton() {
        sendChatMessage("Checking for updates, please wait a few seconds...");
        Main.updateManager.checkUpdate(true);
    }

    @Switch(
            title = "Auto update Routes",
            description = "Automatically updates the routes.json file when it is out of date",
            subcategory = "Updates"
    )
    public static boolean autoUpdateRoutes = false;

    @Info(
            title = "THIS WILL OVERWRITE THE FILE. MAKE SURE YOUR CUSTOM ROUTES ARE NOT NAMED ROUTES.JSON OR PEARLROUTES.JSON",
            description = "THIS WILL OVERWRITE THE FILE. MAKE SURE YOUR CUSTOM ROUTES ARE NOT NAMED ROUTES.JSON OR PEARLROUTES.JSON",
            subcategory = "Updates"
    )
    public static boolean ignored3;

    // Recording
    @Button(
            title = "Set Bat Waypoint",
            description = "Since bat waypoints aren't automatically detected, sadly we must manually set a bat waypoint",
            category = "Route Recording"
    )
    private void setBatWaypointButton() {
        if (Main.routeRecording.recording) {
            BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
            BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
            targetPos = targetPos.add(-1, 2, -1); // Block above the player, the -1 on X and Z have to be like that, trust the process

            Main.routeRecording.addWaypoint(Room.SECRET_TYPES.BAT, targetPos);
            Main.routeRecording.newSecret();
            Main.routeRecording.setRecordingMessage(EnumChatFormatting.YELLOW + "Added bat secret waypoint.");
        } else {
            sendChatMessage(EnumChatFormatting.RED + "Route recording is not enabled. Press the start recording button to begin.");
        }
    }

    @Button(
            title = "Start recording",
            description = "Start recording a custom secret route",
            category = "Route Recording"
    )
    private void startRecordingButton() {
        Main.routeRecording.startRecording();
    }

    @Button(
            title = "Set Exit Waypoint",
            description = "Set an exit waypoint to at the end of your route",
            category = "Route Recording"
    )
    private void setExitWaypointButton() {
        if (Main.routeRecording.recording) {
            BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
            BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
            targetPos = targetPos.add(-1, 0, -1); // The -1 on X and Z have to be like that, trust the process

            Main.routeRecording.addWaypoint(Room.SECRET_TYPES.EXITROUTE, targetPos);
            Main.routeRecording.newSecret();
            Main.routeRecording.stopRecording(); // Exiting the route, it should be stopped
            Main.routeRecording.setRecordingMessage("Added route exit waypoint & stopped recording.");
            LogUtils.info("Added route exit waypoint & stopped recording.");
        } else {
            sendChatMessage(EnumChatFormatting.RED + "Route recording is not enabled. Press the start recording button to begin.");
        }
    }

    @Button(
            title = "Stop recording",
            description = "Stop recording your secret route",
            category = "Route Recording"
    )
    private void stopRecordingButton() {
        Main.routeRecording.stopRecording();
    }

    @Button(
            title = "Export routes",
            description = "Export your current secret routes to your downloads folder as routes.json",
            category = "Route Recording"
    )
    private void exportRoutesButton() {
        Main.routeRecording.exportAllRoutes();
    }

    @Button(
            title = "Import routes",
            description = "Import routes to recording from a routes.json in your downloads folder",
            category = "Route Recording"
    )
    private void importRoutesToRecordingButton() {
        Main.routeRecording.importRoutes("routes.json");
    }

    @Number(
            title = "Route number",
            description = "Sets the number of the route you are currently recording (NOTE: the preceding number needs to be filled for this route to be checked)",
            min = 0,
            max = 10,
            category = "Route Recording"
    )
    public static int routeNumber = 0;

    @Slider(
            title = "Ping",
            description = "Amount of time to wait before checking pos again to determine etherwarp",
            max = 1000, min = 0,
            category = "Route Recording"
    )
    public static int etherwarpPing = 150;

    @HUD(
            title = "Recording info",
            category = "HUD"
    )
    public static RecordingHUD recordingHUD = new RecordingHUD();

    @HUD(
            title = "Current room",
            category = "HUD"
    )
    public static CurrentRoomHUD currentRoomHUD = new CurrentRoomHUD();


    //Color profile saving and loading
    @Text(
            title = "Color Profile Name",
            description = "The name of the color profile to save or load",
            subcategory = "Profiles",
            category = "Rendering",
            placeholder = "default.json"
    )
    public static String colorProfileName = "default.json";

    @Info(
            title = "Will auto append the .json extension if not provided",
            description = "Will auto append the .json extension if not provided",
            subcategory = "Profiles",
            category = "Rendering"
    )
    public static boolean b;

    @Button(
            title = "Save Color Profile",
            description = "Write the current color profile, excluding waypoints, to a file",
            subcategory = "Profiles",
            category = "Rendering"
    )
    private void saveColorProfileButton() {
        ConfigUtils.writeColorConfig(colorProfileName);
    }

    @Button(
            title = "Load Color Profile",
            description = "Reads the color profile from a file",
            subcategory = "Profiles",
            category = "Rendering"
    )
    private void loadColorProfileButton() {
        if (ConfigUtils.loadColorConfig(colorProfileName.isEmpty() ? "default.json" : colorProfileName)) {
            sendChatMessage(EnumChatFormatting.DARK_GREEN + "Loaded " + EnumChatFormatting.GREEN + colorProfileName + EnumChatFormatting.DARK_GREEN + " as color profile");
        }
    }

    @Button(
            title = "List all Color Profiles",
            description = "Lists all the color profiles in the color profile directory",
            subcategory = "Profiles",
            category = "Rendering"
    )
    private void listAllColorProfilesButton() {
        sendChatMessage("Color Profiles:", EnumChatFormatting.DARK_AQUA);
        for (String name : FileUtils.getFileNames(Main.COLOR_PROFILE_PATH)) {
            sendChatMessage(" - " + name, EnumChatFormatting.AQUA);
        }
    }

    @Button(
            title = "Import Color Profile",
            description = "Select a color profile to import, this will be copied to .minecraft/config/SecretRoutes/colorprofiles",
            subcategory = "Profiles",
            category = "Rendering"
    )
    private void importColorProfileButton() {
        try {
            File file = FileUtils.promptUserForFile();
            if (file != null) {
                FileUtils.copyFileToDirectory(file, Main.COLOR_PROFILE_PATH);
            }
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    // Rendering
    @Slider(
            title = "Alpha multiplier",
            description = "Default opacity multiplier. ONLY HAS AN EFFECT ON THE FULL BLOCK",
            min = 0f, max = 1f,
            category = "Rendering",
            subcategory = "Waypoint Colors"
    )
    public static float alphaMultiplier = 0.5f;


    @Color(
            title = "Line color",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor lineColor = ColorUtils.rgba(255, 0, 0);

    @Color(
            title = "Pearl line color",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor pearlLineColor = ColorUtils.rgba(0, 255, 255);

    @Color(
            title = "EtherWarp",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor etherWarp = ColorUtils.rgba(128, 0, 128);

    @Color(
            title = "Mine",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor mine = ColorUtils.rgba(255, 255, 0);

    @Color(
            title = "Interacts",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor interacts = ColorUtils.rgba(0, 0, 255);

    @Color(
            title = "superbooms",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor superbooms = ColorUtils.rgba(255, 0, 0);

    @Color(
            title = "enderpearls",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor enderpearls = ColorUtils.rgba(0, 255, 255);


    @Color(
            title = "Secrets - item",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor secretsItem = ColorUtils.rgba(0, 255, 255);

    @Color(
            title = "Secrets - interact",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor secretsInteract = ColorUtils.rgba(0, 0, 255);

    @Color(
            title = "Secrets - bat",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    public static PolyColor secretsBat = ColorUtils.rgba(0, 255, 0);

    @Button(
            title = "Reset to default colors",
            subcategory = "Waypoint Colors",
            category = "Rendering"
    )
    private void resetToDefaultColorsButton() {
        alphaMultiplier = 0.5f;
        lineColor = ColorUtils.rgba(255, 0, 0);
        pearlLineColor = ColorUtils.rgba(0, 255, 255);
        etherWarp = ColorUtils.rgba(128, 0, 128);
        mine = ColorUtils.rgba(255, 255, 0);
        interacts = ColorUtils.rgba(0, 0, 255);
        superbooms = ColorUtils.rgba(255, 0, 0);
        enderpearls = ColorUtils.rgba(0, 255, 255);
        secretsItem = ColorUtils.rgba(0, 255, 255);
        secretsInteract = ColorUtils.rgba(0, 0, 255);
        secretsBat = ColorUtils.rgba(0, 255, 0);
    }

    @Switch(
            title = "Render etherwarps",
            subcategory = "Render Options",
            category =  "Rendering",
            description = "Toggles the rendering of etherwarp waypoints"
    )
    public static boolean renderEtherwarps = true;

    @Switch(
            title = "Full block",
            subcategory = "Render Options",
            category = "Rendering"
    )
    public static boolean etherwarpFullBlock = false;

    @Switch(
            title = "Render mines",
            subcategory = "Render Options",
            category =  "Rendering",
            description = "Toggles the rendering of mines waypoints"
    )
    public static boolean renderMines = true;

    @Switch(
            title = "Full block",
            subcategory = "Render Options",
            category = "Rendering"
    )
    public static boolean mineFullBlock = false;

    @Switch(
            title = "Render interacts",
            subcategory = "Render Options",
            category =  "Rendering",
            description = "Toggles the rendering of interact waypoints"
    )
    public static boolean renderInteracts = true;

    @Switch(
            title = "Full block",
            subcategory = "Render Options",
            category = "Rendering"
    )
    public static boolean interactsFullBlock = false;

    @Switch(
            title = "Render superbooms",
            subcategory = "Render Options",
            category =  "Rendering",
            description = "Toggles the rendering of superbooms waypoints"
    )
    public static boolean renderSuperboom = true;

    @Switch(
            title = "Full block",
            subcategory = "Render Options",
            category = "Rendering"
    )
    public static boolean superboomsFullBlock = false;

    @Switch(
            title = "Render enderpearls",
            subcategory = "Render Options",
            category =  "Rendering",
            description = "Toggles the rendering of enderpearls waypoints"
    )
    public static boolean renderEnderpearls = true;

    @Switch(
            title = "Full block",
            subcategory = "Render Options",
            category = "Rendering"
    )
    public static boolean enderpearlFullBlock = false;

    @Switch(
            title = "Render item secrets",
            subcategory = "Render Options",
            category =  "Rendering",
            description = "Toggles the rendering of item secret waypoints"
    )
    public static boolean renderSecretsItem = true;

    @Switch(
            title = "Full block",
            subcategory = "Render Options",
            category = "Rendering"
    )
    public static boolean secretsItemFullBlock = false;

    @Switch(
            title = "Render interact secrets",
            subcategory = "Render Options",
            category =  "Rendering",
            description = "Toggles the rendering of interact secrets waypoints"
    )
    public static boolean renderSecretIteract = true;

    @Switch(
            title = "Full block",
            subcategory = "Render Options",
            category = "Rendering"
    )
    public static boolean secretsInteractFullBlock = false;

    @Switch(
            title = "Render bat secrets",
            subcategory = "Render Options",
            category =  "Rendering",
            description = "Toggles the rendering of bat secrets waypoints"
    )
    public static boolean renderSecretBat = true;

    @Switch(
            title = "Full block",
            subcategory = "Render Options",
            category = "Rendering"
    )
    public static boolean secretsBatFullBlock = false;

    @Button(
            title = "Reset default options",
            category = "Rendering",
            subcategory = "Render Options"
    )
    private void resetDefaultOptionsButton() {
        renderEtherwarps = true;
        etherwarpFullBlock = false;
        renderMines = true;
        mineFullBlock = false;
        renderInteracts = true;
        interactsFullBlock = false;
        renderSuperboom = true;
        superboomsFullBlock = false;
        renderEnderpearls = true;
        enderpearlFullBlock = false;
        renderSecretsItem = true;
        secretsItemFullBlock = false;
        renderSecretIteract = true;
        secretsInteractFullBlock = false;
        renderSecretBat = true;
        secretsBatFullBlock = false;
    }

    // Start waypoints
    @Switch(
            title = "Start text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean startTextToggle = true;

    @Dropdown(
            title = "Start waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int startWaypointColorIndex = 12;

    @Slider(
            title = "Start waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float startTextSize = 3;

    // Exit route waypoints
    @Switch(
            title = "Exit text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean exitTextToggle = true;

    @Dropdown(
            title = "Exit waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int exitWaypointColorIndex = 12;

    @Slider(
            title = "Exit waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float exitTextSize = 3;

    // Interact waypoints
    @Switch(
            title = "Interact text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean interactTextToggle = true;

    @Dropdown(
            title = "Interact waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int interactWaypointColorIndex = 9;

    @Slider(
            title = "Interact waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float interactTextSize = 3;

    // Item waypoints
    @Switch(
            title = "Item text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean itemTextToggle = true;

    @Dropdown(
            title = "Item waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int itemWaypointColorIndex = 11;

    @Slider(
            title = "Item waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float itemTextSize = 3;

    // Bat waypoints
    @Switch(
            title = "Bat text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean batTextToggle = true;

    @Dropdown(
            title = "Bat waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int batWaypointColorIndex = 10;

    @Slider(
            title = "Bat waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float batTextSize = 3;

    // Etherwarp waypoints
    @Switch(
            title = "Etherwarp text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean etherwarpsTextToggle = false;

    @Switch(
            title = "Etherwarp enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the etherwarp waypoints"
    )
    public static boolean etherwarpsEnumToggle = false;

    @Dropdown(
            title = "Etherwarp waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int etherwarpsWaypointColorIndex = 5;

    @Slider(
            title = "Etherwarp waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float etherwarpsTextSize = 3;

    // Mines waypoints
    @Switch(
            title = "Stonk text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean minesTextToggle = false;

    @Switch(
            title = "Stonk enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the mines waypoints"
    )
    public static boolean minesEnumToggle = false;

    @Dropdown(
            title = "Stonk waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int minesWaypointColorIndex = 14;

    @Slider(
            title = "Stonk waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float minesTextSize = 3;

    // Interacts waypoints
    @Switch(
            title = "Interact text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean interactsTextToggle = false;

    @Switch(
            title = "Interact enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the interact waypoints"
    )
    public static boolean interactsEnumToggle = false;

    @Dropdown(
            title = "Interact waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int interactsWaypointColorIndex = 9;

    @Slider(
            title = "Interact waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float interactsTextSize = 3;

    // Superboom waypoints
    @Switch(
            title = "Superboom text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean superboomsTextToggle = false;

    @Switch(
            title = "Superboom enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the superboom waypoints"
    )
    public static boolean superboomsEnumToggle = false;

    @Dropdown(
            title = "Superboom waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int superboomsWaypointColorIndex = 12;

    @Slider(
            title = "Superboom waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float superboomsTextSize = 3;

    // Enderpearl waypoints
    @Switch(
            title = "Ender Pearl text toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static boolean enderpearlTextToggle = true;

    @Switch(
            title = "Enderpearl enumeration toggle",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering",
            description = "Adds a number to the superboom waypoints"
    )
    public static boolean enderpearlEnumToggle = false;

    @Dropdown(
            title = "Ender Pearl waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static int enderpearlWaypointColorIndex = 11;

    @Slider(
            title = "Ender Pearl waypoint text size",
            min = 1,
            max = 10,
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    public static float enderpearlTextSize = 3;

    // Reset to text defaults
    @Button(
            title = "Reset to text defaults",
            description = "Resets all the text options to their default values",
            subcategory = "Waypoint Text Rendering",
            category = "Rendering"
    )
    private void resetToTextDefaultsButton() {
        startTextToggle = true;
        startWaypointColorIndex = 12;
        startTextSize = 3;
        exitTextToggle = true;
        exitWaypointColorIndex = 12;
        exitTextSize = 3;
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
        etherwarpsEnumToggle = false;
        etherwarpsWaypointColorIndex = 5;
        etherwarpsTextSize = 3;
        minesTextToggle = false;
        minesEnumToggle = false;
        minesWaypointColorIndex = 14;
        minesTextSize = 3;
        interactsTextToggle = false;
        interactsEnumToggle = false;
        interactsWaypointColorIndex = 9;
        interactsTextSize = 3;
        superboomsTextToggle = false;
        superboomsEnumToggle = false;
        superboomsWaypointColorIndex = 12;
        superboomsTextSize = 3;
        enderpearlTextToggle = true;
        enderpearlEnumToggle = false;
        enderpearlWaypointColorIndex = 11;
        enderpearlTextSize = 3;
    }

    @Text(
            title = "Dev password",
            description = "The password to access the dev options",
            subcategory = "General",
            category = "Dev"
    )
    public static String devPassword = "";

    @Switch(
            title = "Verbose logging",
            description = "Adds more detailed logging, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseLogging = false;

    @Switch(
            title = "Better recording",
            description = "Adds more detailed logging for recording, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseRecording = true;
    //More verbose logging options will come in future releases
    @Switch(
            title = "Better updating",
            description = "adds more detailed logging for updating, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseUpdating = true;

    @Switch(
            title = "Better info",
            description = "adds more detailed logging for info, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseInfo = false;

    @Switch(
            title = "Better rendering",
            description = "adds more detailed logging rendering, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verboseRendering = false;
    @Switch(
            title = "Better personal bests",
            description = "adds more detailed logging personal bests, useful for debugging",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean verbosePersonalBests = false;

    @Switch(
            title = "ActionBar info",
            description = "Send the actionbar in chat for debugging purposes",
            subcategory = "Chat logging",
            category = "Dev"
    )
    public static boolean actionbarInfo = false;

    @Switch(
            title = "Force outdated",
            description = "Forces the version to be outdated, useful for testing the auto updater",
            subcategory = "General",
            category = "Dev"
    )
    public static boolean forceUpdateDEBUG = false;

    @Info(
            title = "Do not turn this on unless you know exactly what you are doing",
            description = "Do not turn this on unless you know exactly what you are doing",
            category = "Dev",
            subcategory = "General"
    )
    public static boolean c;

    @Dropdown(
            title = "Custom Pearl Orientation (Unused)",
            options = {"Default", "SW", "NW", "NE", "SE"},
            category = "Dev"
    )
    public static int customPearlOrientation = 0;

    @Switch(
            title = "Warn when keybinds used outside of dungeons",
            description = "Sends a warning message when keybinds are used outside of dungeons",
            category = "Keybinds",
            subcategory = "General"
    )
    public static boolean warnKeybindsOutsideDungeon = true;

    @Keybind(
            title = "Next Secret",
            description = "Cycles to the next secret",
            category = "Keybinds",
            subcategory = "Secrets"
    )
    public static PolyBind nextSecret = KeybindHelper.builder().keys(OmniKeys.KEY_RIGHT_BRACKET.getCode()).does((something) -> {
        if (Utils.inCatacombs) {
            Main.currentRoom.nextSecretKeybind();
        } else {
            if(warnKeybindsOutsideDungeon){
                sendChatMessage("§cYou are not in a dungeon!");
            }
        }
    }).build();
    //public static OneKeyBind nextSecret = new OneKeyBind(UKeyboard.KEY_RBRACKET);

    @Keybind(
            title = "Last Secret",
            description = "Cycles to the last secret",
            category = "Keybinds",
            subcategory = "Secrets"
    )
    public static PolyBind lastSecret = KeybindHelper.builder().keys(OmniKeys.KEY_LEFT_BRACKET.getCode()).does((something) -> {
        if (Utils.inCatacombs) {
            Main.currentRoom.lastSecretKeybind();
        } else {
            if(warnKeybindsOutsideDungeon){
                sendChatMessage("§cYou are not in a dungeon!");
            }
        }
    }).build();
    //public static OneKeyBind lastSecret = new OneKeyBind(UKeyboard.KEY_LBRACKET);

    @Keybind(
            title = "Toggle Secret rendering",
            description = "Toggles the rendering of secrets",
            category = "Keybinds",
            subcategory = "Secrets"
    )
    public static PolyBind toggleSecrets = KeybindHelper.builder().keys(OmniKeys.KEY_BACKSPACE.getCode()).does((something) -> {
        if (Utils.inCatacombs) {
            Main.toggleSecretsKeybind();
        } else {
            if(warnKeybindsOutsideDungeon){
                sendChatMessage("§cYou are not in a dungeon!");
            }
        }
    }).build();
    //public static OneKeyBind toggleSecrets = new OneKeyBind(UKeyboard.KEY_BACKSLASH);

    @Keybind(
            title = "Start recording",
            description = "Starts the recording process",
            category = "Keybinds",
            subcategory = "Recording"
    )
    public static PolyBind startRecording = KeybindHelper.builder().keys(OmniKeys.KEY_NONE.getCode()).does((something) -> {
        INSTANCE.startRecordingButton();
    }).build();
    //public static OneKeyBind startRecording = new OneKeyBind();

    @Keybind(
            title = "Stop recording",
            description = "Stops the recording process and adds an exit waypoint",
            subcategory = "Recording",
            category = "Keybinds"
    )
    public static PolyBind stopRecording = KeybindHelper.builder().keys(OmniKeys.KEY_NONE.getCode()).does((something) -> {
        INSTANCE.stopRecordingButton();
    }).build();
    //public static OneKeyBind stopRecording = new OneKeyBind();

    @Keybind(
            title = "Set Bat Waypoint",
            description = "Adds a bat waypoint on your current position",
            category = "Keybinds",
            subcategory = "Recording"
    )
    public static PolyBind setBatWaypoint = KeybindHelper.builder().keys(OmniKeys.KEY_NONE.getCode()).does((something) -> {
        INSTANCE.setBatWaypointButton();
    }).build();
    //public static OneKeyBind setBatWaypoint = new OneKeyBind();

    @Keybind(
            title = "Export Routes",
            description = "Exports current routes to the routes.json in your downloads folder",
            category = "Keybinds",
            subcategory = "Recording"
    )
    public static PolyBind exportRoutes = KeybindHelper.builder().keys(OmniKeys.KEY_NONE.getCode()).does((something) -> {
        INSTANCE.exportRoutesButton();
    }).build();
    //public static OneKeyBind exportRoutes = new OneKeyBind();

    @Switch(
            title = "Custom Secret Sound",
            description = "Plays a custom sound when a secret is found",
            category = "General",
            subcategory = "Sound"
    )
    public static boolean customSecretSound = false;

    @Dropdown(
            title = "Custom Secret Sound",
            options = {"mob.blaze.hit", "fire.ignite", "random.orb", "random.break", "mob.guardian.land.hit", "note.pling", "zyra.meow"},
            category = "General",
            subcategory = "Sound"
    )
    public static int customSecretSoundIndex = 6;

    @Slider(
            title = "Custom Secret Sound Volume",
            min = 0,
            max = 1.0f,
            category = "General",
            subcategory = "Sound"
    )
    public static float customSecretSoundVolume = 1.0f;

    @Slider(
            title = "Custom Secret Sound Pitch",
            min = 0,
            max = 2.0f,
            category = "General",
            subcategory = "Sound"
    )
    public static float customSecretSoundPitch = 1.0f;

    @Button(
            title = "Play Custom Secret Sound",
            description = "Plays the custom secret sound",
            category = "General",
            subcategory = "Sound"
    )
    private static void playCustomSecretSoundButton() {
        SecretSounds.secretChime(true);
    }

    @Switch(
            title = "Hide boss messages",
            description = "Hides boss messages without impacting other mods",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideBossMessages = false;

    @Checkbox(
            title = "Hide watcher",
            description = "Hides watcher messages",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideWatcher = true;

    @Checkbox(
            title = "Hide Bonzo",
            description = "Hides Bonzo messages",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideBonzo = true;

    @Checkbox(
            title = "Hide Scarf",
            description = "Hides Scarf messages (f2/m2)",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideScarf = true;

    @Checkbox(
            title = "Hide Professor",
            description = "Hides Professor messages (f3/m3)",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideProfessor = true;

    @Checkbox(
            title = "Hide Thorn",
            description = "Hides Thron messages (f4/m4)",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideThorn = true;

    @Checkbox(
            title = "Hide Livid",
            description = "Hides Livid messages (f5/m5)",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideLivid = true;

    @Checkbox(
            title = "Hide Sadan",
            description = "Hides Sadan messages (f6/m6)",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideSadan = true;

    @Checkbox(
            title = "Hide Wither lords",
            description = "Hides wither lords messages (f7/m7)",
            category = "General",
            subcategory = "Messages"
    )
    public static boolean hideWitherLords = false;

    @Switch(
            title = "Blood spawned notification",
            description = "Notifies when blood is fully spawned",
            category = "General",
            subcategory = "Messages"

    )
    public static boolean bloodNotif = false;

    @Text(
            title = "Blood ready text",
            description = "Text to show when blood is fully spawned",
            subcategory = "Messages"
    )
    public static String bloodReadytitle = "Blood Ready";

    @Dropdown(
            title = "Color",
            description = "Color of the message",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            subcategory = "Messages"
    )
    public static int bloodReadyColor = 6;

    @Number(
            title = "Duration",
            description = "Duration of the banner",
            max = 15000, min =1,
            subcategory = "Messages"
    )
    public static int bloodBannerDuration = 3000;

    @Number(
            title = "Scale",
            description = "Scale of the text",
            min = 1, max = 10,
            subcategory = "Messages"
    )
    public static int bloodScale = 2;

    @Slider(
            title = "X Offset",
            description = "X Offset for the message. (POSITIVE TO THE RIGHT)",
            subcategory = "Messages",
            min = -1000, max = 1000
    )
    public static int bloodX = 0;

    @Slider(
            title = "Y Offset",
            description = "Y Offset for the message. (POSITIVE TO THE BOTTOM)",
            subcategory = "Messages",
            min = -1000, max = 1000)
    public static int bloodY = -100;

    @Checkbox(
            title = "Render Test message",
            description = "Renders a test message with the paramaters to change position. (Untick when done)",
            subcategory = "Messages"
    )
    public static Boolean renderBlood = false;


    @Switch(
            title = "Render lines through walls",
            category = "Dev",
            subcategory = "WIP"
    )
    public static boolean renderLinesThroughWalls = false;

    @Switch(
            title = "Player crosshair to next waypoint",
            category = "Dev",
            subcategory = "WIP"
    )
    public static boolean playerWaypointLine = false;

    @Switch(
            title = "Player to Etherwarp",
            description = "Draws a line to the next etherwarp location",
            category = "Dev",
            subcategory = "WIP"
    )
    public static boolean playerToEtherwarp = false;

    @Checkbox(
            title = "debug",
            category = "Dev",
            subcategory = "WIP"
    )
    public static boolean debug = false;
    @Switch(
            title = "Disable Server Checking (You have to relog for it to work)",
            category = "Dev",
            subcategory = "WIP"
    )
    public static boolean disableServerChecking = false;

    @Switch(
            title = "Bridge",
            category = "Guild",
            subcategory = "WIP"
    )
    public static boolean bridge = false;

    @Switch(
            title = "Server Data",
            subcategory = "Data Privacy",
            description = "Sends data to the server (Masked UUID, Login Timestamp, Mod Version, Online Data)"
    )
    public static boolean sendData = true;

    public SRMConfig() {
        super(Main.MODID + ".json", "/assets/" + Main.MODID + "/logo.png", "Secret Routes", Category.HYPIXEL);

        try {
            addDependency("lineType", "modEnabled", true);

            addDependency("lineType", "modEnabled", true);
            addDependency("width", "modEnabled", true);
            addDependency("routesFileName", "modEnabled", true);
            addDependency("updateRoutesButton", "modEnabled", true);
            addDependency("importRoutesButton", "modEnabled", true);
            addDependency("checkForUpdatesButton", "modEnabled", true);
            // TODO: Check if this works properly in the config:
            hideIf("particles", () -> !isEqualTo(lineType, 0));
            addDependency("particles", "modEnabled", true);
            // TODO: Check if this works properly in the config:
            hideIf("tickInterval", () -> !isEqualTo(lineType, 0));
            addDependency("tickInterval", "modEnabled", true);
            addDependency("pearlLineWidth", "modEnabled", true);
            addDependency("routeTypeIndex", "modEnabled", true);
            addDependency("pearlRoutesFileName", "modEnabled", true);
            addDependency("allSecrets", "modEnabled", true);
            addDependency("renderComplete", "modEnabled", true);
            addDependency("allSteps", "modEnabled", true);
            addDependency("allSteps", "wholeRoute", true);
            addDependency("ignored", "modEnabled", true);

            addDependency("autoDownload", "autoCheckUpdates", true);

            addDependency("startWaypointColorIndex", "startTextToggle", true);
            addDependency("startTextSize", "startTextToggle", true);
            addDependency("exitWaypointColorIndex", "exitTextToggle", true);
            addDependency("exitTextSize", "exitTextToggle", true);
            addDependency("interactWaypointColorIndex", "interactTextToggle", true);
            addDependency("interactTextSize", "interactTextToggle", true);
            addDependency("itemWaypointColorIndex", "itemTextToggle", true);
            addDependency("itemTextSize", "itemTextToggle", true);
            addDependency("batWaypointColorIndex", "batTextToggle", true);
            addDependency("batTextSize", "batTextToggle", true);

            addDependency("etherwarpsEnumToggle", "etherwarpsTextToggle", true);
            addDependency("etherwarpsWaypointColorIndex", "etherwarpsTextToggle", true);
            addDependency("etherwarpsTextSize", "etherwarpsTextToggle", true);
            addDependency("minesEnumToggle", "minesTextToggle", true);
            addDependency("minesWaypointColorIndex", "minesTextToggle", true);
            addDependency("minesTextSize", "minesTextToggle", true);
            addDependency("interactsEnumToggle", "interactsTextToggle", true);
            addDependency("interactsWaypointColorIndex", "interactsTextToggle", true);
            addDependency("interactsTextSize", "interactsTextToggle", true);
            addDependency("superboomsEnumToggle", "superboomsTextToggle", true);
            addDependency("superboomsWaypointColorIndex", "superboomsTextToggle", true);
            addDependency("superboomsTextSize", "superboomsTextToggle", true);
            addDependency("enderpearlEnumToggle", "enderpearlTextToggle", true);
            addDependency("enderpearlWaypointColorIndex", "enderpearlTextToggle", true);
            addDependency("enderpearlTextSize", "enderpearlTextToggle", true);

            // TODO: Check if these 4 work properly in the config:
            hideIf("forceUpdateDEBUG", () -> isDevPasswordNotCorrect());
            hideIf("verboseLogging", () -> isDevPasswordNotCorrect());
            hideIf("c", () -> isDevPasswordNotCorrect());
            hideIf("debug", () -> isDevPasswordNotCorrect());
            addDependency("verboseRecording", "verboseLogging", true);
            addDependency("verboseUpdating", "verboseLogging", true);
            addDependency("verboseInfo", "verboseLogging", true);
            addDependency("verboseRendering", "verboseLogging", true);
            addDependency("actionbarInfo", "verboseLogging", true);

            addDependency("customSecretSoundIndex", "customSecretSound", true);
            addDependency("customSecretSoundVolume", "customSecretSound", true);
            addDependency("customSecretSoundPitch", "customSecretSound", true);
            addDependency("playCustomSecretSoundButton", "customSecretSound", true);

            addDependency("hideWatcher", "hideBossMessages", true);
            addDependency("hideBonzo", "hideBossMessages", true);
            addDependency("hideScarf", "hideBossMessages", true);
            addDependency("hideProfessor", "hideBossMessages", true);
            addDependency("hideThorn", "hideBossMessages", true);
            addDependency("hideLivid", "hideBossMessages", true);
            addDependency("hideSadan", "hideBossMessages", true);
            addDependency("hideWitherLords", "hideBossMessages", true);

            addDependency("bloodReadyText", "bloodNotif", true);
            addDependency("bloodReadyColor", "bloodNotif", true);
            addDependency("bloodBannerDuration", "bloodNotif", true);
            addDependency("bloodScale", "bloodNotif", true);
            addDependency("bloodX", "bloodNotif", true);
            addDependency("bloodY", "bloodNotif", true);
            addDependency("renderBlood", "bloodNotif", true);

            KeybindManager.registerKeybind(nextSecret);
            KeybindManager.registerKeybind(lastSecret);
            KeybindManager.registerKeybind(toggleSecrets);
            KeybindManager.registerKeybind(startRecording);
            KeybindManager.registerKeybind(stopRecording);
            KeybindManager.registerKeybind(setBatWaypoint);
            KeybindManager.registerKeybind(exportRoutes);

            /*registerKeyBind(lastSecret, () -> {
                if (Utils.inCatacombs) {
                    Main.currentRoom.lastSecretKeybind();
                } else {
                    if(warnKeybindsOutsideDungeon){
                        sendChatMessage("§cYou are not in a dungeon!");
                    }
                }
            });
            registerKeyBind(nextSecret, () -> {
                if (Utils.inCatacombs) {
                    Main.currentRoom.nextSecretKeybind();
                } else {
                    if(warnKeybindsOutsideDungeon){
                        sendChatMessage("§cYou are not in a dungeon!");
                    }
                }
            });*
            registerKeyBind(toggleSecrets, () -> {
                if (Utils.inCatacombs) {
                    Main.toggleSecretsKeybind();
                } else {
                    if(warnKeybindsOutsideDungeon){
                        sendChatMessage("§cYou are not in a dungeon!");
                    }
                }
            });

            registerKeyBind(startRecording, runnable2);
            registerKeyBind(stopRecording, runnable16);
            registerKeyBind(setBatWaypoint, runnable3);
            registerKeyBind(exportRoutes, runnable5);*/


        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    public boolean isDevPasswordNotCorrect() {
        if (devPassword.equals("KyleIsMyDaddy")) {
            return false;
        }
        verboseLogging = false;
        forceUpdateDEBUG = false;
        return true;
    }

    public boolean isEqualTo(Object a, Object b) {
        return a.equals(b);
    }

    public void openGui() {
        ModConfigPage test = new ModConfigPage(Main.config.mod.defaultPage);
        test.getPage().categories.get("add").subcategories.get(1);
    }
}