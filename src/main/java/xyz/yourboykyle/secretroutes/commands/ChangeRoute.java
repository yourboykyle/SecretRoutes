package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.List;

public class ChangeRoute extends CommandBase {
    private ArrayList<String> subCommands = new ArrayList<String>();

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
        //THIS ISI WHERE YOU NEED TO CONTINUE WORKING ON ADDING THE THING
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
