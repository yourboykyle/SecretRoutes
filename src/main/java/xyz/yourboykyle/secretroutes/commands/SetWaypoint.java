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

package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;

public class SetWaypoint extends CommandBase {
    @Override
    public String getCommandName() {
        return "setwaypoint";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/setwaypoint <type>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args[0].equalsIgnoreCase("move")) {
            //
        } else if(args[0].equalsIgnoreCase("stonk")) {
            Main.currentRoom.add(sender.getPosition(), "stonk");
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created stonk waypoint."));
        } else if(args[0].equalsIgnoreCase("aotv")) {
            Main.currentRoom.add(sender.getPosition(), "aotv");
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created aotv waypoint."));
        } else if(args[0].equalsIgnoreCase("chest")) {
            Main.currentRoom.add(sender.getPosition(), "chest");
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created chest waypoint."));
        } else if(args[0].equalsIgnoreCase("item")) {
            Main.currentRoom.add(sender.getPosition(), "item");
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created item waypoint."));
        } else if(args[0].equalsIgnoreCase("superboom")) {
            Main.currentRoom.add(sender.getPosition(), "superboom");
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created superboom waypoint."));
        } else if(args[0].equalsIgnoreCase("bat")) {
            Main.currentRoom.add(sender.getPosition(), "bat");
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created bat waypoint."));
        } else if(args[0].equalsIgnoreCase("lever")) {
            Main.currentRoom.add(sender.getPosition(), "lever");
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created lever waypoint."));
        } else if(args[0].equalsIgnoreCase("wither")) {
            Main.currentRoom.add(sender.getPosition(), "wither");
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created wither waypoint."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
