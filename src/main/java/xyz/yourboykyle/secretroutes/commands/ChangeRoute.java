package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.SRMConfig;
import xyz.yourboykyle.secretroutes.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        Minecraft.getMinecraft().thePlayer.addChatMessage(new net.minecraft.util.ChatComponentText("Change route... (WIP)"));
        if(args.length == 0){
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Incorrect usage: /changeroute [list|load [route]]"));
            return;
        }
        if(args.length == 1 && args[0].equals(subCommands.get(0))){
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("list called, list all the files"));
            entries.addAll(FileUtils.getRouteFileNames());
            for(String entry : entries){
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("File: "+entry));
            }
        }else if(args.length == 1 && args[0].equals(subCommands.get(1))){
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Incorrect usage: /changeroute load [routename]"));
        }else if(args.length == 2 && args[0].equals(subCommands.get(1))){
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("This command will load the route of the specified file name"));
            if(FileUtils.doesFileExist(Main.ROUTES_PATH+ File.separator+args[1])){
                SRMConfig.routesFileName = args[1];
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Loaded "+args[1]+" as filename for custom routes"));
            }else{
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Specified file does not exist"));
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
