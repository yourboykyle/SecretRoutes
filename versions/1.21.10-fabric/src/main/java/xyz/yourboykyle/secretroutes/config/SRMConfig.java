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

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.FileUtils;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RouteUtils;

import java.awt.*;
import java.io.File;

public class SRMConfig {

    // YACL Handler Setup
    public static final ConfigClassHandler<SRMConfig> HANDLER = ConfigClassHandler.createBuilder(SRMConfig.class)
            .id(Identifier.of(Main.MODID, "config"))
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
    public boolean renderComplete = false;
    @SerialEntry
    public boolean wholeRoute = false;
    @SerialEntry
    public boolean allSteps = false;
    @SerialEntry
    public boolean allSecrets = false;
    @SerialEntry
    public LineType lineType = LineType.LINES;
    @SerialEntry
    public ParticleType particles = ParticleType.FLAME;
    @SerialEntry
    public int tickInterval = 1;
    @SerialEntry
    public int width = 5;
    @SerialEntry
    public int pearlLineWidth = 5;
    @SerialEntry
    public RouteType routeType = RouteType.PEARLS;
    @SerialEntry
    public String routesFileName = "routes.json";
    @SerialEntry
    public String pearlRoutesFileName = "pearlroutes.json";
    @SerialEntry
    public String copyFileName = "";
    // Personal Bests
    @SerialEntry
    public boolean trackPersonalBests = true;
    @SerialEntry
    public boolean sendChatMessages = true;
    // Updates
    @SerialEntry
    public boolean autoCheckUpdates = true;
    @SerialEntry
    public boolean autoDownload = false;
    @SerialEntry
    public boolean autoUpdateRoutes = false;
    // Route Recording
    @SerialEntry
    public int routeNumber = 0;
    @SerialEntry
    public int etherwarpPing = 150;
    // Profiles
    @SerialEntry
    public String colorProfileName = "default.json";
    // Rendering
    @SerialEntry
    public float filledBoxAlpha = 0.5f;
    @SerialEntry
    public float boxLineWidth = 10f;
    @SerialEntry
    public Color lineColor = new Color(255, 0, 0);
    @SerialEntry
    public Color pearlLineColor = new Color(0, 255, 255);
    @SerialEntry
    public Color etherWarp = new Color(128, 0, 128);
    @SerialEntry
    public Color mine = new Color(255, 255, 0);
    @SerialEntry
    public Color interacts = new Color(0, 0, 255);
    @SerialEntry
    public Color superbooms = new Color(255, 0, 0);
    @SerialEntry
    public Color enderpearls = new Color(0, 255, 255);
    @SerialEntry
    public Color secretsItem = new Color(0, 255, 255);
    @SerialEntry
    public Color secretsInteract = new Color(0, 0, 255);
    @SerialEntry
    public Color secretsBat = new Color(0, 255, 0);
    // Render Options
    @SerialEntry
    public boolean renderEtherwarps = true;
    @SerialEntry
    public boolean etherwarpFullBlock = false;
    @SerialEntry
    public boolean renderMines = true;
    @SerialEntry
    public boolean mineFullBlock = false;
    @SerialEntry
    public boolean renderInteracts = true;
    @SerialEntry
    public boolean interactsFullBlock = false;
    @SerialEntry
    public boolean renderSuperboom = true;
    @SerialEntry
    public boolean superboomsFullBlock = false;
    @SerialEntry
    public boolean renderEnderpearls = true;
    @SerialEntry
    public boolean enderpearlFullBlock = false;
    @SerialEntry
    public boolean renderSecretsItem = true;
    @SerialEntry
    public boolean secretsItemFullBlock = false;
    @SerialEntry
    public boolean renderSecretIteract = true;
    @SerialEntry
    public boolean secretsInteractFullBlock = false;
    @SerialEntry
    public boolean renderSecretBat = true;
    @SerialEntry
    public boolean secretsBatFullBlock = false;
    // Start
    @SerialEntry
    public boolean startTextToggle = true;

