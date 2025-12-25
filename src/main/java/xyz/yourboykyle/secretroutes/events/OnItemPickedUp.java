//#if FORGE == 1.8.9
// TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
 *
 * <DO NOT REMOVE THIS COPYRIGHT NOTICE>
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


package xyz.yourboykyle.secretroutes.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import xyz.yourboykyle.secretroutes.utils.*;

import java.util.Arrays;

public class OnItemPickedUp {
    public static boolean itemSecretOnCooldown = false; // True: do not add item secret waypoint, False: add item secret waypoint
    public static final String[] validItems ={
        "Decoy",
        "Defuse Kit",
        "Dungeon Chest Key",
        "Healing VIII",
        "Inflatable Jerry",
        "Spirit Leap",
        "Training Weights",
        "Trap",
        "Treasure Talisman"
    };

    @SubscribeEvent
    public void onPickupItem(PlayerEvent.ItemPickupEvent e) {
        Utils.checkForCatacombs();
        if(!Utils.inCatacombs){return;}
        if(!isSecretItem(e.pickedUp.getEntityItem().getDisplayName())){return;}

        if(SRMConfig.allSecrets){
            if(SecretUtils.secrets == null){return;}
            for(JsonElement obj : SecretUtils.secrets){
                try{
                    JsonObject secret = obj.getAsJsonObject();
                    if(!secret.get("category").getAsString().equals("category")){return;}
                    int x = secret.get("x").getAsInt();
                    int y = secret.get("y").getAsInt();
                    int z = secret.get("z").getAsInt();
                    BlockPos pos = e.player.getPosition();
                    if (pos.getX() >= x - 10 && pos.getX() <= x + 10 && pos.getY() >= y - 10 && pos.getY() <= y + 10 && pos.getZ() >= z - 10 && pos.getZ() <= z + 10) {
                        if(!SecretUtils.secretLocations.contains(BlockUtils.blockPos(new BlockPos(x, y, z)))){
                            SecretUtils.secretLocations.add(BlockUtils.blockPos(new BlockPos(x, y, z)));
                        }
                    }
                }catch (Exception ignored){}
            }
        }


        if(Main.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM) {
            BlockPos pos = e.player.getPosition();
            BlockPos itemPos = Main.currentRoom.getSecretLocation();

            if (pos.getX() >= itemPos.getX() - 10 && pos.getX() <= itemPos.getX() + 10 && pos.getY() >= itemPos.getY() - 10 && pos.getY() <= itemPos.getY() + 10 && pos.getZ() >= itemPos.getZ() - 10 && pos.getZ() <= itemPos.getZ() + 10) {
                Main.currentRoom.nextSecret();
                SecretSounds.secretChime();
                LogUtils.info("Picked up item at " + itemPos);
            }
        }

        // Route Recording
        if(Main.routeRecording.recording) {
            String itemName = e.pickedUp.getEntityItem().getDisplayName();
            if (!itemSecretOnCooldown && isSecretItem(itemName)) {
                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.ITEM, e.player.getPosition());
                Main.routeRecording.newSecret();
                Main.routeRecording.setRecordingMessage("Added item secret waypoint.");
            }
        }
    }
    public static boolean isSecretItem(String itemName){
        for(String item : validItems){
            if(itemName.contains(item)){return true;}
        }
        return false;
    }
}
//#endif
