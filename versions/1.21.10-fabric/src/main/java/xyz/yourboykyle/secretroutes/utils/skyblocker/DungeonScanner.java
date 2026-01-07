package xyz.yourboykyle.secretroutes.utils.skyblocker;

/*
 * This file incorporates code from Skyblocker, licensed under the GNU Lesser General Public License v3.0.
 * Original Copyright: de.hysky (Skyblocker)
 * License: https://www.gnu.org/licenses/lgpl-3.0.en.html
 *
 * This file also contains logic adapted from Dungeon Rooms Mod.
 * Original Copyright: Quantizr
 */

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ByteMap;
import it.unimi.dsi.fastutil.ints.Int2ByteOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import org.joml.Vector2ic;
import xyz.yourboykyle.secretroutes.events.OnEnterNewRoom;
import xyz.yourboykyle.secretroutes.utils.LocationUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.zip.InflaterInputStream;

public class DungeonScanner {
    public static final Map<String, Map<String, Map<String, int[]>>> ROOMS_DATA = new ConcurrentHashMap<>();
    public static final Map<String, List<SecretWaypoint>> ROOMS_WAYPOINTS = new ConcurrentHashMap<>();
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Gson GSON = new Gson();
    private static final Map<Vector2ic, StructureRoom> rooms = new HashMap<>();
    // Legacy ID Mapping (1.8.9 ID -> 1.21 Internal Byte ID)
    private static final Int2ByteMap LEGACY_ID_MAP = new Int2ByteOpenHashMap();
    public static StructureRoom currentRoom;
    private static Vector2ic physicalEntrancePos;
    private static Vector2ic mapEntrancePos;
    private static int mapRoomSize;

    static {
        LEGACY_ID_MAP.put(100, (byte) 1);   // Stone
        LEGACY_ID_MAP.put(103, (byte) 2);   // Diorite
        LEGACY_ID_MAP.put(104, (byte) 3);   // Polished Diorite
        LEGACY_ID_MAP.put(105, (byte) 4);   // Andesite
        LEGACY_ID_MAP.put(106, (byte) 5);   // Polished Andesite
        LEGACY_ID_MAP.put(200, (byte) 6);   // Grass
        LEGACY_ID_MAP.put(300, (byte) 7);   // Dirt
        LEGACY_ID_MAP.put(301, (byte) 8);   // Coarse Dirt
        LEGACY_ID_MAP.put(400, (byte) 9);   // Cobblestone
        LEGACY_ID_MAP.put(700, (byte) 10);  // Bedrock
        LEGACY_ID_MAP.put(1800, (byte) 11); // Oak Leaves
        LEGACY_ID_MAP.put(3507, (byte) 12); // Gray Wool
        LEGACY_ID_MAP.put(4300, (byte) 13); // Double Slab
        LEGACY_ID_MAP.put(4800, (byte) 14); // Mossy Cobble
        LEGACY_ID_MAP.put(8200, (byte) 15); // Clay
        LEGACY_ID_MAP.put(9800, (byte) 16); // Stone Bricks
        LEGACY_ID_MAP.put(9801, (byte) 17); // Mossy Stone Bricks
        LEGACY_ID_MAP.put(9803, (byte) 18); // Chiseled Stone Bricks
        LEGACY_ID_MAP.put(15907, (byte) 19); // Gray Terracotta
        LEGACY_ID_MAP.put(15909, (byte) 20); // Cyan Terracotta
        LEGACY_ID_MAP.put(15915, (byte) 21); // Black Terracotta
    }

    public static void init() {
        System.out.println("[SecretRoutes] Initializing Dungeon Scanner...");
        loadResources();
        ClientTickEvents.END_CLIENT_TICK.register(client -> tick());
    }

