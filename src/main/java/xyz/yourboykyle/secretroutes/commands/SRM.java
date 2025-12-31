//#if FORGE && MC == 1.8.9
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

package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;

public class SRM extends CommandBase {
    @Override
    public String getCommandName() {
        return "srm";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/srm";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        SRMConfig.INSTANCE.openGui();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> getCommandAliases()
    {
        List<String> aliases = new ArrayList<>();
        aliases.add("secretroutes");
        aliases.add("secretroutesmod");
        return aliases;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> completions = new ArrayList<>();
        completions.add("General");
        completions.add("RouteRecording");
        completions.add("HUD");
        completions.add("Rendering");
        completions.add("Dev");
        completions.add("Keybinds");
        completions.removeIf(completion -> args.length > 0 && !(completion.toLowerCase().startsWith(args[0].toLowerCase())));

        return completions;
    }
}
//#endif
