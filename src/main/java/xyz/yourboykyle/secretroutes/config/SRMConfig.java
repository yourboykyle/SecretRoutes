package xyz.yourboykyle.secretroutes.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import cc.polyfrost.oneconfig.config.data.PageLocation;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.pages.ColorsPage;
import xyz.yourboykyle.secretroutes.config.annotations.DynamicDropdownAnnotation;



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

/*
    @DynamicDropdownAnnotation(
            name = "Route Files",
            options = {"1", "2", "3", "4"}
            //options = FileUtils.getRouteFileNamesArray()
    )
    public static String selectedRouteFile;

 */



    @Page(
            name = "Custom Colors",
            location = PageLocation.TOP,
            description = "Custom colors for waypoints and tracking lines"
    )
    public static ColorsPage colours = new ColorsPage();

    public SRMConfig() {
        super(new Mod(Main.MODID, ModType.SKYBLOCK), Main.MODID + ".json");
        initialize();
    }

}