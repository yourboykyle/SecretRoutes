//#if FORGE && MC == 1.8.9
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

package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.polyfrost.polyui.color.PolyColor;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;

import java.util.List;

public class RenderUtils {
    private static final ResourceLocation beaconBeam = new ResourceLocation("textures/entity/beacon_beam.png");
    public static void drawBoxAtBlock(double x, double y, double z, PolyColor color, double width, double height, double alpha) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(3);
//        GlStateManager.pushAttrib();
//        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
//        GlStateManager.disableLighting();

        GL11.glTranslated(x, y, z);

        GL11.glColor4f(color.red() / 255.0f, color.green() / 255.0f, color.blue() / 255.0f, (float) alpha);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(width, height, width);
        GL11.glVertex3d(width, height, 0);
        GL11.glVertex3d(0, height, 0);
        GL11.glVertex3d(0, height, width);
        GL11.glVertex3d(width, height, width);
        GL11.glVertex3d(width, 0, width);
        GL11.glVertex3d(width, 0, 0);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, 0, width);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(0, height, 0);
        GL11.glVertex3d(0, 0, 0);
        GL11.glVertex3d(width, 0, 0);
        GL11.glVertex3d(width, height, 0);
        GL11.glVertex3d(width, 0, 0);
        GL11.glVertex3d(width, 0, width);
        GL11.glVertex3d(0, 0, width);
        GL11.glVertex3d(0, height, width);
        GL11.glVertex3d(width, height, width);
        GL11.glEnd();

        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
