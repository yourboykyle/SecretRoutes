package xyz.yourboykyle.secretroutes.events;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.utils.GuiUitls;

import static xyz.yourboykyle.secretroutes.utils.SecretUtils.removeBannerTime;

public class OnGuiRender {

    @SubscribeEvent
    public void onGuiRender(RenderGameOverlayEvent.Text event) {
        if(removeBannerTime == null){return;}
        if(System.currentTimeMillis()<removeBannerTime){
            GuiUitls.displayText("§bSet waypoint at lever", 0, -100, 2);
        }
    }

}
