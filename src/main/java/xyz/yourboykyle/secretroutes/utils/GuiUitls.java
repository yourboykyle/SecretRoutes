/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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

package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

public class GuiUitls {
    public static void displayText(String text, float posx, float posy, float scale) {
        try {
            FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();

            int textWidth = fr.getStringWidth(text);
            float x = (screenWidth/2.0f-((textWidth*scale)/ 2.0f))+posx;
            float y = screenHeight/2.0f+posy;

            GlStateManager.pushMatrix();

            GlStateManager.enableBlend();
            GlStateManager.disableDepth();

            GlStateManager.translate(x, y, 10);
            GlStateManager.scale(scale, scale, 1f);



            fr.drawString(text, 0, 0, 0xFFFFFF, true);

            GlStateManager.enableDepth();
            GlStateManager.disableBlend();

            GlStateManager.popMatrix();
        } catch (Exception e) {
           LogUtils.error(e);
        }

    }
}