    private static void loadResources() {
        new Thread(() -> {
            try {
                loadFromJar();
                loadSecretsDirectly();

                if (ROOMS_DATA.containsKey("catacombs")) {
                    System.out.println("[SecretRoutes] Dungeon Data Loaded Successfully (" + ROOMS_DATA.get("catacombs").size() + " shapes).");
                } else {
                    System.err.println("[SecretRoutes] WARNING: Catacombs data missing!");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static void loadFromJar() {
        try {
            URI uri = DungeonScanner.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path myPath;
            FileSystem fs = null;

            if (uri.toString().endsWith(".jar")) {
                try {
                    fs = FileSystems.newFileSystem(URI.create("jar:" + uri), Collections.emptyMap());
                    myPath = fs.getPath("/");
                } catch (FileSystemAlreadyExistsException e) {
                    fs = FileSystems.getFileSystem(URI.create("jar:" + uri));
                    myPath = fs.getPath("/");
                }
            } else {
                myPath = Paths.get(uri);
            }

            Stream<Path> walk = Files.walk(myPath);

            for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                Path p = it.next();
                String pathString = p.toString().replace("\\", "/");

                if (pathString.endsWith(".skeleton")) {
                    loadSkeletonFile(p);
                }
            }

            if (fs != null) fs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadSkeletonFile(Path path) {
        try {
            String pathStr = path.toString().replace("\\", "/");
            String[] parts = pathStr.split("/");

            int catIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("catacombs")) {
                    catIndex = i;
                    break;
                }
            }

            if (catIndex == -1 || parts.length < catIndex + 3) return;

            String category = "catacombs";
            String shape = parts[catIndex + 1];
            String name = parts[catIndex + 2].replace(".skeleton", "");

            ROOMS_DATA.computeIfAbsent(category, k -> new ConcurrentHashMap<>())
                    .computeIfAbsent(shape, k -> new ConcurrentHashMap<>());

            try (ObjectInputStream in = new ObjectInputStream(new InflaterInputStream(Files.newInputStream(path)))) {
                Object obj = in.readObject();

                if (obj instanceof int[]) {
                    ROOMS_DATA.get(category).get(shape).put(name, (int[]) obj);
                } else if (obj instanceof long[]) {
                    // Legacy Format Conversion (1.8.9 -> 1.21)
                    long[] legacyData = (long[]) obj;
                    int[] modernData = new int[legacyData.length];

                    for (int i = 0; i < legacyData.length; i++) {
                        long l = legacyData[i];
                        short x = (short) (l >> 48);
                        short y = (short) (l >> 32);
                        short z = (short) (l >> 16);
                        short legacyId = (short) (l);

                        byte newId = LEGACY_ID_MAP.getOrDefault((int) legacyId, (byte) 0);
                        modernData[i] = (x << 24) | (y << 16) | (z << 8) | (newId & 0xFF);
                    }
                    Arrays.sort(modernData);
                    ROOMS_DATA.get(category).get(shape).put(name, modernData);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void loadSecretsDirectly() {
        String[] possiblePaths = {
                "/assets/roomdetection/secretlocations.json",
                "/assets/roomdetection/catacombs/secretlocations.json",
                "/assets/secretroutes/roomdetection/secretlocations.json"
        };

        for (String path : possiblePaths) {
            try (InputStream is = DungeonScanner.class.getResourceAsStream(path)) {
                if (is != null) {
                    JsonObject json = GSON.fromJson(new InputStreamReader(is), JsonObject.class);
                    for (String roomName : json.keySet()) {
                        if (roomName.equals("copyright") || roomName.equals("license")) continue;
                        List<SecretWaypoint> secrets = new ArrayList<>();
                        for (JsonElement el : json.getAsJsonArray(roomName)) {
                            JsonObject obj = el.getAsJsonObject();
                            secrets.add(new SecretWaypoint(
                                    obj.has("secretName") ? obj.get("secretName").getAsString() : "Unknown",
                                    obj.get("category").getAsString(),
                                    obj.get("x").getAsInt(),
                                    obj.get("y").getAsInt(),
                                    obj.get("z").getAsInt()
                            ));
                        }
                        ROOMS_WAYPOINTS.put(roomName, secrets);
                    }
                    return;
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean isClearingDungeon() {
        return physicalEntrancePos != null && mapEntrancePos != null && mapRoomSize > 0;
    }

    public static StructureRoom getCurrentRoom() {
        return currentRoom;
    }

    public static void tick() {
        if (!LocationUtils.isInDungeons() || client.player == null || client.world == null) {
            if (physicalEntrancePos != null) reset();
            return;
        }

        if (physicalEntrancePos == null) {
            client.world.getEntities().forEach(e -> {
                if (e.getName().getString().contains("Mort")) {
                    physicalEntrancePos = DungeonMapUtils.getPhysicalRoomPos(e.getBlockPos());
                    System.out.println("[SecretRoutes] Dungeon Started. Entrance: " + physicalEntrancePos.x() + ", " + physicalEntrancePos.y());
                }
            });
            if (physicalEntrancePos == null) return;
        }

        if (mapEntrancePos == null) {
            ItemStack stack = client.player.getInventory().getMainStacks().get(8);
            if (stack.isOf(Items.FILLED_MAP)) {
                MapIdComponent mapId = stack.get(DataComponentTypes.MAP_ID);
                if (mapId != null) {
                    MapState state = client.world.getMapState(mapId);
                    if (state != null) {
                        mapEntrancePos = DungeonMapUtils.findMapEntrance(state);
                        if (mapEntrancePos != null) {
                            mapRoomSize = DungeonMapUtils.getMapRoomSize(state, mapEntrancePos);
                            System.out.println("[SecretRoutes] Map Calibrated.");
                        }
                    }
                }
            }
            return;
        }

        Vector2ic playerPhysPos = DungeonMapUtils.getPhysicalRoomPos(client.player.getBlockPos());
        StructureRoom room = rooms.get(playerPhysPos);

        if (room == null) {
            ItemStack stack = client.player.getInventory().getMainStacks().get(8);
            if (stack.isOf(Items.FILLED_MAP)) {
                MapIdComponent mapId = stack.get(DataComponentTypes.MAP_ID);
                if (mapId != null) {
                    MapState state = client.world.getMapState(mapId);
                    if (state != null) {
                        Vector2ic mapPos = DungeonMapUtils.getMapPosFromPhysical(physicalEntrancePos, mapEntrancePos, mapRoomSize, playerPhysPos);
                        StructureRoom.Type type = DungeonMapUtils.getRoomType(state, mapPos);

                        if (type != StructureRoom.Type.UNKNOWN) {
                            Set<Vector2ic> mapSegments = DungeonMapUtils.getRoomSegments(state, mapPos, mapRoomSize, DungeonMapUtils.getColor(state, mapPos.x(), mapPos.y()));
                            Set<Vector2ic> physicalSegments = new HashSet<>();
                            for (Vector2ic seg : mapSegments) {
                                physicalSegments.add(DungeonMapUtils.getPhysicalPosFromMap(mapEntrancePos, mapRoomSize, physicalEntrancePos, seg));
                            }
                            room = new StructureRoom(type, physicalSegments);
                            for (Vector2ic segmentPhysPos : physicalSegments) {
                                rooms.put(segmentPhysPos, room);
                            }
                        }
                    }
                }
            }
        }

        if (room != null && currentRoom != room) {
            currentRoom = room;
            if (currentRoom.isMatched()) {
                OnEnterNewRoom.onEnterNewRoom(new xyz.yourboykyle.secretroutes.utils.Room(currentRoom.getName()));
            }
        }

        if (currentRoom != null && !currentRoom.isMatched()) {
            currentRoom.tickMatch(client.world, client.player);
            if (currentRoom.isMatched()) {
                System.out.println("[SecretRoutes] Room Identified: " + currentRoom.getName());
                OnEnterNewRoom.onEnterNewRoom(new xyz.yourboykyle.secretroutes.utils.Room(currentRoom.getName()));
            }
        }
    }

    private static void reset() {
        currentRoom = null;
        physicalEntrancePos = null;
        mapEntrancePos = null;
        rooms.clear();
    }

    public record SecretWaypoint(String secretName, String category, int x, int y, int z) {
    }
}