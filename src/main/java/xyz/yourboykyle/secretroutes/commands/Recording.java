package xyz.yourboykyle.secretroutes.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;

import java.io.File;

public class Recording extends CommandBase {
    @Override
    public String getCommandName() {
        return "recording";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/recording start|stop|export|roomname|setbat";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args[0].equalsIgnoreCase("start")) {
            Main.routeRecording.startRecording();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Started recording"));
        } else if(args[0].equalsIgnoreCase("stop")) {
            Main.routeRecording.stopRecording();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Stopped recording"));
        } else if(args[0].equalsIgnoreCase("export")) {
            Main.routeRecording.exportAllRoutes();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Exported all routes to " + System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "routes.json"));
        } else if(args[0].equalsIgnoreCase("roomname")) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Room Name: " + Main.currentRoom.name));
        } else if(args[0].equalsIgnoreCase("setbat")) {
            // Route Recording
            if(Main.routeRecording.recording) {
                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.BAT, Minecraft.getMinecraft().thePlayer.getPosition());
                Main.routeRecording.newSecret();
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Added bat waypoint."));
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Route recording is not enabled. Run /recording start"));
            }
        } else {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}