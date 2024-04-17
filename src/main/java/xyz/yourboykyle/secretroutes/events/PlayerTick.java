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

import io.github.quantizr.dungeonrooms.dungeons.catacombs.Waypoints;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

public class PlayerTick {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent e) {

        //If all secrets in the room have been completed
        if(Waypoints.allFound) {
            Main.currentRoom = new Room(null);
        }

        Main.currentRoom.renderLines();

        if(Main.currentRoom.getSecretType() == Room.SECRET_TYPES.BAT) {
            BlockPos pos = e.player.getPosition();
            BlockPos batPos = Main.currentRoom.getSecretLocation();

            if (pos.getX() >= batPos.getX() - 5 && pos.getX() <= batPos.getX() + 5 && pos.getY() >= batPos.getY() - 5 && pos.getY() <= batPos.getY() + 5 && pos.getZ() >= batPos.getZ() - 5 && pos.getZ() <= batPos.getZ() + 5) {
                Main.currentRoom.nextSecret();
                System.out.println("Went by bat at " + batPos);
            }
        }
    }
}