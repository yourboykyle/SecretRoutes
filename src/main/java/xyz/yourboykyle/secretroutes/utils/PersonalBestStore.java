package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Numeric room-name records with an explicit timing policy; legacy measurements are backed up. */
public final class PersonalBestStore {
    private static final String TIMING = "room-entry-v1";
    private final Path path;
    private final Saver saver;
    private Map<String, Long> times = new LinkedHashMap<>();
    private boolean loaded;
    private Path backup;

    public PersonalBestStore(Path path) {
        this(path, JsonFileUtils::writeAtomically);
    }

    @FunctionalInterface
    interface Saver {
        void write(Path path, JsonObject data) throws IOException;
    }

    PersonalBestStore(Path path, Saver saver) {
        this.path = path;
        this.saver = saver;
    }

    public void load() throws IOException {
        loaded = false;
        times = new LinkedHashMap<>();
        backup = null;
        if (!Files.exists(path)) {
            loaded = true;
            return;
        }
        JsonElement parsed;
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            parsed = JsonParser.parseReader(reader);
        } catch (RuntimeException malformed) {
            recover("invalid");
            return;
        }
        if (!parsed.isJsonObject()) {
            recover("invalid");
            return;
        }
        JsonObject data = parsed.getAsJsonObject();
        if (data.isEmpty()) {
            loaded = true;
            return;
        }
        if (!data.has("timing") || !data.get("timing").isJsonPrimitive()
                || !TIMING.equals(data.get("timing").getAsString())) {
            recover("legacy");
            return;
        }
        if (!data.has("rooms") || !data.get("rooms").isJsonObject()) {
            recover("invalid");
            return;
        }
        for (var entry : data.getAsJsonObject("rooms").entrySet()) {
            try {
                long time = entry.getValue().getAsBigDecimal().longValueExact();
                if (time >= 0) times.merge(key(entry.getKey()), time, Math::min);
            } catch (RuntimeException ignored) {
                // A bad record must not prevent valid room records from loading.
            }
        }
        loaded = true;
    }

    private void recover(String reason) throws IOException {
        Path directory = path.toAbsolutePath().getParent();
        Path copy = Files.createTempFile(directory, "personal_bests-" + reason + "-", ".json");
        Files.copy(path, copy, StandardCopyOption.REPLACE_EXISTING);
        backup = copy;
        write(times);
        loaded = true;
    }

    public Path backupPath() {
        return backup;
    }

    public long get(String roomName) {
        return times.getOrDefault(key(roomName), -1L);
    }

    public boolean record(String roomName, long millis) throws IOException {
        if (!loaded) throw new IOException("Personal bests have not loaded successfully");
        if (millis < 0) throw new IllegalArgumentException("Time cannot be negative");
        String key = key(roomName);
        Long previous = times.get(key);
        if (previous != null && previous <= millis) return false;
        Map<String, Long> updated = new LinkedHashMap<>(times);
        updated.put(key, millis);
        write(updated);
        times = updated;
        return true;
    }

    public void remove(String roomName) throws IOException {
        if (!loaded) throw new IOException("Personal bests have not loaded successfully");
        Map<String, Long> updated = new LinkedHashMap<>(times);
        if (updated.remove(key(roomName)) == null) return;
        write(updated);
        times = updated;
    }

    private void write(Map<String, Long> values) throws IOException {
        JsonObject data = new JsonObject();
        data.addProperty("timing", TIMING);
        JsonObject rooms = new JsonObject();
        values.forEach(rooms::addProperty);
        data.add("rooms", rooms);
        saver.write(path, data);
    }

    private static String key(String roomName) {
        return roomName.split(":", 2)[0].toLowerCase(Locale.ROOT);
    }
}
