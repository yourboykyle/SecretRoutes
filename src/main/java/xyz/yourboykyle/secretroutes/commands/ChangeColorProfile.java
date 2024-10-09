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
