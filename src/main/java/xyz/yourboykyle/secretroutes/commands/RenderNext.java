package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.customevents.SecretCompleted;

public class RenderNext extends CommandBase {
    @Override
    public String getCommandName() {
        return "rendernext";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/rendernext";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(Main.currentRoom.getRoute().size() < 2) {
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "There are no more waypoints to render, cancelled rendernext."));
            return;
        }
        Main.currentRoom.removeNext();
        SecretCompleted.onSecretCompleted();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
