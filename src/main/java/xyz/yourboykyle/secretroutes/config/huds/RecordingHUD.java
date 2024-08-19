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
        super("", true);
    }

    @Override
    public String getText(boolean example) {
        if(Main.routeRecording.recording){
            /*
            if(recording.message != ""){
                return recording.message
            }

             */
            return "Recording...";
        }
        return "";
    }
}