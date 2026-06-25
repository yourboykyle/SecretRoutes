//#if FABRIC
package xyz.yourboykyle.secretroutes.config.huds;

import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;

// Route recording doesnt work/I have no idea to what in 26.1 this would translate to

public class RecordingHUD {

    /*public void render(DrawContext context) {
        if (Main.routeRecording == null || !Main.routeRecording.recording) {
            return;
        }

        String prefix = "Recording status: ";
        String message = Main.routeRecording.recordingMessage;
        if (message == null) message = "";
        String fullText = prefix + message;

        int x = SRMConfig.get().recordingHudX;
        int y = SRMConfig.get().recordingHudY;

        int color = SRMConfig.get().recordingHudColor.getRGB();

        context.drawTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                Text.literal(fullText),
                x,
                y,
                color
        );
    }*/
}
//#endif
