package xyz.yourboykyle.secretroutes.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;

public class OnKeyInput {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        try {
            if (Main.lastSecret.isPressed()) {
                Main.currentRoom.lastSecretKeybind();
            } else if (Main.nextSecret.isPressed()) {
                Main.currentRoom.nextSecretKeybind();
            }
        } catch (Exception error) {
            LogUtils.error(error);
        }
    }
}