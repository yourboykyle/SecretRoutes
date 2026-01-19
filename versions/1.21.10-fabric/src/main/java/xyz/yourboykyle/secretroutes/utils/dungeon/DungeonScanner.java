package xyz.yourboykyle.secretroutes.utils.dungeon;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Vector2i;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.events.OnEnterNewRoom;
import xyz.yourboykyle.secretroutes.utils.LocationUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/*BSD 3-Clause License

Copyright (c) 2025, odtheking

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/

public class DungeonScanner {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Gson GSON = new Gson();

    private static final Map<Integer, RoomData> CORE_TO_ROOM_MAP = new HashMap<>();

    public static DungeonRoom currentRoom = null;
    public static final Set<DungeonRoom> passedRooms = new HashSet<>();
    private static Vector2i lastRoomCentre = new Vector2i(0, 0);

    private static final int ROOM_SIZE_SHIFT = 5;
    private static final int START_COORDINATE = -185;
    private static final List<Direction> HORIZONTALS = Arrays.stream(Direction.values())
            .filter(d -> d.getAxis().isHorizontal()).toList();

    public static void init() {
        ClientLifecycleEvents.CLIENT_STARTED.register(c -> loadResources());
        ClientPlayConnectionEvents.JOIN.register((h, s, c) -> reset());
        ClientPlayConnectionEvents.DISCONNECT.register((h, c) -> reset());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (!LocationUtils.isInDungeons()) {
                if (currentRoom != null) {
                    currentRoom = null;
                    Main.currentRoom = new Room(null);
                }
                return;
            }
            tick();
        });
    }

    private static void loadResources() {
        CORE_TO_ROOM_MAP.clear();
        client.getResourceManager().findResources("rooms.json", id -> id.getPath().endsWith("rooms.json"))
                .forEach((id, resource) -> {
                    try (Reader reader = new InputStreamReader(resource.getInputStream())) {
                        List<RoomData> data = GSON.fromJson(reader, new TypeToken<List<RoomData>>(){}.getType());
                        for (RoomData room : data) {
                            if (room.cores() != null) {
                                for (Integer core : room.cores()) CORE_TO_ROOM_MAP.put(core, room);
                            }
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
    }

    private static void tick() {
        int playerX = (int) client.player.getX();
        int playerZ = (int) client.player.getZ();
        Vector2i roomCentre = getRoomCentre(playerX, playerZ);

        if (roomCentre.equals(lastRoomCentre)) return;
        lastRoomCentre = roomCentre;

        for (DungeonRoom passed : passedRooms) {
            for (RoomComponent comp : passed.roomComponents) {
                if (comp.vec2().equals(roomCentre)) {
                    if (currentRoom == null || !hasComponent(currentRoom, roomCentre)) enterRoom(passed);
                    return;
                }
            }
        }

        if (!client.world.getChunkManager().isChunkLoaded(roomCentre.x >> 4, roomCentre.y >> 4)) return;

        DungeonRoom newRoom = scanRoom(roomCentre);
        if (newRoom != null && newRoom.rotation != Rotations.NONE) enterRoom(newRoom);
    }

    private static void enterRoom(DungeonRoom room) {
        currentRoom = room;
        boolean exists = false;
        for(DungeonRoom r : passedRooms) {
            if(r.data.name().equals(room.data.name())) { exists = true; break; }
        }
        if(!exists) passedRooms.add(room);

        System.out.println("[SecretRoutes] Entered Dungeon Room: " + room.data.name() + " (" + room.rotation + ")");

        xyz.yourboykyle.secretroutes.utils.Room newRoomObj = new xyz.yourboykyle.secretroutes.utils.Room(room.data.name());
        Main.currentRoom = newRoomObj;
        OnEnterNewRoom.onEnterNewRoom(newRoomObj);
    }

    private static boolean hasComponent(DungeonRoom room, Vector2i target) {
        for (RoomComponent comp : room.roomComponents) {
            if (comp.vec2().equals(target)) return true;
        }
        return false;
    }

    private static DungeonRoom scanRoom(Vector2i vec2) {
        int roomHeight = getTopLayerOfRoom(vec2);
        int core = getCore(vec2, roomHeight);
        RoomData data = CORE_TO_ROOM_MAP.get(core);
        if (data != null) {
            Set<RoomComponent> components = findRoomComponentsRecursively(vec2, data.cores(), roomHeight, new HashSet<>(), new HashSet<>());
            DungeonRoom room = new DungeonRoom(data, components);
            updateRotation(room, roomHeight);
            return room;
        }
        return null;
    }

    private static Set<RoomComponent> findRoomComponentsRecursively(Vector2i vec2, List<Integer> cores, int roomHeight, Set<Vector2i> visited, Set<RoomComponent> tiles) {
        if (visited.contains(vec2)) return tiles;
        visited.add(vec2);
        int core = getCore(vec2, roomHeight);
        if (!cores.contains(core)) return tiles;
        tiles.add(new RoomComponent(vec2.x, vec2.y, core));
        for (Direction facing : HORIZONTALS) {
            int dx = (facing.getAxis() == Direction.Axis.X ? facing.getOffsetX() : 0) << ROOM_SIZE_SHIFT;
            int dz = (facing.getAxis() == Direction.Axis.Z ? facing.getOffsetZ() : 0) << ROOM_SIZE_SHIFT;
            findRoomComponentsRecursively(new Vector2i(vec2.x + dx, vec2.y + dz), cores, roomHeight, visited, tiles);
        }
        return tiles;
    }

    private static void updateRotation(DungeonRoom room, int roomHeight) {
        if ("Fairy".equals(room.data.name())) {
            if (!room.roomComponents.isEmpty()) {
                RoomComponent first = room.roomComponents.iterator().next();
                room.clayPos = new BlockPos(first.x() - 15, roomHeight, first.z() - 15);
                room.rotation = Rotations.SOUTH;
            }
            return;
        }

        List<Rotations> rotations = Arrays.asList(Rotations.NORTH, Rotations.SOUTH, Rotations.WEST, Rotations.EAST);
        boolean found = false;

        for (Rotations rot : rotations) {
            for (RoomComponent comp : room.roomComponents) {
                BlockPos checkPos = new BlockPos(comp.x() + rot.x, roomHeight, comp.z() + rot.z);
                if (isBlueTerracotta(checkPos)) {
                    room.clayPos = calculateCorner(room.roomComponents, rot);
                    room.rotation = rot;
                    found = true;
                    break;
                }
            }
            if (found) break;
        }

        if (!found) room.rotation = Rotations.NONE;

        // Hardcoded solution to Slime-5 and Sewer-7
        if (room.rotation != Rotations.NONE && (room.data.name().equalsIgnoreCase("Slime-5") || room.data.name().equalsIgnoreCase("Sewer-7"))) {
            Rotations newRot = room.rotation;

            switch (room.rotation) {
                case NORTH -> newRot = Rotations.SOUTH;
                case SOUTH -> newRot = Rotations.NORTH;
                case EAST  -> newRot = Rotations.WEST;
                case WEST  -> newRot = Rotations.EAST;
            }

            room.rotation = newRot;
            room.clayPos = calculateCorner(room.roomComponents, newRot);

            System.out.println("[SecretRoutes] Manually flipped " + room.data.name() + " to " + newRot);
        }
    }

    // Calculates corner position.
    private static BlockPos calculateCorner(Set<RoomComponent> components, Rotations rotation) {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (RoomComponent comp : components) {
            if (comp.x() < minX) minX = comp.x();
            if (comp.x() > maxX) maxX = comp.x();
            if (comp.z() < minZ) minZ = comp.z();
            if (comp.z() > maxZ) maxZ = comp.z();
        }

        return switch (rotation) {
            case SOUTH -> new BlockPos(minX + rotation.x, 0, minZ + rotation.z);
            case WEST  -> new BlockPos(maxX + rotation.x, 0, minZ + rotation.z);
            case NORTH -> new BlockPos(maxX + rotation.x, 0, maxZ + rotation.z);
            case EAST  -> new BlockPos(minX + rotation.x, 0, maxZ + rotation.z);
            default    -> new BlockPos(minX, 0, minZ);
        };
    }

    private static boolean isBlueTerracotta(BlockPos pos) {
        if (client.world == null) return false;
        return client.world.getBlockState(pos).getBlock() == Blocks.BLUE_TERRACOTTA;
    }

    private static boolean isBlueTerracottaOrAir(BlockPos pos) {
        if (client.world == null) return false;
        Block b = client.world.getBlockState(pos).getBlock();
        return b == Blocks.AIR || b == Blocks.BLUE_TERRACOTTA;
    }

    public static Vector2i getRoomCentre(int posX, int posZ) {
        int roomX = (posX - START_COORDINATE + (1 << (ROOM_SIZE_SHIFT - 1))) >> ROOM_SIZE_SHIFT;
        int roomZ = (posZ - START_COORDINATE + (1 << (ROOM_SIZE_SHIFT - 1))) >> ROOM_SIZE_SHIFT;
        return new Vector2i((roomX << ROOM_SIZE_SHIFT) + START_COORDINATE, (roomZ << ROOM_SIZE_SHIFT) + START_COORDINATE);
    }

    private static int getCore(Vector2i vec2, Integer knownHeight) {
        if (client.world == null) return 0;
        int roomHeight = (knownHeight != null ? knownHeight : getTopLayerOfRoom(vec2));
        int clampedHeight = Math.max(11, Math.min(140, roomHeight));
        StringBuilder sb = new StringBuilder(150);
        for (int i = 0; i < 140 - clampedHeight; i++) sb.append('0');
        int bedrock = 0;
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int y = clampedHeight; y >= 12; y--) {
            pos.set(vec2.x, y, vec2.y);
            Block block = client.world.getBlockState(pos).getBlock();
            if (block == Blocks.AIR && bedrock >= 2 && y < 69) {
                int remaining = y - 11;
                for(int k=0; k < remaining; k++) sb.append('0');
                break;
            }
            if (block == Blocks.BEDROCK) bedrock++;
            else {
                bedrock = 0;
                if (block == Blocks.OAK_PLANKS || block == Blocks.TRAPPED_CHEST || block == Blocks.CHEST) continue;
            }
            sb.append(block);
        }
        return sb.toString().hashCode();
    }

    private static int getTopLayerOfRoom(Vector2i vec2) {
        if (client.world == null) return 0;
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int y = 160; y >= 12; y--) {
            pos.set(vec2.x, y, vec2.y);
            if (!client.world.isAir(pos)) {
                if (client.world.getBlockState(pos).getBlock() == Blocks.GOLD_BLOCK) return y - 1;
                return y;
            }
        }
        return 0;
    }

    private static void reset() {
        lastRoomCentre = new Vector2i(0, 0);
        currentRoom = null;
        passedRooms.clear();
        Main.currentRoom = null;
    }
}