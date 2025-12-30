//#if FABRIC && MC == 1.21.10
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

import de.hysky.skyblocker.events.DungeonEvents;
import de.hysky.skyblocker.skyblock.dungeon.secrets.DungeonManager;
import de.hysky.skyblocker.utils.Utils;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RoomDetection;
import xyz.yourboykyle.secretroutes.utils.SecretUtils;

import java.util.ArrayList;

public class OnEnterNewRoom {
    public static void register() {
        DungeonEvents.ROOM_MATCHED.register(room -> {
            LogUtils.info("Entered new room: " + room.getName());
            onEnterNewRoom(new Room(room.getName()));
        });
    }

    public static void onEnterNewRoom(Room room) {
        try {
            // Make sure the player is actually in a dungeon
            if(!Utils.isInDungeons() || !DungeonManager.isClearingDungeon()) {
                return;
            }

            LogUtils.info("Entered new room \"" + RoomDetection.roomName() + "\".");
            LogUtils.info("Room direction: \"" + RoomDetection.roomDirection());

            Main.currentRoom = room;
            SecretUtils.secrets = null;
            SecretUtils.secretLocations = new ArrayList<>();
            SecretUtils.resetValues();
        } catch(Exception e) {
            LogUtils.error(e);
        }
    }
}
//#endif