package xyz.yourboykyle.secretroutes.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import xyz.yourboykyle.secretroutes.Main;

public class SRMConfig extends Config {
    @Switch(
            name = "Render Routes",
            size = OptionSize.DUAL
    )
    public static boolean modEnabled = true;

    @Dropdown(
            name = "Line Type",
            options = {"Fire Particles", "Lines"}
    )
    public static int particleType = 0;

    @Slider(
            name = "Line width (not for particles)",
            min = 1,
            max = 10.1F,
            step = 1
    )
    public static int width = 5;

    @Color(
            name="Line color"
    )
    public static OneColor lineColor = new OneColor(255, 0, 0);

    @Color(
            name="EtherWarp"
    )
    public static OneColor etherWarp = new OneColor(128, 0, 128);

    @Color(
            name = "Mine"
    )
    public static OneColor mine = new OneColor(255, 255, 0);

    @Color(
            name ="Interacts"
    )
    public static OneColor interacts = new OneColor(0, 0, 255);

    @Color(
            name="superbooms"
    )
    public static OneColor superbooms = new OneColor(255, 0, 0);

    @Color(
            name = "Secrets - item"
    )
    public static OneColor secretsItem = new OneColor(0, 255, 255);

    @Color(
            name = "Secrets - interact"
    )
    public static OneColor secretsInteract = new OneColor(0, 0, 255);

    @Color(
            name = "Secrets - bat"
    )
    public static OneColor secretsBat = new OneColor(0, 255, 0);

    public SRMConfig() {
        super(new Mod(Main.MODID, ModType.SKYBLOCK), Main.MODID + ".json");
        initialize();
    }
}