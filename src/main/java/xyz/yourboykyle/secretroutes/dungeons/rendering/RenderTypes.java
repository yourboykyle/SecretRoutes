//#if FABRIC
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
package xyz.yourboykyle.secretroutes.dungeons.rendering;

import net.minecraft.network.chat.Component;
import org.joml.Vector3d;

import java.awt.*;

public class RenderTypes {
    public static class WorldText {
        public Component text;
        public Vector3d position;
        public boolean throughWalls;
        public float scale;

        public WorldText(Component text, Vector3d position, boolean throughWalls) {
            this.text = text;
            this.position = position;
            this.throughWalls = throughWalls;
        }

        public WorldText(Component text, Vector3d position, boolean throughWalls, float scale) {
            this.text = text;
            this.position = position;
            this.throughWalls = throughWalls;
            this.scale = scale;
        }
    }

    public static class OutlinedBox {
        public Vector3d position;
        public Color color;
        public float boxWidth;
        public float boxHeight;
        public boolean throughWalls;

        public OutlinedBox(Vector3d position, Color color, float boxWidth, float boxHeight, boolean throughWalls) {
            this.position = position;
            this.color = color;
            this.boxWidth = boxWidth;
            this.boxHeight = boxHeight;
            this.throughWalls = throughWalls;
        }
    }

    public static class FilledBox {
        public Vector3d position;
        public Color color;
        public float boxWidth;
        public float boxHeight;
        public boolean throughWalls;

        public FilledBox(Vector3d position, Color color, float boxWidth, float boxHeight, boolean throughWalls) {
            this.position = position;
            this.color = color;
            this.boxWidth = boxWidth;
            this.boxHeight = boxHeight;
            this.throughWalls = throughWalls;
        }
    }

    public static class Line {
        public Vector3d start;
        public Vector3d end;
        public Color color;
        public float lineWidth;
        public boolean throughWalls;

        public Line(Vector3d start, Vector3d end, Color color, float lineWidth, boolean throughWalls) {
            this.start = start;
            this.end = end;
            this.color = color;
            this.lineWidth = lineWidth;
            this.throughWalls = throughWalls;
        }
    }

    public static class LineFromCursor {
        public Vector3d point;
        public Color color;
        public float lineWidth;

        public LineFromCursor(Vector3d point, Color color, float lineWidth) {
            this.point = point;
            this.color = color;
            this.lineWidth = lineWidth;
        }
    }
}
//#endif
