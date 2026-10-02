package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/** Caches relative route data only. Each caller receives its own room-sized copy. */
public final class RouteFileCache {
    public static final RouteFileCache SHARED = new RouteFileCache();
    private final int capacity;
    private final Loader loader;
    private final Map<Path, Entry> files = new LinkedHashMap<>(8, 0.75f, true);

    @FunctionalInterface
    interface Loader {
        JsonObject read(Path path) throws IOException;
    }

    private record Stamp(FileTime modified, long size, Object fileKey) {
        static Stamp read(Path path) throws IOException {
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
            return new Stamp(attributes.lastModifiedTime(), attributes.size(), attributes.fileKey());
        }
    }

    private record Entry(Stamp stamp, JsonObject data) { }

    public RouteFileCache() {
        this(8, RouteFileCache::readFile);
    }

    RouteFileCache(int capacity, Loader loader) {
        if (capacity < 1) throw new IllegalArgumentException("Cache capacity must be positive");
        this.capacity = capacity;
        this.loader = loader;
    }

    public synchronized JsonObject roomData(Path path, String roomName) throws IOException {
        Path key = path.toRealPath();
        Stamp stamp = Stamp.read(key);
        Entry entry = files.get(key);
        if (entry == null || !entry.stamp().equals(stamp)) {
            files.remove(key);
            JsonObject data = loader.read(key);
            Stamp afterRead = Stamp.read(key);
            if (!stamp.equals(afterRead)) {
                // An external editor or downloader replaced the file while it was read.
                data = loader.read(key);
                if (!afterRead.equals(Stamp.read(key))) throw new IOException("Route file changed while loading");
            }
            entry = new Entry(afterRead, data);
            files.put(key, entry);
            if (files.size() > capacity) files.remove(files.keySet().iterator().next());
        }
        String name = roomName.toLowerCase(Locale.ROOT);
        JsonObject room = new JsonObject();
        for (var route : entry.data().entrySet()) {
            String routeName = route.getKey().toLowerCase(Locale.ROOT);
            if (routeName.equals(name) || routeName.startsWith(name + ":")) {
                room.add(route.getKey(), route.getValue().deepCopy());
            }
        }
        return room;
    }

    public synchronized void invalidate(Path path) {
        Path key = path.toAbsolutePath().normalize();
        try {
            key = path.toRealPath();
        } catch (IOException ignored) { }
        files.remove(key);
    }

    private static JsonObject readFile(Path path) throws IOException {
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement data = JsonParser.parseReader(reader);
            if (!data.isJsonObject()) throw new IOException("Route file must contain a JSON object");
            return data.getAsJsonObject();
        }
    }
}
