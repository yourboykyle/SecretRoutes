package xyz.yourboykyle.secretroutes.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Button;
import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Header;
import cc.polyfrost.oneconfig.config.core.OneColor;

public class ColorsPage {
    @Header(
            text = "Colors",
            size = 2
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

    @Button(
            name = "Reset to default colors",
            text = "Reset"
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
