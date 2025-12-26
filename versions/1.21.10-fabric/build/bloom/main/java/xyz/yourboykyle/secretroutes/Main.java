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

package xyz.yourboykyle.secretroutes;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.text.SimpleDateFormat;

public class Main implements ClientModInitializer {
    public static final String MODID = "secretroutesmod";
    public static final String NAME = "SecretRoutes";
    public static final String VERSION = "1.0.0-beta1";
    public static final String CONFIG_FOLDER_PATH = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes";
    public static final String ROUTES_PATH = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes"+File.separator+"routes";
    public static final String COLOR_PROFILE_PATH = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "config" + File.separator + "SecretRoutes"+File.separator+"colorprofiles";
    public static final String tmpDir = MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "SecretRoutes" + File.separator + "tmp";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public final static File logDir = new File(MinecraftClient.getInstance().runDirectory.getAbsolutePath() + File.separator + "logs" + File.separator + "SecretRoutes");
    public static File outputLogs;

    /*
    public static Room currentRoom = new Room(null);
    public static RouteRecording routeRecording = null;
    public static UpdateManager updateManager = new UpdateManager();
    private static DungeonRooms dungeonRooms = new DungeonRooms();
     */

    public static Main instance;

    // HUD instances
    //public static RecordingHUD recordingHUD;
    //public static CurrentRoomHUD currentRoomHUD;

    @Override
    public void onInitializeClient() {
        System.out.println("Hello from SecretRoutes 1.21.10!");
    }
}
//#endif