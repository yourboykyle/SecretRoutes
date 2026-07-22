//#if FABRIC
package xyz.yourboykyle.secretroutes.config;
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
//#if FABRIC

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.utils.GuiUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ConfigUtils;
import xyz.yourboykyle.secretroutes.utils.RouteUtils;

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
    public float boxLineWidth = 4f;
    @SerialEntry
    public int tickInterval = 1;
    @SerialEntry
    public boolean playerWaypointLine = false;
    @SerialEntry
    public boolean playerToEtherwarp = false;

    // Colours and toggles
    @SerialEntry
    public boolean renderEtherwarps = true;
    @SerialEntry
    public boolean etherwarpFullBlock = false;
    @SerialEntry
    public Color etherWarp = new Color(128, 0, 128);
    @SerialEntry
    public Color secondStepEtherWarp = new Color(95, 61, 97);

    @SerialEntry
    public boolean renderMines = true;
    @SerialEntry
    public boolean mineFullBlock = false;
    @SerialEntry
    public Color mine = new Color(255, 255, 0);
    @SerialEntry
    public Color secondStepMine = new Color(177, 173, 97);

    @SerialEntry
    public boolean renderSuperboom = true;
    @SerialEntry
    public boolean superboomsFullBlock = false;
    @SerialEntry
    public Color superbooms = new Color(255, 0, 0);
    @SerialEntry
    public Color secondStepSuperbooms = new Color(168, 90, 90);

    @SerialEntry
    public boolean renderInteracts = true;
    @SerialEntry
    public boolean interactsFullBlock = false;
    @SerialEntry
    public Color interacts = new Color(0, 0, 255);
    @SerialEntry
    public Color secondStepInteracts = new Color(73, 82, 149);

    // Secrets
    @SerialEntry
    public boolean renderSecretsItem = true;
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
    public SoundType customSecretSoundType = SoundType.ZYRA_MEOW;
    @SerialEntry
    public float customSecretSoundVolume = 1.0f;
    @SerialEntry
    public float customSecretSoundPitch = 1.0f;
    @SerialEntry
    public boolean renderBlood = false;
    @SerialEntry
    public boolean bloodNotif = false;
    @SerialEntry
    public String bloodReadyText = "Blood Ready";
    @SerialEntry
    public TextColor bloodReadyColor = TextColor.GOLD;
    @SerialEntry
    public int bloodBannerDuration = 3000;
    @SerialEntry
    public int bloodScale = 2;
    @SerialEntry
    public int bloodX = 0;
    @SerialEntry
    public int bloodY = -100;

    // Boss Hiding
    @SerialEntry
    public boolean hideBossMessages = false;
    @SerialEntry
    public boolean hideWatcher = true;
    @SerialEntry
    public boolean hideBonzo = true;
    @SerialEntry
    public boolean hideScarf = true;
    @SerialEntry
    public boolean hideProfessor = true;
    @SerialEntry
    public boolean hideThorn = true;
    @SerialEntry
    public boolean hideLivid = true;
    @SerialEntry
    public boolean hideSadan = true;
    @SerialEntry
    public boolean hideWitherLords = false;

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

            var profilesGroup = OptionGroup.createBuilder()
                    .name(Component.literal("Load Existing Profile"));

            File[] profileFiles = ConfigUtils.COLOR_PROFILE_DIR.listFiles((dir, name) -> name.endsWith(".json"));
            if (profileFiles != null) {
                for (File file : profileFiles) {
                    String profileName = file.getName().replace(".json", "");
                    profilesGroup.option(ButtonOption.createBuilder()
                            .name(Component.literal("Load: " + profileName))
                            .description(OptionDescription.of(Component.literal("Loads " + profileName + ".json and closes menu.")))
                            .action((screen, opt) -> {
                                ConfigUtils.loadColorConfig(profileName);
                                GuiUtils.setScreen(null);
                            })
                            .build());
                }
            }

            return builder
                    .title(Component.literal("Secret Routes Config"))

                    // General
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("General"))
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Mod Enabled"))
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
                                    .description(OptionDescription.of(Component.literal("Renders secrets even if the room is cleared.")))
                                    .binding(false, () -> config.renderComplete, v -> config.renderComplete = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Show Whole Route"))
                                    .description(OptionDescription.of(Component.literal("Render all steps at once instead of sequential.")))
                                    .binding(false, () -> config.wholeRoute, v -> config.wholeRoute = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.literal("Visible Route Steps"))
                                    .description(OptionDescription.of(Component.literal("How many route steps to show at once when Show Whole Route is off.")))
                                    .binding(1, () -> config.visibleRouteSteps, v -> config.visibleRouteSteps = v)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 5).step(1))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Show All Secrets"))
                                    .description(OptionDescription.of(Component.literal("Highlight all secrets in the room, not just the route.")))
                                    .binding(false, () -> config.allSecrets, v -> config.allSecrets = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(ButtonOption.createBuilder()
                                    .name(Component.literal("Update Routes"))
                                    .text(Component.literal("Download"))
                                    .action((screen, opt) -> {
                                        RouteUtils.checkRoutesFiles();
                                    })
                                    .build())
                            .build())

                    // Visuals
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Visuals"))
                            .option(Option.<LineType>createBuilder()
                                    .name(Component.literal("Line Style"))
                                    .binding(LineType.LINES, () -> config.lineType != null ? config.lineType : LineType.LINES, v -> config.lineType = v)
                                    .controller(opt -> EnumControllerBuilder.create(opt).enumClass(LineType.class))
                                    .build())
                            .option(Option.<Integer>createBuilder()
                                    .name(Component.literal("Line Width"))
                                    .binding(5, () -> config.width, v -> config.width = v)
                                    .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Player to Secret Line"))
                                    .binding(false, () -> config.playerWaypointLine, v -> config.playerWaypointLine = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("Player to Etherwarp Line"))
                                    .binding(false, () -> config.playerToEtherwarp, v -> config.playerToEtherwarp = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .option(Option.<Float>createBuilder()
                                    .name(Component.literal("Box Line Width"))
                                    .binding(4.0f, () -> config.boxLineWidth, v -> config.boxLineWidth = v)
                                    .controller(opt -> FloatSliderControllerBuilder.create(opt).range(1.0f, 10.0f).step(0.5f))
                                    .build())
                            .option(Option.<Color>createBuilder()
                                    .name(Component.literal("Line Color"))
                                    .binding(new Color(255, 0, 0), () -> config.lineColor != null ? config.lineColor : new Color(255, 0, 0), v -> config.lineColor = v)
                                    .controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true))
                                    .build())
                            .option(Option.<Boolean>createBuilder()
                                    .name(Component.literal("See Through Walls"))
                                    .binding(true, () -> config.renderLinesThroughWalls, v -> config.renderLinesThroughWalls = v)
                                    .controller(TickBoxControllerBuilder::create)
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Particles"))
                                    .option(Option.<ParticleType>createBuilder()
                                            .name(Component.literal("Type"))
                                            .binding(ParticleType.FLAME, () -> config.particles != null ? config.particles : ParticleType.FLAME, v -> config.particles = v)
                                            .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ParticleType.class))
                                            .build())
                                    .option(Option.<Double>createBuilder()
                                            .name(Component.literal("Density"))
                                            .binding(2.0, () -> config.particleDensity, v -> config.particleDensity = v)
                                            .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.1, 10.0).step(0.1))
                                            .build())
                                    .build())
                            .build())

                    // Components
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Components"))
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Etherwarps"))
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).binding(true, () -> config.renderEtherwarps, v -> config.renderEtherwarps = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).binding(new Color(128, 0, 128), () -> config.etherWarp != null ? config.etherWarp : new Color(128, 0, 128), v -> config.etherWarp = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for etherwarp waypoints in the second and later visible route steps."))).binding(new Color(95, 61, 97), () -> config.secondStepEtherWarp != null ? config.secondStepEtherWarp : new Color(95, 61, 97), v -> config.secondStepEtherWarp = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).binding(false, () -> config.etherwarpFullBlock, v -> config.etherwarpFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Secrets"))
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Items")).binding(true, () -> config.renderSecretsItem, v -> config.renderSecretsItem = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Item Color")).binding(new Color(0, 255, 255), () -> config.secretsItem != null ? config.secretsItem : new Color(0, 255, 255), v -> config.secretsItem = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Item Second Step Color")).description(OptionDescription.of(Component.literal("Used for item secret waypoints in the second and later visible route steps."))).binding(new Color(95, 167, 167), () -> config.secondStepSecretsItem != null ? config.secondStepSecretsItem : new Color(95, 167, 167), v -> config.secondStepSecretsItem = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Interacts")).binding(true, () -> config.renderSecretIteract, v -> config.renderSecretIteract = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Interact Color")).binding(new Color(0, 0, 255), () -> config.secretsInteract != null ? config.secretsInteract : new Color(0, 0, 255), v -> config.secretsInteract = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Interact Second Step Color")).description(OptionDescription.of(Component.literal("Used for interact secret waypoints in the second and later visible route steps."))).binding(new Color(73, 82, 149), () -> config.secondStepSecretsInteract != null ? config.secondStepSecretsInteract : new Color(73, 82, 149), v -> config.secondStepSecretsInteract = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Bats")).binding(true, () -> config.renderSecretBat, v -> config.renderSecretBat = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Bat Color")).binding(new Color(0, 255, 0), () -> config.secretsBat != null ? config.secretsBat : new Color(0, 255, 0), v -> config.secretsBat = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Bat Second Step Color")).description(OptionDescription.of(Component.literal("Used for bat secret waypoints in the second and later visible route steps."))).binding(new Color(91, 154, 91), () -> config.secondStepSecretsBat != null ? config.secondStepSecretsBat : new Color(91, 154, 91), v -> config.secondStepSecretsBat = v).controller(ColorControllerBuilder::create).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Mines"))
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).binding(true, () -> config.renderMines, v -> config.renderMines = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).binding(new Color(255, 255, 0), () -> config.mine != null ? config.mine : new Color(255, 255, 0), v -> config.mine = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for mine waypoints in the second and later visible route steps."))).binding(new Color(177, 173, 97), () -> config.secondStepMine != null ? config.secondStepMine : new Color(177, 173, 97), v -> config.secondStepMine = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).binding(false, () -> config.mineFullBlock, v -> config.mineFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Superbooms"))
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).binding(true, () -> config.renderSuperboom, v -> config.renderSuperboom = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).binding(new Color(255, 0, 0), () -> config.superbooms != null ? config.superbooms : new Color(255, 0, 0), v -> config.superbooms = v).controller(opt -> ColorControllerBuilder.create(opt).allowAlpha(true)).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for superboom waypoints in the second and later visible route steps."))).binding(new Color(168, 90, 90), () -> config.secondStepSuperbooms != null ? config.secondStepSuperbooms : new Color(168, 90, 90), v -> config.secondStepSuperbooms = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).binding(false, () -> config.superboomsFullBlock, v -> config.superboomsFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Enderpearls"))
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Enabled")).binding(true, () -> config.renderEnderpearls, v -> config.renderEnderpearls = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Color")).binding(new Color(0, 255, 255), () -> config.enderpearls != null ? config.enderpearls : new Color(0, 255, 255), v -> config.enderpearls = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Second Step Color")).description(OptionDescription.of(Component.literal("Used for enderpearl waypoints in the second and later visible route steps."))).binding(new Color(95, 167, 167), () -> config.secondStepEnderpearls != null ? config.secondStepEnderpearls : new Color(95, 167, 167), v -> config.secondStepEnderpearls = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Full Block")).binding(false, () -> config.enderpearlFullBlock, v -> config.enderpearlFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Color>createBuilder().name(Component.literal("Line Color")).binding(new Color(0, 255, 255), () -> config.pearlLineColor != null ? config.pearlLineColor : new Color(0, 255, 255), v -> config.pearlLineColor = v).controller(ColorControllerBuilder::create).build())
                                    .option(Option.<Integer>createBuilder().name(Component.literal("Line Width")).binding(5, () -> config.pearlLineWidth, v -> config.pearlLineWidth = v).controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1)).build())
                                    .build())
                            .build())

                    // Text Settings
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Text Settings"))
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Etherwarps Text"))
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show")).binding(false, () -> config.etherwarpsTextToggle, v -> config.etherwarpsTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Numbering")).binding(false, () -> config.etherwarpNumberingToggle, v -> config.etherwarpNumberingToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Color")).binding(TextColor.DARK_PURPLE, () -> config.etherwarpsWaypointColor != null ? config.etherwarpsWaypointColor : TextColor.DARK_PURPLE, v -> config.etherwarpsWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Size")).binding(1.0f, () -> config.etherwarpsTextSize, v -> config.etherwarpsTextSize = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.1f, 5f).step(0.1f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Interacts Text"))
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show")).binding(true, () -> config.interactsTextToggle, v -> config.interactsTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Numbering")).binding(false, () -> config.interactsEnumToggle, v -> config.interactsEnumToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Color")).binding(TextColor.BLUE, () -> config.interactsWaypointColor != null ? config.interactsWaypointColor : TextColor.BLUE, v -> config.interactsWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .option(Option.<Float>createBuilder().name(Component.literal("Size")).binding(1.0f, () -> config.interactsTextSize, v -> config.interactsTextSize = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.1f, 5f).step(0.1f)).build())
                                    .build())
                            .group(OptionGroup.createBuilder()
                                    .name(Component.literal("Start/Exit"))
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show Start")).binding(true, () -> config.startTextToggle, v -> config.startTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Start Color")).binding(TextColor.RED, () -> config.startWaypointColor != null ? config.startWaypointColor : TextColor.RED, v -> config.startWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .option(Option.<Boolean>createBuilder().name(Component.literal("Show Exit")).binding(true, () -> config.exitTextToggle, v -> config.exitTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                                    .option(Option.<TextColor>createBuilder().name(Component.literal("Exit Color")).binding(TextColor.RED, () -> config.exitWaypointColor != null ? config.exitWaypointColor : TextColor.RED, v -> config.exitWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                                    .build())
                            .build())

                    // Profiles
                    .category(ConfigCategory.createBuilder()
                            .name(Component.literal("Profiles"))
                            .option(Option.<String>createBuilder()
                                    .name(Component.literal("Profile Name"))
                                    .description(OptionDescription.of(Component.literal("Enter name to Save/Load a specific profile.")))
                                    .binding("default", () -> config.copyFileName != null ? config.copyFileName : "default", v -> config.copyFileName = v)
                                    .controller(StringControllerBuilder::create)
                                    .build())
                            .option(ButtonOption.createBuilder()
                                    .name(Component.literal("Save Current Profile"))
                                    .description(OptionDescription.of(Component.literal("Saves current settings to the filename above.")))
                                    .action((screen, opt) -> {
                                        ConfigUtils.writeColorConfig(config.copyFileName);
                                    })
                                    .build())
                            .option(ButtonOption.createBuilder()
                                    .name(Component.literal("Load From Text Input"))
                                    .action((screen, opt) -> {
                                        ConfigUtils.loadColorConfig(config.copyFileName);
                                        GuiUtils.setScreen(null);
                                    })
                                    .build())
                            .group(profilesGroup.build())
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
        MOB_BLAZE_HIT("mob.blaze.hit"), FIRE_IGNITE("fire.ignite"), RANDOM_ORB("random.orb"),
        RANDOM_BREAK("random.break"), MOB_GUARDIAN_LAND_HIT("mob.guardian.land.hit"),
        NOTE_PLING("note.pling"), ZYRA_MEOW("zyra.meow");
        private final String name;

        SoundType(String name) {
            this.name = name;
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