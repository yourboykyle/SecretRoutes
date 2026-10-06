//#if FABRIC
/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2025 yourboykyle & R-aMcC & christechs
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
import xyz.yourboykyle.secretroutes.utils.SecretSounds;

public class OnSecretComplete {
    public static void onSecretCompleteNoKeybind() {
        // This is where you would put your code that you want to run when a secret is completed.
        SecretSounds.secretChime();

        if (Main.currentRoom == null || Main.currentRoom.currentSecretRoute == null) return;
        Main.currentRoom.recordPersonalBestStep();
    }
}
//#endif
