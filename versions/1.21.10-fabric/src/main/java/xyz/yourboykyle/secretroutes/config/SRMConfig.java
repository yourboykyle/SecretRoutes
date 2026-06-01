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
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ConfigUtils;
import xyz.yourboykyle.secretroutes.utils.RouteUtils;

import java.awt.Color;
import java.io.File;

public class SRMConfig {

    public static final ConfigClassHandler<SRMConfig> HANDLER = ConfigClassHandler.createBuilder(SRMConfig.class)
            .id(Identifier.of(Main.MODID, "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("SecretRoutes/xyz.yourboykyle.secretroutes.config.json"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    // General
    @SerialEntry public boolean modEnabled = true;
    @SerialEntry public RouteType routeType = RouteType.PEARLS;
    @SerialEntry public boolean renderComplete = false;
    @SerialEntry public boolean wholeRoute = false;
    @SerialEntry public boolean allSteps = false;
    @SerialEntry public boolean allSecrets = false;
    @SerialEntry public boolean trackPersonalBests = true;
    @SerialEntry public boolean sendChatMessages = true;

    // Visual
    @SerialEntry public LineType lineType = LineType.LINES;
    @SerialEntry public int width = 5;
    @SerialEntry public Color lineColor = new Color(255, 0, 0);
    @SerialEntry public boolean renderLinesThroughWalls = true;
    @SerialEntry public ParticleType particles = ParticleType.FLAME;
    @SerialEntry public double particleDensity = 2.0;
    @SerialEntry public float filledBoxAlpha = 0.5f;
    @SerialEntry public float boxLineWidth = 4f;
    @SerialEntry public int tickInterval = 1;
    @SerialEntry public boolean playerWaypointLine = false;
    @SerialEntry public boolean playerToEtherwarp = false;

    // Colours and toggles
    @SerialEntry public boolean renderEtherwarps = true;
    @SerialEntry public boolean etherwarpFullBlock = false;
    @SerialEntry public Color etherWarp = new Color(128, 0, 128);

    @SerialEntry public boolean renderMines = true;
    @SerialEntry public boolean mineFullBlock = false;
    @SerialEntry public Color mine = new Color(255, 255, 0);

    @SerialEntry public boolean renderSuperboom = true;
    @SerialEntry public boolean superboomsFullBlock = false;
    @SerialEntry public Color superbooms = new Color(255, 0, 0);

    @SerialEntry public boolean renderInteracts = true;
    @SerialEntry public boolean interactsFullBlock = false;
    @SerialEntry public Color interacts = new Color(0, 0, 255);

    // Secrets
    @SerialEntry public boolean renderSecretsItem = true;
    @SerialEntry public boolean secretsItemFullBlock = false;
    @SerialEntry public Color secretsItem = new Color(0, 255, 255);

    @SerialEntry public boolean renderSecretIteract = true;
    @SerialEntry public boolean secretsInteractFullBlock = false;
    @SerialEntry public Color secretsInteract = new Color(0, 0, 255);

    @SerialEntry public boolean renderSecretBat = true;
    @SerialEntry public boolean secretsBatFullBlock = false;
    @SerialEntry public Color secretsBat = new Color(0, 255, 0);

    // Ender pearls
    @SerialEntry public boolean renderEnderpearls = true;
    @SerialEntry public boolean enderpearlFullBlock = false;
    @SerialEntry public Color enderpearls = new Color(0, 255, 255);
    @SerialEntry public int pearlLineWidth = 5;
    @SerialEntry public Color pearlLineColor = new Color(0, 255, 255);

    // text
    @SerialEntry public boolean startTextToggle = true;
    @SerialEntry public TextColor startWaypointColor = TextColor.RED;
    @SerialEntry public float startTextSize = 1.0f;

    @SerialEntry public boolean exitTextToggle = true;
    @SerialEntry public TextColor exitWaypointColor = TextColor.RED;
    @SerialEntry public float exitTextSize = 1.0f;

    @SerialEntry public boolean etherwarpsTextToggle = false;
    @SerialEntry public boolean etherwarpsEnumToggle = false;
    @SerialEntry public TextColor etherwarpsWaypointColor = TextColor.DARK_PURPLE;
    @SerialEntry public float etherwarpsTextSize = 1.0f;

    @SerialEntry public boolean minesTextToggle = false;
    @SerialEntry public boolean minesEnumToggle = false;
    @SerialEntry public TextColor minesWaypointColor = TextColor.YELLOW;
    @SerialEntry public float minesTextSize = 1.0f;

    @SerialEntry public boolean interactsTextToggle = true;
    @SerialEntry public boolean interactsEnumToggle = false;
    @SerialEntry public TextColor interactsWaypointColor = TextColor.BLUE;
    @SerialEntry public float interactsTextSize = 1.0f;

    @SerialEntry public boolean superboomsTextToggle = true;
    @SerialEntry public boolean superboomsEnumToggle = false;
    @SerialEntry public TextColor superboomsWaypointColor = TextColor.RED;
    @SerialEntry public float superboomsTextSize = 1.0f;

    @SerialEntry public boolean enderpearlTextToggle = true;
    @SerialEntry public boolean enderpearlEnumToggle = false;
    @SerialEntry public TextColor enderpearlWaypointColor = TextColor.AQUA;
    @SerialEntry public float enderpearlTextSize = 1.0f;

    @SerialEntry public boolean interactTextToggle = true;
    @SerialEntry public TextColor interactWaypointColor = TextColor.BLUE;
    @SerialEntry public float interactTextSize = 1.0f;

    @SerialEntry public boolean itemTextToggle = true;
    @SerialEntry public TextColor itemWaypointColor = TextColor.GREEN;
    @SerialEntry public float itemTextSize = 1.0f;

    @SerialEntry public boolean batTextToggle = true;
    @SerialEntry public TextColor batWaypointColor = TextColor.GREEN;
    @SerialEntry public float batTextSize = 1.0f;

    @SerialEntry public boolean autoCheckUpdates = true;
    @SerialEntry public boolean autoDownload = false;
    @SerialEntry public boolean autoUpdateRoutes = false;

    @SerialEntry public boolean customSecretSound = false;
    @SerialEntry public SoundType customSecretSoundType = SoundType.ZYRA_MEOW;
    @SerialEntry public float customSecretSoundVolume = 1.0f;
    @SerialEntry public float customSecretSoundPitch = 1.0f;
    @SerialEntry public boolean renderBlood = false;
    @SerialEntry public boolean bloodNotif = false;
    @SerialEntry public String bloodReadyText = "Blood Ready";
    @SerialEntry public TextColor bloodReadyColor = TextColor.GOLD;
    @SerialEntry public int bloodBannerDuration = 3000;
    @SerialEntry public int bloodScale = 2;
    @SerialEntry public int bloodX = 0;
    @SerialEntry public int bloodY = -100;

    // Boss Hiding
    @SerialEntry public boolean hideBossMessages = false;
    @SerialEntry public boolean hideWatcher = true;
    @SerialEntry public boolean hideBonzo = true;
    @SerialEntry public boolean hideScarf = true;
    @SerialEntry public boolean hideProfessor = true;
    @SerialEntry public boolean hideThorn = true;
    @SerialEntry public boolean hideLivid = true;
    @SerialEntry public boolean hideSadan = true;
    @SerialEntry public boolean hideWitherLords = false;

    // Recording and Dev
    @SerialEntry public int recordingHudX = 10;
    @SerialEntry public int recordingHudY = 10;
    @SerialEntry public Color recordingHudColor = new Color(255, 255, 255);
    //@SerialEntry public int etherwarpPing = 150;

    // Dev
    @SerialEntry public boolean verboseLogging = false;
    @SerialEntry public boolean verboseRecording = true;
    @SerialEntry public boolean verboseUpdating = true;
    @SerialEntry public boolean verboseInfo = false;
    @SerialEntry public boolean verboseRendering = false;
    @SerialEntry public boolean bridge = false;
    @SerialEntry public boolean disableServerChecking = false;
    @SerialEntry public boolean forceUpdateDEBUG = false;
    @SerialEntry public boolean sendData = true;
    // Unused
    /*@SerialEntry public boolean warnKeybindsOutsideDungeon = true;
    @SerialEntry public boolean playerWaypointLine = false;
    @SerialEntry public boolean playerToEtherwarp = false;
    @SerialEntry public boolean debug = false;*/
    @SerialEntry public boolean actionbarInfo = false;
    @SerialEntry public boolean verbosePersonalBests = false;

    @SerialEntry public String routesFileName = "routes.json";
    @SerialEntry public String pearlRoutesFileName = "pearlroutes.json";
    @SerialEntry public String copyFileName = "default";
    @SerialEntry public int routeNumber = 0;
    // Unused
    // @SerialEntry public String colorProfileName = "default.json";

    public static SRMConfig get() {
        return HANDLER.instance();
    }

    public static Screen getScreen(Screen parent) {
        var builder = YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Secret Routes Config"));

        // General
        builder.category(ConfigCategory.createBuilder()
                .name(Text.literal("General"))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.literal("Mod Enabled"))
                        .binding(true, () -> get().modEnabled, v -> get().modEnabled = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<RouteType>createBuilder()
                        .name(Text.literal("Route Type"))
                        .binding(RouteType.PEARLS, () -> get().routeType, v -> get().routeType = v)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(RouteType.class))
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.literal("Render Completed Rooms"))
                        .description(OptionDescription.of(Text.literal("Renders secrets even if the room is cleared.")))
                        .binding(false, () -> get().renderComplete, v -> get().renderComplete = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.literal("Show Whole Route"))
                        .description(OptionDescription.of(Text.literal("Render all steps at once instead of sequential.")))
                        .binding(false, () -> get().wholeRoute, v -> get().wholeRoute = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.literal("Show All Secrets"))
                        .description(OptionDescription.of(Text.literal("Highlight all secrets in the room, not just the route.")))
                        .binding(false, () -> get().allSecrets, v -> get().allSecrets = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(ButtonOption.createBuilder()
                        .name(Text.literal("Update Routes"))
                        .text(Text.literal("Download"))
                        .action((yacl, opt) -> {
                            if (get().routeType == RouteType.PEARLS) RouteUtils.updatePearlRoutes();
                            else RouteUtils.updateRoutes();
                        })
                        .build())
                .build());

        // Visuals
        builder.category(ConfigCategory.createBuilder()
                .name(Text.literal("Visuals"))
                .option(Option.<LineType>createBuilder()
                        .name(Text.literal("Line Style"))
                        .binding(LineType.LINES, () -> get().lineType, v -> get().lineType = v)
                        .controller(opt -> EnumControllerBuilder.create(opt).enumClass(LineType.class))
                        .build())
                .option(Option.<Integer>createBuilder()
                        .name(Text.literal("Line Width"))
                        .binding(5, () -> get().width, v -> get().width = v)
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 10).step(1))
                        .build())
                .option(Option.<Color>createBuilder()
                        .name(Text.literal("Line Color"))
                        .binding(Color.RED, () -> get().lineColor, v -> get().lineColor = v)
                        .controller(ColorControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.literal("See Through Walls"))
                        .binding(true, () -> get().renderLinesThroughWalls, v -> get().renderLinesThroughWalls = v)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("Particles"))
                        .option(Option.<ParticleType>createBuilder()
                                .name(Text.literal("Type"))
                                .binding(ParticleType.FLAME, () -> get().particles, v -> get().particles = v)
                                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ParticleType.class))
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Text.literal("Density"))
                                .binding(2.0, () -> get().particleDensity, v -> get().particleDensity = v)
                                .controller(opt -> DoubleSliderControllerBuilder.create(opt).range(0.1, 10.0).step(0.1))
                                .build())
                        .build())
                .build());

        // Components
        builder.category(ConfigCategory.createBuilder()
                .name(Text.literal("Components"))
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("Etherwarps"))
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Enabled")).binding(true, () -> get().renderEtherwarps, v -> get().renderEtherwarps = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<Color>createBuilder().name(Text.literal("Color")).binding(new Color(128, 0, 128), () -> get().etherWarp, v -> get().etherWarp = v).controller(ColorControllerBuilder::create).build())
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Full Block")).binding(false, () -> get().etherwarpFullBlock, v -> get().etherwarpFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("Secrets"))
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Items")).binding(true, () -> get().renderSecretsItem, v -> get().renderSecretsItem = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<Color>createBuilder().name(Text.literal("Item Color")).binding(new Color(0, 255, 255), () -> get().secretsItem, v -> get().secretsItem = v).controller(ColorControllerBuilder::create).build())
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Interacts")).binding(true, () -> get().renderSecretIteract, v -> get().renderSecretIteract = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<Color>createBuilder().name(Text.literal("Interact Color")).binding(new Color(0, 0, 255), () -> get().secretsInteract, v -> get().secretsInteract = v).controller(ColorControllerBuilder::create).build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("Mines"))
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Enabled")).binding(true, () -> get().renderMines, v -> get().renderMines = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<Color>createBuilder().name(Text.literal("Color")).binding(new Color(255, 255, 0), () -> get().mine, v -> get().mine = v).controller(ColorControllerBuilder::create).build())
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Full Block")).binding(false, () -> get().mineFullBlock, v -> get().mineFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("Superbooms"))
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Enabled")).binding(true, () -> get().renderSuperboom, v -> get().renderSuperboom = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<Color>createBuilder().name(Text.literal("Color")).binding(new Color(255, 0, 0), () -> get().superbooms, v -> get().superbooms = v).controller(ColorControllerBuilder::create).build())
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Full Block")).binding(false, () -> get().superboomsFullBlock, v -> get().superboomsFullBlock = v).controller(TickBoxControllerBuilder::create).build())
                        .build())
                .build());

        // Text Settings
        builder.category(ConfigCategory.createBuilder()
                .name(Text.literal("Text Settings"))
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("Etherwarps Text"))
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Show")).binding(false, () -> get().etherwarpsTextToggle, v -> get().etherwarpsTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Numbering")).binding(false, () -> get().etherwarpsEnumToggle, v -> get().etherwarpsEnumToggle = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<TextColor>createBuilder().name(Text.literal("Color")).binding(TextColor.DARK_PURPLE, () -> get().etherwarpsWaypointColor, v -> get().etherwarpsWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                        .option(Option.<Float>createBuilder().name(Text.literal("Size")).binding(1.0f, () -> get().etherwarpsTextSize, v -> get().etherwarpsTextSize = v).controller(opt -> FloatSliderControllerBuilder.create(opt).range(0.1f, 5f).step(0.1f)).build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("Interacts Text"))
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Show")).binding(true, () -> get().interactsTextToggle, v -> get().interactsTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<TextColor>createBuilder().name(Text.literal("Color")).binding(TextColor.BLUE, () -> get().interactsWaypointColor, v -> get().interactsWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.literal("Start/Exit"))
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Show Start")).binding(true, () -> get().startTextToggle, v -> get().startTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<TextColor>createBuilder().name(Text.literal("Start Color")).binding(TextColor.RED, () -> get().startWaypointColor, v -> get().startWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                        .option(Option.<Boolean>createBuilder().name(Text.literal("Show Exit")).binding(true, () -> get().exitTextToggle, v -> get().exitTextToggle = v).controller(TickBoxControllerBuilder::create).build())
                        .option(Option.<TextColor>createBuilder().name(Text.literal("Exit Color")).binding(TextColor.RED, () -> get().exitWaypointColor, v -> get().exitWaypointColor = v).controller(opt -> EnumControllerBuilder.create(opt).enumClass(TextColor.class)).build())
                        .build())
                .build());

        var profilesGroup = OptionGroup.createBuilder()
                .name(Text.literal("Load Existing Profile"));

        File[] profileFiles = ConfigUtils.COLOR_PROFILE_DIR.listFiles((dir, name) -> name.endsWith(".json"));
        if (profileFiles != null) {
            for (File file : profileFiles) {
                String profileName = file.getName().replace(".json", "");
                profilesGroup.option(ButtonOption.createBuilder()
                        .name(Text.literal("Load: " + profileName))
                        .description(OptionDescription.of(Text.literal("Loads " + profileName + ".json and closes menu.")))
                        .action((yacl, opt) -> {
                            ConfigUtils.loadColorConfig(profileName);
                            MinecraftClient.getInstance().setScreen(null);
                        })
                        .build());
            }
        }

        builder.category(ConfigCategory.createBuilder()
                .name(Text.literal("Profiles"))
                .option(Option.<String>createBuilder()
                        .name(Text.literal("Profile Name"))
                        .description(OptionDescription.of(Text.literal("Enter name to Save/Load a specific profile.")))
                        .binding("default", () -> get().copyFileName, v -> get().copyFileName = v)
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(ButtonOption.createBuilder()
                        .name(Text.literal("Save Current Profile"))
                        .description(OptionDescription.of(Text.literal("Saves current settings to the filename above.")))
                        .action((yacl, opt) -> {
                            ConfigUtils.writeColorConfig(get().copyFileName);
                        })
                        .build())
                .option(ButtonOption.createBuilder()
                        .name(Text.literal("Load From Text Input"))
                        .action((yacl, opt) -> {
                            ConfigUtils.loadColorConfig(get().copyFileName);
                            MinecraftClient.getInstance().setScreen(null);
                        })
                        .build())
                .group(profilesGroup.build())
                .build());

        return builder.save(HANDLER::save).build().generateScreen(parent);
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
//#endif
