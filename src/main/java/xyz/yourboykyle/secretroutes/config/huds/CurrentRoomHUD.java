/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2024 yourboykyle & R-aMcC
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

package xyz.yourboykyle.secretroutes.config.huds;

import cc.polyfrost.oneconfig.config.annotations.Color;
import cc.polyfrost.oneconfig.config.core.OneColor;
import cc.polyfrost.oneconfig.hud.SingleTextHud;
import xyz.yourboykyle.secretroutes.deps.dungeonrooms.dungeons.catacombs.RoomDetection;
import xyz.yourboykyle.secretroutes.Main;

public class CurrentRoomHUD extends SingleTextHud {
    @Color(
            name = "Default HUD colour"
    )
    public static OneColor hudColour = new OneColor(255, 255, 255);

    public CurrentRoomHUD(){
        super("", false);
    }

    @Override
    public String getText(boolean example) {
        if(Main.routeRecording.recording){
            return RoomDetection.roomName;
        }
        return "";
    }

    public void enable() {
        this.enabled = true;
        // Cant be accessed from static context
    }

    public void disable() {
        this.enabled = false;
    }
}