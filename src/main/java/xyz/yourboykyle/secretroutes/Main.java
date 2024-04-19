/*
 * Secret Routes Mod - Secret Route Waypoints for Hypixel Skyblock Dungeons
 * Copyright 2023 yourboykyle
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

import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.yourboykyle.secretroutes.commands.EnterNewRoom;
import xyz.yourboykyle.secretroutes.commands.LoadRoute;
import xyz.yourboykyle.secretroutes.commands.NextSecret;
import xyz.yourboykyle.secretroutes.commands.Recording;
import xyz.yourboykyle.secretroutes.events.*;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RouteRecording;

import java.awt.*;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
    public static final String MODID = "SecretRoutes";
    public static final String VERSION = "1.0";

    public static Room currentRoom = new Room(null);
    public static RouteRecording routeRecording = new RouteRecording();
    private static DungeonRooms dungeonRooms = new DungeonRooms();

    public static Main instance = new Main();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        dungeonRooms.preInit(e);
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        instance = this;
        dungeonRooms.init(e);

        //Events
        MinecraftForge.EVENT_BUS.register(new OnItemPickedUp());
        MinecraftForge.EVENT_BUS.register(new OnBlockPlace());
        MinecraftForge.EVENT_BUS.register(new OnPlaySound());
        MinecraftForge.EVENT_BUS.register(new OnRecievePacket());
        MinecraftForge.EVENT_BUS.register(new OnPlayerInteract());
        MinecraftForge.EVENT_BUS.register(new OnPlayerTick());
        MinecraftForge.EVENT_BUS.register(new OnWorldRender());

        ClientCommandHandler.instance.registerCommand(new EnterNewRoom());
        ClientCommandHandler.instance.registerCommand(new LoadRoute());
        ClientCommandHandler.instance.registerCommand(new NextSecret());
        ClientCommandHandler.instance.registerCommand(new Recording());

        RoomDetection.roomName = "undefined";
        RoomDetection.roomCorner = new Point(0, 0);
        RoomDetection.roomDirection = "NW";
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        dungeonRooms.postInit(e);
    }

    public static void checkRoomData() {
        if(RoomDetection.roomName == null) {
            RoomDetection.roomName = "undefined";
        }
        if(RoomDetection.roomCorner == null) {
            RoomDetection.roomCorner = new Point(0, 0);
        }
        if(RoomDetection.roomDirection == null) {
            RoomDetection.roomDirection = "NW";
        }
    }
}