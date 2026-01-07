package xyz.yourboykyle.secretroutes.utils.skyblocker;

/*
 * This file incorporates code from Skyblocker, licensed under the GNU Lesser General Public License v3.0.
 * Original Copyright: de.hysky (Skyblocker)
 * License: https://www.gnu.org/licenses/lgpl-3.0.en.html
 */

import net.minecraft.item.map.MapState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class DungeonMapUtils {

    public static byte getColor(MapState map, int x, int z) {
        if (x < 0 || z < 0 || x >= 128 || z >= 128) return -1;
        if (map == null || map.colors == null) return -1;
        return map.colors[x + (z * 128)];
    }

    public static byte getColor(MapState map, Vector2ic pos) {
        if (pos == null) return -1;
        return getColor(map, pos.x(), pos.y());
    }

    public static boolean isEntranceColor(MapState map, int x, int z) {
        return getColor(map, x, z) == DungeonConstants.GREEN_COLOR;
    }

    public static Vector2ic getPhysicalRoomPos(Vec3d pos) {
        Vector2i physicalPos = new Vector2i((int) (pos.x + 8.5), (int) (pos.z + 8.5));
        int modX = Math.floorMod(physicalPos.x, 32);
        int modY = Math.floorMod(physicalPos.y, 32);
        return physicalPos.sub(modX, modY).sub(8, 8);
    }

    public static Vector2ic getPhysicalRoomPos(BlockPos pos) {
        return getPhysicalRoomPos(new Vec3d(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static boolean notInDoorway(BlockPos pos) {
        if (pos.getY() < 66 || pos.getY() > 73) {
            return true;
        }
        // Shift by -8 because Hypixel offsets dungeons by 8 blocks
        int x = Math.floorMod(pos.getX() - 8, 32);
        int z = Math.floorMod(pos.getZ() - 8, 32);

        // Standard door is ~13-18. Entrance gate can be wider.
        // We exclude a safer buffer (11 to 20) to ignore all connection geometry.
        return (x < 11 || x > 20 || z > 2 && z < 28) && (z < 11 || z > 20 || x > 2 && x < 28);
    }

    public static Vector2ic findMapEntrance(MapState map) {
        if (map == null) return null;
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                if (isEntranceColor(map, x, y)) {
                    if (!isEntranceColor(map, x - 1, y) && !isEntranceColor(map, x, y - 1)) {
                        return new Vector2i(x, y);
                    }
                }
            }
        }
        return null;
    }

    public static int getMapRoomSize(MapState map, Vector2ic entrancePos) {
        int size = 0;
        while (isEntranceColor(map, entrancePos.x() + size + 1, entrancePos.y())) {
            size++;
        }
        return size > 5 ? size + 1 : 0;
    }

    public static Vector2ic getMapPosFromPhysical(Vector2ic physicalEntrancePos, Vector2ic mapEntrancePos, int mapRoomSize, Vector2ic physicalPos) {
        if (mapRoomSize == 0) return new Vector2i(0, 0);
        return new Vector2i(physicalPos)
                .sub(physicalEntrancePos)
                .div(32)
                .mul(mapRoomSize + 4)
                .add(mapEntrancePos);
    }

    public static Vector2ic getPhysicalPosFromMap(Vector2ic mapEntrancePos, int mapRoomSize, Vector2ic physicalEntrancePos, Vector2ic mapPos) {
        if (mapRoomSize == 0) return new Vector2i(0, 0);
        return new Vector2i(mapPos)
                .sub(mapEntrancePos)
                .div(mapRoomSize + 4)
                .mul(32)
                .add(physicalEntrancePos);
    }

    public static StructureRoom.Type getRoomType(MapState map, Vector2ic mapPos) {
        byte color = getColor(map, mapPos.x(), mapPos.y());
        return switch (color) {
            case DungeonConstants.GREEN_COLOR -> StructureRoom.Type.ENTRANCE;
            case 63 -> StructureRoom.Type.ROOM;
            case 66 -> StructureRoom.Type.PUZZLE;
            case 62 -> StructureRoom.Type.TRAP;
            case 74 -> StructureRoom.Type.MINIBOSS;
            case 82 -> StructureRoom.Type.FAIRY;
            case DungeonConstants.RED_COLOR -> StructureRoom.Type.BLOOD;
            default -> StructureRoom.Type.UNKNOWN;
        };
    }

    public static Set<Vector2ic> getRoomSegments(MapState map, Vector2ic mapPos, int mapRoomSize, byte color) {
        Set<Vector2ic> segments = new HashSet<>();
        Queue<Vector2ic> queue = new ArrayDeque<>();
        segments.add(mapPos);
        queue.add(mapPos);

        while (!queue.isEmpty()) {
            Vector2ic curMapPos = queue.poll();
            Vector2i newMapPos = new Vector2i();

            if (getColor(map, newMapPos.set(curMapPos).sub(1, 0)) == color && !segments.contains(newMapPos.sub(mapRoomSize + 3, 0))) {
                segments.add(new Vector2i(newMapPos));
                queue.add(new Vector2i(newMapPos));
            }
            if (getColor(map, newMapPos.set(curMapPos).sub(0, 1)) == color && !segments.contains(newMapPos.sub(0, mapRoomSize + 3))) {
                segments.add(new Vector2i(newMapPos));
                queue.add(new Vector2i(newMapPos));
            }
            if (getColor(map, newMapPos.set(curMapPos).add(mapRoomSize, 0)) == color && !segments.contains(newMapPos.add(4, 0))) {
                segments.add(new Vector2i(newMapPos));
                queue.add(new Vector2i(newMapPos));
            }
            if (getColor(map, newMapPos.set(curMapPos).add(0, mapRoomSize)) == color && !segments.contains(newMapPos.add(0, 4))) {
                segments.add(new Vector2i(newMapPos));
                queue.add(new Vector2i(newMapPos));
            }
        }
        return segments;
    }

    public static Vector2ic getPhysicalCornerPos(StructureRoom.Direction direction, Set<Vector2ic> segments) {
        int minX = segments.stream().mapToInt(Vector2ic::x).min().orElse(0);
        int maxX = segments.stream().mapToInt(Vector2ic::x).max().orElse(0);
        int minY = segments.stream().mapToInt(Vector2ic::y).min().orElse(0);
        int maxY = segments.stream().mapToInt(Vector2ic::y).max().orElse(0);

        return switch (direction) {
            case NW -> new Vector2i(minX, minY);
            case NE -> new Vector2i(maxX + 30, minY);
            case SW -> new Vector2i(minX, maxY + 30);
            case SE -> new Vector2i(maxX + 30, maxY + 30);
        };
    }

    public static BlockPos actualToRelative(StructureRoom.Direction direction, Vector2ic physicalCornerPos, BlockPos pos) {
        return switch (direction) {
            case NW -> new BlockPos(pos.getX() - physicalCornerPos.x(), pos.getY(), pos.getZ() - physicalCornerPos.y());
            case NE ->
                    new BlockPos(pos.getZ() - physicalCornerPos.y(), pos.getY(), -pos.getX() + physicalCornerPos.x());
            case SW ->
                    new BlockPos(-pos.getZ() + physicalCornerPos.y(), pos.getY(), pos.getX() - physicalCornerPos.x());
            case SE ->
                    new BlockPos(-pos.getX() + physicalCornerPos.x(), pos.getY(), -pos.getZ() + physicalCornerPos.y());
        };
    }

    public static BlockPos relativeToActual(StructureRoom.Direction direction, Vector2ic physicalCornerPos, BlockPos pos) {
        return switch (direction) {
            case NW -> new BlockPos(pos.getX() + physicalCornerPos.x(), pos.getY(), pos.getZ() + physicalCornerPos.y());
            case NE ->
                    new BlockPos(-pos.getZ() + physicalCornerPos.x(), pos.getY(), pos.getX() + physicalCornerPos.y());
            case SW ->
                    new BlockPos(pos.getZ() + physicalCornerPos.x(), pos.getY(), -pos.getX() + physicalCornerPos.y());
            case SE ->
                    new BlockPos(-pos.getX() + physicalCornerPos.x(), pos.getY(), -pos.getZ() + physicalCornerPos.y());
        };
    }
}