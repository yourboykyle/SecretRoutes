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
        // New Rooms
        Main.currentRoom.renderLines();

        //If all secrets in the room have been completed
        if(Waypoints.allFound) {
            Main.currentRoom = new Room(null);
        }

        //Path Maker
        //RenderUtils.drawLineMultipleParticles(EnumParticleTypes.FLAME, Main.getPath());

        //Basic check
        /*if(Main.currentRoom.getNext() == null || Main.currentRoom.getNext().getKey() == null || Main.currentRoom.getNext().getValue() == null) {
            return;
        }*/

        /*
        //AOTV
        if(Main.currentRoom.getNext().getValue().equals("aotv")) {
            EntityPlayer p = e.player;
            BlockPos aotvPos = Main.currentRoom.getNext().getKey();

            int x = 0;
            int y = p.getPosition().getY();
            int z = 0;

            if(p.getPosition().getX() < 0) { // -1 -> -2
                x = p.getPosition().getX() - 1;
            } else { // 1 -> 2
                x = p.getPosition().getX() + 1;
            }
            if(p.getPosition().getZ() < 0) { // -1 -> -2
                z = p.getPosition().getZ() - 1;
            } else { // 1 -> 2
                z = p.getPosition().getZ() + 1;
            }

            BlockPos pos = new BlockPos(x, y, z);

            if(pos.getX() >= aotvPos.getX() && pos.getX() <= aotvPos.getX() && pos.getY() >= aotvPos.getY() + 1 && pos.getY() <= aotvPos.getY() + 2 && pos.getZ() >= aotvPos.getZ() && pos.getZ() <= aotvPos.getZ()) {
                if(Main.currentRoom.getRoute().size() < 2) {
                    Main.currentRoom.add(null, null);
                }
                Main.currentRoom.removeNext();
                SecretCompleted.onSecretCompleted();
                p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "AOTVed!"));
            }
            return;
        }

        //Bat
        if(Main.currentRoom.getNext().getValue().equals("bat")) {
            EntityPlayer p = e.player;
            BlockPos pos = p.getPosition();
            BlockPos batPos = Main.currentRoom.getNext().getKey();

            if (pos.getX() >= batPos.getX() - 3 && pos.getX() <= batPos.getX() + 3 && pos.getY() >= batPos.getY() - 3 && pos.getY() <= batPos.getY() + 3 && pos.getZ() >= batPos.getZ() - 3 && pos.getZ() <= batPos.getZ() + 3) {
                if(Main.currentRoom.getRoute().size() < 2) {
                    Main.currentRoom.add(null, null);
                }
                Main.currentRoom.removeNext();
                SecretCompleted.onSecretCompleted();
                Main.displayNotification(EnumChatFormatting.RED + "WARNING! Kill the bat!");
                p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Killed the bat!"));
            }
            return;
        }*/



        // New Rooms

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