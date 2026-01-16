package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.util.math.BlockPos;
import xyz.yourboykyle.secretroutes.utils.multistorage.Triple;
import java.awt.Point;

public class RoomRotationUtils {

    public static BlockPos relativeToActual(BlockPos relative, String rotation, Point corner) {
        if (corner == null) corner = new Point(0, 0);
        if (rotation == null) rotation = "S";

        int cx = (int) corner.getX();
        int cz = (int) corner.getY();

        int rx = relative.getX();
        int ry = relative.getY();
        int rz = relative.getZ();

        return switch (rotation) {
            case "W" -> new BlockPos(cx - rz, ry, cz + rx);
            case "N" -> new BlockPos(cx - rx, ry, cz - rz);
            case "E" -> new BlockPos(cx + rz, ry, cz - rx);
            default -> new BlockPos(cx + rx, ry, cz + rz);
        };
    }

    public static Triple<Double, Double, Double> relativeToActual(double rx, double ry, double rz, String rotation, Point corner) {
        if (corner == null) corner = new Point(0, 0);
        if (rotation == null) rotation = "S";

        double cx = corner.getX();
        double cz = corner.getY();

        return switch (rotation) {
            case "W" -> new Triple<>(cx - rz, ry, cz + rx);
            case "N" -> new Triple<>(cx - rx, ry, cz - rz);
            case "E" -> new Triple<>(cx + rz, ry, cz - rx);
            default  -> new Triple<>(cx + rx, ry, cz + rz);
        };
    }

    public static BlockPos actualToRelative(BlockPos actual, String rotation, Point corner) {
        if (corner == null) corner = new Point(0, 0);
        if (rotation == null) rotation = "S";

        int cx = (int) corner.getX();
        int cz = (int) corner.getY();

        int dx = actual.getX() - cx;
        int dy = actual.getY();
        int dz = actual.getZ() - cz;

        return switch (rotation) {
            case "W" -> new BlockPos(dz, dy, -dx);
            case "N" -> new BlockPos(-dx, dy, -dz);
            case "E" -> new BlockPos(-dz, dy, dx);
            default -> new BlockPos(dx, dy, dz);
        };
    }

    public static Triple<Double, Double, Double> actualToRelative(double ax, double ay, double az, String rotation, Point corner) {
        if (corner == null) corner = new Point(0, 0);
        if (rotation == null) rotation = "S";

        double cx = corner.getX();
        double cz = corner.getY();
        double dx = ax - cx;
        double dz = az - cz;

        return switch (rotation) {
            case "W" -> new Triple<>(dz, ay, -dx);
            case "N" -> new Triple<>(-dx, ay, -dz);
            case "E" -> new Triple<>(-dz, ay, dx);
            default  -> new Triple<>(dx, ay, dz);
        };
    }
}