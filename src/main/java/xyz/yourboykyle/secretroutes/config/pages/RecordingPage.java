package xyz.yourboykyle.secretroutes.config.pages;

import cc.polyfrost.oneconfig.config.annotations.Button;
import cc.polyfrost.oneconfig.config.annotations.HUD;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.data.OptionSize;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.config.huds.CurrentRoomHUD;
import xyz.yourboykyle.secretroutes.config.huds.RecordingHUD;
import xyz.yourboykyle.secretroutes.utils.Room;

public class RecordingPage {
    @Button(
            name = "Set Bat Waypoint",
            text = "Set Bat Waypoint",
            description = "Since bat waypoints aren't automatically detected, sadly we must manually set a bat waypoint",
            size = 2
    )
    Runnable runnable = () -> {
        new Thread(() -> {
            if(Main.routeRecording.recording) {
                BlockPos playerPos = Minecraft.getMinecraft().thePlayer.getPosition();
                BlockPos targetPos = new BlockPos(playerPos.getX(), playerPos.getY(), playerPos.getZ());
                targetPos = targetPos.add(-1, 2, -1); // Block above the player, the -1 on X and Z have to be like that, trust the process

                Main.routeRecording.addWaypoint(Room.SECRET_TYPES.BAT, targetPos);
                Main.routeRecording.newSecret();
                Main.routeRecording.setRecordingMessage("Added bat secret waypoint.");
            } else {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Route recording is not enabled. Press the start recording button to begin."));
            }
        }).start();
    };

    @Button(
            name = "Start recording",
            text = "Start recording",
            description = "Start recording a custom secret route",
            size = 2
    )
    Runnable runnable2 = () -> {
        new Thread(() -> {
            Main.routeRecording.startRecording();
        }).start();
    };

    @Button(
            name = "Stop recording",
            text = "Stop recording",
            description = "Stop recording your secret route",
            size = 2
    )
    Runnable runnable3 = () -> {
        new Thread(() -> {
            Main.routeRecording.stopRecording();
        }).start();
    };

    @Button(
            name = "Export routes",
            text = "Export routes",
            description = "Export your current secret routes to your downloads folder as routes.json",
            size = 2
    )
    Runnable runnable4 = () -> {
        new Thread(() -> {
            Main.routeRecording.exportAllRoutes();
        }).start();
    };

    @Button(
            name = "Import routes",
            text = "Import routes",
            description = "Import routes to recording from a routes.json in your downloads folder",
            size = 2
    )
    Runnable runnable5 = () -> {
        new Thread(() -> {
            Main.routeRecording.importRoutes("routes.json");
        }).start();
    };

    @HUD(
            name = "Recording info",
            category = "HUD"
    )
    public static RecordingHUD recordingHUD = new RecordingHUD();

    @HUD(
            name = "Current room",
            category = "HUD"
    )
    public static CurrentRoomHUD currentRoomHUD = new CurrentRoomHUD();
}