//        GlStateManager.enableLighting();
//        GlStateManager.popMatrix();
//        GlStateManager.popAttrib();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1, 1, 1, 0);
        GL11.glPopMatrix();
    }

    public static void spawnParticleAtLocation(BlockPos loc, BlockPos offset, EnumParticleTypes particle) {
        World world = Minecraft.getMinecraft().theWorld;

        if (world != null) {
            double x = loc.getX() + 0.5;
            double y = loc.getY() + 0.5;
            double z = loc.getZ() + 0.5;

            double offsetX = offset.getX();
            double offsetY = offset.getY();
            double offsetZ = offset.getZ();

            world.spawnParticle(particle, x, y, z, offsetX, offsetY, offsetZ);
        }
    }

    public static void drawLineParticles(BlockPos loc1, BlockPos loc2, EnumParticleTypes particle) {
        double distanceX = loc2.getX() - loc1.getX();
        double distanceY = loc2.getY() - loc1.getY();
        double distanceZ = loc2.getZ() - loc1.getZ();

        double maxDistance = Math.max(Math.abs(distanceX), Math.abs(distanceZ));
        int maxPoints = (int) Math.ceil(maxDistance * 1);

        double deltaX = distanceX / (double) maxPoints;
        double deltaY = distanceY / (double) maxPoints;
        double deltaZ = distanceZ / (double) maxPoints;

        double x = loc1.getX();
        double y = loc1.getY();
        double z = loc1.getZ();

        for (int i = 0; i <= maxPoints; i++) {
            //double offsetRot = Math.atan2 (distanceX, distanceY);
            //double offsetX = Math.cos(offsetRot)*0.25;
            //double offsetZ = Math.sin(offsetRot)*0.25;

            spawnParticleAtLocation(new BlockPos(x, y, z), new BlockPos(0, 0, 0), particle);

            x += deltaX;
            y += deltaY;
            z += deltaZ;
        }
    }

    public static void drawLineMultipleParticles(EnumParticleTypes particle, List<BlockPos> locations) {
        if(locations == null) {
            return;
        }
        if(locations.size() >= 2) {
            BlockPos lastLoc = null;
            for (BlockPos loc : locations) {
                if (lastLoc == null) {
                    lastLoc = loc;
                    continue;
                }

                drawLineParticles(lastLoc, loc, particle);
                lastLoc = loc;
            }
        }
    }

    public static void drawMultipleNormalLines(List<Triple<Double, Double, Double>> locations, float partialTicks, PolyColor color, int width) {
        if(locations == null) {
            return;
        }
        if(locations.size() >= 2) {
            Triple<Double, Double, Double> lastLoc = null;
            for (Triple<Double, Double, Double> loc : locations) {
                if (lastLoc == null) {
                    lastLoc = loc;
                    continue;
                }

                drawNormalLine(lastLoc.getOne(), lastLoc.getTwo(), lastLoc.getThree(), loc.getOne(), loc.getTwo(), loc.getThree(), color, partialTicks, !SRMConfig.renderLinesThroughWalls, width);
                lastLoc = loc;
            }
        }
    }

    //
    public static void drawNormalLine(double x1, double y1, double z1, BlockPos pos, PolyColor color, float partialTicks, boolean depth, int width) {
        drawNormalLine(x1, y1, z1, pos.getX(), pos.getY(), pos.getZ(), color, partialTicks, depth, width);
    }
    public static void drawNormalLine(double x1, double y1, double z1, double x2, double y2, double z2, PolyColor colour, float partialTicks, boolean depth, int width) {
        Entity render = Minecraft.getMinecraft().getRenderViewEntity();
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

        double realX = render.lastTickPosX + (render.posX - render.lastTickPosX) * partialTicks;
        double realY = render.lastTickPosY + (render.posY - render.lastTickPosY) * partialTicks;
        double realZ = render.lastTickPosZ + (render.posZ - render.lastTickPosZ) * partialTicks;


        GlStateManager.pushMatrix();
        GlStateManager.translate(-realX, -realY, -realZ);
        GlStateManager.disableTexture2D();
        GlStateManager.enableDepth();
        if (!depth) {
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
        }
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glLineWidth(width);
        GlStateManager.color(colour.red() / 255f, colour.green() / 255f, colour.blue()/ 255f, colour.getAlpha() / 255f);
        worldRenderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        worldRenderer.pos(x1, y1, z1).endVertex();
        worldRenderer.pos(x2, y2, z2).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.translate(realX, realY, realZ);
        GlStateManager.disableBlend();
        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableDepth(); //
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    public static void drawNormalLine(BlockPos pos1, BlockPos pos2, PolyColor color, float partialTicks, boolean depth, int width){
        drawNormalLine(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), color, partialTicks, depth, width);
    }



    public static void drawText(String text, BlockPos pos, float partialTicks, Boolean depth, Boolean shadow, Float scale) {
        Minecraft mc = Minecraft.getMinecraft();
        RenderManager rm = mc.getRenderManager();
        FontRenderer fr = mc.fontRendererObj;
        EntityPlayerSP player = mc.thePlayer;

        double viewerPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double viewerPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double viewerPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        double posX = pos.getX() - viewerPosX + 0.5;
        double posY = pos.getY() - viewerPosY - player.getEyeHeight();
        double posZ = pos.getZ() - viewerPosZ + 0.5;

        double distance = Math.sqrt(posX * posX + posY * posY + posZ * posZ);

        GlStateManager.pushMatrix();
//        GlStateManager.enableTexture2D();
        GlStateManager.translate(posX, posY, posZ);
        GlStateManager.translate(0, player.getEyeHeight(), 0);
        GlStateManager.rotate(-rm.playerViewY, 0, 1, 0);
        GlStateManager.rotate(rm.playerViewX, 1, 0, 0);
        GlStateManager.scale(-Constants.baseScale * scale, -Constants.baseScale * scale, -Constants.baseScale * scale);

        float constantScaleFactor = (float) (distance * Constants.distanceScaleFactor);
        GlStateManager.scale(1 + constantScaleFactor, 1 + constantScaleFactor, 1 + constantScaleFactor);

        GlStateManager.disableLighting();
        if (!depth) {
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        float width = fr.getStringWidth(text) / 2.0f;
        fr.drawString(text, -width, 0f, 0xFFFFFF, shadow);

        if (!depth) {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
        }
        GlStateManager.disableBlend();
//        GlStateManager.disableTexture2D();
        GlStateManager.popMatrix();
    }

    public static void drawFromPlayer(EntityPlayerSP p, BlockPos pos, PolyColor color, float partialticks, int width){

        double px = p.prevPosX + (p.posX - p.prevPosX)*partialticks;
        double py = p.prevPosY + (p.posY - p.prevPosY)*partialticks;
        double pz = p.prevPosZ + (p.posZ - p.prevPosZ)*partialticks;



        drawNormalLine(px, py+p.getEyeHeight(), pz, pos.getX()+0.5, pos.getY(), pos.getZ() +0.5, color, partialticks, !SRMConfig.renderLinesThroughWalls, width);
    }

    public static void drawFromPlayer(EntityPlayerSP p, double x, double y, double z, PolyColor color, float partialticks, int width){

        double px = p.prevPosX + (p.posX - p.prevPosX)*partialticks;
        double py = p.prevPosY + (p.posY - p.prevPosY)*partialticks;
        double pz = p.prevPosZ + (p.posZ - p.prevPosZ)*partialticks;



        drawNormalLine(px, py+p.getEyeHeight(), pz, x+0.5, y, z+0.5, color, partialticks, !SRMConfig.renderLinesThroughWalls, width);
    }

}
//#endif
