//#if FORGE == 1.8.9
// TODO: update this file for multi versioning (1.8.9 -> 1.21.10)
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

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.Constants;
import xyz.yourboykyle.secretroutes.utils.FileUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class ChangeRoute extends CommandBase {
    private ArrayList<String> subCommands = new ArrayList<>();
    private ArrayList<String> entries = new ArrayList<>();

    public ChangeRoute(){
        this.subCommands.add("list");
        this.subCommands.add("load");
    }


    @Override
    public String getCommandName() {
        return "changeroute";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/changeroute [list|load [route]]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0){
            sendChatMessage("Incorrect usage: /changeroute [list|load [route]]", EnumChatFormatting.RED);
            return;
        }
        if(args.length == 1 && args[0].equals(subCommands.get(0))){
            entries.addAll(FileUtils.getRouteFileNames());
            sendChatMessage("Routes:", EnumChatFormatting.DARK_AQUA);
            for(String entry : entries){
                sendChatMessage(" - "+entry, EnumChatFormatting.AQUA);
            }
        }else if(args.length == 1 && args[0].equals(subCommands.get(1))){
            sendChatMessage("Incorrect usage: /changeroute load [routename]", EnumChatFormatting.RED);
        }else if(args.length == 2 && args[0].equals(subCommands.get(1))){
            if(FileUtils.doesFileExist(Main.ROUTES_PATH+ File.separator+args[1])){
                SRMConfig.routesFileName = args[1];
                sendChatMessage("Loaded "+args[1]+" as filename for custom routes", EnumChatFormatting.DARK_GREEN);
            }else{
                sendChatMessage("Specified file does not exist", EnumChatFormatting.RED);
            }
        }else if(args.length > 2 && args[0].equals(subCommands.get(1))){
            StringBuilder sb = new StringBuilder("\"");
            for(int i = 1; i < args.length; i++){
                sb.append(args[i]).append(" ");
            }
            sb.deleteCharAt(sb.length()-1).append("\"");
            SRMConfig.routesFileName = sb.toString();
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    public List<String> getCommandAliases()
    {
        List<String> aliases = new ArrayList<>();
        aliases.add("cr");
        aliases.add("croute");
        return aliases;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> completions = new ArrayList<>();
        List<String> basicOptions = new ArrayList<>();
        basicOptions.add("list");
        basicOptions.add("load");
        switch (args.length) {
            case 0:
                completions.addAll(basicOptions);
            case 1:
                completions.addAll(basicOptions);
                completions.removeIf(completion -> !(completion.toLowerCase().startsWith(args[0].toLowerCase())));
                break;
            case 2:
                if(args[0].equalsIgnoreCase("load")) {
                    completions.addAll(FileUtils.getRouteFileNames());
                    break;
                }
            case 3:
                if(args[0].equalsIgnoreCase("load")) {
                    completions.addAll(FileUtils.getRouteFileNames());
                    completions.removeIf(completion -> !(completion.toLowerCase().startsWith(args[1].toLowerCase())));
                    break;
                }
        }

        return completions;
    }
}
//#endif
