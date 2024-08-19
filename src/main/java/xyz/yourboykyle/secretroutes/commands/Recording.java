package xyz.yourboykyle.secretroutes.commands;

import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RouteRecording;

import java.io.File;

public class Recording extends CommandBase {
    @Override
    public String getCommandName() {
        return "recording";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/recording start|stop|export|getroom|setbat|import <filename.json>";
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
        } else if(args[0].equalsIgnoreCase("getroom")) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Room Name: " + RoomDetection.roomName + ", Room Corner: " + RoomDetection.roomCorner + ", Room Direction: " + RoomDetection.roomDirection));
        } else if(args[0].equalsIgnoreCase("setbat")) {
            // Route Recording
            if(Main.routeRecording.recording) {
                BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                targetPos = targetPos.add(-1, 2, -1); // Block above the player, the -1 on X and Z have to be like that, trust the process

                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.BAT, targetPos);
                Main.routeRecording.newSecret();
                Main.routeRecording.setRecordingMessage("Added bat secret waypoint.");
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Route recording is not enabled. Run /recording start"));
            }
        }  else if(args[0].equalsIgnoreCase("import")) {
            if(args.length != 2) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Usage: /recording import <filename.json>"));
            } else {
                Main.routeRecording.importRoutes(args[1]);
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Imported routes from " + args[1]));
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