    // Waypoint Text Rendering
    @SerialEntry
    public TextColor startWaypointColor = TextColor.RED;
    @SerialEntry
    public float startTextSize = 3;
    // Exit
    @SerialEntry
    public boolean exitTextToggle = true;
    @SerialEntry
    public TextColor exitWaypointColor = TextColor.RED;
    @SerialEntry
    public float exitTextSize = 3;
    // Interact
    @SerialEntry
    public boolean interactTextToggle = true;
    @SerialEntry
    public TextColor interactWaypointColor = TextColor.BLUE;
    @SerialEntry
    public float interactTextSize = 3;
    // Interacts
    @SerialEntry
    public boolean interactsTextToggle = true;
    @SerialEntry
    public boolean interactsEnumToggle = false;
    @SerialEntry
    public TextColor interactsWaypointColor = TextColor.BLUE;
    @SerialEntry
    public float interactsTextSize = 3;
    // Item
    @SerialEntry
    public boolean itemTextToggle = true;
    @SerialEntry
    public TextColor itemWaypointColor = TextColor.GREEN;
    @SerialEntry
    public float itemTextSize = 3;
    // Bat
    @SerialEntry
    public boolean batTextToggle = true;
    @SerialEntry
    public TextColor batWaypointColor = TextColor.GREEN;
    @SerialEntry
    public float batTextSize = 3;
    // Etherwarp
    @SerialEntry
    public boolean etherwarpsTextToggle = false;
    @SerialEntry
    public boolean etherwarpsEnumToggle = false;
    @SerialEntry
    public TextColor etherwarpsWaypointColor = TextColor.DARK_PURPLE;
    @SerialEntry
    public float etherwarpsTextSize = 3;
    // Mines
    @SerialEntry
    public boolean minesTextToggle = false;
    @SerialEntry
    public boolean minesEnumToggle = false;
    @SerialEntry
    public TextColor minesWaypointColor = TextColor.YELLOW;
    @SerialEntry
    public float minesTextSize = 3;
    // Superbooms
    @SerialEntry
    public boolean superboomsTextToggle = true;
    @SerialEntry
    public boolean superboomsEnumToggle = false;
    @SerialEntry
    public TextColor superboomsWaypointColor = TextColor.RED;
    @SerialEntry
    public float superboomsTextSize = 3;
    // Enderpearls
    @SerialEntry
    public boolean enderpearlTextToggle = true;
    @SerialEntry
    public boolean enderpearlEnumToggle = false;
    @SerialEntry
    public TextColor enderpearlWaypointColor = TextColor.AQUA;
    @SerialEntry
    public float enderpearlTextSize = 3;
    // Dev
    @SerialEntry
    public boolean enableDevOptions = false;
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
    public boolean verbosePersonalBests = false;
    @SerialEntry
    public boolean actionbarInfo = false;
    @SerialEntry
    public boolean forceUpdateDEBUG = false;
    @SerialEntry
    public boolean warnKeybindsOutsideDungeon = true;
    @SerialEntry
    public boolean renderLinesThroughWalls = true;
    @SerialEntry
    public boolean playerWaypointLine = false;
    @SerialEntry
    public boolean playerToEtherwarp = false;
    @SerialEntry
    public boolean debug = false;
    @SerialEntry
    public boolean disableServerChecking = false;
    @SerialEntry
    public boolean bridge = false;
    @SerialEntry
    public boolean sendData = true;
    // HUD
    @SerialEntry
    public int recordingHudX = 10;
    @SerialEntry
    public int recordingHudY = 10;
    @SerialEntry
    public Color recordingHudColor = new Color(255, 255, 255);
    // Sound
    @SerialEntry
    public boolean customSecretSound = false;
    @SerialEntry
    public SoundType customSecretSoundType = SoundType.ZYRA_MEOW;
    @SerialEntry
    public float customSecretSoundVolume = 1.0f;
    @SerialEntry
    public float customSecretSoundPitch = 1.0f;
    // Messages
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
    @SerialEntry
    public boolean renderBlood = false;

    public static SRMConfig get() {
        return HANDLER.instance();
    }

    // Enums

