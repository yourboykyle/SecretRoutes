package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.customevents.EnterNewRoom;
import xyz.yourboykyle.secretroutes.utils.Room;

public class enterNewRoom extends CommandBase {
    @Override
    public String getCommandName() {
        return "enternewroom";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/enternewroom";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 1) {
            EnterNewRoom.onEnterNewRoom(new Room(args[0], true));
        } else {
            EnterNewRoom.onEnterNewRoom(new Room("testRoom"));
        }
        sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Created new testRoom!"));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
