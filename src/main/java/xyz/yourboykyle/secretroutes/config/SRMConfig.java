package xyz.yourboykyle.secretroutes.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import xyz.yourboykyle.secretroutes.Main;

public class SRMConfig extends Config {
    @Switch(
            name = "Main Toggle",
            description = "Enable / disable the mod",
            size = OptionSize.DUAL
    )
    public static boolean modEnabled = true;

    public SRMConfig() {
        super(new Mod(Main.MODID, ModType.SKYBLOCK), Main.MODID + ".json");
        initialize();
    }
}