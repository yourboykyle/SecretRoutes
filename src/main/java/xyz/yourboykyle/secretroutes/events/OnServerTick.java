package xyz.yourboykyle.secretroutes.events;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;

import java.util.ArrayList;
import java.util.List;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class OnServerTick {
    public static int ticks = 0;
    public static long time = -1;
    public static ArrayList<Integer> times = new ArrayList<>();
    public static int seconds = 0;

    @SubscribeEvent
    public void onServerTick(ServerTickEvent event) {
        if(!event.modid.equals(Main.MODID)){return;}

        ticks++;

        int timeSince = (int) (System.currentTimeMillis()-time);
        seconds += timeSince;

        //sendChatMessage("Tick received: " + (ticks++) +" at: " + timeSince + " ms");
        if(ticks%20==0){
            sendChatMessage("20 ticks received, Time : "+seconds);
            seconds = 0;
        }
        time = System.currentTimeMillis();
    }
}
