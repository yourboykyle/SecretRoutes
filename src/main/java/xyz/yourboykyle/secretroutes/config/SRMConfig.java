package xyz.yourboykyle.secretroutes.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
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

    public SRMConfig() {
        super(new Mod(Main.MODID, ModType.SKYBLOCK), Main.MODID + ".json");
        initialize();
    }
}