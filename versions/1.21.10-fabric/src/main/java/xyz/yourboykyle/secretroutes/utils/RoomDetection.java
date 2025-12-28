//#if FABRIC && MC == 1.21.10
package xyz.yourboykyle.secretroutes.utils;

import de.hysky.skyblocker.skyblock.dungeon.secrets.DungeonManager;

import java.awt.*;

public class RoomDetection {
    public static String roomDirection() {
        if (DungeonManager.getCurrentRoom() == null || DungeonManager.getCurrentRoom().getDirection() == null) {
            return "UNKNOWN";
        }
        return DungeonManager.getCurrentRoom().getDirection().toString();
    }

    public static Point roomCorner() {
        if (DungeonManager.getCurrentRoom() == null || DungeonManager.getCurrentRoom().getPhysicalCornerPos() == null) {
            return null;
        }
        return new Point(DungeonManager.getCurrentRoom().getPhysicalCornerPos().x(), DungeonManager.getCurrentRoom().getPhysicalCornerPos().y());
    }

    public static String roomName() {
        if (DungeonManager.getCurrentRoom() == null || DungeonManager.getCurrentRoom().getName() == null) {
            return "UNKNOWN";
        }
        return DungeonManager.getCurrentRoom().getName();
    }
}
//#endif