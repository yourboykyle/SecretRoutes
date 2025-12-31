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
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.RenderTypes;

import java.util.ArrayList;
import java.util.List;

public class OnSkyblockerRender {

    public static List<RenderTypes.WorldText> worldTexts = new ArrayList<>();
    public static List<RenderTypes.OutlinedBox> outlinedBoxes = new ArrayList<>();

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
}
//#endif