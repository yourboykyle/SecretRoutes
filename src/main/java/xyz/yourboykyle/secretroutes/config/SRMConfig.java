//#if FABRIC
package xyz.yourboykyle.secretroutes.config;
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC & christechs
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.diego.configlib.ConfigHandle;
import dev.diego.configlib.ConfigLib;
import dev.diego.configlib.api.Labelled;
import dev.diego.configlib.core.ConfigSpec;
import dev.diego.configlib.core.SpecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ConfigUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.RouteUtils;
import xyz.yourboykyle.secretroutes.utils.SecretSounds;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Every setting the mod has, and the configlib spec that draws them.
 *
 * <p>The fields are deliberately flat and public: the render path reads them straight off
 * {@link #get()} on every frame, {@code /srm debug varset} reaches them by name, and colour
 * profiles are stored on disk keyed by field name. The menu's shape lives in {@link #buildSpec}
 * instead, bound to those fields through getter/setter pairs, so reorganising the GUI never moves
 * a field and never invalidates a saved profile.
 *
 * <p>Option ids match their field names for the same reason - what lands in the JSON is what you
 * can grep for here.
 *
 * <p>Colours are packed ARGB ints rather than {@code java.awt.Color}: that is what configlib's
 * picker edits, and it keeps the render path from boxing a colour per waypoint per frame.
 */
public class SRMConfig {

    /** Where configlib keeps the settings - beside the colour profiles, in the mod's own folder. */
    public static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("SecretRoutes/secretroutesmod.json");

    /** The YACL file this mod used before configlib. Read once, then renamed. See {@link #migrate}. */
    private static final Path LEGACY_FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("SecretRoutes/xyz.yourboykyle.secretroutes.config.json");

    private static final SRMConfig INSTANCE = new SRMConfig();

    // General
    public boolean modEnabled = true;
    public RouteType routeType = RouteType.ROUTE_FOW;
    public boolean renderComplete = false;
    public boolean wholeRoute = false;
    public int visibleRouteSteps = 1;
    public boolean allSteps = false;
    public boolean allSecrets = false;
    public boolean trackPersonalBests = true;
    public boolean sendChatMessages = true;

    // F7 Boss
    public boolean pdRoutesEnabled = false;
    public boolean pdHideAfterPhase2 = true;

    // Visual
    public LineType lineType = LineType.LINES;
    public int width = 5;
    public int lineColor = 0xFFFF0000;
    public boolean renderLinesThroughWalls = true;
    public ParticleType particles = ParticleType.FLAME;
    public double particleDensity = 2.0;
    public float filledBoxAlpha = 0.5f;
    public int tickInterval = 1;
    public boolean playerWaypointLine = false;
    public int playerToSecretLineWidth = 4;
    public int playerToSecretLineColor = 0xFFFF0000;
    public boolean playerToEtherwarp = false;
    public int playerToEtherwarpLineWidth = 4;
    public boolean autoSkipEtherwarps = true;
    public float etherwarpDetectionDistance = 1.5f;
    public boolean etherwarpAimSound = false;
    public SoundType etherwarpAimSoundType = SoundType.NOTE_PLING;
    public float etherwarpAimSoundVolume = 0.35f;
    public float etherwarpAimSoundPitch = 1.6f;
    public int etherwarpAimSoundRearmDelay = 400;
    public boolean useEtherwarpColorForLine = true;
    public int playerToEtherwarpLineColor = 0xFF800080;

    // Colours and toggles
    public boolean renderEtherwarps = true;
    public boolean etherwarpFullBlock = false;
    public float etherwarpBoxLineWidth = 7f;
    public int etherWarp = 0xFF800080;
    public int secondStepEtherWarp = 0xFF5F3D61;

    public boolean renderMines = true;
    public boolean mineFullBlock = false;
    public float mineBoxLineWidth = 5f;
    public int mine = 0x52FFEC00;
    public int secondStepMine = 0xFFB1AD61;

    public boolean renderSuperboom = true;
    public boolean superboomsFullBlock = false;
    public float superboomBoxLineWidth = 5f;
    public int superbooms = 0xFFFF0000;
    public int secondStepSuperbooms = 0xFFA85A5A;

    public boolean renderInteracts = true;
    public boolean interactsFullBlock = false;
    public float leverBoxLineWidth = 5f;
    public int interacts = 0xFF0000FF;
    public int secondStepInteracts = 0xFF495295;

    public boolean renderBonzoStaff = true;
    public boolean bonzoStaffFullBlock = false;
    public float bonzoStaffBoxLineWidth = 5f;
    public int bonzoStaff = 0xFFFFA500;
    public int secondStepBonzoStaff = 0xFFC86E00;

    // Secrets
    public boolean renderSecretsItem = true;
    public float secretBoxLineWidth = 5f;
    public boolean secretsItemFullBlock = false;
    public int secretsItem = 0xFF00FFFF;
    public int secondStepSecretsItem = 0xFF5FA7A7;

    public boolean renderSecretIteract = true;
    public boolean secretsInteractFullBlock = false;
    public int secretsInteract = 0xFF0000FF;
    public int secondStepSecretsInteract = 0xFF495295;

    public boolean renderSecretBat = true;
    public boolean secretsBatFullBlock = false;
    public int secretsBat = 0xFF00FF00;
    public int secondStepSecretsBat = 0xFF5B9A5B;

    // Ender pearls
    public boolean renderEnderpearls = true;
    public boolean enderpearlFullBlock = false;
    public float enderpearlBoxLineWidth = 5f;
    public int enderpearls = 0xFF00FFFF;
    public int secondStepEnderpearls = 0xFF5FA7A7;
    public int pearlLineWidth = 5;
    public int pearlLineColor = 0xFF00FFFF;

    // text
    public boolean startTextToggle = true;
    public TextColor startWaypointColor = TextColor.RED;
    public float startTextSize = 1.0f;

    public boolean exitTextToggle = true;
    public TextColor exitWaypointColor = TextColor.RED;
    public float exitTextSize = 1.0f;

    public boolean etherwarpsTextToggle = false;
    public boolean etherwarpNumberingToggle = false;
    public TextColor etherwarpsWaypointColor = TextColor.DARK_PURPLE;
    public float etherwarpsTextSize = 1.0f;

    public boolean minesTextToggle = false;
    public boolean minesEnumToggle = false;
    public TextColor minesWaypointColor = TextColor.YELLOW;
    public float minesTextSize = 1.0f;

    public boolean interactsTextToggle = true;
    public boolean interactsEnumToggle = false;
    public TextColor interactsWaypointColor = TextColor.BLUE;
    public float interactsTextSize = 1.0f;

    public boolean superboomsTextToggle = true;
    public boolean superboomsEnumToggle = false;
    public TextColor superboomsWaypointColor = TextColor.RED;
    public float superboomsTextSize = 1.0f;

    public boolean bonzoStaffTextToggle = true;
    public boolean bonzoStaffEnumToggle = false;
    public TextColor bonzoStaffWaypointColor = TextColor.RED;
    public float bonzoStaffTextSize = 1.0f;

    public boolean enderpearlTextToggle = true;
    public boolean enderpearlEnumToggle = false;
    public TextColor enderpearlWaypointColor = TextColor.AQUA;
    public float enderpearlTextSize = 1.0f;

    public boolean interactTextToggle = true;
    public TextColor interactWaypointColor = TextColor.BLUE;
    public float interactTextSize = 1.0f;

    public boolean itemTextToggle = true;
    public TextColor itemWaypointColor = TextColor.GREEN;
    public float itemTextSize = 1.0f;

    public boolean batTextToggle = true;
    public TextColor batWaypointColor = TextColor.GREEN;
    public float batTextSize = 1.0f;

    public boolean autoCheckUpdates = true;
    public boolean autoDownload = false;
    public boolean autoUpdateRoutes = false;

    public boolean customSecretSound = false;
    public SoundType customSecretSoundType = SoundType.NOTE_PLING;
    public float customSecretSoundVolume = 1.0f;
    public float customSecretSoundPitch = 1.0f;

    // Recording and Dev
    public int recordingHudX = 10;
    public int recordingHudY = 10;
    public int recordingHudColor = 0xFFFFFFFF;

    // Dev
    public boolean verboseLogging = false;
    public boolean verboseRecording = true;
    public boolean verboseUpdating = true;
    public boolean verboseInfo = false;
    public boolean verboseRendering = false;
    public boolean bridge = false;
    public boolean disableServerChecking = false;
    public boolean forceUpdateDEBUG = false;
    public boolean sendData = true;
    public boolean actionbarInfo = false;
    public boolean verbosePersonalBests = false;

    public String route3ppopkaFileName = "3ppopkaroutes.json";
    public String routeFOWFileName = "fowroutes.json";
    public String copyFileName = "default";
    public int routeNumber = 0;

    /**
     * Registered eagerly, so the saved settings are in the fields before anything reads them.
     *
     * <p>{@code register()} loads the file itself, which is why nothing calls {@code reload()}.
     */
    public static final ConfigHandle<SRMConfig> HANDLER = ConfigLib.builder(Main.MODID)
            .spec(buildSpec(INSTANCE))
            .title("Secret Routes")
            .subtitle("Secret Route Waypoints for Hypixel Skyblock Dungeons")
            .file(FILE)
            .register();

    static {
        migrate();
    }

    public static SRMConfig get() {
        return INSTANCE;
    }

    public static Screen getScreen(Screen parent) {
        return HANDLER.screen(parent);
    }

    // ------------------------------------------------------------------ the menu

    private static ConfigSpec buildSpec(SRMConfig c) {
        SpecBuilder b = SpecBuilder.create().root(c);

        declareUndrawn(b, c);

        // ------------------------------------------------------------ General

        b.category("general", "General", "✦", "Routes, waypoint lines and personal bests.", 0)
                .toggle("modEnabled", "Mod Enabled",
                        "Enables or disables all Secret Routes features",
                        () -> c.modEnabled, v -> c.modEnabled = v)
                .choiceOfEnum("routeType", "Route Type",
                        "A toggle between different routes. FlameOfWar: routes by FlameOfWar, with "
                                + "recorded videos of each route at hypixeldungeons.com. 3ppopka: "
                                + "routes by 3ppopka, with instructions available when using Odin "
                                + "Dungeon Waypoints, found in the Odin Discord server.",
                        RouteType.class,
                        () -> c.routeType == null ? RouteType.ROUTE_FOW : c.routeType,
                        v -> c.routeType = v, false)
                .toggle("renderComplete", "Render Completed Rooms",
                        "Renders secrets even if the room is cleared",
                        () -> c.renderComplete, v -> c.renderComplete = v)
                .toggle("wholeRoute", "Show Whole Route",
                        "Render all steps at once instead of sequential",
                        () -> c.wholeRoute, v -> c.wholeRoute = v)
                .intSlider("visibleRouteSteps", "Visible Route Steps",
                        "How many route steps to show at once when Show Whole Route is off",
                        () -> c.visibleRouteSteps, v -> c.visibleRouteSteps = v, 1, 5)
                .toggle("allSecrets", "Show All Secrets",
                        "Highlight all secrets in the room, not just the route",
                        () -> c.allSecrets, v -> c.allSecrets = v)
                .action("updateRoutes", "Update Routes",
                        "Updates to the latest route files from GitHub, overwriting the old routes",
                        "Download", false, RouteUtils::checkRoutesFiles);

        b.section("Predev Routes",
                        "Configure routes for doing predev. You should watch a tutorial, as important "
                                + "information is not displayed in the route", 0)
                .toggle("pdRoutesEnabled", "Enable Predev Routes",
                        "Master toggle for showing predev routes during the F7 Boss fight.",
                        () -> c.pdRoutesEnabled, v -> c.pdRoutesEnabled = v)
                .toggle("pdHideAfterPhase2", "Hide After Storm",
                        "Hide predev routes when Storm ends.",
                        () -> c.pdHideAfterPhase2, v -> c.pdHideAfterPhase2 = v);

        b.section("Line to Etherwarp",
                        "Controls the line from your crosshair to the next Etherwarp waypoint", 0)
                .toggle("playerToEtherwarp", "Enabled",
                        "Draws a line from your crosshair to the next Etherwarp waypoint",
                        () -> c.playerToEtherwarp, v -> c.playerToEtherwarp = v)
                .intSlider("playerToEtherwarpLineWidth", "Line Width",
                        "Controls the thickness of the line to the next Etherwarp",
                        () -> c.playerToEtherwarpLineWidth, v -> c.playerToEtherwarpLineWidth = v, 1, 10)
                .toggle("autoSkipEtherwarps", "Auto Skip Etherwarps",
                        "Automatically skips to the next etherwarp when standing near an etherwarp "
                                + "further in the route, so the line never stays stuck on one you "
                                + "have already passed",
                        () -> c.autoSkipEtherwarps, v -> c.autoSkipEtherwarps = v)
                .slider("etherwarpDetectionDistance", "Detection Distance",
                        "How close you need to be to an Etherwarp waypoint for it to count as reached",
                        () -> (double) c.etherwarpDetectionDistance,
                        v -> c.etherwarpDetectionDistance = v.floatValue(),
                        0.5, 5.0, 0.5, 1, "", false)
                .toggle("useEtherwarpColorForLine", "Use Etherwarp Color",
                        "Uses the colour set in Components -> Etherwarps as the colour for the line",
                        () -> c.useEtherwarpColorForLine, v -> c.useEtherwarpColorForLine = v)
                .color("playerToEtherwarpLineColor", "Line Color",
                        "Sets the line colour when Use Etherwarp Color is disabled",
                        () -> c.playerToEtherwarpLineColor, v -> c.playerToEtherwarpLineColor = v, true);

        b.section("Etherwarp Aim Sound",
                        "Controls the sound played when aiming directly at the current Etherwarp "
                                + "waypoint", 0)
                .toggle("etherwarpAimSound", "Enabled",
                        "Plays a sound when your crosshair acquires the current Etherwarp waypoint "
                                + "while sneaking",
                        () -> c.etherwarpAimSound, v -> c.etherwarpAimSound = v)
                .choiceOfEnum("etherwarpAimSoundType", "Aim Sound",
                        "Selects the sound played when acquiring an Etherwarp target", SoundType.class,
                        () -> c.etherwarpAimSoundType == null
                                ? SoundType.NOTE_PLING : c.etherwarpAimSoundType,
                        v -> c.etherwarpAimSoundType = v, false)
                .slider("etherwarpAimSoundVolume", "Aim Sound Volume",
                        "Controls the volume of the Etherwarp aim sound",
                        () -> (double) c.etherwarpAimSoundVolume,
                        v -> c.etherwarpAimSoundVolume = v.floatValue(), 0.0, 5.0, 0.1, 1, "", false)
                .slider("etherwarpAimSoundPitch", "Aim Sound Pitch",
                        "Controls the pitch of the Etherwarp aim sound",
                        () -> (double) c.etherwarpAimSoundPitch,
                        v -> c.etherwarpAimSoundPitch = v.floatValue(), 0.5, 2.0, 0.1, 1, "", false)
                .slider("etherwarpAimSoundRearmDelay", "Rearm Delay",
                        "Controls how long you must look away or stop sneaking before the aim sound "
                                + "can play again",
                        () -> (double) c.etherwarpAimSoundRearmDelay,
                        v -> c.etherwarpAimSoundRearmDelay = v.intValue(),
                        0, 1000, 50, 0, " ms", false)
                .action("previewAimSound", "Preview Aim Sound",
                        "Plays the selected aim sound using the current volume and pitch",
                        "Play", false, () -> SecretSounds.preview(
                                c.etherwarpAimSoundType == null
                                        ? SoundType.NOTE_PLING : c.etherwarpAimSoundType,
                                c.etherwarpAimSoundVolume, c.etherwarpAimSoundPitch));

        b.section("Line to Secret", "Controls the line from your crosshair to the next secret", 0)
                .toggle("playerWaypointLine", "Enabled",
                        "Draws a line from your crosshair to the next secret",
                        () -> c.playerWaypointLine, v -> c.playerWaypointLine = v)
                .intSlider("playerToSecretLineWidth", "Line Width",
                        "Controls the thickness of the line to the next secret",
                        () -> c.playerToSecretLineWidth, v -> c.playerToSecretLineWidth = v, 1, 10)
                .color("playerToSecretLineColor", "Line Color",
                        "Sets the colour of the line to the next secret",
                        () -> c.playerToSecretLineColor, v -> c.playerToSecretLineColor = v, true);

        b.section("Personal Bests",
                        "Tracks and reports your fastest completion time for each room", 0)
                .toggle("trackPersonalBests", "Track Personal Bests",
                        "Tracks your fastest completion time for each room",
                        () -> c.trackPersonalBests, v -> c.trackPersonalBests = v)
                .toggle("sendChatMessages", "Send Chat Messages",
                        "Sends a chat message when you set a new personal best",
                        () -> c.sendChatMessages, v -> c.sendChatMessages = v);

        // ------------------------------------------------------------ Visuals

        b.category("visuals", "Visuals", "◩", "Lines, particles, labels and colour profiles.", 1)
                .toggle("renderLinesThroughWalls", "See Through Walls",
                        "Renders waypoints through walls",
                        () -> c.renderLinesThroughWalls, v -> c.renderLinesThroughWalls = v);

        b.section("Movement Lines",
                        "Controls the lines connecting movement points throughout a route", 0)
                .choiceOfEnum("lineType", "Line Style",
                        "Chooses between different styles of your movement path", LineType.class,
                        () -> c.lineType == null ? LineType.LINES : c.lineType,
                        v -> c.lineType = v, true)
                .intSlider("width", "Line Width",
                        "Controls the thickness of solid movement lines",
                        () -> c.width, v -> c.width = v, 1, 10)
                .color("lineColor", "Line Color", "Sets the colour of solid movement lines",
                        () -> c.lineColor, v -> c.lineColor = v, true);

        b.section("Particles",
                        "Visual settings for the particles used when the movement line style is "
                                + "set to Particles", 0)
                .choiceOfEnum("particles", "Type",
                        "Selects the particle used for particle-based movement paths",
                        ParticleType.class,
                        () -> c.particles == null ? ParticleType.FLAME : c.particles,
                        v -> c.particles = v, false)
                .slider("particleDensity", "Density",
                        "Controls the spacing of particles along movement paths",
                        () -> c.particleDensity, v -> c.particleDensity = v, 0.1, 10.0, 0.1, 1, "", false);

        b.section("Color Profiles",
                        "Colour profiles to import, export and switch between, so the visuals can be "
                                + "changed without redoing every setting", 0)
                .text("copyFileName", "Profile Name", "The name that saving writes to",
                        () -> c.copyFileName, v -> c.copyFileName = v, "default", 64, 220)
                .action("saveProfile", "Save Current Profile",
                        "Saves the current colours and label settings under the name above",
                        "Save", false, () -> ConfigUtils.writeColorConfig(c.copyFileName))
                .picker("loadProfile", "Load Profile",
                        "Loads a saved profile. The open menu updates in place, so the change is "
                                + "visible straight away",
                        SRMConfig::colorProfileNames, () -> "", ConfigUtils::loadColorConfig,
                        name -> name.equalsIgnoreCase("default") ? "Restore to Default" : name,
                        "Choose a profile...");

        b.section("Etherwarps Text", "Controls labels displayed on Etherwarp waypoints", 0)
                .toggle("etherwarpsTextToggle", "Show", "Shows labels on Etherwarp waypoints",
                        () -> c.etherwarpsTextToggle, v -> c.etherwarpsTextToggle = v)
                .toggle("etherwarpNumberingToggle", "Numbering",
                        "Adds a sequence number to each Etherwarp label",
                        () -> c.etherwarpNumberingToggle, v -> c.etherwarpNumberingToggle = v)
                .choiceOfEnum("etherwarpsWaypointColor", "Color",
                        "Sets the colour of Etherwarp labels", TextColor.class,
                        () -> c.etherwarpsWaypointColor == null
                                ? TextColor.DARK_PURPLE : c.etherwarpsWaypointColor,
                        v -> c.etherwarpsWaypointColor = v, false)
                .slider("etherwarpsTextSize", "Size", "Controls the size of Etherwarp labels",
                        () -> (double) c.etherwarpsTextSize,
                        v -> c.etherwarpsTextSize = v.floatValue(), 0.1, 5.0, 0.1, 1, "", false);

        b.section("Interacts Text", "Controls labels displayed on lever and interact waypoints", 0)
                .toggle("interactsTextToggle", "Show", "Shows labels on interact waypoints",
                        () -> c.interactsTextToggle, v -> c.interactsTextToggle = v)
                .toggle("interactsEnumToggle", "Numbering",
                        "Adds a sequence number to each interact label",
                        () -> c.interactsEnumToggle, v -> c.interactsEnumToggle = v)
                .choiceOfEnum("interactsWaypointColor", "Color",
                        "Sets the colour of interact labels", TextColor.class,
                        () -> c.interactsWaypointColor == null
                                ? TextColor.BLUE : c.interactsWaypointColor,
                        v -> c.interactsWaypointColor = v, false)
                .slider("interactsTextSize", "Size", "Controls the size of interact labels",
                        () -> (double) c.interactsTextSize,
                        v -> c.interactsTextSize = v.floatValue(), 0.1, 5.0, 0.1, 1, "", false);

        b.section("Bonzo Staffs Text", "Controls labels displayed on Bonzo Staff waypoints", 0)
                .toggle("bonzoStaffTextToggle", "Show", "Shows labels on Bonzo Staff waypoints",
                        () -> c.bonzoStaffTextToggle, v -> c.bonzoStaffTextToggle = v)
                .toggle("bonzoStaffEnumToggle", "Numbering",
                        "Adds a sequence number to each Bonzo Staff label",
                        () -> c.bonzoStaffEnumToggle, v -> c.bonzoStaffEnumToggle = v)
                .choiceOfEnum("bonzoStaffWaypointColor", "Color",
                        "Sets the colour of Bonzo Staff labels", TextColor.class,
                        () -> c.bonzoStaffWaypointColor == null
                                ? TextColor.RED : c.bonzoStaffWaypointColor,
                        v -> c.bonzoStaffWaypointColor = v, false)
                .slider("bonzoStaffTextSize", "Size", "Controls the size of Bonzo Staff labels",
                        () -> (double) c.bonzoStaffTextSize,
                        v -> c.bonzoStaffTextSize = v.floatValue(), 0.1, 5.0, 0.1, 1, "", false);

        b.section("Start/Exit", "Controls the Start and Exit labels of a route", 0)
                .toggle("startTextToggle", "Show Start", "Shows the Start label",
                        () -> c.startTextToggle, v -> c.startTextToggle = v)
                .choiceOfEnum("startWaypointColor", "Start Color",
                        "Sets the colour of the Start label", TextColor.class,
                        () -> c.startWaypointColor == null ? TextColor.RED : c.startWaypointColor,
                        v -> c.startWaypointColor = v, false)
                .toggle("exitTextToggle", "Show Exit", "Shows the Exit label",
                        () -> c.exitTextToggle, v -> c.exitTextToggle = v)
                .choiceOfEnum("exitWaypointColor", "Exit Color",
                        "Sets the colour of the Exit label", TextColor.class,
                        () -> c.exitWaypointColor == null ? TextColor.RED : c.exitWaypointColor,
                        v -> c.exitWaypointColor = v, false);

        // ------------------------------------------------------------ Components

        b.category("components", "Components", "▦",
                "Per-waypoint colours, box styles and the secret sound.", 2);

        b.section("Etherwarps", "Etherwarp waypoints", 0)
                .toggle("renderEtherwarps", "Enabled", "Renders Etherwarp waypoints",
                        () -> c.renderEtherwarps, v -> c.renderEtherwarps = v)
                .color("etherWarp", "Color", "The colour of the current Etherwarp waypoint",
                        () -> c.etherWarp, v -> c.etherWarp = v, true)
                .color("secondStepEtherWarp", "Second Step Color",
                        "The colour of Etherwarp waypoints further along the route",
                        () -> c.secondStepEtherWarp, v -> c.secondStepEtherWarp = v, true)
                .toggle("etherwarpFullBlock", "Full Block",
                        "Draws a filled box instead of an outline",
                        () -> c.etherwarpFullBlock, v -> c.etherwarpFullBlock = v)
                .slider("etherwarpBoxLineWidth", "Box Line Width", "Outline thickness",
                        () -> (double) c.etherwarpBoxLineWidth,
                        v -> c.etherwarpBoxLineWidth = v.floatValue(), 1.0, 10.0, 0.5, 1, "", false);

        b.section("Secrets", "Item, interact and bat secrets", 0)
                .toggle("renderSecretsItem", "Items", "Renders item secrets",
                        () -> c.renderSecretsItem, v -> c.renderSecretsItem = v)
                .color("secretsItem", "Item Color", "The colour of the current item secret",
                        () -> c.secretsItem, v -> c.secretsItem = v, true)
                .color("secondStepSecretsItem", "Item Second Step Color",
                        "The colour of item secrets further along the route",
                        () -> c.secondStepSecretsItem, v -> c.secondStepSecretsItem = v, true)
                .toggle("renderSecretIteract", "Interacts", "Renders interact secrets",
                        () -> c.renderSecretIteract, v -> c.renderSecretIteract = v)
                .color("secretsInteract", "Interact Color",
                        "The colour of the current interact secret",
                        () -> c.secretsInteract, v -> c.secretsInteract = v, true)
                .color("secondStepSecretsInteract", "Interact Second Step Color",
                        "The colour of interact secrets further along the route",
                        () -> c.secondStepSecretsInteract, v -> c.secondStepSecretsInteract = v, true)
                .toggle("renderSecretBat", "Bats", "Renders bat secrets",
                        () -> c.renderSecretBat, v -> c.renderSecretBat = v)
                .color("secretsBat", "Bat Color", "The colour of the current bat secret",
                        () -> c.secretsBat, v -> c.secretsBat = v, true)
                .color("secondStepSecretsBat", "Bat Second Step Color",
                        "The colour of bat secrets further along the route",
                        () -> c.secondStepSecretsBat, v -> c.secondStepSecretsBat = v, true)
                .slider("secretBoxLineWidth", "Box Line Width", "Outline thickness",
                        () -> (double) c.secretBoxLineWidth,
                        v -> c.secretBoxLineWidth = v.floatValue(), 1.0, 10.0, 0.5, 1, "", false);

        b.section("Mines", "Mine waypoints", 0)
                .toggle("renderMines", "Enabled", "Renders mine waypoints",
                        () -> c.renderMines, v -> c.renderMines = v)
                .color("mine", "Color", "The colour of the current mine waypoint",
                        () -> c.mine, v -> c.mine = v, true)
                .color("secondStepMine", "Second Step Color",
                        "The colour of mine waypoints further along the route",
                        () -> c.secondStepMine, v -> c.secondStepMine = v, true)
                .toggle("mineFullBlock", "Full Block", "Draws a filled box instead of an outline",
                        () -> c.mineFullBlock, v -> c.mineFullBlock = v)
                .slider("mineBoxLineWidth", "Box Line Width", "Outline thickness",
                        () -> (double) c.mineBoxLineWidth,
                        v -> c.mineBoxLineWidth = v.floatValue(), 1.0, 10.0, 0.5, 1, "", false);

        b.section("Levers", "Lever waypoints", 0)
                .toggle("renderInteracts", "Enabled", "Renders lever waypoints",
                        () -> c.renderInteracts, v -> c.renderInteracts = v)
                .color("interacts", "Color", "The colour of the current lever waypoint",
                        () -> c.interacts, v -> c.interacts = v, true)
                .color("secondStepInteracts", "Second Step Color",
                        "The colour of lever waypoints further along the route",
                        () -> c.secondStepInteracts, v -> c.secondStepInteracts = v, true)
                .toggle("interactsFullBlock", "Full Block",
                        "Draws a filled box instead of an outline",
                        () -> c.interactsFullBlock, v -> c.interactsFullBlock = v)
                .slider("leverBoxLineWidth", "Box Line Width", "Outline thickness",
                        () -> (double) c.leverBoxLineWidth,
                        v -> c.leverBoxLineWidth = v.floatValue(), 1.0, 10.0, 0.5, 1, "", false);

        b.section("Superbooms", "Superboom wall waypoints", 0)
                .toggle("renderSuperboom", "Enabled", "Renders superboom waypoints",
                        () -> c.renderSuperboom, v -> c.renderSuperboom = v)
                .color("superbooms", "Color", "The colour of the current superboom waypoint",
                        () -> c.superbooms, v -> c.superbooms = v, true)
                .color("secondStepSuperbooms", "Second Step Color",
                        "The colour of superboom waypoints further along the route",
                        () -> c.secondStepSuperbooms, v -> c.secondStepSuperbooms = v, true)
                .toggle("superboomsFullBlock", "Full Block",
                        "Draws a filled box instead of an outline",
                        () -> c.superboomsFullBlock, v -> c.superboomsFullBlock = v)
                .slider("superboomBoxLineWidth", "Box Line Width", "Outline thickness",
                        () -> (double) c.superboomBoxLineWidth,
                        v -> c.superboomBoxLineWidth = v.floatValue(), 1.0, 10.0, 0.5, 1, "", false);

        b.section("Bonzo Staffs", "Bonzo Staff waypoints", 0)
                .toggle("renderBonzoStaff", "Enabled", "Renders Bonzo Staff waypoints",
                        () -> c.renderBonzoStaff, v -> c.renderBonzoStaff = v)
                .color("bonzoStaff", "Color", "The colour of the current Bonzo Staff waypoint",
                        () -> c.bonzoStaff, v -> c.bonzoStaff = v, true)
                .color("secondStepBonzoStaff", "Second Step Color",
                        "The colour of Bonzo Staff waypoints further along the route",
                        () -> c.secondStepBonzoStaff, v -> c.secondStepBonzoStaff = v, true)
                .toggle("bonzoStaffFullBlock", "Full Block",
                        "Draws a filled box instead of an outline",
                        () -> c.bonzoStaffFullBlock, v -> c.bonzoStaffFullBlock = v)
                .slider("bonzoStaffBoxLineWidth", "Box Line Width", "Outline thickness",
                        () -> (double) c.bonzoStaffBoxLineWidth,
                        v -> c.bonzoStaffBoxLineWidth = v.floatValue(), 1.0, 10.0, 0.5, 1, "", false);

        b.section("Enderpearls", "Enderpearl waypoints and the line between them", 0)
                .toggle("renderEnderpearls", "Enabled", "Renders enderpearl waypoints",
                        () -> c.renderEnderpearls, v -> c.renderEnderpearls = v)
                .color("enderpearls", "Color", "The colour of the current enderpearl waypoint",
                        () -> c.enderpearls, v -> c.enderpearls = v, true)
                .color("secondStepEnderpearls", "Second Step Color",
                        "The colour of enderpearl waypoints further along the route",
                        () -> c.secondStepEnderpearls, v -> c.secondStepEnderpearls = v, true)
                .toggle("enderpearlFullBlock", "Full Block",
                        "Draws a filled box instead of an outline",
                        () -> c.enderpearlFullBlock, v -> c.enderpearlFullBlock = v)
                .slider("enderpearlBoxLineWidth", "Box Line Width", "Outline thickness",
                        () -> (double) c.enderpearlBoxLineWidth,
                        v -> c.enderpearlBoxLineWidth = v.floatValue(), 1.0, 10.0, 0.5, 1, "", false)
                .color("pearlLineColor", "Line Color",
                        "The colour of the line between enderpearl waypoints",
                        () -> c.pearlLineColor, v -> c.pearlLineColor = v, true)
                .intSlider("pearlLineWidth", "Line Width",
                        "The thickness of the line between enderpearl waypoints",
                        () -> c.pearlLineWidth, v -> c.pearlLineWidth = v, 1, 10);

        b.section("Custom Secret Sound", "A sound played whenever a secret is collected", 0)
                .toggle("customSecretSound", "Enabled",
                        "Plays the selected sound when a secret is collected",
                        () -> c.customSecretSound, v -> c.customSecretSound = v)
                .choiceOfEnum("customSecretSoundType", "Sound", "Selects which sound is played",
                        SoundType.class,
                        () -> c.customSecretSoundType == null
                                ? SoundType.NOTE_PLING : c.customSecretSoundType,
                        v -> c.customSecretSoundType = v, false)
                .slider("customSecretSoundVolume", "Volume",
                        "Controls the volume of the secret sound",
                        () -> (double) c.customSecretSoundVolume,
                        v -> c.customSecretSoundVolume = v.floatValue(), 0.0, 5.0, 0.1, 1, "", false)
                .slider("customSecretSoundPitch", "Pitch", "Controls the pitch of the secret sound",
                        () -> (double) c.customSecretSoundPitch,
                        v -> c.customSecretSoundPitch = v.floatValue(), 0.5, 2.0, 0.1, 1, "", false)
                .action("previewSecretSound", "Preview Sound",
                        "Plays the selected sound using the current volume and pitch",
                        "Play", false, () -> SecretSounds.preview(
                                c.customSecretSoundType == null
                                        ? SoundType.NOTE_PLING : c.customSecretSoundType,
                                c.customSecretSoundVolume, c.customSecretSoundPitch));

        return b.build();
    }

    /**
     * Settings that are persisted but never drawn.
     *
     * <p>The menu does not expose these, but the render path, {@code /srm debug varset}, the update
     * checker and the colour profiles all read and write them, so they still have to survive a
     * restart. Declared before any category so their keys stay unqualified.
     */
    private static void declareUndrawn(SpecBuilder b, SRMConfig c) {
        b.hidden("allSteps", boolean.class, () -> c.allSteps, v -> c.allSteps = (boolean) v);
        b.hidden("filledBoxAlpha", float.class, () -> c.filledBoxAlpha, v -> c.filledBoxAlpha = ((Number) v).floatValue());
        b.hidden("tickInterval", int.class, () -> c.tickInterval, v -> c.tickInterval = ((Number) v).intValue());
        b.hidden("routeNumber", int.class, () -> c.routeNumber, v -> c.routeNumber = ((Number) v).intValue());

        b.hidden("startTextSize", float.class, () -> c.startTextSize, v -> c.startTextSize = ((Number) v).floatValue());
        b.hidden("exitTextSize", float.class, () -> c.exitTextSize, v -> c.exitTextSize = ((Number) v).floatValue());

        b.hidden("minesTextToggle", boolean.class, () -> c.minesTextToggle, v -> c.minesTextToggle = (boolean) v);
        b.hidden("minesEnumToggle", boolean.class, () -> c.minesEnumToggle, v -> c.minesEnumToggle = (boolean) v);
        b.hidden("minesWaypointColor", TextColor.class, () -> c.minesWaypointColor, v -> c.minesWaypointColor = (TextColor) v);
        b.hidden("minesTextSize", float.class, () -> c.minesTextSize, v -> c.minesTextSize = ((Number) v).floatValue());

        b.hidden("superboomsTextToggle", boolean.class, () -> c.superboomsTextToggle, v -> c.superboomsTextToggle = (boolean) v);
        b.hidden("superboomsEnumToggle", boolean.class, () -> c.superboomsEnumToggle, v -> c.superboomsEnumToggle = (boolean) v);
        b.hidden("superboomsWaypointColor", TextColor.class, () -> c.superboomsWaypointColor, v -> c.superboomsWaypointColor = (TextColor) v);
        b.hidden("superboomsTextSize", float.class, () -> c.superboomsTextSize, v -> c.superboomsTextSize = ((Number) v).floatValue());

        b.hidden("enderpearlTextToggle", boolean.class, () -> c.enderpearlTextToggle, v -> c.enderpearlTextToggle = (boolean) v);
        b.hidden("enderpearlEnumToggle", boolean.class, () -> c.enderpearlEnumToggle, v -> c.enderpearlEnumToggle = (boolean) v);
        b.hidden("enderpearlWaypointColor", TextColor.class, () -> c.enderpearlWaypointColor, v -> c.enderpearlWaypointColor = (TextColor) v);
        b.hidden("enderpearlTextSize", float.class, () -> c.enderpearlTextSize, v -> c.enderpearlTextSize = ((Number) v).floatValue());

        b.hidden("interactTextToggle", boolean.class, () -> c.interactTextToggle, v -> c.interactTextToggle = (boolean) v);
        b.hidden("interactWaypointColor", TextColor.class, () -> c.interactWaypointColor, v -> c.interactWaypointColor = (TextColor) v);
        b.hidden("interactTextSize", float.class, () -> c.interactTextSize, v -> c.interactTextSize = ((Number) v).floatValue());

        b.hidden("itemTextToggle", boolean.class, () -> c.itemTextToggle, v -> c.itemTextToggle = (boolean) v);
        b.hidden("itemWaypointColor", TextColor.class, () -> c.itemWaypointColor, v -> c.itemWaypointColor = (TextColor) v);
        b.hidden("itemTextSize", float.class, () -> c.itemTextSize, v -> c.itemTextSize = ((Number) v).floatValue());

        b.hidden("batTextToggle", boolean.class, () -> c.batTextToggle, v -> c.batTextToggle = (boolean) v);
        b.hidden("batWaypointColor", TextColor.class, () -> c.batWaypointColor, v -> c.batWaypointColor = (TextColor) v);
        b.hidden("batTextSize", float.class, () -> c.batTextSize, v -> c.batTextSize = ((Number) v).floatValue());

        b.hidden("secretsItemFullBlock", boolean.class, () -> c.secretsItemFullBlock, v -> c.secretsItemFullBlock = (boolean) v);
        b.hidden("secretsInteractFullBlock", boolean.class, () -> c.secretsInteractFullBlock, v -> c.secretsInteractFullBlock = (boolean) v);
        b.hidden("secretsBatFullBlock", boolean.class, () -> c.secretsBatFullBlock, v -> c.secretsBatFullBlock = (boolean) v);

        b.hidden("autoCheckUpdates", boolean.class, () -> c.autoCheckUpdates, v -> c.autoCheckUpdates = (boolean) v);
        b.hidden("autoDownload", boolean.class, () -> c.autoDownload, v -> c.autoDownload = (boolean) v);
        b.hidden("autoUpdateRoutes", boolean.class, () -> c.autoUpdateRoutes, v -> c.autoUpdateRoutes = (boolean) v);

        b.hidden("recordingHudX", int.class, () -> c.recordingHudX, v -> c.recordingHudX = ((Number) v).intValue());
        b.hidden("recordingHudY", int.class, () -> c.recordingHudY, v -> c.recordingHudY = ((Number) v).intValue());
        b.hidden("recordingHudColor", int.class, () -> c.recordingHudColor, v -> c.recordingHudColor = ((Number) v).intValue());

        b.hidden("verboseLogging", boolean.class, () -> c.verboseLogging, v -> c.verboseLogging = (boolean) v);
        b.hidden("verboseRecording", boolean.class, () -> c.verboseRecording, v -> c.verboseRecording = (boolean) v);
        b.hidden("verboseUpdating", boolean.class, () -> c.verboseUpdating, v -> c.verboseUpdating = (boolean) v);
        b.hidden("verboseInfo", boolean.class, () -> c.verboseInfo, v -> c.verboseInfo = (boolean) v);
        b.hidden("verboseRendering", boolean.class, () -> c.verboseRendering, v -> c.verboseRendering = (boolean) v);
        b.hidden("verbosePersonalBests", boolean.class, () -> c.verbosePersonalBests, v -> c.verbosePersonalBests = (boolean) v);
        b.hidden("bridge", boolean.class, () -> c.bridge, v -> c.bridge = (boolean) v);
        b.hidden("disableServerChecking", boolean.class, () -> c.disableServerChecking, v -> c.disableServerChecking = (boolean) v);
        b.hidden("forceUpdateDEBUG", boolean.class, () -> c.forceUpdateDEBUG, v -> c.forceUpdateDEBUG = (boolean) v);
        b.hidden("sendData", boolean.class, () -> c.sendData, v -> c.sendData = (boolean) v);
        b.hidden("actionbarInfo", boolean.class, () -> c.actionbarInfo, v -> c.actionbarInfo = (boolean) v);
    }

    /** Every saved colour profile, re-listed each time the picker opens. */
    private static List<String> colorProfileNames() {
        File[] files = ConfigUtils.COLOR_PROFILE_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) {
            return List.of();
        }
        List<String> names = new ArrayList<>(files.length);
        for (File file : files) {
            String name = file.getName();
            names.add(name.substring(0, name.length() - ".json".length()));
        }
        names.sort(String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    // ------------------------------------------------------------------ migration

    /**
     * Carries a pre-configlib YACL config across, once.
     *
     * <p>The field names never changed, so this is a name-for-name copy; only colours need real
     * work, since YACL wrote them as objects where configlib wants a packed int. The old file is
     * renamed rather than deleted, so a mistake here stays recoverable by hand.
     */
    private static void migrate() {
        if (Files.exists(FILE) || !Files.exists(LEGACY_FILE)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(LEGACY_FILE)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (!parsed.isJsonObject()) {
                return;
            }
            JsonObject old = parsed.getAsJsonObject();
            int carried = 0;
            for (String key : old.keySet()) {
                try {
                    if (applyLegacy(SRMConfig.class.getField(key), old.get(key))) {
                        carried++;
                    }
                } catch (NoSuchFieldException ignored) {
                    // A setting that no longer exists. Dropping it is the point of a migration.
                } catch (Exception e) {
                    LogUtils.error(new IOException("Could not migrate setting '" + key + "'", e));
                }
            }
            HANDLER.save();
            Files.move(LEGACY_FILE,
                    LEGACY_FILE.resolveSibling(LEGACY_FILE.getFileName() + ".migrated"));
            LogUtils.info("§bMigrated " + carried + " settings from the old config to configlib");
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

    private static boolean applyLegacy(Field field, JsonElement value) throws IllegalAccessException {
        if (value == null || value.isJsonNull()) {
            return false;
        }
        Class<?> type = field.getType();
        if (type == boolean.class) {
            field.setBoolean(INSTANCE, value.getAsBoolean());
        } else if (type == int.class) {
            // Colours were java.awt.Color and are ints now; every other int was already a number.
            field.setInt(INSTANCE, value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()
                    ? value.getAsInt()
                    : ConfigUtils.parseColorArgb(value));
        } else if (type == float.class) {
            field.setFloat(INSTANCE, value.getAsFloat());
        } else if (type == double.class) {
            field.setDouble(INSTANCE, value.getAsDouble());
        } else if (type == String.class) {
            field.set(INSTANCE, value.getAsString());
        } else if (type.isEnum()) {
            String name = value.getAsString();
            Object constant = Arrays.stream(type.getEnumConstants())
                    .filter(k -> ((Enum<?>) k).name().equals(name))
                    .findFirst().orElse(null);
            if (constant == null) {
                return false;
            }
            field.set(INSTANCE, constant);
        } else {
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------------ enums

    public enum LineType implements Labelled {
        PARTICLES("Particles"), LINES("Lines"), NONE("None");

        private final String label;

        LineType(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum RouteType implements Labelled {
        ROUTE_3ppopka("3ppopka"), ROUTE_FOW("FlameOfWar");

        private final String label;

        RouteType(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum SoundType implements Labelled {
        MOB_BLAZE_HIT("Blaze Hit", "entity.blaze.hurt"),
        FIRE_IGNITE("Fire Ignite", "item.flintandsteel.use"),
        RANDOM_ORB("Experience Orb", "entity.experience_orb.pickup"),
        RANDOM_BREAK("Item Break", "entity.item.break"),
        MOB_GUARDIAN_LAND_HIT("Guardian Land Hit", "entity.guardian.hurt_land"),
        NOTE_PLING("Note Pling", "block.note_block.pling"),
        ZYRA_MEOW("Zyra Meow", "secretroutesmod:zyra.meow");

        private final String label;
        public final String soundId;

        SoundType(String label, String soundId) {
            this.label = label;
            this.soundId = soundId;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum TextColor implements Labelled {
        BLACK("Black", ChatFormatting.BLACK), DARK_BLUE("Dark Blue", ChatFormatting.DARK_BLUE),
        DARK_GREEN("Dark Green", ChatFormatting.DARK_GREEN), DARK_AQUA("Dark Aqua", ChatFormatting.DARK_AQUA),
        DARK_RED("Dark Red", ChatFormatting.DARK_RED), DARK_PURPLE("Dark Purple", ChatFormatting.DARK_PURPLE),
        GOLD("Gold", ChatFormatting.GOLD), GRAY("Gray", ChatFormatting.GRAY),
        DARK_GRAY("Dark Gray", ChatFormatting.DARK_GRAY), BLUE("Blue", ChatFormatting.BLUE),
        GREEN("Green", ChatFormatting.GREEN), AQUA("Aqua", ChatFormatting.AQUA),
        RED("Red", ChatFormatting.RED), LIGHT_PURPLE("Light Purple", ChatFormatting.LIGHT_PURPLE),
        YELLOW("Yellow", ChatFormatting.YELLOW), WHITE("White", ChatFormatting.WHITE);

        public final ChatFormatting formatting;
        private final String label;

        TextColor(String label, ChatFormatting formatting) {
            this.label = label;
            this.formatting = formatting;
        }

        @Override
        public String label() {
            return label;
        }
    }

    public enum ParticleType implements Labelled {
        EXPLOSION_NORMAL("Explosion Normal"), EXPLOSION_LARGE("Explosion Large"), EXPLOSION_HUGE("Explosion Huge"),
        FIREWORKS_SPARK("Fireworks Spark"), BUBBLE("Bubble"), WATER_SPLASH("Water Splash"), WATER_WAKE("Water Wake"),
        SUSPENDED("Suspended"), SUSPENDED_DEPTH("Suspended Depth"), CRIT("Crit"), MAGIC_CRIT("Magic Crit"),
        SMOKE_NORMAL("Smoke Normal"), SMOKE_LARGE("Smoke Large"), SPELL("Spell"), INSTANT_SPELL("Instant Spell"),
        MOB_SPELL("Mob Spell"), MOB_SPELL_AMBIENT("Mob Spell Ambient"), WITCH_MAGIC("Witch Magic"),
        DRIP_WATER("Drip Water"), DRIP_LAVA("Drip Lava"), VILLAGER_ANGRY("Villager Angry"),
        VILLAGER_HAPPY("Villager Happy"), TOWN_AURA("Town Aura"), NOTE("Note"), PORTAL("Portal"),
        ENCHANTMENT_TABLE("Enchantment Table"), FLAME("Flame"), LAVA("Lava"), FOOTSTEP("Footstep"),
        CLOUD("Cloud"), REDSTONE("Redstone"), SNOWBALL("Snowball"), SNOW_SHOVEL("Snow Shovel"),
        SLIME("Slime"), HEART("Heart"), BARRIER("Barrier"), WATER_DROP("Water Drop"),
        ITEM_TAKE("Item Take"), MOB_APPEARANCE("Mob Appearance");

        private final String label;

        ParticleType(String label) {
            this.label = label;
        }

        @Override
        public String label() {
            return label;
        }
    }
}
//#endif
