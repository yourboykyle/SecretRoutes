package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.BlockUtils;

public class OnEtherwarp {
    public static int etherwarp = 0;
    public static JsonObject currWaypoints = null;
    public static BlockPos etherwarpPosition = null;
    @SubscribeEvent(receiveCanceled = true, priority = EventPriority.HIGHEST)
    public static void onEtherwarp(){
        //make sure we have the route
        if(currWaypoints == null){
            currWaypoints = Main.currentRoom.currentSecretWaypoints;
        }
        if(etherwarpPosition == null){
            setEtherwarp(0);
        }
        //Get the current secret index
//        int secretIndex = Main.currentRoom.currentSecretIndex;
        // Make sure that we have the setting enabled and etherwarps in the route
        if(SRMConfig.nextEtherwarp && currWaypoints.has("etherwarps")){
            //Check if within three blocks
            if(BlockUtils.compareBlocks(etherwarpPosition, Minecraft.getMinecraft().thePlayer.getPosition(), 3)){
                etherwarp++;
                setEtherwarp(etherwarp);
            }
        }
    }
    public static void setEtherwarp(int index){
        JsonArray secretEtherwarps = currWaypoints.getAsJsonArray("etherwarps");
        JsonArray etherwarpCoordinate = secretEtherwarps.get(index).getAsJsonArray();
        //Update the current location block
        etherwarpPosition = new BlockPos(etherwarpCoordinate.get(0).getAsInt(), etherwarpCoordinate.get(1).getAsInt(), etherwarpCoordinate.get(2).getAsInt());

    }
    public static void resetValues(){
        etherwarp = 0;
        currWaypoints = null;
        etherwarpPosition = null;
    }
}
