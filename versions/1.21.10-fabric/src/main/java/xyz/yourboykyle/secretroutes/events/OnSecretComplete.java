//#if FABRIC && MC == 1.21.10
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC
 *
 * <DO NOT REMOVE THIS COPYRIGHT NOTICE>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package xyz.yourboykyle.secretroutes.events;

import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.ChatUtils;
import xyz.yourboykyle.secretroutes.utils.PBUtils;
import xyz.yourboykyle.secretroutes.utils.RoomDetection;
import xyz.yourboykyle.secretroutes.utils.SecretSounds;

public class OnSecretComplete {
    public static void onSecretCompleteNoKeybind() {
        // This is where you would put your code that you want to run when a secret is completed.
        SecretSounds.secretChime();

        // PB Stuff
        if(Main.currentRoom.currentSecretIndex == 0) {
            ChatUtils.sendVerboseMessage("Starting timer for " + Main.currentRoom.name, "Personal Bests");
            PBUtils.pbIsValid = true;
            PBUtils.startRoute();
        } else if (Main.currentRoom.currentSecretIndex == Main.currentRoom.currentSecretRoute.size() - 1) {
            ChatUtils.sendVerboseMessage("Stopping timer for " + Main.currentRoom.name, "Personal Bests");
            PBUtils.stopRoute();
        }

        if(Main.currentRoom.currentSecretIndex <= Main.currentRoom.currentSecretRoute.size() - 1) {
            // If the route hasn't been completed yet, log progress for debugging
            ChatUtils.sendVerboseMessage("Secret " + (Main.currentRoom.currentSecretIndex + 1) + "/" + (Main.currentRoom.currentSecretRoute.size()) + " in " + RoomDetection.roomName() + " completed in §a" + ((Main.currentRoom.currentSecretIndex > 0) ? PBUtils.formatTime(System.currentTimeMillis() - PBUtils.startTime) : "0.000s") + " §r(PB is valid: " + (PBUtils.pbIsValid ? "true" : "false") + ")", "Personal Bests");
        }
    }
}
//#endif
