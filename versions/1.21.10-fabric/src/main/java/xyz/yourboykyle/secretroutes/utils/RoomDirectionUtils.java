package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.utils.dungeon.DungeonScanner;
import java.awt.Point;

public class RoomDirectionUtils {

    public static String roomDirection() {
        if (DungeonScanner.currentRoom == null) return "UNKNOWN";

        String direction = switch (DungeonScanner.currentRoom.rotation) {
            case SOUTH -> "S";
            case WEST -> "W";
            case NORTH -> "N";
            case EAST -> "E";
            default -> "S";
        };

        return direction;
    }

    public static BlockPos getRoomAnchor() {
        if (DungeonScanner.currentRoom == null) return null;
        BlockPos clay = DungeonScanner.currentRoom.clayPos;

        String dir = roomDirection();
        int offset = 15;

        return switch (dir) {
            case "S" -> new BlockPos(clay.getX() - offset, clay.getY(), clay.getZ() - offset); // Default
            case "N" -> new BlockPos(clay.getX() + offset, clay.getY(), clay.getZ() + offset); // Rotated 180
            case "W" -> new BlockPos(clay.getX() + offset, clay.getY(), clay.getZ() - offset); // Rotated 90
            case "E" -> new BlockPos(clay.getX() - offset, clay.getY(), clay.getZ() + offset); // Rotated 270
            default -> clay;
        };
    }

    public static Point roomCorner() {
        BlockPos anchor = getRoomAnchor();
        if (anchor == null) return null;
        return new Point(anchor.getX(), anchor.getZ());
    }

    public static String roomName() {
        if (DungeonScanner.currentRoom == null) return "UNKNOWN";
        return DungeonScanner.currentRoom.getName();
    }
}