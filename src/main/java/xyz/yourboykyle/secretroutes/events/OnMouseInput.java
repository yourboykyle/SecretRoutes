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

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
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
                if(item.getDisplayName().toLowerCase().contains("boom")){
                    LogUtils.info("§bPlayer is holding a superboom");
                    if(Main.routeRecording.recording) {
                        Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.TNTS, player);
                    }
                }

                if(item.getDisplayName().toLowerCase().contains("aspect of the void") && button == 1 && player.isSneaking()){
                    LogUtils.info("§bPlayer is holding an aspect of the void");
                    if(Main.routeRecording.recording) {
                        new Thread(()->{
                            try {
                                BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
                                try{
                                    Thread.sleep(SRMConfig.etherwarpPing);
                                } catch (InterruptedException ex) {
                                    LogUtils.error(ex);
                                }
                                BlockPos pos2 = new BlockPos(player.posX, player.posY, player.posZ);

                                if(!pos.toString().equals(pos2.toString())){
                                    LogUtils.info("§bPlayer teleported and moved");
                                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.ETHERWARPS, pos2);
                                }else{
                                    LogUtils.info("§bPlayer teleported and did not move");
                                }
                            } catch (Exception ex) {
                                LogUtils.error(ex);
                            }
                        }).start();
                    }



                }



            }
        } catch(Exception ex) {
            LogUtils.error(ex);
        }
    }
}