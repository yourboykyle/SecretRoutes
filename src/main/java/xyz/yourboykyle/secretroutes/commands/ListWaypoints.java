package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;

public class ListWaypoints extends CommandBase {
    @Override
    public String getCommandName() {
        return "listwaypoints";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/listwaypoints";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        System.out.println(Main.currentRoom.getRoute());
        if(Main.currentRoom.getNext() == null || Main.currentRoom.getNext().getKey() == null || Main.currentRoom.getNext().getValue() == null) {
            sender.addChatMessage(new ChatComponentText("Waypoints are empty!"));
            return;
        }
        sender.addChatMessage(new ChatComponentText(Main.currentRoom.getRoute().toString()));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
