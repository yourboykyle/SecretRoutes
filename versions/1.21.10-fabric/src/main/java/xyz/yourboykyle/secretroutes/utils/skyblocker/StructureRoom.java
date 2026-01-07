package xyz.yourboykyle.secretroutes.utils.skyblocker;

/*
 * This file incorporates code from Skyblocker, licensed under the GNU Lesser General Public License v3.0.
 * Original Copyright: de.hysky (Skyblocker)
 * License: https://www.gnu.org/licenses/lgpl-3.0.en.html
 */

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.ints.IntSortedSets;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.*;

public class StructureRoom {
    private final Type type;
    private final Shape shape;
    private final Set<Vector2ic> segments;
    private final Set<BlockPos> checkedBlocks = new HashSet<>();
    private String name = "UNKNOWN";
    private Direction direction = null;
    private Vector2ic physicalCornerPos;
    private boolean matched = false;
    private boolean failed = false;
    private List<CandidateGroup> candidates = new ArrayList<>();
    public StructureRoom(Type type, Set<Vector2ic> segments) {
        this.type = type;
        this.segments = segments;
        this.shape = determineShape(type, segments);
        initializeCandidates();
    }

    private static Shape determineShape(Type type, Set<Vector2ic> segments) {
        if (type == Type.PUZZLE) return Shape.PUZZLE;
        if (type == Type.TRAP) return Shape.TRAP;
        if (type == Type.MINIBOSS) return Shape.MINIBOSS;

        IntSortedSet x = IntSortedSets.unmodifiable(new IntRBTreeSet(segments.stream().mapToInt(Vector2ic::x).toArray()));
        IntSortedSet y = IntSortedSets.unmodifiable(new IntRBTreeSet(segments.stream().mapToInt(Vector2ic::y).toArray()));

        return switch (segments.size()) {
            case 1 -> Shape.ONE_BY_ONE;
            case 2 -> Shape.ONE_BY_TWO;
            case 3 -> (x.size() == 2 && y.size() == 2) ? Shape.L_SHAPE : Shape.ONE_BY_THREE;
            case 4 -> (x.size() == 2 && y.size() == 2) ? Shape.TWO_BY_TWO : Shape.ONE_BY_FOUR;
            default -> Shape.ONE_BY_ONE;
        };
    }

    private void initializeCandidates() {
        if (failed) return;

        if (!DungeonScanner.ROOMS_DATA.containsKey("catacombs") ||
                !DungeonScanner.ROOMS_DATA.get("catacombs").containsKey(shape.key)) {
            failed = true;
            return;
        }

        Set<String> allRoomNames = DungeonScanner.ROOMS_DATA.get("catacombs").get(shape.key).keySet();
        if (allRoomNames.isEmpty()) {
            failed = true;
            return;
        }

        IntSortedSet segmentsX = IntSortedSets.unmodifiable(new IntRBTreeSet(segments.stream().mapToInt(Vector2ic::x).toArray()));
        IntSortedSet segmentsY = IntSortedSets.unmodifiable(new IntRBTreeSet(segments.stream().mapToInt(Vector2ic::y).toArray()));

        for (Direction dir : getPossibleDirections(segmentsX, segmentsY)) {
            Vector2ic corner = DungeonMapUtils.getPhysicalCornerPos(dir, segments);
            candidates.add(new CandidateGroup(dir, corner, new ArrayList<>(allRoomNames)));
        }
    }

    private Direction[] getPossibleDirections(IntSortedSet x, IntSortedSet y) {
        return switch (shape) {
            case ONE_BY_ONE, TWO_BY_TWO, PUZZLE, TRAP, MINIBOSS -> Direction.values();
            case ONE_BY_TWO, ONE_BY_THREE, ONE_BY_FOUR -> {
                if (x.size() > 1 && y.size() == 1) yield new Direction[]{Direction.NW, Direction.SE};
                if (x.size() == 1 && y.size() > 1) yield new Direction[]{Direction.NE, Direction.SW};
                yield new Direction[]{};
            }
            case L_SHAPE -> {
                if (!segments.contains(new Vector2i(x.firstInt(), y.firstInt()))) yield new Direction[]{Direction.SW};
                if (!segments.contains(new Vector2i(x.firstInt(), y.lastInt()))) yield new Direction[]{Direction.SE};
                if (!segments.contains(new Vector2i(x.lastInt(), y.firstInt()))) yield new Direction[]{Direction.NW};
                if (!segments.contains(new Vector2i(x.lastInt(), y.lastInt()))) yield new Direction[]{Direction.NE};
                yield new Direction[]{};
            }
        };
    }

