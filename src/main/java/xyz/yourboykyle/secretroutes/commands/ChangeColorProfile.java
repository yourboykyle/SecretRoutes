/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ConfigUtils;
import xyz.yourboykyle.secretroutes.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class ChangeColorProfile extends CommandBase {
    List<String> aliases = new ArrayList<>();
    public boolean loadDefault = false;
    @Override
    public String getCommandName() {
        return "changecolorprofile";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/changecolorprofile [list|load|save] [profile]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
       if(args.length == 0){
           Main.config.openGui();
       } else if (args.length == 1) {
           if(args[0].equals("list")){
               sendChatMessage(EnumChatFormatting.DARK_AQUA+"Color Profiles:");
               for(String profile : FileUtils.getFileNames(Main.COLOR_PROFILE_PATH)){
                   sendChatMessage(EnumChatFormatting.AQUA+" - "+profile);
               }
           }else if(args[0].equals("load")){
               if(!loadDefault) {
                   sendChatMessage(EnumChatFormatting.RED + "Incorrect usage: /changecolorprofile load [profile]. Run again to load default");
                   loadDefault = true;
               }else{
                   sendChatMessage(EnumChatFormatting.DARK_GREEN+"Loaded default color profile");
               }
           } else if (args[0].equals("save")) {
               sendChatMessage(EnumChatFormatting.RED+"Incorrect usage: /changecolorprofile save [profile]");
           } else{
               sendChatMessage(EnumChatFormatting.RED+"Incorrect usage: /changecolorprofile [list|load|save] [profile]");
           }

       } else if (args.length == 2) {
           if(args[0].equals("load")){
                 if(ConfigUtils.loadColorConfig(args[1])){
                     sendChatMessage(EnumChatFormatting.DARK_GREEN+"Loaded "+EnumChatFormatting.GREEN+args[1]+EnumChatFormatting.DARK_GREEN+" as color profile");
                 }


           }else if(args[0].equals("save")){
                ConfigUtils.writeColorConfig(args[1]);
           }else{
               sendChatMessage(EnumChatFormatting.RED+"Incorrect usage: /changecolorprofile [list|load|save] [profile]");
           }

       }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> getCommandAliases()
    {
        List<String> aliases = new ArrayList<>();
        aliases.add("ccp");
        aliases.add("changeclrp");
        aliases.add("changecolourprofile");
        return aliases;
    }

}
