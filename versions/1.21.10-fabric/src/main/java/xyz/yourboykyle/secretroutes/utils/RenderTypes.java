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
package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.polyfrost.polyui.color.PolyColor;

public class RenderTypes {
    public static class WorldText {
        public Text text;
        public Vec3d position;
        public boolean throughWalls;
        public float scale;

        public WorldText(Text text, Vec3d position, boolean throughWalls) {
            this.text = text;
            this.position = position;
            this.throughWalls = throughWalls;
        }

        public WorldText(Text text, Vec3d position, boolean throughWalls, float scale) {
            this.text = text;
            this.position = position;
            this.throughWalls = throughWalls;
            this.scale = scale;
        }
    }

    public static class OutlinedBox {
        public Vec3d position;
        public PolyColor color;
        public float boxWidth;
        public float boxHeight;
        public boolean throughWalls;

        public OutlinedBox(Vec3d position, PolyColor color, float boxWidth, float boxHeight, boolean throughWalls) {
            this.position = position;
            this.color = color;
            this.boxWidth = boxWidth;
            this.boxHeight = boxHeight;
            this.throughWalls = throughWalls;
        }
    }
}
//#endif