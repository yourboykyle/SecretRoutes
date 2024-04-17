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

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

public class ItemPickedUp {
    public static void onPickupItem(PlayerEvent.ItemPickupEvent e) {
        if(Main.currentRoom.getSecretType() == Room.SECRET_TYPES.ITEM) {
            BlockPos pos = e.player.getPosition();
            BlockPos itemPos = Main.currentRoom.getSecretLocation();

            if (pos.getX() >= itemPos.getX() - 10 && pos.getX() <= itemPos.getX() + 10 && pos.getY() >= itemPos.getY() - 10 && pos.getY() <= itemPos.getY() + 10 && pos.getZ() >= itemPos.getZ() - 10 && pos.getZ() <= itemPos.getZ() + 10) {
                Main.currentRoom.nextSecret();
                System.out.println("Picked up item at " + itemPos);
            }
        }
    }
}