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
import io.github.quantizr.dungeonrooms.events.PacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xyz.yourboykyle.secretroutes.commands.EnterNewRoom;
import xyz.yourboykyle.secretroutes.commands.NextSecret;
import xyz.yourboykyle.secretroutes.commands.Recording;
import xyz.yourboykyle.secretroutes.events.ItemPickedUp;
import xyz.yourboykyle.secretroutes.events.PlayerInteract;
import xyz.yourboykyle.secretroutes.events.PlayerTick;
import xyz.yourboykyle.secretroutes.events.WorldRender;
import xyz.yourboykyle.secretroutes.utils.Room;
import xyz.yourboykyle.secretroutes.utils.RouteRecording;

import java.awt.*;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
    public static final String MODID = "SecretRoutes";
    public static final String VERSION = "1.0";
    public static final String newRoomsDataPath = "/routes.json";

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
        MinecraftForge.EVENT_BUS.register(Main.instance);
        MinecraftForge.EVENT_BUS.register(new ItemPickedUp());
        MinecraftForge.EVENT_BUS.register(new PlayerInteract());
        MinecraftForge.EVENT_BUS.register(new PlayerTick());
        MinecraftForge.EVENT_BUS.register(new WorldRender());

        ClientCommandHandler.instance.registerCommand(new EnterNewRoom());
        ClientCommandHandler.instance.registerCommand(new NextSecret());
        ClientCommandHandler.instance.registerCommand(new Recording());

        RoomDetection.roomName = "Example-Room-3";
        RoomDetection.roomCorner = new Point(0, 0);
        RoomDetection.roomDirection = "NW";
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        dungeonRooms.postInit(e);
    }

    @SubscribeEvent
    public void onItemPickup(PlayerEvent.ItemPickupEvent e) {
        ItemPickedUp.onPickupItem(e);
    }

    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent e) {
        try {
            if (e.packet instanceof S0DPacketCollectItem) { // Note to Hypixel: This is not manipulating packets, it is simply listening and checking for the collect item packet. If that is the correct packet, it simulates creating an itempickedup event client-side
                S0DPacketCollectItem packet = (S0DPacketCollectItem) e.packet;
                Entity entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.getCollectedItemEntityID());

                if(entity instanceof EntityItem) {
                    EntityItem item = (EntityItem) entity;
                    entity = Minecraft.getMinecraft().theWorld.getEntityByID(packet.getEntityID());
                    if(entity == null) {
                        System.out.println("Entity is null.");
                        return;
                    }
                    if(!entity.getCommandSenderEntity().getName().equals(Minecraft.getMinecraft().thePlayer.getName())) {
                        // Someone else has picked up the item
                        return;
                    }

                    PlayerEvent.ItemPickupEvent itemPickupEvent = new PlayerEvent.ItemPickupEvent(Minecraft.getMinecraft().thePlayer, item);
                    ItemPickedUp.onPickupItem(itemPickupEvent);
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("There was an error with " + MODID));
        }
    }
}