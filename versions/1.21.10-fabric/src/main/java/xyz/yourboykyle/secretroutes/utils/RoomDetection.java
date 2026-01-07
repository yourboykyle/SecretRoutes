package xyz.yourboykyle.secretroutes.utils;

import xyz.yourboykyle.secretroutes.utils.skyblocker.DungeonScanner;
import xyz.yourboykyle.secretroutes.utils.skyblocker.StructureRoom;

import java.awt.*;

public class RoomDetection {
    public static String roomDirection() {
        StructureRoom room = DungeonScanner.currentRoom;

        if (room == null || !room.isMatched() || room.getDirection() == null) {
            return "UNKNOWN";
        }

        return switch (room.getDirection()) {
            case NE -> "northeast";
            case NW -> "northwest";
            case SE -> "southeast";
            case SW -> "southwest";
            default -> "UNKNOWN";
        };
    }

    public static Point roomCorner() {
        StructureRoom room = DungeonScanner.currentRoom;

        if (room == null || room.getPhysicalCornerPos() == null) {
            return null;
        }
        return new Point(room.getPhysicalCornerPos().x(), room.getPhysicalCornerPos().y());
    }

    public static String roomName() {
        StructureRoom room = DungeonScanner.currentRoom;

        if (room == null || room.getName() == null) {
            return "UNKNOWN";
        }
        return room.getName();
    }
}
