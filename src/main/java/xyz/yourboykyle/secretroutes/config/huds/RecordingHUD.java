package xyz.yourboykyle.secretroutes.config.huds;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import xyz.yourboykyle.secretroutes.Main;

public class RecordingHUD extends SingleTextHud {
    @Color(
            name = "Default HUD colour"
    )
    public static OneColor hudColour = new OneColor(255, 255, 255);

    public RecordingHUD(){
        super("", false);
    }

    @Override
    public String getText(boolean example) {
        if(Main.routeRecording.recording){
            return Main.routeRecording.recordingMessage;
        }
        return "";
    }

    public void enable() {
        this.enabled = true;
        // Cant be accessed from static context
    }

    public void disable() {
        this.enabled = false;
    }
}