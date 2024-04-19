/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
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

import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.Utils;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

public class OnEnterNewRoom {
    public static void onEnterNewRoom(Room room) {
        try {
            Utils.checkForCatacombs();
            /*if(!Utils.inCatacombs) {
                return;
            }*/

            System.out.println("Entered new room \"" + RoomDetection.roomName + "\".");

            Main.currentRoom = room;
            System.out.println(Main.currentRoom.currentSecretRoute);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
