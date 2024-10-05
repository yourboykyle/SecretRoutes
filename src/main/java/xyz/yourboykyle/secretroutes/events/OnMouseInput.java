package xyz.yourboykyle.secretroutes.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnMouseInput {

    @SubscribeEvent
    public void onMouseInput(InputEvent.MouseInputEvent e) {
        int button = Mouse.getEventButton();
        boolean action = Mouse.getEventButtonState();

        try {
            if(action){
                EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                if(player == null){
                    return;
                }

                ItemStack item = player.getHeldItem();
                if(item == null) {
                    return;
                }
                if(item.getDisplayName().toLowerCase().contains("ender pearl") && button == 1){
                    LogUtils.info("§bPlayer is holding an ender pearl");
                    if(Main.routeRecording.recording) {
                        Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ENDERPEARLS, player);
                    }
                }
                if(item.getDisplayName().toLowerCase().contains("boom") && button == 0){
                    LogUtils.info("§bPlayer is holding a superboom");
                    if(Main.routeRecording.recording) {
                        Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, player);
                    }
                }

            }
        } catch(Exception ex) {
            LogUtils.error(ex);
        }
    }
}