package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.FileUtils;

import java.io.File;
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
}
