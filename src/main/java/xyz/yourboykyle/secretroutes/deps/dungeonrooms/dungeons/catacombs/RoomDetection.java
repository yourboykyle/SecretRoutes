// TODO: update this file for multi versioning (1.8.9 -> 1.21.8)
/*
 * Dungeon Rooms Mod - Secret Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2021 Quantizr(_risk)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs;

import com.google.gson.JsonObject;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.DungeonRooms;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.MapUtils;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.RoomDetectionUtils;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.yourboykyle.secretroutes.events.OnEnterNewRoom;
import xyz.yourboykyle.secretroutes.utils.Room;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.DungeonManager.*;

public class RoomDetection {
    Minecraft mc = Minecraft.getMinecraft();
    static int stage2Ticks = 0;

    static ExecutorService stage2Executor;

    public static List<Point> currentMapSegments;
    public static List<Point> currentPhysicalSegments;

    public static String roomSize = "undefined";
    public static String roomColor = "undefined";
    public static String roomCategory = "undefined";
    public static String roomName = "undefined";
    public static String roomDirection = "undefined";
    public static Point roomCorner;

    public static HashSet<BlockPos> currentScannedBlocks = new HashSet<>();
    public static HashMap<BlockPos, Integer> blocksToCheck = new HashMap<>();
    public static int totalBlocksAvailableToCheck = 0;
    public static List<BlockPos> blocksUsed = new ArrayList<>();
    static Future<HashMap<String, List<String>>> futureUpdatePossibleRooms;
    public static HashMap<String, List<String>> possibleRooms;

    static long incompleteScan = 0;
    static long redoScan = 0;

    static int entranceMapNullCount = 0;


    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;
        if (!Utils.inCatacombs) return;
        EntityPlayerSP player = mc.thePlayer;

        //From this point forward, everything assumes that Utils.inCatacombs == true
        if (gameStage == 2) { //Room clearing phase
            if (mapId == null && extractMapId() == null)  // Extract the map id
                return; //  If we failed to extract the map id, we cannot detect the dungeon room

            stage2Ticks++;
            if (stage2Ticks == 10) {
                stage2Ticks = 0;
                //start ExecutorService with one thread
                if (stage2Executor == null || stage2Executor.isTerminated()) {
                    stage2Executor = Executors.newSingleThreadExecutor();
                }
                //set entranceMapCorners
                if (entranceMapCorners == null) {
                    map = MapUtils.updatedMap();
                    entranceMapCorners = MapUtils.entranceMapCorners(map);
                } else if (entranceMapCorners[0] == null || entranceMapCorners[1] == null) { //prevent crashes if map data bugged
                    entranceMapNullCount++;
                    entranceMapCorners = null; //retry getting corners again next loop

                } else if (entrancePhysicalNWCorner == null) {
                    //for when people dc and reconnect, or if initial check doesn't work
                    Point playerMarkerPos = MapUtils.playerMarkerPos();
                    if (playerMarkerPos != null) {
                        Point closestNWMapCorner = MapUtils.getClosestNWMapCorner(playerMarkerPos, entranceMapCorners[0], entranceMapCorners[1]);
                        if (MapUtils.getMapColor(playerMarkerPos, map).equals("green") && MapUtils.getMapColor(closestNWMapCorner, map).equals("green")) {
                            if (!player.getPositionVector().equals(new Vec3(0.0D, 0.0D, 0.0D))) {
                                entrancePhysicalNWCorner = MapUtils.getClosestNWPhysicalCorner(player.getPositionVector());
                            }
                        }
                    }
                } else {
                    Point currentPhysicalCorner = MapUtils.getClosestNWPhysicalCorner(player.getPositionVector());
                    if (currentPhysicalSegments != null && !currentPhysicalSegments.contains(currentPhysicalCorner)) {
                        //checks if current location is within the bounds of the last detected room
                        resetCurrentRoom(); //only instance of resetting room other than leaving Dungeon
                    } else if (incompleteScan != 0 && System.currentTimeMillis() > incompleteScan) {
                        incompleteScan = 0;
                        raytraceBlocks();
                    } else if (redoScan != 0 && System.currentTimeMillis() > redoScan) {
                        redoScan = 0;
                        possibleRooms = null;
                        raytraceBlocks();
                    }

                    if (currentPhysicalSegments == null || currentMapSegments == null || roomSize.equals("undefined") || roomColor.equals("undefined")) {
                        updateCurrentRoom();
                        if (!roomColor.equals("undefined")) {
                            switch (roomColor) {
                                case "brown":
                                case "purple":
                                case "orange":
                                    raytraceBlocks();
                                    break;
                                case "yellow":
                                    roomName = "Miniboss Room";
                                    newRoom();
                                    break;
                                case "green":
                                    roomName = "Entrance Room";
                                    newRoom();
                                    break;
                                case "pink":
                                    roomName = "Fairy Room";
                                    newRoom();
                                    break;
                                case "red":
                                    roomName = "Blood Room";
                                    newRoom();
                                    break;
                                default:
                                    roomName = "undefined";
                                    break;
                            }
                        }
                    }
                }
            }

            //these run every tick while in room clearing phase

            if (futureUpdatePossibleRooms != null && futureUpdatePossibleRooms.isDone()) {
                try {
                    possibleRooms = futureUpdatePossibleRooms.get();
                    futureUpdatePossibleRooms = null;

                    TreeSet<String> possibleRoomsSet = new TreeSet<>();

                    String tempDirection = "undefined";

                    for (Map.Entry<String, List<String>> entry : possibleRooms.entrySet()) {
                        List<String> possibleRoomList = entry.getValue();
                        if (!possibleRoomList.isEmpty()) tempDirection = entry.getKey(); //get direction to be used if room identified
                        possibleRoomsSet.addAll(possibleRoomList);
                    }


                    if (possibleRoomsSet.size() == 0) { //no match
                        redoScan = System.currentTimeMillis() + 5000;

                    } else if (possibleRoomsSet.size() == 1) { //room found
                        roomName =  possibleRoomsSet.first();
                        roomDirection = tempDirection;
                        roomCorner = MapUtils.getPhysicalCornerPos(roomDirection, currentPhysicalSegments);

                        newRoom();

                    } else { //too many matches

                        incompleteScan = System.currentTimeMillis() + 1000;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void updateCurrentRoom() {
        EntityPlayerSP player = mc.thePlayer;
        map = MapUtils.updatedMap();
        if (map == null) return;
        Point currentPhysicalCorner = MapUtils.getClosestNWPhysicalCorner(player.getPositionVector());
        Point currentMapCorner = MapUtils.physicalToMapCorner(currentPhysicalCorner, entrancePhysicalNWCorner, entranceMapCorners[0], entranceMapCorners[1]);
        roomColor = MapUtils.getMapColor(currentMapCorner, map);
        if (roomColor.equals("undefined")) return;
        currentMapSegments = MapUtils.neighboringSegments(currentMapCorner, map, entranceMapCorners[0], entranceMapCorners[1], new ArrayList<>());
        currentPhysicalSegments = new ArrayList<>();
        for (Point mapCorner : currentMapSegments) {
            currentPhysicalSegments.add(MapUtils.mapToPhysicalCorner(mapCorner, entrancePhysicalNWCorner, entranceMapCorners[0], entranceMapCorners[1]));
        }
        roomSize = MapUtils.roomSize(currentMapSegments);
        roomCategory = MapUtils.roomCategory(roomSize, roomColor);
    }

    public static void resetCurrentRoom() {
        Waypoints.allFound = false;

        currentPhysicalSegments = null;
        currentMapSegments = null;

        roomSize = "undefined";
        roomColor = "undefined";
        roomCategory = "undefined";
        roomName = "undefined";
        roomDirection = "undefined";
        roomCorner = null;

        currentScannedBlocks = new HashSet<>();
        blocksToCheck = new HashMap<>();
        totalBlocksAvailableToCheck = 0;
        blocksUsed = new ArrayList<>();
        futureUpdatePossibleRooms = null;
        possibleRooms = null;

        incompleteScan = 0;
        redoScan = 0;

        Waypoints.secretNum = 0;
    }

    public static Integer extractMapId() {
        if (!MapUtils.mapExists())
            return null;
        ItemStack mapSlot = Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(8); //get map ItemStack
        // MapUtils.updatedMap(mapSlot); // - Skip check
        return mapId = mapSlot.getMetadata();
    }

    public static void newRoom() {
        if (!roomName.equals("undefined") && !roomCategory.equals("undefined")) {
            //update Waypoints info
            if (DungeonRooms.roomsJson.get(roomName) != null) {
                Waypoints.secretNum = DungeonRooms.roomsJson.get(roomName).getAsJsonObject().get("secrets").getAsInt();
                Waypoints.allSecretsMap.putIfAbsent(roomName, new ArrayList<>(Collections.nCopies(Waypoints.secretNum, true)));
            } else {
                Waypoints.secretNum = 0;
                Waypoints.allSecretsMap.putIfAbsent(roomName, new ArrayList<>(Collections.nCopies(0, true)));
            }
            Waypoints.secretsList = Waypoints.allSecretsMap.get(roomName);
        }
        OnEnterNewRoom.onEnterNewRoom(new Room(roomName));
    }


    void raytraceBlocks() {
        long timeStart = System.currentTimeMillis();

        EntityPlayerSP player = mc.thePlayer;

        List<Vec3> vecList = RoomDetectionUtils.vectorsToRaytrace(24); //actually creates 24^2 = 576 raytrace vectors

        Vec3 eyes = new Vec3(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);

        for (Vec3 vec : vecList) {
            //The super fancy Minecraft built in raytracing function so that the mod only scan line of sight blocks!
            //This is the ONLY place where this mod accesses blocks in the physical map, and they are all within FOV
            MovingObjectPosition raytraceResult = player.getEntityWorld().rayTraceBlocks(eyes, vec, false,false, true);

            if (raytraceResult != null && raytraceResult.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                //the following is filtering out blocks which we don't want for detection, note that these blocks are also line of sight
                BlockPos raytracedBlockPos = raytraceResult.getBlockPos();
                if (currentScannedBlocks.contains(raytracedBlockPos)) continue;
                currentScannedBlocks.add(raytracedBlockPos);

                if (!currentPhysicalSegments.contains(MapUtils.getClosestNWPhysicalCorner(raytracedBlockPos))) {
                    continue; //scanned block is outside of current room
                }

                if (RoomDetectionUtils.blockPartOfDoorway(raytracedBlockPos)) {
                    continue; //scanned block may be part of a corridor
                }

                IBlockState hitBlock = mc.theWorld.getBlockState(raytracedBlockPos);
                int identifier = Block.getIdFromBlock(hitBlock.getBlock()) * 100 + hitBlock.getBlock().damageDropped(hitBlock);

                if (RoomDetectionUtils.whitelistedBlocks.contains(identifier)) {
                    blocksToCheck.put(raytracedBlockPos, identifier); //will be checked  and filtered in getPossibleRooms()
                }
            }
        }
        long timeFinish = System.currentTimeMillis();

        if (futureUpdatePossibleRooms == null && stage2Executor != null && !stage2Executor.isTerminated()) { //start processing in new thread to avoid lag in case of complex scan
            futureUpdatePossibleRooms = getPossibleRooms();
        }
    }

    Future<HashMap<String, List<String>>> getPossibleRooms() {
        return stage2Executor.submit(() -> {
            long timeStart = System.currentTimeMillis();
            //load up hashmap
            HashMap<String, List<String>> updatedPossibleRooms;
            List<String> possibleDirections;
            if (possibleRooms == null) {
                //no previous scans have been done, entering all possible rooms and directions
                possibleDirections = MapUtils.possibleDirections(roomSize, currentMapSegments);
                updatedPossibleRooms = new HashMap<>();
                for (String direction : possibleDirections) {
                    updatedPossibleRooms.put(direction, new ArrayList<>(DungeonRooms.ROOM_DATA.get(roomCategory).keySet()));
                }
            } else {
                //load info from previous scan
                updatedPossibleRooms = possibleRooms;
                possibleDirections = new ArrayList<>(possibleRooms.keySet());
            }

            //create HashMap of the points of the corners because they will be repeatedly used for each block
            HashMap<String, Point> directionCorners = new HashMap<>();
            for (String direction : possibleDirections) {
                directionCorners.put(direction, MapUtils.getPhysicalCornerPos(direction, currentPhysicalSegments));
            }


            List<BlockPos> blocksChecked = new ArrayList<>();
            int doubleCheckedBlocks = 0;

            for (Map.Entry<BlockPos, Integer> entry : blocksToCheck.entrySet()) {
                BlockPos blockPos = entry.getKey();
                int combinedMatchingRooms = 0;

                for (String direction : possibleDirections) {
                    //get specific id for the block to compare with ".skeleton" file room data
                    BlockPos relative = MapUtils.actualToRelative(blockPos, direction, directionCorners.get(direction));
                    long idToCheck = Utils.shortToLong((short) relative.getX(), (short) relative.getY(),
                            (short) relative.getZ(), entry.getValue().shortValue());

                    List<String> matchingRooms = new ArrayList<>();
                    //compare with each saved ".skeleton" room
                    for (String roomName : updatedPossibleRooms.get(direction)) {
                        int index = Arrays.binarySearch(DungeonRooms.ROOM_DATA.get(roomCategory).get(roomName), idToCheck);
                        if (index > -1) {
                            matchingRooms.add(roomName);
                        }
                    }

                    //replace updatedPossibleRooms.get(direction) with the updated matchingRooms list
                    combinedMatchingRooms += matchingRooms.size();
                    updatedPossibleRooms.put(direction, matchingRooms);

                }
                blocksChecked.add(blockPos);

                if (combinedMatchingRooms == 0) {
                    break;
                }
                if (combinedMatchingRooms == 1) {
                    //scan 10 more blocks after 1 room remaining to double check
                    if (doubleCheckedBlocks >= 10) {
                        break;
                    }
                    doubleCheckedBlocks++;
                }


            }


            blocksUsed.addAll(blocksChecked);

            //add blocksToCheck size to totalBlocksAvailableToCheck and clear blocksToCheck
            totalBlocksAvailableToCheck += blocksToCheck.size();
            blocksToCheck = new HashMap<>();

            long timeFinish = System.currentTimeMillis();

            return updatedPossibleRooms;
        });
    }
}