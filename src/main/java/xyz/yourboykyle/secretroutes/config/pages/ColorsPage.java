package xyz.yourboykyle.secretroutes.config.pages;

import cc.polyfrost.oneconfig.config.annotations.*;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.config.data.OptionSize;

public class ColorsPage {
    private static final String[] colors = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"};
    @Header(
            text = "Colors",
            size = OptionSize.DUAL
    )
    public static boolean ignored;


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

    @Dropdown(
            name = "Start waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL
    )
    public static int startWaypointIndex = 12;

    @Slider(
            name = "Start waypoint text size",
            min = 1,
            max = 10.1F,
            step = 1
    )
    public static int startTextSize = 3;

    @Dropdown(
            name = "Secrets waypoint text color",
            options = {"Black", "Dark blue", "Dark green", "Dark aqua", "Dark red", "Dark purple", "Gold", "Gray", "Dark gray", "Blue", "Green", "Aqua", "Red", "Light purple", "Yellow", "White"},
            size = OptionSize.DUAL
    )

    @Button(
            name = "Reset to default colors",
            text = "Reset",
            size = OptionSize.DUAL
    )
    Runnable runnable = () -> {
        lineColor = new OneColor(255, 0, 0);
        etherWarp = new OneColor(128, 0, 128);
        mine = new OneColor(255, 255, 0);
        interacts = new OneColor(0, 0, 255);
        superbooms = new OneColor(255, 0, 0);
        secretsItem = new OneColor(0, 255, 255);
        secretsInteract = new OneColor(0, 0, 255);
        secretsBat = new OneColor(0, 255, 0);
    };
}