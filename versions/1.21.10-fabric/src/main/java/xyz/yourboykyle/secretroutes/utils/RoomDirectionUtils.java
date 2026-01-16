package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.utils.dungeon.DungeonScanner;
import java.awt.Point;

public class RoomDirectionUtils {

    public static String roomDirection() {
        if (DungeonScanner.currentRoom == null) return "UNKNOWN";

        return switch (DungeonScanner.currentRoom.rotation) {
            case SOUTH -> "S";
            case WEST -> "W";
            case NORTH -> "N";
            case EAST -> "E";
            default -> "S";
        };
    }

    public static BlockPos getRoomAnchor() {
        if (DungeonScanner.currentRoom == null) return null;

        return DungeonScanner.currentRoom.clayPos;
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