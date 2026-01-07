package xyz.yourboykyle.secretroutes.utils;

import net.hypixel.data.type.GameType;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class LocationUtils {
    private static boolean inDungeons = false;
    private static boolean isOnSkyblock = false;

    public static void init() {
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);

        HypixelModAPI.getInstance().createHandler(ClientboundLocationPacket.class, packet -> {
            if (packet.getMode().isPresent()) {
                String mode = packet.getMode().get();
                inDungeons = mode.equalsIgnoreCase("dungeon");
            } else {
                inDungeons = false;
            }

            if (packet.getServerType().isPresent()) {
                isOnSkyblock = packet.getServerType().get() == GameType.SKYBLOCK;
            } else {
                isOnSkyblock = false;
            }
        });
    }

    public static boolean isInDungeons() {
        return inDungeons;
    }

    public static boolean isOnSkyblock() {
        return isOnSkyblock;
    }

    // TODO: Reset values on disconnect
    public static void reset() {
        inDungeons = false;
        isOnSkyblock = false;
    }
}
