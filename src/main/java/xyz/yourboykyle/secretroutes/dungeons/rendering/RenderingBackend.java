//#if FABRIC
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

package xyz.yourboykyle.secretroutes.dungeons.rendering;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.OnWorldRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RenderingBackend {
    private static final float THICKNESS_MULTIPLIER = 0.01f;
    private static final RenderPipeline SEE_THROUGH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(Main.MODID, "see_through_overlay"))
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
                    .withPrimitiveTopology(PrimitiveTopology.QUADS)
                    .withDepthStencilState(Optional.empty())
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
                    .withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
                    .withPrimitiveTopology(PrimitiveTopology.QUADS)
                    .withCull(false)
                    .build()
    );
    private static final RenderType NORMAL_LAYER = RenderType.create(
            "secretroutes_normal",
            RenderSetup.builder(NORMAL_PIPELINE).createRenderSetup()
    );

    public static List<RenderTypes.WorldText> worldTexts = new ArrayList<>();
    public static List<RenderTypes.OutlinedBox> outlinedBoxes = new ArrayList<>();
    public static List<RenderTypes.FilledBox> filledBoxes = new ArrayList<>();
    public static List<RenderTypes.Line> lines = new ArrayList<>();
    public static List<RenderTypes.LineFromCursor> linesFromCursor = new ArrayList<>();

    private static final Vector3f SCRATCH_LINE_DIR = new Vector3f();
    private static final Vector3f SCRATCH_CAM_DIR = new Vector3f();
    private static final Vector3f SCRATCH_WIDTH_DIR = new Vector3f();

    public static void register() {
        LevelRenderEvents.COLLECT_SUBMITS.register(RenderingBackend::render);
        LevelRenderEvents.END_MAIN.register(RenderingBackend::cleanup);

        if (FabricLoader.getInstance().isModLoaded("iris")) {
            try {
                IrisApi.getInstance().assignPipeline(SEE_THROUGH_PIPELINE, IrisProgram.BASIC);
                IrisApi.getInstance().assignPipeline(NORMAL_PIPELINE, IrisProgram.BASIC);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    private static void render(LevelRenderContext context) {
        OnWorldRender.onRenderWorld();

        PoseStack poseStack = context.poseStack();
        Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
        Vec3 camPos = camera.position();
        OrderedSubmitNodeCollector collector = context.submitNodeCollector();

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
        if (!linesFromCursor.isEmpty()) {
            hasSeeThrough = true;
        }

        if (hasSeeThrough) {
            collector.submitCustomGeometry(poseStack, SEE_THROUGH_LAYER, (pose, buffer) -> {
                Matrix4f matrix = pose.pose();
                renderFilledBoxes(buffer, matrix, camPos, true);
                renderOutlinedBoxesAsQuads(buffer, matrix, camPos, true);
                renderLinesAsQuads(buffer, matrix, camPos, true);
                renderLinesFromCursorAsQuads(buffer, matrix, camPos, true);
            });
        }

        if (hasNormal) {
            collector.submitCustomGeometry(poseStack, NORMAL_LAYER, (pose, buffer) -> {
                Matrix4f matrix = pose.pose();
                renderFilledBoxes(buffer, matrix, camPos, false);
                renderOutlinedBoxesAsQuads(buffer, matrix, camPos, false);
                renderLinesAsQuads(buffer, matrix, camPos, false);
                renderLinesFromCursorAsQuads(buffer, matrix, camPos, false);
            });
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

                collector.submitText(
                        poseStack, xOffset, 0,
                        wt.text.getVisualOrderText(),
                        true, displayMode,
                        0xF000F0, 0xFFFFFFFF, 0, 0
                );
                poseStack.popPose();
            }
        }
    }

    private static void cleanup(LevelRenderContext context) {
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
        float thickness = Math.max(0.002f, SRMConfig.get().boxLineWidth * THICKNESS_MULTIPLIER);
        for (RenderTypes.OutlinedBox ob : outlinedBoxes) {
            if (ob.throughWalls != throughWalls) continue;
            float minX = (float) ob.position.x - cx, minY = (float) ob.position.y - cy, minZ = (float) ob.position.z - cz;
            float maxX = minX + (float) ob.boxWidth, maxY = minY + (float) ob.boxHeight, maxZ = minZ + (float) ob.boxWidth;
            drawBoxEdgesPrimitive(buffer, mat, minX, minY, minZ, maxX, maxY, maxZ,
                    ob.color.getRed() / 255f, ob.color.getGreen() / 255f, ob.color.getBlue() / 255f, ob.color.getAlpha() / 255f, thickness);
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

    private static void renderLinesFromCursorAsQuads(VertexConsumer buffer, Matrix4f mat, Vec3 camPos, boolean throughWalls) {
        if (!throughWalls) return;
        if (linesFromCursor.isEmpty()) return;

        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.mainCamera();

        Quaternionf rot = camera.rotation();
        Vector3f forward = new Vector3f(0, 0, -1);
        rot.transform(forward);

        Vector3f up = new Vector3f(0, 1, 0);
        rot.transform(up);

        for (RenderTypes.LineFromCursor line : linesFromCursor) {
            float ex = (float)(line.point.x - camPos.x);
            float ey = (float)(line.point.y - camPos.y);
            float ez = (float)(line.point.z - camPos.z);

            float startDist = 0.5f;
            float sx = forward.x * startDist;
            float sy = forward.y * startDist;
            float sz = forward.z * startDist;

            SCRATCH_LINE_DIR.set(ex - sx, ey - sy, ez - sz).normalize();

            SCRATCH_LINE_DIR.cross(forward, SCRATCH_WIDTH_DIR);
            if (SCRATCH_WIDTH_DIR.lengthSquared() < 0.0001f) {
                SCRATCH_LINE_DIR.cross(up, SCRATCH_WIDTH_DIR);
            }

            float thickness = Math.max(0.002f, line.lineWidth * THICKNESS_MULTIPLIER);
            SCRATCH_WIDTH_DIR.normalize().mul(thickness / 2.0f);
            float wx = SCRATCH_WIDTH_DIR.x, wy = SCRATCH_WIDTH_DIR.y, wz = SCRATCH_WIDTH_DIR.z;

            float r = line.color.getRed()   / 255f;
            float g = line.color.getGreen() / 255f;
            float b = line.color.getBlue()  / 255f;
            float a = line.color.getAlpha() / 255f;
            int light = 15728880;

            buffer.addVertex(mat, sx - wx, sy - wy, sz - wz).setColor(r, g, b, a).setLight(light);
            buffer.addVertex(mat, sx + wx, sy + wy, sz + wz).setColor(r, g, b, a).setLight(light);
            buffer.addVertex(mat, ex + wx, ey + wy, ez + wz).setColor(r, g, b, a).setLight(light);
            buffer.addVertex(mat, ex - wx, ey - wy, ez - wz).setColor(r, g, b, a).setLight(light);
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

    private static void drawBoxEdgesPrimitive(VertexConsumer buffer, Matrix4f mat, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a, float t) {
        drawBillboardLinePrimitive(buffer, mat, minX, minY, minZ, maxX, minY, minZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, minX, minY, maxZ, minX, minY, minZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, minX, minY, minZ, minX, maxY, minZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a, t);
        drawBillboardLinePrimitive(buffer, mat, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a, t);
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
//#endif