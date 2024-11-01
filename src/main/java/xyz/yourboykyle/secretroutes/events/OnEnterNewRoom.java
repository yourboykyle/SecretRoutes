/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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

import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.DungeonManager;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.utils.Utils;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.SecretUtils;

import java.util.ArrayList;

public class OnEnterNewRoom {
    public static void onEnterNewRoom(Room room) {
        try {
            // Make sure the player is actually in a dungeon
            Utils.checkForCatacombs();
            if(!Utils.inCatacombs || DungeonManager.gameStage != 2) {
                return;
            }

            LogUtils.info("Entered new room \"" + RoomDetection.roomName + "\".");
            LogUtils.info("Room direction: \"" + RoomDetection.roomDirection);

            Main.currentRoom = room;
            SecretUtils.secrets = null;
            SecretUtils.secretLocations = new ArrayList<>();
            SecretUtils.resetValues();
        } catch(Exception e) {
            LogUtils.error(e);
        }
        if(SRMConfig.debug){
            //Send chat message with index of closest point just to test things
            room.getTest();

        }
    }
}