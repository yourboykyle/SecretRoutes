package xyz.yourboykyle.secretroutes.utils;

import io.github.quantizr.dungeonrooms.utils.WaypointUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.awt.*;

public class SecretRoutesRenderUtils {
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, int R, int G, int B) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        RenderUtils.drawBoxAtBlock(x, y, z, R, G, B, 1, 1, 1);
    }
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, int R, int G, int B, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        RenderUtils.drawBoxAtBlock(x, y, z, R, G, B, width, height, 1);
    }
    public static void drawBoxAtBlock(double worldX, double worldY, double worldZ, int R, int G, int B, int width, int height, int alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        RenderUtils.drawBoxAtBlock(x, y, z, R, G, B, width, height, alpha);
    }

    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, int R, int G, int B) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        int width = 1;
        int height = 1;

        WaypointUtils.drawFilledBoundingBox(new AxisAlignedBB(x - 0.01, y - 0.01, z - 0.01, x + width + 0.01, y + height + 0.01, z + width + 0.01), new Color(R, G, B), 1);
    }
    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, int R, int G, int B, int width, int height) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        WaypointUtils.drawFilledBoundingBox(new AxisAlignedBB(x - 0.01, y - 0.01, z - 0.01, x + width + 0.01, y + height + 0.01, z + width + 0.01), new Color(R, G, B), 0);
    }
    public static void drawFilledBoxAtBlock(double worldX, double worldY, double worldZ, int R, int G, int B, int width, int height, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        WaypointUtils.drawFilledBoundingBox(new AxisAlignedBB(x - 0.01, y - 0.01, z - 0.01, x + width + 0.01, y + height + 0.01, z + width + 0.01), new Color(R, G, B), alpha);
    }

    public static void drawBeaconBeam(double worldX, double worldY, double worldZ, int RGB, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        WaypointUtils.renderBeaconBeam(x, y, z, RGB, alpha, 0);
    }

    public static void drawText(double worldX, double worldY, double worldZ, String text) {
        BlockPos pos = new BlockPos(worldX, worldY, worldZ);

        Minecraft mc = Minecraft.getMinecraft();
        double playerX = mc.getRenderManager().viewerPosX;
        double playerY = mc.getRenderManager().viewerPosY;
        double playerZ = mc.getRenderManager().viewerPosZ;

        double x = worldX - playerX;
        double y = worldY - playerY;
        double z = worldZ - playerZ;

        WaypointUtils.renderWaypointText(text, pos, 0);
    }
}
