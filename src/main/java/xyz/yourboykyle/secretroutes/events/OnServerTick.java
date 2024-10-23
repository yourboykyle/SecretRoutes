/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