    public static Screen getScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Secret Routes Config"))
                // General
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Render Routes"))
                                .binding(true, () -> get().modEnabled, v -> get().modEnabled = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.literal("Render Complete"))
                                .description(OptionDescription.of(Text.literal("Renders secrets even if the room is completed")))
                                .binding(false, () -> get().renderComplete, v -> get().renderComplete = v)
                                .controller(TickBoxControllerBuilder::create)
                                .build())
                        .option(Option.<LineType>createBuilder()
                                .name(Text.literal("Line Type"))
                                .binding(LineType.LINES, () -> get().lineType, v -> get().lineType = v)
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(LineType.class))
                                .build())
                        .option(Option.<ParticleType>createBuilder()
                                .name(Text.literal("Particle Type"))
                                .binding(ParticleType.FLAME, () -> get().particles, v -> get().particles = v)
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ParticleType.class))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Line Width"))
                                .binding(5, () -> get().width, v -> get().width = v)
                                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Routes Data"))
                                .option(Option.<RouteType>createBuilder()
                                        .name(Text.literal("Route Type"))
                                        .binding(RouteType.PEARLS, () -> get().routeType, v -> get().routeType = v)
                                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(RouteType.class))
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Text.literal("Update Routes"))
                                        .text(Text.literal("Download"))
                                        .action((yacl, opt) -> {
                                            if (get().routeType == RouteType.PEARLS) RouteUtils.updatePearlRoutes();
                                            else RouteUtils.updateRoutes();
                                        })
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Text.literal("Import Routes"))
                                        .text(Text.literal("Import"))
                                        .action((yacl, opt) -> {
                                            try {
                                                File file = FileUtils.promptUserForFile();
                                                if (file != null) FileUtils.copyFileToDirectory(file, Main.ROUTES_PATH);
                                            } catch (Exception e) {
                                                LogUtils.error(e);
                                            }
                                        })
                                        .build())
                                .build())
                        .build())
                // Rendering
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Rendering"))
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Colors"))
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Line Color"))
                                        .binding(Color.RED, () -> get().lineColor, v -> get().lineColor = v)
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Pearl Line Color"))
                                        .binding(Color.CYAN, () -> get().pearlLineColor, v -> get().pearlLineColor = v)
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Etherwarp Color"))
                                        .binding(new Color(128, 0, 128), () -> get().etherWarp, v -> get().etherWarp = v)
                                        .controller(ColorControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Toggles"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Render Etherwarps"))
                                        .binding(true, () -> get().renderEtherwarps, v -> get().renderEtherwarps = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Render Enderpearls"))
                                        .binding(true, () -> get().renderEnderpearls, v -> get().renderEnderpearls = v)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                // Recording
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Recording"))
                        .option(ButtonOption.createBuilder()
                                .name(Text.literal("Start Recording"))
                                .action((yacl, opt) -> Main.routeRecording.startRecording())
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.literal("Stop Recording"))
                                .action((yacl, opt) -> Main.routeRecording.stopRecording())
                                .build())
                        .option(ButtonOption.createBuilder()
                                .name(Text.literal("Set Bat Waypoint"))
                                .action((yacl, opt) -> {
                                    if (Main.routeRecording.recording) {
                                        var player = MinecraftClient.getInstance().player;
                                        if (player != null) {
                                            BlockPos targetPos = player.getBlockPos().add(-1, 2, -1);
                                            Main.routeRecording.addWaypoint(Room.SECRET_TYPES.BAT, targetPos);
                                            Main.routeRecording.newSecret();
                                            Main.routeRecording.setRecordingMessage("Added bat secret waypoint.");
                                        }
                                    }
                                })
                                .build())
                        .build())
                // Hud
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("HUD Locations"))
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Recording HUD X"))
                                .binding(10, () -> get().recordingHudX, v -> get().recordingHudX = v)
                                .controller(IntegerFieldControllerBuilder::create)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Recording HUD Y"))
                                .binding(10, () -> get().recordingHudY, v -> get().recordingHudY = v)
                                .controller(IntegerFieldControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.literal("Recording HUD Color"))
                                .binding(Color.WHITE, () -> get().recordingHudColor, v -> get().recordingHudColor = v)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .build())
                .save(HANDLER::save)
                .build()
                .generateScreen(parent);
    }

    public enum LineType implements NameableEnum {
        PARTICLES("Particles"), LINES("Lines"), NONE("None");
        private final String name;

        LineType(String name) {
            this.name = name;
        }

        @Override
        public Text getDisplayName() {
            return Text.literal(name);
        }
    }

    public enum RouteType implements NameableEnum {
        NO_PEARLS("No Pearls"), PEARLS("Pearls");
        private final String name;

        RouteType(String name) {
            this.name = name;
        }

        @Override
        public Text getDisplayName() {
            return Text.literal(name);
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
        public Text getDisplayName() {
            return Text.literal(name);
        }
    }

    public enum TextColor implements NameableEnum {
        BLACK("Black", Formatting.BLACK), DARK_BLUE("Dark Blue", Formatting.DARK_BLUE),
        DARK_GREEN("Dark Green", Formatting.DARK_GREEN), DARK_AQUA("Dark Aqua", Formatting.DARK_AQUA),
        DARK_RED("Dark Red", Formatting.DARK_RED), DARK_PURPLE("Dark Purple", Formatting.DARK_PURPLE),
        GOLD("Gold", Formatting.GOLD), GRAY("Gray", Formatting.GRAY),
        DARK_GRAY("Dark Gray", Formatting.DARK_GRAY), BLUE("Blue", Formatting.BLUE),
        GREEN("Green", Formatting.GREEN), AQUA("Aqua", Formatting.AQUA),
        RED("Red", Formatting.RED), LIGHT_PURPLE("Light Purple", Formatting.LIGHT_PURPLE),
        YELLOW("Yellow", Formatting.YELLOW), WHITE("White", Formatting.WHITE);

        public final Formatting formatting;
        private final String name;

        TextColor(String name, Formatting formatting) {
            this.name = name;
            this.formatting = formatting;
        }

        @Override
        public Text getDisplayName() {
            return Text.literal(name).formatted(formatting);
        }
    }

    // GUI

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
        public Text getDisplayName() {
            return Text.literal(name);
        }
    }
}