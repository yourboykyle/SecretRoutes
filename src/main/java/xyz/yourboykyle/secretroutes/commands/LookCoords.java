package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;

public class LookCoords extends CommandBase {
    @Override
    public String getCommandName() {
        return "lookcoords";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/lookcoords";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        BlockPos lookCoords = ((EntityPlayer) sender).rayTrace(1000, 1.0F).getBlockPos();

        if(lookCoords != null) {
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "X: " + lookCoords.getX() + ", Y: " + lookCoords.getY() + ", Z: " + lookCoords.getZ()));
        } else {
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "No block within 1000 blocks."));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
