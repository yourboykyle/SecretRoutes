/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC & christechs
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

package xyz.yourboykyle.secretroutes.dungeons.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.events.OnWorldRender;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class RenderingBackend {
    private static final float THICKNESS_MULTIPLIER = 0.01f;

    private static final RenderPipeline SEE_THROUGH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(Main.MODID, "see_through_overlay"))
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withCull(false)
                    .build()
    );
    private static final RenderType SEE_THROUGH_LAYER = RenderType.create(
            "secretroutes_see_through",
            RenderSetup.builder(SEE_THROUGH_PIPELINE).createRenderSetup()
    );

    private static final RenderPipeline NORMAL_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(Main.MODID, "normal_overlay"))
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
                    .withCull(false)
                    .build()
    );
    private static final RenderType NORMAL_LAYER = RenderType.create(
            "secretroutes_normal",
            RenderSetup.builder(NORMAL_PIPELINE).createRenderSetup()
    );

    private static final RenderPipeline CURSOR_LINE_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.LINES_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(Main.MODID, "cursor_lines_xray"))
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.Mode.LINES)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .build()
    );
    private static final RenderType CURSOR_LINE_LAYER = RenderType.create(
            "secretroutes_cursor_lines_xray",
            RenderSetup.builder(CURSOR_LINE_PIPELINE).createRenderSetup()
    );

    public static List<RenderTypes.WorldText> worldTexts = new ArrayList<>();
    public static List<RenderTypes.OutlinedBox> outlinedBoxes = new ArrayList<>();
    public static List<RenderTypes.FilledBox> filledBoxes = new ArrayList<>();
    public static List<RenderTypes.Line> lines = new ArrayList<>();
    public static List<RenderTypes.LineFromCursor> linesFromCursor = new ArrayList<>();

    private static final Vector3f SCRATCH_LINE_DIR = new Vector3f();
    private static final Vector3f SCRATCH_CAM_DIR = new Vector3f();
    private static final Vector3f SCRATCH_WIDTH_DIR = new Vector3f();
    private static final BoxMeshCache BOX_MESH_CACHE = new BoxMeshCache();

    public static void register() {
        WorldRenderEvents.END_MAIN.register(RenderingBackend::render);
        WorldRenderEvents.END_MAIN.register(RenderingBackend::cleanup);

        if (FabricLoader.getInstance().isModLoaded("iris")) {
            try {
                IrisApi.getInstance().assignPipeline(SEE_THROUGH_PIPELINE, IrisProgram.BASIC);
                IrisApi.getInstance().assignPipeline(NORMAL_PIPELINE, IrisProgram.BASIC);
                IrisApi.getInstance().assignPipeline(CURSOR_LINE_PIPELINE, IrisProgram.LINES);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private static void render(WorldRenderContext context) {
        OnWorldRender.onRenderWorld();

        PoseStack poseStack = context.matrices();
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.position();
        MultiBufferSource.BufferSource consumers = mc.renderBuffers().bufferSource();

        boolean hasSeeThrough = false;
        boolean hasNormal = false;

        for (RenderTypes.FilledBox fb : filledBoxes) {
            if (hasSeeThrough && hasNormal) break;
            if (fb.throughWalls) hasSeeThrough = true;
            else hasNormal = true;
        }
        for (RenderTypes.OutlinedBox ob : outlinedBoxes) {
            if (hasSeeThrough && hasNormal) break;
            if (ob.throughWalls) hasSeeThrough = true;
            else hasNormal = true;
        }
        for (RenderTypes.Line line : lines) {
            if (hasSeeThrough && hasNormal) break;
            if (line.throughWalls) hasSeeThrough = true;
            else hasNormal = true;
        }

        if (hasSeeThrough) {
            VertexConsumer buffer = consumers.getBuffer(SEE_THROUGH_LAYER);
            Matrix4f matrix = poseStack.last().pose();
            renderFilledBoxes(buffer, matrix, camPos, true);
            renderOutlinedBoxesAsQuads(buffer, matrix, camPos, true);
            renderLinesAsQuads(buffer, matrix, camPos, true);
            consumers.endBatch(SEE_THROUGH_LAYER);
        }

        if (hasNormal) {
            VertexConsumer buffer = consumers.getBuffer(NORMAL_LAYER);
            Matrix4f matrix = poseStack.last().pose();
            renderFilledBoxes(buffer, matrix, camPos, false);
            renderOutlinedBoxesAsQuads(buffer, matrix, camPos, false);
            renderLinesAsQuads(buffer, matrix, camPos, false);
            consumers.endBatch(NORMAL_LAYER);
        }

        if (!linesFromCursor.isEmpty()) {
            VertexConsumer buffer = consumers.getBuffer(CURSOR_LINE_LAYER);
            renderLinesFromCursor(buffer, poseStack.last(), camPos);
            consumers.endBatch(CURSOR_LINE_LAYER);
        }

        if (!worldTexts.isEmpty()) {
            Font font = Minecraft.getInstance().font;
            Quaternionf cameraRotation = camera.rotation();

            for (RenderTypes.WorldText wt : worldTexts) {
                poseStack.pushPose();
                poseStack.translate(wt.position.x - camPos.x, wt.position.y - camPos.y, wt.position.z - camPos.z);
                poseStack.mulPose(cameraRotation);
                poseStack.scale(0.025f, -0.025f, 0.025f);

                Font.DisplayMode displayMode = wt.throughWalls ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
                float xOffset = -font.width(wt.text) / 2f;

                font.drawInBatch(
                        wt.text.getVisualOrderText(),
                        xOffset, 0,
                        0xFFFFFFFF, false, poseStack.last().pose(), consumers,
                        displayMode, 0, 0xF000F0
                );
                consumers.endBatch();
                poseStack.popPose();
            }
        }
    }

    private static void cleanup(WorldRenderContext context) {
        outlinedBoxes.clear();
        filledBoxes.clear();
        lines.clear();
        linesFromCursor.clear();
        worldTexts.clear();
    }

    private static void renderFilledBoxes(VertexConsumer buffer, Matrix4f mat, Vec3 camPos, boolean throughWalls) {
        float cx = (float) camPos.x, cy = (float) camPos.y, cz = (float) camPos.z;
        for (RenderTypes.FilledBox fb : filledBoxes) {
            if (fb.throughWalls != throughWalls) continue;
            float minX = (float) fb.position.x - cx, minY = (float) fb.position.y - cy, minZ = (float) fb.position.z - cz;
            float maxX = minX + (float) fb.boxWidth, maxY = minY + (float) fb.boxHeight, maxZ = minZ + (float) fb.boxWidth;
            drawBoxFacesPrimitive(buffer, mat, minX, minY, minZ, maxX, maxY, maxZ,
                    fb.color.getRed() / 255f, fb.color.getGreen() / 255f, fb.color.getBlue() / 255f, fb.color.getAlpha() / 255f);
        }
    }

    private static void renderOutlinedBoxesAsQuads(VertexConsumer buffer, Matrix4f mat, Vec3 camPos, boolean throughWalls) {
        float cx = (float) camPos.x, cy = (float) camPos.y, cz = (float) camPos.z;

        for (RenderTypes.OutlinedBox ob : outlinedBoxes) {
            if (ob.throughWalls != throughWalls) continue;
            float thickness = Math.max(0.002f, ob.lineWidth * THICKNESS_MULTIPLIER);
            float minX = (float) ob.position.x - cx, minY = (float) ob.position.y - cy, minZ = (float) ob.position.z - cz;
            float width = (float) ob.boxWidth;
            float height = (float) ob.boxHeight;
            float r = ob.color.getRed() / 255f;
            float g = ob.color.getGreen() / 255f;
            float b = ob.color.getBlue() / 255f;
            float a = ob.color.getAlpha() / 255f;

            drawCachedMesh(buffer, mat, BOX_MESH_CACHE.get(width, height, thickness), minX, minY, minZ, r, g, b, a);
        }
    }

    private static void drawCachedMesh(VertexConsumer buffer, Matrix4f mat, float[] vertices, float offsetX, float offsetY, float offsetZ, float r, float g, float b, float a) {
        int light = 15728880;
        for (int i = 0; i < vertices.length; i += 3) {
            buffer.addVertex(mat, vertices[i] + offsetX, vertices[i + 1] + offsetY, vertices[i + 2] + offsetZ)
                    .setColor(r, g, b, a)
                    .setLight(light);
        }
    }

    private static void renderLinesAsQuads(VertexConsumer buffer, Matrix4f mat, Vec3 camPos, boolean throughWalls) {
        float cx = (float) camPos.x, cy = (float) camPos.y, cz = (float) camPos.z;
        for (RenderTypes.Line line : lines) {
            if (line.throughWalls != throughWalls) continue;
            float thickness = Math.max(0.002f, line.lineWidth * THICKNESS_MULTIPLIER);
            drawBillboardLinePrimitive(buffer, mat,
                    (float) line.start.x - cx, (float) line.start.y - cy, (float) line.start.z - cz,
                    (float) line.end.x - cx, (float) line.end.y - cy, (float) line.end.z - cz,
                    line.color.getRed() / 255f, line.color.getGreen() / 255f, line.color.getBlue() / 255f, line.color.getAlpha() / 255f, thickness);
        }
    }

    private static void renderLinesFromCursor(VertexConsumer buffer, PoseStack.Pose pose, Vec3 camPos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
        Vec3 start = mc.player.getEyePosition(partialTick).add(mc.player.getLookAngle().scale(2.0));
        float sx = (float) (start.x - camPos.x);
        float sy = (float) (start.y - camPos.y);
        float sz = (float) (start.z - camPos.z);
        Matrix4f mat = pose.pose();

        for (RenderTypes.LineFromCursor line : linesFromCursor) {
            float ex = (float) (line.point.x - camPos.x);
            float ey = (float) (line.point.y - camPos.y);
            float ez = (float) (line.point.z - camPos.z);
            SCRATCH_LINE_DIR.set(ex - sx, ey - sy, ez - sz);
            if (SCRATCH_LINE_DIR.lengthSquared() < 0.0001f) continue;
            SCRATCH_LINE_DIR.normalize();

            float r = line.color.getRed() / 255f;
            float g = line.color.getGreen() / 255f;
            float b = line.color.getBlue() / 255f;
            float a = line.color.getAlpha() / 255f;
            float width = Math.max(1.0f, line.lineWidth);

            buffer.addVertex(mat, sx, sy, sz)
                    .setNormal(pose, SCRATCH_LINE_DIR.x, SCRATCH_LINE_DIR.y, SCRATCH_LINE_DIR.z)
                    .setColor(r, g, b, a)
                    .setLineWidth(width);
            buffer.addVertex(mat, ex, ey, ez)
                    .setNormal(pose, SCRATCH_LINE_DIR.x, SCRATCH_LINE_DIR.y, SCRATCH_LINE_DIR.z)
                    .setColor(r, g, b, a)
                    .setLineWidth(width);
        }
    }

    private static void drawBillboardLinePrimitive(VertexConsumer buffer, Matrix4f mat, float sx, float sy, float sz, float ex, float ey, float ez, float r, float g, float b, float a, float thickness) {
        SCRATCH_LINE_DIR.set(ex - sx, ey - sy, ez - sz).normalize();
        SCRATCH_CAM_DIR.set(sx, sy, sz);
        if (SCRATCH_CAM_DIR.lengthSquared() < 0.0001f) SCRATCH_CAM_DIR.set(0, 1, 0);
        else SCRATCH_CAM_DIR.normalize();

        SCRATCH_LINE_DIR.cross(SCRATCH_CAM_DIR, SCRATCH_WIDTH_DIR);
        if (SCRATCH_WIDTH_DIR.lengthSquared() < 0.0001f) {
            SCRATCH_WIDTH_DIR.set(1, 0, 0);
            if (Math.abs(SCRATCH_LINE_DIR.x) > 0.9f) SCRATCH_WIDTH_DIR.set(0, 1, 0);
        }

        SCRATCH_WIDTH_DIR.normalize().mul(thickness / 2.0f);
        float wx = SCRATCH_WIDTH_DIR.x, wy = SCRATCH_WIDTH_DIR.y, wz = SCRATCH_WIDTH_DIR.z;

        int light = 15728880;
        buffer.addVertex(mat, sx - wx, sy - wy, sz - wz).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, sx + wx, sy + wy, sz + wz).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, ex + wx, ey + wy, ez + wz).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, ex - wx, ey - wy, ez - wz).setColor(r, g, b, a).setLight(light);
    }

    private static void drawBoxFacesPrimitive(VertexConsumer buffer, Matrix4f mat, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a) {
        int light = 15728880;
        buffer.addVertex(mat, x1, y1, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y1, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y1, z2).setColor(r, g, b, a).setLight(light);

        buffer.addVertex(mat, x1, y2, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x1, y2, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y2, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y2, z1).setColor(r, g, b, a).setLight(light);

        buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x1, y2, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y2, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y1, z1).setColor(r, g, b, a).setLight(light);

        buffer.addVertex(mat, x2, y1, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y2, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x1, y2, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x1, y1, z2).setColor(r, g, b, a).setLight(light);

        buffer.addVertex(mat, x1, y1, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x1, y2, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x1, y2, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setLight(light);

        buffer.addVertex(mat, x2, y1, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y2, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y2, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y1, z2).setColor(r, g, b, a).setLight(light);
    }

    public static void addWorldText(RenderTypes.WorldText worldText) {
        if (!worldTexts.contains(worldText)) worldTexts.add(worldText);
    }

    public static void addOutlinedBox(RenderTypes.OutlinedBox outlinedBox) {
        if (!outlinedBoxes.contains(outlinedBox)) outlinedBoxes.add(outlinedBox);
    }

    public static void addFilledBox(RenderTypes.FilledBox filledBox) {
        if (!filledBoxes.contains(filledBox)) filledBoxes.add(filledBox);
    }

    public static void addLine(RenderTypes.Line line) {
        if (!lines.contains(line)) lines.add(line);
    }

    public static void addLinesFromPoints(org.joml.Vector3d[] points, Color color, float lineWidth, boolean throughWalls) {
        for (int i = 0; i < points.length - 1; i++) {
            RenderTypes.Line line = new RenderTypes.Line(points[i], points[i + 1], color, lineWidth, throughWalls);
            addLine(line);
        }
    }

    public static void addLineFromCursor(RenderTypes.LineFromCursor lineFromCursor) {
        if (!linesFromCursor.contains(lineFromCursor)) linesFromCursor.add(lineFromCursor);
    }
}