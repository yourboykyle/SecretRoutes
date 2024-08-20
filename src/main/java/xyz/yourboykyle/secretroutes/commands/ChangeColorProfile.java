package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
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
        return "/changecolorprofile [list|load [profile]]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
       if(args.length == 0){
           Main.config.openGui();
       } else if (args.length == 1) {
           if(args[0].equals("list")){
               sendChatMessage("Color Profiles:", EnumChatFormatting.DARK_AQUA);
               for(String profile : FileUtils.getFileNames(Main.COLOR_PROFILE_PATH)){
                   sendChatMessage(" - "+profile, EnumChatFormatting.AQUA);
               }
           }else if(args[0].equals("load")){
               if(!loadDefault) {
                   sendChatMessage("Incorrect usage: /changecolorprofile load [profile]. Run again to load default", EnumChatFormatting.RED);
                   loadDefault = true;
               }else{
                   sendChatMessage("Loaded default color profile", EnumChatFormatting.DARK_GREEN);
               }
           } else if (args[0].equals("save")) {
               sendChatMessage("Incorrect usage: /changecolorprofile save [profile]", EnumChatFormatting.RED);
           } else{
               sendChatMessage("Incorrect usage: /changecolorprofile [list|load|save] [profile]", EnumChatFormatting.RED);
           }

       } else if (args.length == 2) {
           if(args[0].equals("load")){
                 if(Main.loadColorConfig(args[1])){
                     sendChatMessage("Loaded "+args[1]+" as color profile", EnumChatFormatting.DARK_GREEN);
                 }


           }else{
               sendChatMessage("Incorrect usage: /changecolorprofile [list|load [profile]]", EnumChatFormatting.RED);
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
