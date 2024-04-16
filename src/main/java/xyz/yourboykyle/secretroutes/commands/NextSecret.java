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
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import xyz.yourboykyle.secretroutes.Main;

public class NextSecret extends CommandBase {
    @Override
    public String getCommandName() {
        return "nextsecret";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/nextsecret";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Main.currentRoom.nextSecret();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
