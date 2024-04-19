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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.quantizr.dungeonrooms.DungeonRooms;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;
import xyz.yourboykyle.secretroutes.commands.LoadRoute;
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

    // Key Binds
    public static KeyBinding lastSecret = new KeyBinding("Last Secret", Keyboard.KEY_LBRACKET, "Secret Routes");
    public static KeyBinding nextSecret = new KeyBinding("Next Secret", Keyboard.KEY_RBRACKET, "Secret Routes");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        dungeonRooms.preInit(e);
    }
    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        instance = this;
        dungeonRooms.init(e);

        // Register Events
        MinecraftForge.EVENT_BUS.register(new OnBlockPlace());
        MinecraftForge.EVENT_BUS.register(new OnItemPickedUp());
        MinecraftForge.EVENT_BUS.register(new OnKeyInput());
        MinecraftForge.EVENT_BUS.register(new OnPlayerInteract());
        MinecraftForge.EVENT_BUS.register(new OnPlayerTick());
        MinecraftForge.EVENT_BUS.register(new OnPlaySound());
        MinecraftForge.EVENT_BUS.register(new OnRecievePacket());
        MinecraftForge.EVENT_BUS.register(new OnWorldRender());

        // Register Commands
        ClientCommandHandler.instance.registerCommand(new LoadRoute());
        ClientCommandHandler.instance.registerCommand(new Recording());

        // Register Keybinds
        ClientRegistry.registerKeyBinding(lastSecret);
        ClientRegistry.registerKeyBinding(nextSecret);

        // Make sure room data isn't null
        RoomDetection.roomName = "undefined";
        RoomDetection.roomCorner = new Point(0, 0);
        RoomDetection.roomDirection = "NW";

        // Testing
        String str = "{\"Temple-3\":[{\"locations\":[[7,64,23]],\"etherwarps\":[],\"mines\":[],\"interacts\":[],\"tnts\":[],\"secret\":{\"type\":\"bat\",\"location\":[5,66,25]}},{\"locations\":[[8,64,18],[19,78,25]],\"etherwarps\":[[18,77,26]],\"mines\":[],\"interacts\":[],\"tnts\":[],\"secret\":{\"type\":\"item\",\"location\":[21,78,28]}},{\"locations\":[[21,73,23],[24,69,22],[27,65,24],[28,60,24],[23,59,25],[19,55,25],[14,54,25],[17,54,15]],\"etherwarps\":[[16,53,16]],\"mines\":[],\"interacts\":[],\"tnts\":[],\"secret\":{\"type\":\"interact\",\"location\":[16,54,15]}}],\"Chains-2\":[{\"locations\":[[11,69,17],[14,69,13],[19,69,11]],\"etherwarps\":[],\"mines\":[[24,70,11],[25,70,11],[25,70,10],[26,69,10],[25,69,11],[26,69,11],[25,69,10]],\"interacts\":[],\"tnts\":[],\"secret\":{\"type\":\"interact\",\"location\":[27,69,10]}},{\"locations\":[[19,80,14]],\"etherwarps\":[[18,79,13]],\"mines\":[[17,83,14],[17,84,14],[17,85,14]],\"interacts\":[],\"tnts\":[],\"secret\":{\"type\":\"interact\",\"location\":[17,86,14]}}]}";
        Gson gson = new GsonBuilder().create();
        JsonObject json = gson.fromJson(str, JsonObject.class);
        System.out.println(RouteRecording.prettyPrint(json));
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