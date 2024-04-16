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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import xyz.yourboykyle.secretroutes.commands.NextSecret;
import xyz.yourboykyle.secretroutes.commands.RenderNext;
import xyz.yourboykyle.secretroutes.commands.enterNewRoom;
import xyz.yourboykyle.secretroutes.customevents.BlockBreak;
import xyz.yourboykyle.secretroutes.customevents.BlockPlace;
import xyz.yourboykyle.secretroutes.customevents.ItemPickedUp;
import xyz.yourboykyle.secretroutes.events.PlayerInteract;
import xyz.yourboykyle.secretroutes.events.PlayerTick;
import xyz.yourboykyle.secretroutes.events.WorldRender;
import xyz.yourboykyle.secretroutes.utils.Room;

import java.awt.*;

@Mod(modid = Main.MODID, version = Main.VERSION)
public class Main {
    public static final String MODID = "SecretRoutes";
    public static final String VERSION = "1.0";
    public static final String chatPrefix = EnumChatFormatting.AQUA + "Secret Routes > " + EnumChatFormatting.RESET;
    public static final String roomsDataPath = "/rooms.json";
    public static final String newRoomsDataPath = "/newrooms.json";

    public static Room currentRoom = new Room(null);
    //private static Queue<List<BlockPos>> path = new LinkedList<>();
    private static DungeonRooms dungeonRooms = new DungeonRooms();

    public static Main instance = new Main();

    //Notification stuff
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static String notificationText = "";
    private static long notificationStartTime = 0;
    private static final int NOTIFICATION_DURATION = 2000; // milliseconds
    private static final int textSize = 20;

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
        MinecraftForge.EVENT_BUS.register(new WorldRender());
        MinecraftForge.EVENT_BUS.register(new PlayerTick());
        MinecraftForge.EVENT_BUS.register(new PlayerInteract());

        //Commands (If commented out, it means the command is for development purposes)
        //ClientCommandHandler.instance.registerCommand(new SetWaypoint());
        //ClientCommandHandler.instance.registerCommand(new ListWaypoints());
        ClientCommandHandler.instance.registerCommand(new RenderNext());
        ClientCommandHandler.instance.registerCommand(new enterNewRoom());
        ClientCommandHandler.instance.registerCommand(new NextSecret());
        //ClientCommandHandler.instance.registerCommand(new RouteBuilder());
        //ClientCommandHandler.instance.registerCommand(new GetRoom());
        //ClientCommandHandler.instance.registerCommand(new LookCoords());

        //Things to make sure code doesn't break
        currentRoom.add(null, null, true);
        RoomDetection.roomName = "testRoom";
        RoomDetection.roomCorner = new Point(0, 0);
        RoomDetection.roomDirection = "NE";
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        dungeonRooms.postInit(e);
    }

    /*public static void addToPath(List<BlockPos> newCoords) {
        path.add(newCoords);
    }
    public static List<BlockPos> getPath() {
        return path.peek();
    }
    public static void nextPath() {
        path.poll();
    }
    public static void clearPath() {
        path.clear();
    }
    public static Queue<List<BlockPos>> getRoomPaths() {
        return path;
    }*/

    @SubscribeEvent
    public void onItemPickup(PlayerEvent.ItemPickupEvent e) {
        ItemPickedUp.onPickupItem(e);
    }

    @SubscribeEvent
    public void onRecievePacket(PacketEvent.ReceiveEvent e) {
        try {
            if(e.packet instanceof S23PacketBlockChange) {
                S23PacketBlockChange packet = (S23PacketBlockChange) e.packet;

                BlockPos pos = packet.getBlockPosition();
                World world = Minecraft.getMinecraft().theWorld;
                IBlockState blockState = world.getBlockState(pos);
                Block block = blockState.getBlock();

                if(block == Blocks.air) { //Block broken
                    BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, blockState, Minecraft.getMinecraft().thePlayer);
                    new BlockBreak().onBlockBreak(event);
                } else if(block == null) {
                    System.out.println("Block is null.");
                } else { //Block placed
                    IBlockState placedAgainst = world.getBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()));
                    BlockEvent.PlaceEvent placeEvent = new BlockEvent.PlaceEvent(new BlockSnapshot(world, pos, blockState), placedAgainst, Minecraft.getMinecraft().thePlayer);
                    new BlockPlace().onBlockPlace(placeEvent);
                }
            } else if(e.packet instanceof S0DPacketCollectItem) {
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
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(Main.chatPrefix + "There was an error with the mod!"));
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (mc.ingameGUI == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - notificationStartTime < NOTIFICATION_DURATION) {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int screenWidth = scaledResolution.getScaledWidth();
            int screenHeight = scaledResolution.getScaledHeight();

            int notificationWidth = mc.fontRendererObj.getStringWidth(notificationText);
            int notificationHeight = mc.fontRendererObj.FONT_HEIGHT;

            int xPos = (screenWidth - notificationWidth * textSize / 10) / 2;
            int yPos = (screenHeight - notificationHeight * textSize / 10) / 2;

            GlStateManager.pushMatrix();
            GlStateManager.translate(xPos, yPos, 0);
            GlStateManager.scale(textSize / 10.0, textSize / 10.0, 1.0);

            mc.fontRendererObj.drawString(notificationText, 0, 0, 0xFFFFFF);

            GlStateManager.popMatrix();
        }
    }

    public static void displayNotification(String text) {
        notificationText = text;
        notificationStartTime = System.currentTimeMillis();
    }
}