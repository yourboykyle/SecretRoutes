package xyz.yourboykyle.secretroutes.commands;

import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.LogUtils;
import xyz.yourboykyle.secretroutes.utils.Room;

import static xyz.yourboykyle.secretroutes.utils.ChatUtils.sendChatMessage;

public class Recording extends CommandBase {
    @Override
    public String getCommandName() {
        return "recording";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/recording start|stop|export|getroom|setbat|setexit|import <filename.json>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0){
            Main.config.openGui();
        }
        if(args[0].equalsIgnoreCase("start")) {
            Main.routeRecording.startRecording();
        } else if(args[0].equalsIgnoreCase("stop")) {
            Main.routeRecording.stopRecording();
        } else if(args[0].equalsIgnoreCase("export")) {
            Main.routeRecording.exportAllRoutes();
        } else if(args[0].equalsIgnoreCase("getroom")) {
           sendChatMessage(EnumChatFormatting.BLUE+"Room Name: " + RoomDetection.roomName + ", Room Corner: " + RoomDetection.roomCorner + ", Room Direction: " + RoomDetection.roomDirection);
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
                sendChatMessage(EnumChatFormatting.RED+"Route recording is not enabled. Run /recording start");
            }
        } else if(args[0].equalsIgnoreCase("setexit")) {
            if(Main.routeRecording.recording) {
                BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                targetPos = targetPos.add(-1, 0, -1); // The -1 on X and Z have to be like that, trust the process

                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.EXITROUTE, targetPos);
                Main.routeRecording.newSecret();
                Main.routeRecording.stopRecording(); // Exiting the route, it should be stopped
                Main.routeRecording.setRecordingMessage("Added route exit waypoint & stopped recording.");
                LogUtils.info("Added route exit waypoint & stopped recording.");
            } else {
                sendChatMessage(EnumChatFormatting.RED+"Route recording is not enabled. Run /recording start");
            }
        } else if(args[0].equalsIgnoreCase("import")) {
            if(args.length != 2) {
              sendChatMessage(EnumChatFormatting.RED+"Usage: /recording import <filename.json>");
            } else {
                Main.routeRecording.importRoutes(args[1]);
                sendChatMessage(EnumChatFormatting.DARK_GREEN+"Imported routes from " + EnumChatFormatting.GREEN + args[1]);
            }
        } else {
           sendChatMessage(EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender));
        }
    }

    @Override
    public int getRequiredPermissionLevel() {return 0;}
}