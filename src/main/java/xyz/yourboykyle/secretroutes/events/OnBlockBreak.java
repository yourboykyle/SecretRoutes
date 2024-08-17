package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnBlockBreak {
    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent e) {
        try {
            // Route Recording
            if(e.getPlayer().getUniqueID() == Minecraft.getMinecraft().thePlayer.getUniqueID() && Main.routeRecording.recording) {
                boolean shouldAddWaypoint = false;

                JsonArray waypoints = Main.routeRecording.currentSecretWaypoints.get("mines").getAsJsonArray();
                if(waypoints.size() > 1) {
                    for (JsonElement waypoint : waypoints) {
                        JsonArray waypointCoords = waypoint.getAsJsonArray();

                        Main.checkRoomData();
                        BlockPos relPos = MapUtils.actualToRelative(e.pos, RoomDetection.roomDirection, RoomDetection.roomCorner);

                        // Check if waypoints already has the broken block
                        if (!(relPos.getX() == waypointCoords.get(0).getAsInt() && relPos.getY() == waypointCoords.get(1).getAsInt() && relPos.getZ() == waypointCoords.get(2).getAsInt())) {
                            // Waypoint doesn't exist yet
                            shouldAddWaypoint = true;
                        }
                    }
                } else {
                    // Waypoint doesn't exist yet
                    shouldAddWaypoint = true;
                }

                ItemStack heldItem = e.getPlayer().getHeldItem();
                if(heldItem != null) {
                    // Check if the player is holding a pickaxe
                    if(!(heldItem.getItem() instanceof ItemPickaxe)) {
                        // Player is not holding a pickaxe, do not add waypoint
                        shouldAddWaypoint = false;
                    }
                } else {
                    // Player is not holding anything
                    shouldAddWaypoint = false;
                }

                if(shouldAddWaypoint) {
                    Main.routeRecording.addWaypoint(Room.WAYPOINT_TYPES.MINES, e.pos);
                    //Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Added mine waypoint."));
                }
            }
        } catch (Exception ex) {

        }
    }
}