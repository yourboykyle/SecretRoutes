/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Pair;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.SecretRoutesRenderUtils;

public class WorldRender {
    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {

        Room room = Main.currentRoom;
        Pair<BlockPos, String> nextPos = room.getNext();
        if(nextPos == null || nextPos.getKey() == null || nextPos.getValue() == null) {
            return;
        }
        BlockPos pos = room.getNext().getKey();

        GlStateManager.disableDepth();
        GlStateManager.disableCull();
        if(room.getNext().getValue().equals("stonk")) {
            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), 255, 255, 0, 1, 1, 0.5f); // Color is yellow
            GlStateManager.disableTexture2D();
            SecretRoutesRenderUtils.drawBeaconBeam(pos.getX(), pos.getY(), pos.getZ(), 0xFFFF00, 0.5f);
            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), "Stonk");
            GlStateManager.enableTexture2D();
        } else if(room.getNext().getValue().equals("aotv")) {
            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), 128, 0, 128, 1, 1, 0.5f); // Color is purple
            GlStateManager.disableTexture2D();
            SecretRoutesRenderUtils.drawBeaconBeam(pos.getX(), pos.getY(), pos.getZ(), 0x800080, 0.5f);
            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), "AOTV");
            GlStateManager.enableTexture2D();
        } else if(room.getNext().getValue().equals("chest")) {
            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), 64, 32, 16, 1, 1, 0.5f); // Color is brown
            GlStateManager.disableTexture2D();
            SecretRoutesRenderUtils.drawBeaconBeam(pos.getX(), pos.getY(), pos.getZ(), 0x402010, 0.5f);
            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), "Chest");
            GlStateManager.enableTexture2D();
        } else if(room.getNext().getValue().equals("item")) {
            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), 0, 0, 255, 1, 1, 0.5f); // Color is blue
            GlStateManager.disableTexture2D();
            SecretRoutesRenderUtils.drawBeaconBeam(pos.getX(), pos.getY(), pos.getZ(), 0x0000FF, 0.5f);
            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), "Item");
            GlStateManager.enableTexture2D();
        } else if(room.getNext().getValue().equals("superboom")) {
            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), 255, 0, 0, 1, 1, 0.5f); // Color is red
            GlStateManager.disableTexture2D();
            SecretRoutesRenderUtils.drawBeaconBeam(pos.getX(), pos.getY(), pos.getZ(), 0xFF0000, 0.5f);
            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), "Superboom");
            GlStateManager.enableTexture2D();
        } else if(room.getNext().getValue().equals("bat")) {
            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), 0, 255, 0, 1, 1, 0.5f); // Color is green
            GlStateManager.disableTexture2D();
            SecretRoutesRenderUtils.drawBeaconBeam(pos.getX(), pos.getY(), pos.getZ(), 0x00FF00, 0.5f);
            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), "Bat");
            GlStateManager.enableTexture2D();
        } else if(room.getNext().getValue().equals("lever")) {
            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), 64, 0, 128, 1, 1, 0.5f); // Color is purple
            GlStateManager.disableTexture2D();
            SecretRoutesRenderUtils.drawBeaconBeam(pos.getX(), pos.getY(), pos.getZ(), 0x400080, 0.5f);
            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), "Lever");
            GlStateManager.enableTexture2D();
        } else if(room.getNext().getValue().equals("wither")) {
            SecretRoutesRenderUtils.drawFilledBoxAtBlock(pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 1, 1, 0.5f); // Color is black
            GlStateManager.disableTexture2D();
            SecretRoutesRenderUtils.drawBeaconBeam(pos.getX(), pos.getY(), pos.getZ(), 0x000000, 0.5f);
            SecretRoutesRenderUtils.drawText(pos.getX(), pos.getY(), pos.getZ(), "Essence");
            GlStateManager.enableTexture2D();
        }
        GlStateManager.disableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
    }
}