package xyz.yourboykyle.secretroutes.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import xyz.yourboykyle.secretroutes.Main;

public class OnKeyInput {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        try {
            if (Main.lastSecret.isPressed()) {
                Main.currentRoom.lastSecret();
            } else if (Main.nextSecret.isPressed()) {
                Main.currentRoom.nextSecret();
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}