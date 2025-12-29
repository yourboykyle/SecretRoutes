//#if FABRIC && MC == 1.21.10
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
 *
 * <DO NOT REMOVE THIS COPYRIGHT NOTICE>
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

import com.google.gson.JsonArray;
import de.hysky.skyblocker.utils.Utils;
import org.polyfrost.oneconfig.api.event.v1.events.PostWorldRenderEvent;
import org.polyfrost.oneconfig.api.event.v1.invoke.impl.Subscribe;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.*;

public class OnWorldRender {
    private final static String verboseTAG = "Rendering";
    public static boolean playCompleteFirst = true;

    @Subscribe
    public void onRenderWorld(PostWorldRenderEvent event) {
        try {
            // Make sure the player is actually in a dungeon
            if (!Utils.isInDungeons() || !SRMConfig.modEnabled) {
                return;
            }

            // Check dungeon game stage (if available in Skyblocker)
            // DungeonManager.gameStage check might need adjustment based on Skyblocker API

            if(OnChatReceive.isAllFound()){
                /*
                if(playCompleteFirst){
                    playCompleteFirst = false;
                    new Thread( ()->{
                        for(int i = 0; i<10; i++){
                            SecretSounds.playLoudSound("note.pling", 1.0f, 1.0f, Minecraft.getMinecraft().thePlayer.getPositionVector());
                            try{
                                Thread.sleep(200);
                            }catch (InterruptedException ignored){

                            }
                        }
                    }).start();
                }

                 */
                if(!SRMConfig.renderComplete){
                    return;
                }
            }else{
                playCompleteFirst = true;
            }

            if(SRMConfig.allSecrets){
                SecretUtils.renderSecrets(event);
            }else if(SRMConfig.wholeRoute){
                JsonArray csr = Main.currentRoom.currentSecretRoute;
                if(csr != null){
                    for(int i = Main.currentRoom.currentSecretIndex; i<csr.size(); i++){
                        SecretUtils.renderingCallback(csr.get(i).getAsJsonObject(), event, i);
                    }
                }

            }else{
                SecretUtils.renderingCallback(Main.currentRoom.currentSecretWaypoints, event, Main.currentRoom.currentSecretIndex);
            }
            if(SecretUtils.renderLever){
                SecretUtils.renderLever(event);
            }


        } catch (Exception e) {
            LogUtils.error(e);
        }
    }

}
//#endif
