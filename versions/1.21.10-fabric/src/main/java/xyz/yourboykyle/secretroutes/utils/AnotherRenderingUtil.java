package xyz.yourboykyle.secretroutes.utils;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.events.OnWorldRender;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AnotherRenderingUtil {
    private static final float THICKNESS_MULTIPLIER = 0.01f;
    private static final RenderPipeline SEE_THROUGH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
                    .withLocation(Identifier.of(Main.MODID, "see_through_overlay"))
                    .withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS)
                    .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                    .withCull(false)
                    .build()
    );
    private static final RenderLayer SEE_THROUGH_LAYER = RenderLayer.of(
            "secretroutes_see_through",
            1536,
            SEE_THROUGH_PIPELINE,
            RenderLayer.MultiPhaseParameters.builder()
                    .target(RenderLayer.MAIN_TARGET)
                    .build(false)
    );
    public static List<RenderTypes.WorldText> worldTexts = new ArrayList<>();
    public static List<RenderTypes.OutlinedBox> outlinedBoxes = new ArrayList<>();
    public static List<RenderTypes.FilledBox> filledBoxes = new ArrayList<>();
    public static List<RenderTypes.Line> lines = new ArrayList<>();
    public static List<RenderTypes.LineFromCursor> linesFromCursor = new ArrayList<>();

    public static void register() {
        WorldRenderEvents.END_MAIN.register(AnotherRenderingUtil::render);
    }

    private static void render(WorldRenderContext context) {
        OnWorldRender.onRenderWorld();

        MatrixStack matrices = context.matrices();
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        try (BufferAllocator allocator = new BufferAllocator(196608)) {
            VertexConsumerProvider.Immediate consumers = VertexConsumerProvider.immediate(allocator);

            renderFilledBoxes(consumers, matrices, cameraPos, true, SEE_THROUGH_LAYER);
            renderOutlinedBoxesAsQuads(consumers, matrices, cameraPos, true, SEE_THROUGH_LAYER);
            renderLinesAsQuads(consumers, matrices, cameraPos, true, SEE_THROUGH_LAYER);
            renderLinesFromCursorAsQuads(consumers, matrices, cameraPos, true, SEE_THROUGH_LAYER);

            consumers.draw();

            RenderLayer normalLayer = RenderLayer.getDebugFilledBox();

            renderFilledBoxes(consumers, matrices, cameraPos, false, normalLayer);
            renderOutlinedBoxesAsQuads(consumers, matrices, cameraPos, false, normalLayer);
            renderLinesAsQuads(consumers, matrices, cameraPos, false, normalLayer);
            renderLinesFromCursorAsQuads(consumers, matrices, cameraPos, false, normalLayer);

            consumers.draw();
        }

        outlinedBoxes.clear();
        filledBoxes.clear();
        lines.clear();
        linesFromCursor.clear();

        renderText(matrices, cameraPos);
        worldTexts.clear();
    }

    private static void renderFilledBoxes(VertexConsumerProvider consumers, MatrixStack matrices, Vec3d camPos, boolean throughWalls, RenderLayer layer) {
        VertexConsumer buffer = consumers.getBuffer(layer);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        for (RenderTypes.FilledBox fb : filledBoxes) {
            if (fb.throughWalls != throughWalls) continue;

            Box box = new Box(fb.position.x, fb.position.y, fb.position.z,
                    fb.position.x + fb.boxWidth,
                    fb.position.y + fb.boxHeight,
                    fb.position.z + fb.boxWidth)
                    .offset(camPos.negate());

            float r = fb.color.getRed() / 255f;
            float g = fb.color.getGreen() / 255f;
            float b = fb.color.getBlue() / 255f;
            float a = SRMConfig.get().filledBoxAlpha;

            drawBoxFaces(buffer, matrix, box, r, g, b, a);
        }
    }

    private static void renderOutlinedBoxesAsQuads(VertexConsumerProvider consumers, MatrixStack matrices, Vec3d camPos, boolean throughWalls, RenderLayer layer) {
        VertexConsumer buffer = consumers.getBuffer(layer);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float thickness = Math.max(0.002f, SRMConfig.get().boxLineWidth * THICKNESS_MULTIPLIER);

        for (RenderTypes.OutlinedBox ob : outlinedBoxes) {
            if (ob.throughWalls != throughWalls) continue;

            Box box = new Box(ob.position.x, ob.position.y, ob.position.z,
                    ob.position.x + ob.boxWidth,
                    ob.position.y + ob.boxHeight,
                    ob.position.z + ob.boxWidth)
                    .offset(camPos.negate());

            float r = ob.color.getRed() / 255f;
            float g = ob.color.getGreen() / 255f;
            float b = ob.color.getBlue() / 255f;

            drawBoxEdgesAsQuads(buffer, matrix, box, r, g, b, 1.0f, thickness);
        }
    }

    private static void renderLinesAsQuads(VertexConsumerProvider consumers, MatrixStack matrices, Vec3d camPos, boolean throughWalls, RenderLayer layer) {
        VertexConsumer buffer = consumers.getBuffer(layer);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        for (RenderTypes.Line line : lines) {
            if (line.throughWalls != throughWalls) continue;

            float r = line.color.getRed() / 255f;
            float g = line.color.getGreen() / 255f;
            float b = line.color.getBlue() / 255f;

            float thickness = Math.max(0.002f, line.lineWidth * THICKNESS_MULTIPLIER);

            Vec3d start = line.start.subtract(camPos);
            Vec3d end = line.end.subtract(camPos);

            drawBillboardLine(buffer, matrix, start, end, r, g, b, 1.0f, thickness);
        }
    }

    private static void renderLinesFromCursorAsQuads(VertexConsumerProvider consumers, MatrixStack matrices, Vec3d camPos, boolean throughWalls, RenderLayer layer) {
        VertexConsumer buffer = consumers.getBuffer(layer);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        for (RenderTypes.LineFromCursor line : linesFromCursor) {
            if (!throughWalls) continue;

            float r = line.color.getRed() / 255f;
            float g = line.color.getGreen() / 255f;
            float b = line.color.getBlue() / 255f;

            float thickness = Math.max(0.002f, line.lineWidth * THICKNESS_MULTIPLIER);

            Vec3d target = line.point.subtract(camPos);

            drawBillboardLine(buffer, matrix, Vec3d.ZERO, target, r, g, b, 1.0f, thickness);
        }
    }

    private static void renderText(MatrixStack matrices, Vec3d camPos) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        VertexConsumerProvider consumers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();

        for (RenderTypes.WorldText wt : worldTexts) {
            matrices.push();
            Vec3d pos = wt.position.subtract(camPos);
            matrices.translate(pos.x, pos.y, pos.z);
            matrices.multiply(camera.getRotation());
            matrices.scale(-0.025f, -0.025f, 0.025f);

            Matrix4f matrix = matrices.peek().getPositionMatrix();
            float xOffset = -textRenderer.getWidth(wt.text) / 2f;

            TextRenderer.TextLayerType layerType = wt.throughWalls
                    ? TextRenderer.TextLayerType.SEE_THROUGH
                    : TextRenderer.TextLayerType.NORMAL;

            textRenderer.draw(wt.text, xOffset, 0, 0xFFFFFFFF, false, matrix,
                    consumers, layerType, 0, 15728880);

            matrices.pop();
        }
    }

    private static void drawBillboardLine(VertexConsumer buffer, Matrix4f mat, Vec3d start, Vec3d end, float r, float g, float b, float a, float thickness) {
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

        buffer.vertex(mat, x1, y1, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y2, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x3, y3, z3).color(r, g, b, a).light(light);
        buffer.vertex(mat, x4, y4, z4).color(r, g, b, a).light(light);
    }

    private static void drawBoxEdgesAsQuads(VertexConsumer buffer, Matrix4f mat, Box box, float r, float g, float b, float a, float t) {
        Vec3d c000 = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d c100 = new Vec3d(box.maxX, box.minY, box.minZ);
        Vec3d c010 = new Vec3d(box.minX, box.maxY, box.minZ);
        Vec3d c110 = new Vec3d(box.maxX, box.maxY, box.minZ);
        Vec3d c001 = new Vec3d(box.minX, box.minY, box.maxZ);
        Vec3d c101 = new Vec3d(box.maxX, box.minY, box.maxZ);
        Vec3d c011 = new Vec3d(box.minX, box.maxY, box.maxZ);
        Vec3d c111 = new Vec3d(box.maxX, box.maxY, box.maxZ);

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

    private static void drawBoxFaces(VertexConsumer buffer, Matrix4f mat, Box box, float r, float g, float b, float a) {
        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        int light = 15728880;

        buffer.vertex(mat, x1, y1, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x1, y1, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y1, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y1, z2).color(r, g, b, a).light(light);

        buffer.vertex(mat, x1, y2, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x1, y2, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y2, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y2, z1).color(r, g, b, a).light(light);

        buffer.vertex(mat, x1, y1, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x1, y2, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y2, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y1, z1).color(r, g, b, a).light(light);

        buffer.vertex(mat, x2, y1, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y2, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x1, y2, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x1, y1, z2).color(r, g, b, a).light(light);

        buffer.vertex(mat, x1, y1, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x1, y2, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x1, y2, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x1, y1, z1).color(r, g, b, a).light(light);

        buffer.vertex(mat, x2, y1, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y2, z1).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y2, z2).color(r, g, b, a).light(light);
        buffer.vertex(mat, x2, y1, z2).color(r, g, b, a).light(light);
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

    public static void addLinesFromPoints(Vec3d[] points, Color color, float lineWidth, boolean throughWalls) {
        for (int i = 0; i < points.length - 1; i++) {
            RenderTypes.Line line = new RenderTypes.Line(points[i], points[i + 1], color, lineWidth, throughWalls);
            addLine(line);
        }
    }

    public static void addLineFromCursor(RenderTypes.LineFromCursor lineFromCursor) {
        if (!linesFromCursor.contains(lineFromCursor)) linesFromCursor.add(lineFromCursor);
    }
}