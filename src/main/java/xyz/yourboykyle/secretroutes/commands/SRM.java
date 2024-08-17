package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class SRM extends CommandBase {
    @Override
    public String getCommandName() {
        return "srm";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/srm";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Secret routes mod is loaded..."));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}