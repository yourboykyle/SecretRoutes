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

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.GuiUtils;
import xyz.yourboykyle.secretroutes.utils.SecretRoutesRenderUtils;

import static xyz.yourboykyle.secretroutes.utils.SecretUtils.removeBannerTime;

public class OnGuiRender {
    public static Long spawnNotifTime = null;

    public static void register() {
        HudRenderCallback.EVENT.register(OnGuiRender::onHudRender);
    }

    private static void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        if (removeBannerTime != null && System.currentTimeMillis() < removeBannerTime) {
            GuiUtils.displayText(drawContext, "§bSet waypoint at lever", 0, -100, 2);
        }

        if (spawnNotifTime != null || SRMConfig.get().renderBlood) {
            if (SRMConfig.get().renderBlood || System.currentTimeMillis() < spawnNotifTime) {
                GuiUtils.displayText(
                        drawContext,
                        SecretRoutesRenderUtils.getTextColor(SRMConfig.get().bloodReadyColor) + SRMConfig.get().bloodReadyText,
                        SRMConfig.get().bloodX,
                        SRMConfig.get().bloodY,
                        SRMConfig.get().bloodScale
                );
            } else {
                spawnNotifTime = null;
            }
        }
    }

}
