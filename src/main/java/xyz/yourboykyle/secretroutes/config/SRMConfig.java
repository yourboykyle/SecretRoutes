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

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;

//?if >1.21.11
import dev.isxander.yacl3.gui.utils.GuiUtils;
//?if 1.21.11
//import net.minecraft.client.Minecraft;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ConfigUtils;
import xyz.yourboykyle.secretroutes.utils.RouteUtils;
import xyz.yourboykyle.secretroutes.utils.SecretSounds;

import java.awt.*;
import java.io.File;

public class SRMConfig {

    public static final ConfigClassHandler<SRMConfig> HANDLER = ConfigClassHandler.createBuilder(SRMConfig.class)
            .id(Identifier.fromNamespaceAndPath(Main.MODID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("SecretRoutes/xyz.yourboykyle.secretroutes.config.json"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    // General
    @SerialEntry
    public boolean modEnabled = true;
    @SerialEntry
    public RouteType routeType = RouteType.ROUTE_FOW;
    @SerialEntry
    public boolean renderComplete = false;
    @SerialEntry
    public boolean wholeRoute = false;
    @SerialEntry
    public int visibleRouteSteps = 1;
    @SerialEntry
    public boolean allSteps = false;
    @SerialEntry
    public boolean allSecrets = false;
    @SerialEntry
    public boolean trackPersonalBests = true;
    @SerialEntry
    public boolean sendChatMessages = true;

    // F7 Boss
    @SerialEntry
    public boolean pdRoutesEnabled = false;
    @SerialEntry
    public boolean pdHideAfterPhase2 = true;

    // Visual
    @SerialEntry
    public LineType lineType = LineType.LINES;
    @SerialEntry
    public int width = 5;
    @SerialEntry
    public Color lineColor = new Color(255, 0, 0);
    @SerialEntry
    public boolean renderLinesThroughWalls = true;
    @SerialEntry
    public ParticleType particles = ParticleType.FLAME;
    @SerialEntry
    public double particleDensity = 2.0;
    @SerialEntry
    public float filledBoxAlpha = 0.5f;
    @SerialEntry
    public int tickInterval = 1;
    @SerialEntry
    public boolean playerWaypointLine = false;
    @SerialEntry
    public int playerToSecretLineWidth = 4;
    @SerialEntry
    public Color playerToSecretLineColor = new Color(255, 0, 0);
    @SerialEntry
    public boolean playerToEtherwarp = false;
    @SerialEntry
    public int playerToEtherwarpLineWidth = 4;
    @SerialEntry
    public boolean autoSkipEtherwarps = true;
    @SerialEntry
    public float etherwarpDetectionDistance = 1.5f;
    @SerialEntry
    public boolean etherwarpAimSound = false;
    @SerialEntry
    public SoundType etherwarpAimSoundType = SoundType.NOTE_PLING;
    @SerialEntry
    public float etherwarpAimSoundVolume = 0.35f;
    @SerialEntry
    public float etherwarpAimSoundPitch = 1.6f;
    @SerialEntry
    public int etherwarpAimSoundRearmDelay = 400;
    @SerialEntry
    public boolean useEtherwarpColorForLine = true;
    @SerialEntry
    public Color playerToEtherwarpLineColor = new Color(128, 0, 128);

    // Colours and toggles
    @SerialEntry
    public boolean renderEtherwarps = true;
    @SerialEntry
    public boolean etherwarpFullBlock = false;
    @SerialEntry
    public float etherwarpBoxLineWidth = 7f;
    @SerialEntry
    public Color etherWarp = new Color(128, 0, 128);
    @SerialEntry
    public Color secondStepEtherWarp = new Color(95, 61, 97);

    @SerialEntry
    public boolean renderMines = true;
    @SerialEntry
    public boolean mineFullBlock = false;
    @SerialEntry
    public float mineBoxLineWidth = 5f;
    @SerialEntry
    public Color mine = new Color(255, 236, 0, 82);
    @SerialEntry
    public Color secondStepMine = new Color(177, 173, 97);

    @SerialEntry
    public boolean renderSuperboom = true;
    @SerialEntry
    public boolean superboomsFullBlock = false;
    @SerialEntry
    public float superboomBoxLineWidth = 5f;
    @SerialEntry
    public Color superbooms = new Color(255, 0, 0);
    @SerialEntry
    public Color secondStepSuperbooms = new Color(168, 90, 90);

    @SerialEntry
    public boolean renderInteracts = true;
    @SerialEntry
    public boolean interactsFullBlock = false;
    @SerialEntry
    public float leverBoxLineWidth = 5f;
    @SerialEntry
    public Color interacts = new Color(0, 0, 255);
    @SerialEntry
    public Color secondStepInteracts = new Color(73, 82, 149);

    @SerialEntry
    public boolean renderBonzoStaff = true;
    @SerialEntry
    public boolean bonzoStaffFullBlock = false;
    @SerialEntry
    public float bonzoStaffBoxLineWidth = 5f;
    @SerialEntry
    public Color bonzoStaff = new Color(255, 165, 0);
    @SerialEntry
    public Color secondStepBonzoStaff = new Color(200, 110, 0);

    // Secrets
    @SerialEntry
    public boolean renderSecretsItem = true;
    @SerialEntry
    public float secretBoxLineWidth = 5f;
    @SerialEntry
    public boolean secretsItemFullBlock = false;
    @SerialEntry
    public Color secretsItem = new Color(0, 255, 255);
    @SerialEntry
    public Color secondStepSecretsItem = new Color(95, 167, 167);

    @SerialEntry
    public boolean renderSecretIteract = true;
    @SerialEntry
    public boolean secretsInteractFullBlock = false;
    @SerialEntry
    public Color secretsInteract = new Color(0, 0, 255);
    @SerialEntry
    public Color secondStepSecretsInteract = new Color(73, 82, 149);

    @SerialEntry
    public boolean renderSecretBat = true;
    @SerialEntry
    public boolean secretsBatFullBlock = false;
    @SerialEntry
    public Color secretsBat = new Color(0, 255, 0);
    @SerialEntry
    public Color secondStepSecretsBat = new Color(91, 154, 91);

    // Ender pearls
    @SerialEntry
    public boolean renderEnderpearls = true;
    @SerialEntry
    public boolean enderpearlFullBlock = false;
    @SerialEntry
    public float enderpearlBoxLineWidth = 5f;
    @SerialEntry
    public Color enderpearls = new Color(0, 255, 255);
    @SerialEntry
    public Color secondStepEnderpearls = new Color(95, 167, 167);
    @SerialEntry
    public int pearlLineWidth = 5;
    @SerialEntry
    public Color pearlLineColor = new Color(0, 255, 255);

    // text
    @SerialEntry
    public boolean startTextToggle = true;
    @SerialEntry
    public TextColor startWaypointColor = TextColor.RED;
    @SerialEntry
    public float startTextSize = 1.0f;

    @SerialEntry
    public boolean exitTextToggle = true;
    @SerialEntry
    public TextColor exitWaypointColor = TextColor.RED;
    @SerialEntry
    public float exitTextSize = 1.0f;

    @SerialEntry
    public boolean etherwarpsTextToggle = false;
    @SerialEntry
    public boolean etherwarpNumberingToggle = false;
    @SerialEntry
    public TextColor etherwarpsWaypointColor = TextColor.DARK_PURPLE;
    @SerialEntry
    public float etherwarpsTextSize = 1.0f;

    @SerialEntry
    public boolean minesTextToggle = false;
    @SerialEntry
    public boolean minesEnumToggle = false;
    @SerialEntry
    public TextColor minesWaypointColor = TextColor.YELLOW;
    @SerialEntry
    public float minesTextSize = 1.0f;

    @SerialEntry
    public boolean interactsTextToggle = true;
    @SerialEntry
    public boolean interactsEnumToggle = false;
    @SerialEntry
    public TextColor interactsWaypointColor = TextColor.BLUE;
    @SerialEntry
    public float interactsTextSize = 1.0f;

    @SerialEntry
    public boolean superboomsTextToggle = true;
    @SerialEntry
    public boolean superboomsEnumToggle = false;
    @SerialEntry
    public TextColor superboomsWaypointColor = TextColor.RED;
    @SerialEntry
    public float superboomsTextSize = 1.0f;

    @SerialEntry
    public boolean bonzoStaffTextToggle = true;
    @SerialEntry
    public boolean bonzoStaffEnumToggle = false;
    @SerialEntry
    public TextColor bonzoStaffWaypointColor = TextColor.RED;
    @SerialEntry
    public float bonzoStaffTextSize = 1.0f;

    @SerialEntry
    public boolean enderpearlTextToggle = true;
    @SerialEntry
    public boolean enderpearlEnumToggle = false;
    @SerialEntry
    public TextColor enderpearlWaypointColor = TextColor.AQUA;
    @SerialEntry
    public float enderpearlTextSize = 1.0f;

    @SerialEntry
    public boolean interactTextToggle = true;
    @SerialEntry
    public TextColor interactWaypointColor = TextColor.BLUE;
    @SerialEntry
    public float interactTextSize = 1.0f;

    @SerialEntry
    public boolean itemTextToggle = true;
    @SerialEntry
    public TextColor itemWaypointColor = TextColor.GREEN;
    @SerialEntry
    public float itemTextSize = 1.0f;

    @SerialEntry
    public boolean batTextToggle = true;
    @SerialEntry
    public TextColor batWaypointColor = TextColor.GREEN;
    @SerialEntry
    public float batTextSize = 1.0f;

    @SerialEntry
    public boolean autoCheckUpdates = true;
    @SerialEntry
    public boolean autoDownload = false;
    @SerialEntry
    public boolean autoUpdateRoutes = false;

    @SerialEntry
    public boolean customSecretSound = false;
    @SerialEntry
    public SoundType customSecretSoundType = SoundType.NOTE_PLING;
    @SerialEntry
    public float customSecretSoundVolume = 1.0f;
    @SerialEntry
    public float customSecretSoundPitch = 1.0f;

    // Recording and Dev
    @SerialEntry
    public int recordingHudX = 10;
    @SerialEntry
    public int recordingHudY = 10;
    @SerialEntry
    public Color recordingHudColor = new Color(255, 255, 255);

    // Dev
    @SerialEntry
    public boolean verboseLogging = false;
    @SerialEntry
    public boolean verboseRecording = true;
    @SerialEntry
    public boolean verboseUpdating = true;
    @SerialEntry
    public boolean verboseInfo = false;
    @SerialEntry
    public boolean verboseRendering = false;
    @SerialEntry
    public boolean bridge = false;
    @SerialEntry
    public boolean disableServerChecking = false;
    @SerialEntry
    public boolean forceUpdateDEBUG = false;
    @SerialEntry
    public boolean sendData = true;
    @SerialEntry
    public boolean actionbarInfo = false;
    @SerialEntry
    public boolean verbosePersonalBests = false;

    public String route3ppopkaFileName = "3ppopkaroutes.json";
    public String routeFOWFileName = "fowroutes.json";
    @SerialEntry
    public String copyFileName = "default";
    @SerialEntry
    public int routeNumber = 0;

    public static SRMConfig get() {
        return HANDLER.instance();
    }

    public static Screen getScreen(Screen parent) {
        return YetAnotherConfigLib.create(HANDLER, (defaults, config, builder) -> {

            var colorProfilesGroup = OptionGroup.createBuilder()
                    .name(Component.literal("Color Profiles"))
                    .description(OptionDescription.of(Component.literal("Color Profiles to import, export and toggle to easily change how the visuals look")))
                    .collapsed(true)
                    .option(Option.<String>createBuilder()
                            .name(Component.literal("Profile Name"))
                            .description(OptionDescription.of(Component.literal("Enter name to Save/Load a specific profile")))
                            .binding("default", () -> config.copyFileName != null ? config.copyFileName : "default", v -> config.copyFileName = v)
                            .controller(StringControllerBuilder::create)
                            .build())
                    .option(ButtonOption.createBuilder()
                            .name(Component.literal("Save Current Profile"))
                            .description(OptionDescription.of(Component.literal("Saves current settings to the filename above")))
                            .action((screen, opt) -> ConfigUtils.writeColorConfig(config.copyFileName))
                            .build())
                    .option(ButtonOption.createBuilder()
                            .name(Component.literal("Load From Json"))
                            .description(OptionDescription.of(Component.literal("Loads the profile named above from its JSON file and closes the menu")))
                            .action((screen, opt) -> {
                                ConfigUtils.loadColorConfig(config.copyFileName);
                                //? if >1.21.11 {
                                GuiUtils.setScreen(null);
                                //?} else {
                                // Minecraft.getInstance().setScreen(null);
                                //?}
                            })
                            .build());

            File[] profileFiles = ConfigUtils.COLOR_PROFILE_DIR.listFiles((dir, name) -> name.endsWith(".json"));
            if (profileFiles != null) {
                for (File file : profileFiles) {
                    String profileName = file.getName().replace(".json", "");
                    colorProfilesGroup.option(ButtonOption.createBuilder()
                            .name(Component.literal(profileName.equalsIgnoreCase("default") ? "Restore to Default" : "Load: " + profileName))
                            .description(OptionDescription.of(Component.literal("Loads " + profileName + ".json and closes menu")))
                            .action((screen, opt) -> {
                                ConfigUtils.loadColorConfig(profileName);
                                //? if >1.21.11 {
                                GuiUtils.setScreen(null);
                                //?} else {
                                // Minecraft.getInstance().setScreen(null);
                                //?}
                            })
                            .build());
                }
            }

            Option<SoundType> customSoundTypeOption = Option.<SoundType>createBuilder()
                    .name(Component.literal("Sound"))
                    .description(OptionDescription.of(Component.literal("Selects which sound is played")))
                    .binding(SoundType.NOTE_PLING, () -> config.customSecretSoundType != null ? config.customSecretSoundType : SoundType.NOTE_PLING, v -> config.customSecretSoundType = v)
                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(SoundType.class))
                    .build();
            Option<Float> customSoundVolumeOption = Option.<Float>createBuilder()
                    .name(Component.literal("Volume"))
                    .description(OptionDescription.of(Component.literal("Controls the volume of the secret sound")))
                    .binding(1.0f, () -> config.customSecretSoundVolume, v -> config.customSecretSoundVolume = v)
                    .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 5.0f).step(0.1f))
                    .build();
            Option<Float> customSoundPitchOption = Option.<Float>createBuilder()
                    .name(Component.literal("Pitch"))
                    .description(OptionDescription.of(Component.literal("Controls the pitch of the secret sound")))
                    .binding(1.0f, () -> config.customSecretSoundPitch, v -> config.customSecretSoundPitch = v)
                    .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.5f, 2.0f).step(0.1f))
                    .build();
            Option<SoundType> etherwarpAimSoundTypeOption = Option.<SoundType>createBuilder()
                    .name(Component.literal("Aim Sound"))
                    .description(OptionDescription.of(Component.literal("Selects the sound played when acquiring an Etherwarp target")))
                    .binding(SoundType.NOTE_PLING, () -> config.etherwarpAimSoundType != null ? config.etherwarpAimSoundType : SoundType.NOTE_PLING, v -> config.etherwarpAimSoundType = v)
                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(SoundType.class))
                    .build();
            Option<Float> etherwarpAimSoundVolumeOption = Option.<Float>createBuilder()
                    .name(Component.literal("Aim Sound Volume"))
                    .description(OptionDescription.of(Component.literal("Controls the volume of the Etherwarp aim sound")))
                    .binding(0.35f, () -> config.etherwarpAimSoundVolume, v -> config.etherwarpAimSoundVolume = v)
                    .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.0f, 5.0f).step(0.1f))
                    .build();
            Option<Float> etherwarpAimSoundPitchOption = Option.<Float>createBuilder()
                    .name(Component.literal("Aim Sound Pitch"))
                    .description(OptionDescription.of(Component.literal("Controls the pitch of the Etherwarp aim sound")))
                    .binding(1.6f, () -> config.etherwarpAimSoundPitch, v -> config.etherwarpAimSoundPitch = v)
                    .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.5f, 2.0f).step(0.1f))
                    .build();

            return builder
                    .title(Component.literal("Secret Routes Config"))

                    // General
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("General"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Mod Enabled"))
                                    .description(OptionDescription.of(Component.literal("Enables or disables all Secret Routes features")))
                                    .binding(true, () -> config.modEnabled, v -> config.modEnabled = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<RouteType>createBuilder()
                                    .name(Component.literal("Route Type"))
                                    .description(OptionDescription.of(Component.literal("A toggle between different routes\n\n§n§6FlameOfWar: Routes by FlameOfWar.§r§f\nRecorded Videos of each route can be found here: §nhypixeldungeons.com§r\n\n§n§b3ppopka: Routes by 3ppopka.§n§f\nInstructions of Routes can be found when using Odin Dungeon Waypoints, found in the Odin Discord Server")))
                                    .binding(RouteType.ROUTE_FOW, () -> config.routeType != null ? config.routeType : RouteType.ROUTE_FOW, v -> config.routeType = v)
                                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(RouteType.class))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Render Completed Rooms"))
                                    .description(OptionDescription.of(Component.literal("Renders secrets even if the room is cleared")))
                                    .binding(false, () -> config.renderComplete, v -> config.renderComplete = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Show Whole Route"))
                                    .description(OptionDescription.of(Component.literal("Render all steps at once instead of sequential")))
                                    .binding(false, () -> config.wholeRoute, v -> config.wholeRoute = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.literal("Visible Route Steps"))
                                    .description(OptionDescription.of(Component.literal("How many route steps to show at once when Show Whole Route is off")))
                                    .binding(1, () -> config.visibleRouteSteps, v -> config.visibleRouteSteps = v)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 5).step(1))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Show All Secrets"))
                                    .description(OptionDescription.of(Component.literal("Highlight all secrets in the room, not just the route")))
                                    .binding(false, () -> config.allSecrets, v -> config.allSecrets = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(ButtonOption.createBuilder()
                                    .name(Component.literal("Update Routes"))
                                    .description(OptionDescription.of(Component.literal("Updates to the latest route files from GitHub, overwriting the old routes")))
                                    .text(Component.literal("Download"))
                                    .action((screen, opt) -> {
                                        RouteUtils.checkRoutesFiles();
                                    })
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Predev Routes"))
                                    .description(OptionDescription.of(Component.literal("Configure routes for doing predev. You should watch a tutorial as important information is not displayed in the route")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Enable Predev Routes"))
                                            .description(OptionDescription.of(Component.literal("Master toggle for showing predev routes during the F7 Boss fight.")))
                                            .binding(false, () -> config.pdRoutesEnabled, v -> config.pdRoutesEnabled = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Hide After Storm"))
                                            .description(OptionDescription.of(Component.literal("Hide predev routes when Storm ends.")))
                                            .binding(true, () -> config.pdHideAfterPhase2, v -> config.pdHideAfterPhase2 = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Line to Etherwarp"))
                                    .description(OptionDescription.of(Component.literal("Controls the line from your crosshair to the next Etherwarp waypoint")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Enabled"))
                                            .description(OptionDescription.of(Component.literal("Draws a line from your crosshair to the next Etherwarp waypoint")))
                                            .binding(false, () -> config.playerToEtherwarp, v -> config.playerToEtherwarp = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .option(Option.<Integer>createBuilder()
                                            .name(Component.literal("Line Width"))
                                            .description(OptionDescription.of(Component.literal("Controls the thickness of the line to the next Etherwarp")))
                                            .binding(4, () -> config.playerToEtherwarpLineWidth, v -> config.playerToEtherwarpLineWidth = v)
                                            .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1))
                                            .build())
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Auto Skip Etherwarps"))
                                            .description(OptionDescription.of(Component.literal("automatically skips to the next etherwarp when standing near an etherwarp further in the route. This avoids having the etherwarp line stuck on a previous etherwarp waypoint and automatically jumps to the correct one")))
                                            .binding(true, () -> config.autoSkipEtherwarps, v -> config.autoSkipEtherwarps = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .option(Option.<Float>createBuilder()
                                            .name(Component.literal("Detection Distance"))
                                            .description(OptionDescription.of(Component.literal("How close you need to be to an Etherwarp waypoint for it to count as reached")))
                                            .binding(1.5f, () -> config.etherwarpDetectionDistance, v -> config.etherwarpDetectionDistance = v)
                                            .controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.5f, 5.0f).step(0.5f))
                                            .build())
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Use Etherwarp Color"))
                                            .description(OptionDescription.of(Component.literal("uses the color set in the components -> etherwarps tab as the color for the line")))
                                            .binding(true, () -> config.useEtherwarpColorForLine, v -> config.useEtherwarpColorForLine = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .option(Option.<Color>createBuilder()
                                            .name(Component.literal("Line Color"))
                                            .description(OptionDescription.of(Component.literal("Sets the line color when Use Etherwarp Color is disabled")))
                                            .binding(new Color(128, 0, 128), () -> config.playerToEtherwarpLineColor != null ? config.playerToEtherwarpLineColor : new Color(128, 0, 128), v -> config.playerToEtherwarpLineColor = v)
                                            .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                            .build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Etherwarp Aim Sound"))
                                    .description(OptionDescription.of(Component.literal("Controls the sound played when aiming directly at the current Etherwarp waypoint")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Enabled"))
                                            .description(OptionDescription.of(Component.literal("Plays a sound when your crosshair acquires the current Etherwarp waypoint while sneaking")))
                                            .binding(false, () -> config.etherwarpAimSound, v -> config.etherwarpAimSound = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .option(etherwarpAimSoundTypeOption)
                                    .option(etherwarpAimSoundVolumeOption)
                                    .option(etherwarpAimSoundPitchOption)
                                    .option(Option.<Integer>createBuilder()
                                            .name(Component.literal("Rearm Delay (ms)"))
                                            .description(OptionDescription.of(Component.literal("Controls how long you must look away or stop sneaking before the aim sound can play again")))
                                            .binding(400, () -> config.etherwarpAimSoundRearmDelay, v -> config.etherwarpAimSoundRearmDelay = v)
                                            .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 1000).step(50))
                                            .build())
                                    .option(ButtonOption.createBuilder()
                                            .name(Component.literal("Preview Aim Sound"))
                                            .description(OptionDescription.of(Component.literal("Plays the selected aim sound using the current volume and pitch")))
                                            .text(Component.literal("Play"))
                                            .action((screen, opt) -> SecretSounds.preview(
                                                    etherwarpAimSoundTypeOption.pendingValue(),
                                                    etherwarpAimSoundVolumeOption.pendingValue(),
                                                    etherwarpAimSoundPitchOption.pendingValue()))
                                            .build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Line to Secret"))
                                    .description(OptionDescription.of(Component.literal("Controls the line from your crosshair to the next secret")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Enabled"))
                                            .description(OptionDescription.of(Component.literal("Draws a line from your crosshair to the next secret")))
                                            .binding(false, () -> config.playerWaypointLine, v -> config.playerWaypointLine = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .option(Option.<Integer>createBuilder()
                                            .name(Component.literal("Line Width"))
                                            .description(OptionDescription.of(Component.literal("Controls the thickness of the line to the next secret")))
                                            .binding(4, () -> config.playerToSecretLineWidth, v -> config.playerToSecretLineWidth = v)
                                            .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1))
                                            .build())
                                    .option(Option.<Color>createBuilder()
                                            .name(Component.literal("Line Color"))
                                            .description(OptionDescription.of(Component.literal("Sets the color of the line to the next secret")))
                                            .binding(new Color(255, 0, 0), () -> config.playerToSecretLineColor != null ? config.playerToSecretLineColor : new Color(255, 0, 0), v -> config.playerToSecretLineColor = v)
                                            .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                            .build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Personal Bests"))
                                    .description(OptionDescription.of(Component.literal("Tracks and reports your fastest completion time for each room")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Track Personal Bests"))
                                            .description(OptionDescription.of(Component.literal("Tracks your fastest completion time for each room")))
                                            .binding(true, () -> config.trackPersonalBests, v -> config.trackPersonalBests = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Send Chat Messages"))
                                            .description(OptionDescription.of(Component.literal("Sends a chat message when you set a new personal best")))
                                            .binding(true, () -> config.sendChatMessages, v -> config.sendChatMessages = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .build())
                            .build())
                    // Visuals
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Visuals"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("See Through Walls"))
                                    .description(OptionDescription.of(Component.literal("Renders waypoints through walls")))
                                    .binding(true, () -> config.renderLinesThroughWalls, v -> config.renderLinesThroughWalls = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Movement Lines"))
                                    .description(OptionDescription.of(Component.literal("Controls the lines connecting movement points throughout a route")))
                                    .collapsed(true)
                                    .option(Option.<LineType>createBuilder()
                                            .name(Component.literal("Line Style"))
                                            .description(OptionDescription.of(Component.literal("Chooses between different styles of your movement path")))
                                            .binding(LineType.LINES, () -> config.lineType != null ? config.lineType : LineType.LINES, v -> config.lineType = v)
                                            .controller(opt -> EnumControllerBuilder.create(opt).enumClass(LineType.class))
                                            .build())
                                    .option(Option.<Integer>createBuilder()
                                            .name(Component.literal("Line Width"))
                                            .description(OptionDescription.of(Component.literal("Controls the thickness of solid movement lines")))
                                            .binding(5, () -> config.width, v -> config.width = v)
                                            .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1))
                                            .build())
                                    .option(Option.<Color>createBuilder()
                                            .name(Component.literal("Line Color"))
                                            .description(OptionDescription.of(Component.literal("Sets the color of solid movement lines")))
                                            .binding(new Color(255, 0, 0), () -> config.lineColor != null ? config.lineColor : new Color(255, 0, 0), v -> config.lineColor = v)
                                            .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                            .build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Particles"))
                                    .description(OptionDescription.of(Component.literal("Visual settings for the particles when having your movement line style set to particles")))
                                    .collapsed(true)
                                    .option(Option.<ParticleType>createBuilder()
                                            .name(Component.literal("Type"))
                                            .description(OptionDescription.of(Component.literal("Selects the particle used for particle-based movement paths")))
                                            .binding(ParticleType.FLAME, () -> config.particles != null ? config.particles : ParticleType.FLAME, v -> config.particles = v)
                                            .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ParticleType.class))
                                            .build())
                                    .option(Option.<Double>createBuilder()
                                            .name(Component.literal("Density"))
                                            .description(OptionDescription.of(Component.literal("Controls the spacing of particles along movement paths")))
                                            .binding(2.0, () -> config.particleDensity, v -> config.particleDensity = v)
                                            .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.1, 10.0).step(0.1))
                                            .build())
                                    .build())
                            .group(colorProfilesGroup.build())
                            .build())

                    // Components
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Components"))
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Etherwarps"))
                                    .description(OptionDescription.of(Component.literal("Controls the visuals of Etherwarp waypoints")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).description(OptionDescription.of(Component.literal("Shows Etherwarp waypoint boxes"))).binding(true, () -> config.renderEtherwarps, v -> config.renderEtherwarps = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of Etherwarps in the current route step"))).binding(new Color(128, 0, 128), () -> config.etherWarp != null ? config.etherWarp : new Color(128, 0, 128), v -> config.etherWarp = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for etherwarp waypoints in the second and later visible route steps"))).binding(new Color(95, 61, 97), () -> config.secondStepEtherWarp != null ? config.secondStepEtherWarp : new Color(95, 61, 97), v -> config.secondStepEtherWarp = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).description(OptionDescription.of(Component.literal("Renders Etherwarps as filled blocks instead of outlines"))).binding(false, () -> config.etherwarpFullBlock, v -> config.etherwarpFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Box Line Width")).description(OptionDescription.of(Component.literal("Controls the Etherwarp outline thickness when Full Block is disabled"))).binding(7.0f, () -> config.etherwarpBoxLineWidth, v -> config.etherwarpBoxLineWidth = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 10.0f).step(0.5f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Secrets"))
                                    .description(OptionDescription.of(Component.literal("Controls the visuals of item, interact and bat secrets")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Items")).description(OptionDescription.of(Component.literal("Shows item-secret waypoint boxes"))).binding(true, () -> config.renderSecretsItem, v -> config.renderSecretsItem = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Item Color")).description(OptionDescription.of(Component.literal("Sets the color of item secrets in the current route step"))).binding(new Color(0, 255, 255), () -> config.secretsItem != null ? config.secretsItem : new Color(0, 255, 255), v -> config.secretsItem = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Item Second Step Color")).description(OptionDescription.of(Component.literal("Used for item secret waypoints in the second and later visible route steps"))).binding(new Color(95, 167, 167), () -> config.secondStepSecretsItem != null ? config.secondStepSecretsItem : new Color(95, 167, 167), v -> config.secondStepSecretsItem = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Interacts")).description(OptionDescription.of(Component.literal("Shows interact-secret waypoint boxes"))).binding(true, () -> config.renderSecretIteract, v -> config.renderSecretIteract = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Interact Color")).description(OptionDescription.of(Component.literal("Sets the color of interact secrets in the current route step"))).binding(new Color(0, 0, 255), () -> config.secretsInteract != null ? config.secretsInteract : new Color(0, 0, 255), v -> config.secretsInteract = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Interact Second Step Color")).description(OptionDescription.of(Component.literal("Used for interact secret waypoints in the second and later visible route steps"))).binding(new Color(73, 82, 149), () -> config.secondStepSecretsInteract != null ? config.secondStepSecretsInteract : new Color(73, 82, 149), v -> config.secondStepSecretsInteract = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Bats")).description(OptionDescription.of(Component.literal("Shows bat-secret waypoint boxes"))).binding(true, () -> config.renderSecretBat, v -> config.renderSecretBat = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Bat Color")).description(OptionDescription.of(Component.literal("Sets the color of bat secrets in the current route step"))).binding(new Color(0, 255, 0), () -> config.secretsBat != null ? config.secretsBat : new Color(0, 255, 0), v -> config.secretsBat = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Bat Second Step Color")).description(OptionDescription.of(Component.literal("Used for bat secret waypoints in the second and later visible route steps"))).binding(new Color(91, 154, 91), () -> config.secondStepSecretsBat != null ? config.secondStepSecretsBat : new Color(91, 154, 91), v -> config.secondStepSecretsBat = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Box Line Width")).description(OptionDescription.of(Component.literal("Controls the outline thickness of all secret boxes"))).binding(5.0f, () -> config.secretBoxLineWidth, v -> config.secretBoxLineWidth = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 10.0f).step(0.5f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Mines"))
                                    .description(OptionDescription.of(Component.literal("Controls the visuals of mine waypoints")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).description(OptionDescription.of(Component.literal("Shows mine waypoint boxes"))).binding(true, () -> config.renderMines, v -> config.renderMines = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of mines in the current route step"))).binding(new Color(255, 236, 0, 82), () -> config.mine != null ? config.mine : new Color(255, 236, 0, 82), v -> config.mine = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for mine waypoints in the second and later visible route steps"))).binding(new Color(177, 173, 97), () -> config.secondStepMine != null ? config.secondStepMine : new Color(177, 173, 97), v -> config.secondStepMine = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).description(OptionDescription.of(Component.literal("Renders mines as filled blocks instead of outlines"))).binding(false, () -> config.mineFullBlock, v -> config.mineFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Box Line Width")).description(OptionDescription.of(Component.literal("Controls the mine outline thickness when Full Block is disabled"))).binding(5.0f, () -> config.mineBoxLineWidth, v -> config.mineBoxLineWidth = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 10.0f).step(0.5f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Levers"))
                                    .description(OptionDescription.of(Component.literal("Controls the visuals of lever waypoints")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).description(OptionDescription.of(Component.literal("Shows lever waypoint boxes"))).binding(true, () -> config.renderInteracts, v -> config.renderInteracts = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of levers in the current route step"))).binding(new Color(0, 0, 255), () -> config.interacts != null ? config.interacts : new Color(0, 0, 255), v -> config.interacts = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for lever waypoints in the second and later visible route steps"))).binding(new Color(73, 82, 149), () -> config.secondStepInteracts != null ? config.secondStepInteracts : new Color(73, 82, 149), v -> config.secondStepInteracts = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).description(OptionDescription.of(Component.literal("Renders levers as filled blocks instead of outlines"))).binding(false, () -> config.interactsFullBlock, v -> config.interactsFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Box Line Width")).description(OptionDescription.of(Component.literal("Controls the lever outline thickness when Full Block is disabled"))).binding(5.0f, () -> config.leverBoxLineWidth, v -> config.leverBoxLineWidth = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 10.0f).step(0.5f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Superbooms"))
                                    .description(OptionDescription.of(Component.literal("Controls the visuals of Superboom waypoints")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).description(OptionDescription.of(Component.literal("Shows Superboom waypoint boxes"))).binding(true, () -> config.renderSuperboom, v -> config.renderSuperboom = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of Superbooms in the current route step"))).binding(new Color(255, 0, 0), () -> config.superbooms != null ? config.superbooms : new Color(255, 0, 0), v -> config.superbooms = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for superboom waypoints in the second and later visible route steps"))).binding(new Color(168, 90, 90), () -> config.secondStepSuperbooms != null ? config.secondStepSuperbooms : new Color(168, 90, 90), v -> config.secondStepSuperbooms = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).description(OptionDescription.of(Component.literal("Renders Superbooms as filled blocks instead of outlines"))).binding(false, () -> config.superboomsFullBlock, v -> config.superboomsFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Box Line Width")).description(OptionDescription.of(Component.literal("Controls the Superboom outline thickness when Full Block is disabled"))).binding(5.0f, () -> config.superboomBoxLineWidth, v -> config.superboomBoxLineWidth = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 10.0f).step(0.5f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Bonzo Staffs"))
                                    .description(OptionDescription.of(Component.literal("Controls the visuals of Bonzo Staff waypoints")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).description(OptionDescription.of(Component.literal("Shows Bonzo Staff waypoint boxes"))).binding(true, () -> config.renderBonzoStaff, v -> config.renderBonzoStaff = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of Bonzo Staffs in the current route step"))).binding(new Color(255, 165, 0), () -> config.bonzoStaff != null ? config.bonzoStaff : new Color(255, 165, 0), v -> config.bonzoStaff = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for Bonzo Staff waypoints in the second and later visible route steps"))).binding(new Color(200, 110, 0), () -> config.secondStepBonzoStaff != null ? config.secondStepBonzoStaff : new Color(200, 110, 0), v -> config.secondStepBonzoStaff = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).description(OptionDescription.of(Component.literal("Renders Bonzo Staffs as filled blocks instead of outlines"))).binding(false, () -> config.bonzoStaffFullBlock, v -> config.bonzoStaffFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Box Line Width")).description(OptionDescription.of(Component.literal("Controls the Bonzo Staff outline thickness when Full Block is disabled"))).binding(5.0f, () -> config.bonzoStaffBoxLineWidth, v -> config.bonzoStaffBoxLineWidth = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 10.0f).step(0.5f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Enderpearls"))
                                    .description(OptionDescription.of(Component.literal("Controls the visuals of Enderpearl waypoints and their directional guide lines")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).description(OptionDescription.of(Component.literal("Shows Enderpearl waypoints and their directional guide lines"))).binding(true, () -> config.renderEnderpearls, v -> config.renderEnderpearls = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of Enderpearl waypoint boxes in the current route step"))).binding(new Color(0, 255, 255), () -> config.enderpearls != null ? config.enderpearls : new Color(0, 255, 255), v -> config.enderpearls = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for enderpearl waypoints in the second and later visible route steps"))).binding(new Color(95, 167, 167), () -> config.secondStepEnderpearls != null ? config.secondStepEnderpearls : new Color(95, 167, 167), v -> config.secondStepEnderpearls = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).description(OptionDescription.of(Component.literal("Renders Enderpearl waypoints as filled blocks instead of outlines"))).binding(false, () -> config.enderpearlFullBlock, v -> config.enderpearlFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Box Line Width")).description(OptionDescription.of(Component.literal("Controls the Enderpearl outline thickness when Full Block is disabled"))).binding(5.0f, () -> config.enderpearlBoxLineWidth, v -> config.enderpearlBoxLineWidth = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 10.0f).step(0.5f)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Line Color")).description(OptionDescription.of(Component.literal("Sets the color of Enderpearl directional guide lines"))).binding(new Color(0, 255, 255), () -> config.pearlLineColor != null ? config.pearlLineColor : new Color(0, 255, 255), v -> config.pearlLineColor = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Integer>createBuilder().name(Component.literal("Line Width")).description(OptionDescription.of(Component.literal("Controls the thickness of Enderpearl directional guide lines"))).binding(5, () -> config.pearlLineWidth, v -> config.pearlLineWidth = v).controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Custom Secret Sound"))
                                    .description(OptionDescription.of(Component.literal("Controls the sound played when a secret is collected")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder()
                                            .name(Component.literal("Enabled"))
                                            .description(OptionDescription.of(Component.literal("Plays the selected sound when a secret is collected")))
                                            .binding(false, () -> config.customSecretSound, v -> config.customSecretSound = v)
                                            .controller(TickBoxControllerBuilder::create)
                                            .build())
                                    .option(customSoundTypeOption)
                                    .option(customSoundVolumeOption)
                                    .option(customSoundPitchOption)
                                    .option(ButtonOption.createBuilder()
                                            .name(Component.literal("Preview Sound"))
                                            .description(OptionDescription.of(Component.literal("Plays the selected sound using the current volume and pitch")))
                                            .text(Component.literal("Play"))
                                            .action((screen, opt) -> SecretSounds.preview(
                                                    customSoundTypeOption.pendingValue(),
                                                    customSoundVolumeOption.pendingValue(),
                                                    customSoundPitchOption.pendingValue()))
                                            .build())
                                    .build())
                            .build())

                    // Text Settings
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Text Settings"))
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Etherwarps Text"))
                                    .description(OptionDescription.of(Component.literal("Controls labels displayed on Etherwarp waypoints")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show")).description(OptionDescription.of(Component.literal("Shows labels on Etherwarp waypoints"))).binding(false, () -> config.etherwarpsTextToggle, v -> config.etherwarpsTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Numbering")).description(OptionDescription.of(Component.literal("Adds a sequence number to each Etherwarp label"))).binding(false, () -> config.etherwarpNumberingToggle, v -> config.etherwarpNumberingToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of Etherwarp labels"))).binding(TextColor.DARK_PURPLE, () -> config.etherwarpsWaypointColor != null ? config.etherwarpsWaypointColor : TextColor.DARK_PURPLE, v -> config.etherwarpsWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Size")).description(OptionDescription.of(Component.literal("Controls the size of Etherwarp labels"))).binding(1.0f, () -> config.etherwarpsTextSize, v -> config.etherwarpsTextSize = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.1f, 5f).step(0.1f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Interacts Text"))
                                    .description(OptionDescription.of(Component.literal("Controls labels displayed on lever and interact waypoints")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show")).description(OptionDescription.of(Component.literal("Shows labels on lever and interact waypoints"))).binding(true, () -> config.interactsTextToggle, v -> config.interactsTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Numbering")).description(OptionDescription.of(Component.literal("Adds a sequence number to each lever or interact label"))).binding(false, () -> config.interactsEnumToggle, v -> config.interactsEnumToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of lever and interact labels"))).binding(TextColor.BLUE, () -> config.interactsWaypointColor != null ? config.interactsWaypointColor : TextColor.BLUE, v -> config.interactsWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Size")).description(OptionDescription.of(Component.literal("Controls the size of lever and interact labels"))).binding(1.0f, () -> config.interactsTextSize, v -> config.interactsTextSize = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.1f, 5f).step(0.1f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Bonzo Staffs Text"))
                                    .description(OptionDescription.of(Component.literal("Controls labels displayed on Bonzo Staff waypoints")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show")).description(OptionDescription.of(Component.literal("Shows labels on Bonzo Staff waypoints"))).binding(true, () -> config.bonzoStaffTextToggle, v -> config.bonzoStaffTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Numbering")).description(OptionDescription.of(Component.literal("Adds a sequence number to each Bonzo Staff label"))).binding(false, () -> config.bonzoStaffEnumToggle, v -> config.bonzoStaffEnumToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Color")).description(OptionDescription.of(Component.literal("Sets the color of Bonzo Staff labels"))).binding(TextColor.RED, () -> config.bonzoStaffWaypointColor != null ? config.bonzoStaffWaypointColor : TextColor.RED, v -> config.bonzoStaffWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Size")).description(OptionDescription.of(Component.literal("Controls the size of Bonzo Staff labels"))).binding(1.0f, () -> config.bonzoStaffTextSize, v -> config.bonzoStaffTextSize = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.1f, 5f).step(0.1f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Start/Exit"))
                                    .description(OptionDescription.of(Component.literal("Controls labels displayed at route entrances and exits")))
                                    .collapsed(true)
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show Start")).description(OptionDescription.of(Component.literal("Shows a label at the beginning of the route"))).binding(true, () -> config.startTextToggle, v -> config.startTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Start Color")).description(OptionDescription.of(Component.literal("Sets the color of the route-start label"))).binding(TextColor.RED, () -> config.startWaypointColor != null ? config.startWaypointColor : TextColor.RED, v -> config.startWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show Exit")).description(OptionDescription.of(Component.literal("Shows a label at the room exit"))).binding(true, () -> config.exitTextToggle, v -> config.exitTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Exit Color")).description(OptionDescription.of(Component.literal("Sets the color of the room-exit label"))).binding(TextColor.RED, () -> config.exitWaypointColor != null ? config.exitWaypointColor : TextColor.RED, v -> config.exitWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .build())
                            .build());

        }).generateScreen(parent);
    }

    public enum LineType implements NameableEnum {
        PARTICLES("Particles"), LINES("Lines"), NONE("None");
        private final String name;

        LineType(String name) {
            this.name = name;
        }

        @Override
        public Component getDisplayName() {
            return Component.literal(name);
        }
    }

    public enum RouteType implements NameableEnum {
        ROUTE_3ppopka("3ppopka"), ROUTE_FOW("FlameOfWar");
        private final String name;

        RouteType(String name) {
            this.name = name;
        }

        @Override
        public Component getDisplayName() {
            return Component.literal(name);
        }
    }

    public enum SoundType implements NameableEnum {
        MOB_BLAZE_HIT("Blaze Hit", "entity.blaze.hurt"),
        FIRE_IGNITE("Fire Ignite", "item.flintandsteel.use"),
        RANDOM_ORB("Experience Orb", "entity.experience_orb.pickup"),
        RANDOM_BREAK("Item Break", "entity.item.break"),
        MOB_GUARDIAN_LAND_HIT("Guardian Land Hit", "entity.guardian.hurt_land"),
        NOTE_PLING("Note Pling", "block.note_block.pling"),
        ZYRA_MEOW("Zyra Meow", "secretroutesmod:zyra.meow");
        private final String name;
        public final String soundId;

        SoundType(String name, String soundId) {
            this.name = name;
            this.soundId = soundId;
        }

        @Override
        public Component getDisplayName() {
            return Component.literal(name);
        }
    }

    public enum TextColor implements NameableEnum {
        BLACK("Black", ChatFormatting.BLACK), DARK_BLUE("Dark Blue", ChatFormatting.DARK_BLUE),
        DARK_GREEN("Dark Green", ChatFormatting.DARK_GREEN), DARK_AQUA("Dark Aqua", ChatFormatting.DARK_AQUA),
        DARK_RED("Dark Red", ChatFormatting.DARK_RED), DARK_PURPLE("Dark Purple", ChatFormatting.DARK_PURPLE),
        GOLD("Gold", ChatFormatting.GOLD), GRAY("Gray", ChatFormatting.GRAY),
        DARK_GRAY("Dark Gray", ChatFormatting.DARK_GRAY), BLUE("Blue", ChatFormatting.BLUE),
        GREEN("Green", ChatFormatting.GREEN), AQUA("Aqua", ChatFormatting.AQUA),
        RED("Red", ChatFormatting.RED), LIGHT_PURPLE("Light Purple", ChatFormatting.LIGHT_PURPLE),
        YELLOW("Yellow", ChatFormatting.YELLOW), WHITE("White", ChatFormatting.WHITE);

        public final ChatFormatting formatting;
        private final String name;

        TextColor(String name, ChatFormatting formatting) {
            this.name = name;
            this.formatting = formatting;
        }

        @Override
        public Component getDisplayName() {
            return Component.literal(name).withStyle(formatting);
        }
    }

    public enum ParticleType implements NameableEnum {
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

        private final String name;

        ParticleType(String name) {
            this.name = name;
        }

        @Override
        public Component getDisplayName() {
            return Component.literal(name);
        }
    }
}
//#endif
