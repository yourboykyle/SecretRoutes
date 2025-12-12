// TODO: update this file for multi versioning (1.8.9 -> 1.21.8)
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

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.GuiUitls;
import xyz.yourboykyle.secretroutes.utils.SecretRoutesRenderUtils;

import static xyz.yourboykyle.secretroutes.utils.SecretUtils.removeBannerTime;

public class OnGuiRender {
    public static Long spawnNotifTime = null;

    @SubscribeEvent
    public void onGuiRender(RenderGameOverlayEvent.Text event) {


        if(removeBannerTime != null && System.currentTimeMillis()<removeBannerTime){
            GuiUitls.displayText("§bSet waypoint at lever", 0, -100, 2);
        }

        if(spawnNotifTime != null || SRMConfig.renderBlood){
            if(SRMConfig.renderBlood || System.currentTimeMillis()<spawnNotifTime){
                GuiUitls.displayText(SecretRoutesRenderUtils.getTextColor(SRMConfig.bloodReadyColor)+SRMConfig.bloodReadyText, SRMConfig.bloodX, SRMConfig.bloodY, SRMConfig.bloodScale);
            }else{
                spawnNotifTime = null;
            }
        }

    }

}
