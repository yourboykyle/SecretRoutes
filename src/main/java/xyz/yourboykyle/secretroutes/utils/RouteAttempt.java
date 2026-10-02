package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.OptionalLong;
import java.util.function.LongSupplier;

/** One room visit. Route loading and pre-secret route selection retain the entry timestamp. */
public final class RouteAttempt {
    private final LongSupplier clock;
    private final long enteredAt;
    private final BitSet required = new BitSet();
    private final BitSet collected = new BitSet();
    private final List<Long> splits = new ArrayList<>();
    private boolean invalid;
    private boolean finished;

    public RouteAttempt(LongSupplier clock) {
        this.clock = clock;
        enteredAt = clock.getAsLong();
    }

    public void bindRoute(JsonArray route) {
        if (!collected.isEmpty() || finished) invalidate();
        required.clear();
        if (route == null) return;
        for (int i = 0; i < route.size(); i++) {
            if (isCollectible(route.get(i))) required.set(i);
        }
    }

    private static boolean isCollectible(JsonElement step) {
        if (!step.isJsonObject()) return false;
        JsonElement secretElement = step.getAsJsonObject().get("secret");
        if (secretElement == null || !secretElement.isJsonObject()) return false;
        JsonObject secret = secretElement.getAsJsonObject();
        if (!secret.has("type") || !secret.get("type").isJsonPrimitive()) return false;
        return switch (secret.get("type").getAsString()) {
            case "interact", "item", "bat" -> true;
            default -> false;
        };
    }

    public OptionalLong completeStep(int index) {
        if (!isValid() || index < 0 || !required.get(index) || collected.get(index)) return OptionalLong.empty();
        collected.set(index);
        long elapsed = elapsedMillis();
        splits.add(elapsed);
        if (!collected.equals(required)) return OptionalLong.empty();
        finished = true;
        return OptionalLong.of(elapsed);
    }

    public long elapsedMillis() {
        return Math.max(0L, (clock.getAsLong() - enteredAt) / 1_000_000L);
    }

    public List<Long> splitsMillis() {
        return List.copyOf(splits);
    }

    public void invalidate() {
        invalid = true;
    }

    public boolean isValid() {
        return !invalid && !finished;
    }
}
