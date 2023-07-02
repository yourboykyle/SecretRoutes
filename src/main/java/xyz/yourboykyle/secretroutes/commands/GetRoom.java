package xyz.yourboykyle.secretroutes.commands;

import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;

public class GetRoom extends CommandBase {
    @Override
    public String getCommandName() {
        return "getroom";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/getroom";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Room Name: " + RoomDetection.roomName));
        sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Room Corner: " + RoomDetection.roomCorner));
        sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Room Direction: " + RoomDetection.roomDirection));
        sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Relative Position: " + MapUtils.actualToRelative(sender.getPosition(), RoomDetection.roomDirection, RoomDetection.roomCorner)));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
