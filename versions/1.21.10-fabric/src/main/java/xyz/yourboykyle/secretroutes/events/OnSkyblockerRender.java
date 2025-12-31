//#if FABRIC && MC == 1.21.10
/*
 * Dungeon Rooms Mod - Secret Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2021 Quantizr(_risk)
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

import de.hysky.skyblocker.utils.Utils;
import de.hysky.skyblocker.utils.render.WorldRenderExtractionCallback;
import de.hysky.skyblocker.utils.render.primitive.PrimitiveCollector;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.polyfrost.polyui.color.PolyColor;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.RenderTypes;

import java.util.ArrayList;
import java.util.List;

public class OnSkyblockerRender {
    public static List<RenderTypes.WorldText> worldTexts = new ArrayList<>();
    public static List<RenderTypes.OutlinedBox> outlinedBoxes = new ArrayList<>();
    public static List<RenderTypes.FilledBox> filledBoxes = new ArrayList<>();
    public static List<RenderTypes.Line> lines = new ArrayList<>();
    public static List<RenderTypes.LineFromCursor> linesFromCursor = new ArrayList<>();

    public static void register() {
        WorldRenderExtractionCallback.EVENT.register(OnSkyblockerRender::renderItems);
    }

    public static void renderItems(PrimitiveCollector collector) {
        if (!Utils.isInDungeons()) return;

        // Call other onRenderWorld functions
        OnWorldRender.onRenderWorld();

        // Render the world texts
        for (RenderTypes.WorldText wt : worldTexts) {
            collector.submitText(wt.text, wt.position, wt.throughWalls);
        }
        worldTexts.clear();

        // Render the outlined boxes
        for (RenderTypes.OutlinedBox ob : outlinedBoxes) {
            float[] colorComponents = new float[] {
                ob.color.red() / 255f,
                ob.color.green() / 255f,
                ob.color.blue() / 255f
            };
            collector.submitOutlinedBox(
                new Box(ob.position.x, ob.position.y, ob.position.z,
                        ob.position.x + ob.boxWidth,
                        ob.position.y + ob.boxHeight,
                        ob.position.z + ob.boxWidth),
                colorComponents,
                SRMConfig.boxLineWidth,
                ob.throughWalls
            );
        }
        outlinedBoxes.clear();

        // Render the filled boxes
        for (RenderTypes.FilledBox fb : filledBoxes) {
            float[] colorComponents = new float[] {
                fb.color.red() / 255f,
                fb.color.green() / 255f,
                fb.color.blue() / 255f
            };
            collector.submitFilledBox(
                new Box(fb.position.x, fb.position.y, fb.position.z,
                        fb.position.x + fb.boxWidth,
                        fb.position.y + fb.boxHeight,
                        fb.position.z + fb.boxWidth),
                colorComponents,
                SRMConfig.filledBoxAlpha,
                fb.throughWalls
            );
        }
        filledBoxes.clear();

        // Render the lines
        for (RenderTypes.Line linesSet : lines) {
            float[] colorComponents = new float[] {
                linesSet.color.red() / 255f,
                linesSet.color.green() / 255f,
                linesSet.color.blue() / 255f
            };
            Vec3d[] points = new Vec3d[] {
                new Vec3d(linesSet.start.x, linesSet.start.y, linesSet.start.z),
                new Vec3d(linesSet.end.x, linesSet.end.y, linesSet.end.z)
            };
            collector.submitLinesFromPoints(
                points,
                colorComponents,
             1.0f,
                linesSet.lineWidth,
                linesSet.throughWalls
            );
        }
        lines.clear();

        // Render the lines from cursor
        for (RenderTypes.LineFromCursor lineFromCursor : linesFromCursor) {
            float[] colorComponents = new float[] {
                lineFromCursor.color.red() / 255f,
                lineFromCursor.color.green() / 255f,
                lineFromCursor.color.blue() / 255f
            };
            collector.submitLineFromCursor(
                new Vec3d(lineFromCursor.point.x, lineFromCursor.point.y, lineFromCursor.point.z),
                colorComponents,
                1.0f,
                lineFromCursor.lineWidth
            );
        }
        linesFromCursor.clear();
    }

    // Functions to be called to add stuff to render
    public static void addWorldText(RenderTypes.WorldText worldText) {
        if (worldTexts.contains(worldText)) {
            return;
        }
        worldTexts.add(worldText);
    }

    public static void addOutlinedBox(RenderTypes.OutlinedBox outlinedBox) {
        if (outlinedBoxes.contains(outlinedBox)) {
            return;
        }
        outlinedBoxes.add(outlinedBox);
    }

    public static void addFilledBox(RenderTypes.FilledBox filledBox) {
        if (filledBoxes.contains(filledBox)) {
            return;
        }
        filledBoxes.add(filledBox);
    }

    public static void addLine(RenderTypes.Line line) {
        if (lines.contains(line)) {
            return;
        }
        lines.add(line);
    }

    public static void addLinesFromPoints(Vec3d[] points, PolyColor color, float lineWidth, boolean throughWalls) {
        for (int i = 0; i < points.length - 1; i++) {
            RenderTypes.Line line = new RenderTypes.Line(
                points[i],
                points[i + 1],
                color,
                lineWidth,
                throughWalls
            );
            addLine(line);
        }
    }

    public static void addLineFromCursor(RenderTypes.LineFromCursor lineFromCursor) {
        if (linesFromCursor.contains(lineFromCursor)) {
            return;
        }
        linesFromCursor.add(lineFromCursor);
    }
}
//#endif