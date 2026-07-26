//#if FABRIC
package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonArray;
import net.minecraft.core.BlockPos;
import xyz.yourboykyle.secretroutes.dungeons.Room;

final class ItemSecretProximityTracker {
    private static final int DETECTION_RADIUS = 2;

    private final long delayNanos;
    private Room pendingRoom;
    private JsonArray pendingRoute;
    private int pendingStepIndex;
    private BlockPos pendingTarget;
    private long pendingDeadlineNanos;
    private boolean pending;

    ItemSecretProximityTracker(long delayNanos) {
        if (delayNanos < 0L) {
            throw new IllegalArgumentException("Delay cannot be negative");
        }
        this.delayNanos = delayNanos;
    }

    boolean update(
            Room room,
            JsonArray route,
            int stepIndex,
            boolean isItemSecret,
            BlockPos target,
            BlockPos playerPosition,
            long nowNanos
    ) {
        if (room == null || !isItemSecret || target == null || playerPosition == null) {
            reset();
            return false;
        }

        if (pending && !matches(room, route, stepIndex, target)) {
            reset();
        }

        if (pending) {
            if (nowNanos - pendingDeadlineNanos >= 0L) {
                reset();
                return true;
            }
            return false;
        }

        if (isWithinDetectionRange(playerPosition, target)) {
            pendingRoom = room;
            pendingRoute = route;
            pendingStepIndex = stepIndex;
            pendingTarget = target;
            pendingDeadlineNanos = nowNanos + delayNanos;
            pending = true;
        }
        return false;
    }

    void reset() {
        pendingRoom = null;
        pendingRoute = null;
        pendingStepIndex = 0;
        pendingTarget = null;
        pendingDeadlineNanos = 0L;
        pending = false;
    }

    boolean isPending() {
        return pending;
    }

    private boolean matches(Room room, JsonArray route, int stepIndex, BlockPos target) {
        return pendingRoom == room
                && pendingRoute == route
                && pendingStepIndex == stepIndex
                && pendingTarget.equals(target);
    }

    private static boolean isWithinDetectionRange(BlockPos playerPosition, BlockPos target) {
        return playerPosition.getX() >= target.getX() - DETECTION_RADIUS
                && playerPosition.getX() <= target.getX() + DETECTION_RADIUS
                && playerPosition.getY() >= target.getY() - DETECTION_RADIUS
                && playerPosition.getY() <= target.getY() + DETECTION_RADIUS
                && playerPosition.getZ() >= target.getZ() - DETECTION_RADIUS
                && playerPosition.getZ() <= target.getZ() + DETECTION_RADIUS;
    }
}
//#endif
