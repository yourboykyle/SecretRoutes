package xyz.yourboykyle.secretroutes.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.annotations.Header;
import cc.polyfrost.oneconfig.config.core.OneColor;

public class ColorsPage {
    @Header(
            text = "Colors"
    )
    public static boolean ignored;
    @Header(
            text = ""
    )
    public static boolean ignored2;

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

}
