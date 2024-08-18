package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import xyz.yourboykyle.secretroutes.Main;

import java.util.ArrayList;
import java.util.List;


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
        Main.config.openGui();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> getCommandAliases()
    {
        List<String> aliases = new ArrayList<>();
        aliases.add("secretroutes");
        aliases.add("secretroutesmod");
        return aliases;
    }
}