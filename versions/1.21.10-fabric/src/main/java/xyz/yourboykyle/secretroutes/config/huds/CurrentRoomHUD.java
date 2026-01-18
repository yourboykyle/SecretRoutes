package xyz.yourboykyle.secretroutes.config.huds;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.RoomDirectionUtils;

public class CurrentRoomHUD {

    public void render(DrawContext context) {
        if (!Main.routeRecording.recording) return;

        String text = "Room: " + RoomDirectionUtils.roomName();
        int x = 10;
        int y = 10;

        context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, 0xFFFFFF, true);
    }
}