    public void tickMatch(ClientWorld world, ClientPlayerEntity player) {
        if (matched || failed || candidates.isEmpty()) return;

        BlockPos pPos = player.getBlockPos();

        for (BlockPos pos : BlockPos.iterate(pPos.add(-5, -5, -5), pPos.add(5, 5, 5))) {
            if (pos.getY() < 66 || pos.getY() > 73) continue;
            if (!checkedBlocks.add(pos.toImmutable())) continue;

            if (!segments.contains(DungeonMapUtils.getPhysicalRoomPos(pos))) continue;

            byte blockId = DungeonConstants.NUMERIC_ID.getByte(Registries.BLOCK.getId(world.getBlockState(pos).getBlock()).toString());
            if (blockId == 0) continue;

            checkBlock(pos, blockId);
            if (matched || failed) return;
        }
    }

    private void checkBlock(BlockPos worldPos, byte blockId) {
        Iterator<CandidateGroup> groupIt = candidates.iterator();

        while (groupIt.hasNext()) {
            CandidateGroup group = groupIt.next();
            BlockPos relativePos = DungeonMapUtils.actualToRelative(group.direction, group.cornerPos, worldPos);
            int encodedBlock = encode(relativePos, blockId);

            Iterator<String> nameIt = group.potentialRoomNames.iterator();
            while (nameIt.hasNext()) {
                String roomName = nameIt.next();
                int[] data = DungeonScanner.ROOMS_DATA.get("catacombs").get(shape.key).get(roomName);

                if (data == null || Arrays.binarySearch(data, encodedBlock) < 0) {
                    nameIt.remove();
                }
            }
            if (group.potentialRoomNames.isEmpty()) {
                groupIt.remove();
            }
        }

        int totalPossibilities = candidates.stream().mapToInt(c -> c.potentialRoomNames.size()).sum();

        if (totalPossibilities == 1) {
            CandidateGroup winner = candidates.get(0);
            this.name = winner.potentialRoomNames.get(0);
            this.direction = winner.direction;
            this.physicalCornerPos = winner.cornerPos;
            this.matched = true;
            this.candidates.clear();
            this.checkedBlocks.clear();
        } else if (totalPossibilities == 0) {
            this.failed = true;
            this.checkedBlocks.clear();
            this.candidates.clear();
        }
    }

    private int encode(BlockPos pos, int id) {
        return (pos.getX() << 24) | (pos.getY() << 16) | (pos.getZ() << 8) | id;
    }

    public String getName() {
        return name;
    }

    public boolean isMatched() {
        return matched;
    }

    public Direction getDirection() {
        return direction;
    }

    public Vector2ic getPhysicalCornerPos() {
        return physicalCornerPos;
    }

    public enum Direction {NW, NE, SW, SE}

    public enum Type {ENTRANCE, ROOM, PUZZLE, TRAP, MINIBOSS, FAIRY, BLOOD, UNKNOWN}

    public enum Shape {
        ONE_BY_ONE("1x1"), ONE_BY_TWO("1x2"), ONE_BY_THREE("1x3"), ONE_BY_FOUR("1x4"),
        L_SHAPE("L-shape"), TWO_BY_TWO("2x2"), PUZZLE("puzzle"), TRAP("trap"), MINIBOSS("miniboss");
        public final String key;

        Shape(String key) {
            this.key = key;
        }
    }

    private static class CandidateGroup {
        final Direction direction;
        final Vector2ic cornerPos;
        final List<String> potentialRoomNames;

        CandidateGroup(Direction direction, Vector2ic cornerPos, List<String> potentialRoomNames) {
            this.direction = direction;
            this.cornerPos = cornerPos;
            this.potentialRoomNames = potentialRoomNames;
        }
    }
}