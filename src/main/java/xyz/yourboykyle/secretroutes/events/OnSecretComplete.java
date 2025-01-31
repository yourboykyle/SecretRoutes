package xyz.yourboykyle.secretroutes.events;

import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.PBUtils;
import xyz.yourboykyle.secretroutes.utils.SecretSounds;

public class OnSecretComplete {
    public static void onSecretCompleteNoKeybind() {
        // This is where you would put your code that you want to run when a secret is completed.
        SecretSounds.secretChime();

        // PB Stuff
        if(Main.currentRoom.currentSecretIndex == 0) {
            ChatUtils.sendVerboseMessage("Starting timer for " + Main.currentRoom.name);
            PBUtils.pbIsValid = true;
            PBUtils.startRoute();
        } else if (Main.currentRoom.currentSecretIndex == Main.currentRoom.currentSecretRoute.size() - 1) {
            ChatUtils.sendVerboseMessage("Stopping timer for " + Main.currentRoom.name);
            PBUtils.stopRoute();
        }

        if(Main.currentRoom.currentSecretIndex <= Main.currentRoom.currentSecretRoute.size() - 1) {
            // If the route hasn't been completed yet, log progress for debugging
            ChatUtils.sendVerboseMessage("Secret " + (Main.currentRoom.currentSecretIndex + 1) + "/" + (Main.currentRoom.currentSecretRoute.size()) + " in " + RoomDetection.roomName + " completed in §a" + ((Main.currentRoom.currentSecretIndex > 0) ? PBUtils.formatTime(System.currentTimeMillis() - PBUtils.startTime) : "0.000s") + " §r(PB is valid: " + (PBUtils.pbIsValid ? "true" : "false") + ")");
        }
    }
}