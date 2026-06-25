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

package xyz.yourboykyle.secretroutes.utils;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.OnWorldRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AnotherRenderingUtil {
    private static final float THICKNESS_MULTIPLIER = 0.01f;
    private static final RenderPipeline SEE_THROUGH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.DEBUG_FILLED_SNIPPET)
                    .withLocation(Identifier.fromNamespaceAndPath(Main.MODID, "see_through_overlay"))
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
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
                    .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
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

    public static void register() {
        LevelRenderEvents.END_MAIN.register(AnotherRenderingUtil::render);

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

        PoseStack matrices = context.poseStack();
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 cameraPos = camera.position();

        try (ByteBufferBuilder allocator = new ByteBufferBuilder(196608)) {
            MultiBufferSource.BufferSource consumers = MultiBufferSource.immediate(allocator);

            renderFilledBoxes(consumers, matrices, cameraPos, true, SEE_THROUGH_LAYER);
            renderOutlinedBoxesAsQuads(consumers, matrices, cameraPos, true, SEE_THROUGH_LAYER);
            renderLinesAsQuads(consumers, matrices, cameraPos, true, SEE_THROUGH_LAYER);
            renderLinesFromCursorAsQuads(consumers, matrices, cameraPos, true, SEE_THROUGH_LAYER);

            renderFilledBoxes(consumers, matrices, cameraPos, false, NORMAL_LAYER);
            renderOutlinedBoxesAsQuads(consumers, matrices, cameraPos, false, NORMAL_LAYER);
            renderLinesAsQuads(consumers, matrices, cameraPos, false, NORMAL_LAYER);
            renderLinesFromCursorAsQuads(consumers, matrices, cameraPos, false, NORMAL_LAYER);

            renderText(consumers, matrices, cameraPos);

            consumers.endBatch();
        }

        outlinedBoxes.clear();
        filledBoxes.clear();
        lines.clear();
        linesFromCursor.clear();
        worldTexts.clear();
    }

    private static void renderFilledBoxes(MultiBufferSource consumers, PoseStack matrices, Vec3 camPos, boolean throughWalls, RenderType layer) {
        VertexConsumer buffer = consumers.getBuffer(layer);
        Matrix4f matrix = matrices.last().pose();

        for (RenderTypes.FilledBox fb : filledBoxes) {
            if (fb.throughWalls != throughWalls) continue;

            AABB box = new AABB(fb.position.x, fb.position.y, fb.position.z,
                    fb.position.x + fb.boxWidth,
                    fb.position.y + fb.boxHeight,
                    fb.position.z + fb.boxWidth)
                    .move(-camPos.x, -camPos.y, -camPos.z);

            float r = fb.color.getRed() / 255f;
            float g = fb.color.getGreen() / 255f;
            float b = fb.color.getBlue() / 255f;
            float a = SRMConfig.get().filledBoxAlpha;

            drawBoxFaces(buffer, matrix, box, r, g, b, a);
        }
    }

    private static void renderOutlinedBoxesAsQuads(MultiBufferSource consumers, PoseStack matrices, Vec3 camPos, boolean throughWalls, RenderType layer) {
        VertexConsumer buffer = consumers.getBuffer(layer);
        Matrix4f matrix = matrices.last().pose();

        float thickness = Math.max(0.002f, SRMConfig.get().boxLineWidth * THICKNESS_MULTIPLIER);

        for (RenderTypes.OutlinedBox ob : outlinedBoxes) {
            if (ob.throughWalls != throughWalls) continue;

            AABB box = new AABB(ob.position.x, ob.position.y, ob.position.z,
                    ob.position.x + ob.boxWidth,
                    ob.position.y + ob.boxHeight,
                    ob.position.z + ob.boxWidth)
                    .move(-camPos.x, -camPos.y, -camPos.z);

            float r = ob.color.getRed() / 255f;
            float g = ob.color.getGreen() / 255f;
            float b = ob.color.getBlue() / 255f;

            drawBoxEdgesAsQuads(buffer, matrix, box, r, g, b, 1.0f, thickness);
        }
    }

    private static void renderLinesAsQuads(MultiBufferSource consumers, PoseStack matrices, Vec3 camPos, boolean throughWalls, RenderType layer) {
        VertexConsumer buffer = consumers.getBuffer(layer);
        Matrix4f matrix = matrices.last().pose();

        for (RenderTypes.Line line : lines) {
            if (line.throughWalls != throughWalls) continue;

            float r = line.color.getRed() / 255f;
            float g = line.color.getGreen() / 255f;
            float b = line.color.getBlue() / 255f;

            float thickness = Math.max(0.002f, line.lineWidth * THICKNESS_MULTIPLIER);

            Vec3 start = new Vec3(line.start.x - camPos.x, line.start.y - camPos.y, line.start.z - camPos.z);
            Vec3 end = new Vec3(line.end.x - camPos.x, line.end.y - camPos.y, line.end.z - camPos.z);

            drawBillboardLine(buffer, matrix, start, end, r, g, b, 1.0f, thickness);
        }
    }

    private static void renderLinesFromCursorAsQuads(MultiBufferSource consumers, PoseStack matrices, Vec3 camPos, boolean throughWalls, RenderType layer) {
        VertexConsumer buffer = consumers.getBuffer(layer);
        Matrix4f matrix = matrices.last().pose();

        for (RenderTypes.LineFromCursor line : linesFromCursor) {
            if (!throughWalls) continue;

            float r = line.color.getRed() / 255f;
            float g = line.color.getGreen() / 255f;
            float b = line.color.getBlue() / 255f;

            float thickness = Math.max(0.002f, line.lineWidth * THICKNESS_MULTIPLIER);

            Vec3 target = new Vec3(line.point.x - camPos.x, line.point.y - camPos.y, line.point.z - camPos.z);

            drawBillboardLine(buffer, matrix, Vec3.ZERO, target, r, g, b, 1.0f, thickness);
        }
    }

    private static void renderText(MultiBufferSource.BufferSource consumers, PoseStack matrices, Vec3 camPos) {
        if (worldTexts.isEmpty()) return;

        Font font = Minecraft.getInstance().font;
        Quaternionf cameraRotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();

        for (RenderTypes.WorldText wt : worldTexts) {
            matrices.pushPose();
            matrices.translate((float) wt.position.x, (float) wt.position.y, (float) wt.position.z);
            matrices.translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);
            matrices.mulPose(cameraRotation);
            matrices.scale(0.025f, -0.025f, 0.025f);

            Matrix4f matrix = matrices.last().pose();
            float xOffset = -font.width(wt.text) / 2f;

            Font.DisplayMode layerType = wt.throughWalls
                    ? Font.DisplayMode.SEE_THROUGH
                    : Font.DisplayMode.NORMAL;

            font.drawInBatch(wt.text, xOffset, 0, 0xFFFFFFFF, true, matrix, consumers, layerType, 0, 0xF000F0);
            matrices.popPose();
        }
    }

    private static void drawBillboardLine(VertexConsumer buffer, Matrix4f mat, Vec3 start, Vec3 end, float r, float g, float b, float a, float thickness) {
        Vector3f lineDir = new Vector3f((float) (end.x - start.x), (float) (end.y - start.y), (float) (end.z - start.z));
        lineDir.normalize();

        Vector3f camDir = new Vector3f((float) start.x, (float) start.y, (float) start.z);
        if (camDir.lengthSquared() < 0.0001f) camDir.set(0, 1, 0);
        camDir.normalize();

        Vector3f widthDir = new Vector3f();
        lineDir.cross(camDir, widthDir);

        if (widthDir.lengthSquared() < 0.0001f) {
            widthDir.set(1, 0, 0);
            if (Math.abs(lineDir.x) > 0.9f) widthDir.set(0, 1, 0);
        }

        widthDir.normalize();
        widthDir.mul(thickness / 2.0f);

        float x1 = (float) start.x - widthDir.x;
        float y1 = (float) start.y - widthDir.y;
        float z1 = (float) start.z - widthDir.z;
        float x2 = (float) start.x + widthDir.x;
        float y2 = (float) start.y + widthDir.y;
        float z2 = (float) start.z + widthDir.z;
        float x3 = (float) end.x + widthDir.x;
        float y3 = (float) end.y + widthDir.y;
        float z3 = (float) end.z + widthDir.z;
        float x4 = (float) end.x - widthDir.x;
        float y4 = (float) end.y - widthDir.y;
        float z4 = (float) end.z - widthDir.z;

        int light = 15728880;

        buffer.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x2, y2, z2).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x3, y3, z3).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(mat, x4, y4, z4).setColor(r, g, b, a).setLight(light);
    }

    private static void drawBoxEdgesAsQuads(VertexConsumer buffer, Matrix4f mat, AABB box, float r, float g, float b, float a, float t) {
        Vec3 c000 = new Vec3(box.minX, box.minY, box.minZ);
        Vec3 c100 = new Vec3(box.maxX, box.minY, box.minZ);
        Vec3 c010 = new Vec3(box.minX, box.maxY, box.minZ);
        Vec3 c110 = new Vec3(box.maxX, box.maxY, box.minZ);
        Vec3 c001 = new Vec3(box.minX, box.minY, box.maxZ);
        Vec3 c101 = new Vec3(box.maxX, box.minY, box.maxZ);
        Vec3 c011 = new Vec3(box.minX, box.maxY, box.maxZ);
        Vec3 c111 = new Vec3(box.maxX, box.maxY, box.maxZ);

        drawBillboardLine(buffer, mat, c000, c100, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c100, c101, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c101, c001, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c001, c000, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c010, c110, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c110, c111, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c111, c011, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c011, c010, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c000, c010, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c100, c110, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c001, c011, r, g, b, a, t);
        drawBillboardLine(buffer, mat, c101, c111, r, g, b, a, t);
    }

    private static void drawBoxFaces(VertexConsumer buffer, Matrix4f mat, AABB box, float r, float g, float b, float a) {
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

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

    public static void addLinesFromPoints(Vector3d[] points, Color color, float lineWidth, boolean throughWalls) {
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