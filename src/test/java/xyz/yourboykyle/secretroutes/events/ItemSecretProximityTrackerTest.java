package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonArray;
import net.minecraft.core.BlockPos;
import xyz.yourboykyle.secretroutes.dungeons.Room;

public final class ItemSecretProximityTrackerTest {
    private static final long DELAY_NANOS = 1_500_000_000L;
    private static final BlockPos TARGET = new BlockPos(10, 70, -4);
    private static final BlockPos INSIDE_EDGE = new BlockPos(12, 68, -2);
    private static final BlockPos OUTSIDE = new BlockPos(13, 70, -4);

    private ItemSecretProximityTrackerTest() {
    }

    public static void main(String[] args) {
        armsOnlyOnceAndFiresAfterDelay();
        leavingRangeDoesNotCancel();
        realPickupInvalidatesPendingFallback();
        consecutiveSamePositionStepsNeedSeparateTimers();
        targetIdentityChangesInvalidatePendingFallback();
        losingContextResetsPendingFallback();
        System.out.println("Item-secret proximity tracker tests passed");
    }

    private static void armsOnlyOnceAndFiresAfterDelay() {
        ItemSecretProximityTracker tracker = new ItemSecretProximityTracker(DELAY_NANOS);
        Room room = new Room(null);
        JsonArray route = new JsonArray();
        long started = 100L;

        assertFalse(tracker.update(room, route, 2, true, TARGET, INSIDE_EDGE, started), "Arming must not advance");
        assertTrue(tracker.isPending(), "Tracker should be armed");
        for (int tick = 1; tick < 30; tick++) {
            long now = started + tick * 50_000_000L;
            assertFalse(tracker.update(room, route, 2, true, TARGET, INSIDE_EDGE, now), "Repeated nearby ticks must not rearm or advance early");
        }
        assertTrue(tracker.update(room, route, 2, true, TARGET, INSIDE_EDGE, started + DELAY_NANOS), "Tracker should advance at its original deadline");
        assertFalse(tracker.isPending(), "Tracker must clear before reporting advancement");
    }

    private static void leavingRangeDoesNotCancel() {
        ItemSecretProximityTracker tracker = new ItemSecretProximityTracker(DELAY_NANOS);
        Room room = new Room(null);
        JsonArray route = new JsonArray();
        long started = 1_000L;

        tracker.update(room, route, 1, true, TARGET, INSIDE_EDGE, started);
        assertFalse(tracker.update(room, route, 1, true, TARGET, OUTSIDE, started + DELAY_NANOS - 1L), "Leaving range must not advance early");
        assertTrue(tracker.update(room, route, 1, true, TARGET, OUTSIDE, started + DELAY_NANOS), "Leaving range must not cancel an armed fallback");
    }

    private static void realPickupInvalidatesPendingFallback() {
        ItemSecretProximityTracker tracker = new ItemSecretProximityTracker(DELAY_NANOS);
        Room room = new Room(null);
        JsonArray route = new JsonArray();
        long started = 2_000L;

        tracker.update(room, route, 3, true, TARGET, INSIDE_EDGE, started);
        assertFalse(tracker.update(room, route, 4, false, null, INSIDE_EDGE, started + 100L), "A real route advance must invalidate the pending fallback");
        assertFalse(tracker.isPending(), "Invalidated fallback must be cleared");
        assertFalse(tracker.update(room, route, 4, false, null, INSIDE_EDGE, started + DELAY_NANOS), "Invalidated fallback must not fire later");
    }

    private static void consecutiveSamePositionStepsNeedSeparateTimers() {
        ItemSecretProximityTracker tracker = new ItemSecretProximityTracker(DELAY_NANOS);
        Room room = new Room(null);
        JsonArray route = new JsonArray();
        long firstStarted = 3_000L;

        tracker.update(room, route, 5, true, TARGET, INSIDE_EDGE, firstStarted);
        assertTrue(tracker.update(room, route, 5, true, TARGET, INSIDE_EDGE, firstStarted + DELAY_NANOS), "First item step should advance once");

        long secondStarted = firstStarted + DELAY_NANOS + 1L;
        assertFalse(tracker.update(room, route, 6, true, TARGET, INSIDE_EDGE, secondStarted), "Second item step should arm a new timer");
        assertFalse(tracker.update(room, route, 6, true, TARGET, INSIDE_EDGE, secondStarted + DELAY_NANOS - 1L), "Second item step must wait for its full delay");
        assertTrue(tracker.update(room, route, 6, true, TARGET, INSIDE_EDGE, secondStarted + DELAY_NANOS), "Second item step should advance only at its own deadline");
    }

    private static void targetIdentityChangesInvalidatePendingFallback() {
        ItemSecretProximityTracker tracker = new ItemSecretProximityTracker(DELAY_NANOS);
        Room firstRoom = new Room(null);
        Room secondRoom = new Room(null);
        JsonArray firstRoute = new JsonArray();
        JsonArray secondRoute = new JsonArray();
        long started = 4_000L;

        tracker.update(firstRoom, firstRoute, 0, true, TARGET, INSIDE_EDGE, started);
        assertFalse(tracker.update(secondRoom, firstRoute, 0, true, TARGET, OUTSIDE, started + DELAY_NANOS), "Room changes must invalidate the old timer");
        assertFalse(tracker.isPending(), "An out-of-range replacement room must not arm");

        tracker.update(firstRoom, firstRoute, 0, true, TARGET, INSIDE_EDGE, started);
        assertFalse(tracker.update(firstRoom, secondRoute, 0, true, TARGET, OUTSIDE, started + DELAY_NANOS), "Route identity changes must invalidate the old timer");

        tracker.update(firstRoom, firstRoute, 0, true, TARGET, INSIDE_EDGE, started);
        BlockPos changedTarget = TARGET.offset(1, 0, 0);
        assertFalse(tracker.update(firstRoom, firstRoute, 0, true, changedTarget, OUTSIDE, started + DELAY_NANOS), "Target changes must invalidate the old timer");
    }

    private static void losingContextResetsPendingFallback() {
        ItemSecretProximityTracker tracker = new ItemSecretProximityTracker(DELAY_NANOS);
        Room room = new Room(null);
        JsonArray route = new JsonArray();
        long started = 5_000L;

        tracker.update(room, route, 0, true, TARGET, INSIDE_EDGE, started);
        assertFalse(tracker.update(null, null, 0, false, null, null, started + 1L), "Missing world or room context must not advance");
        assertFalse(tracker.isPending(), "Missing context must clear the tracker");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) throw new AssertionError(message);
    }
}
