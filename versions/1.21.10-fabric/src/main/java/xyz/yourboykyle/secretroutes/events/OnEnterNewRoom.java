package xyz.yourboykyle.secretroutes.events;

import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.*;

import java.util.ArrayList;

public class OnEnterNewRoom {
    public static String lastKnownRoom = null;

    public static void checkForNewRoom() {
        if (!LocationUtils.isInDungeons()) return;

        String roomName = RoomDirectionUtils.roomName();

        if (roomName.equals("UNKNOWN")) return;

        if (lastKnownRoom == null || !lastKnownRoom.equals(roomName)) {
            lastKnownRoom = roomName;
            onEnterNewRoom(new Room(lastKnownRoom));
        }
    }

    public static void onEnterNewRoom(Room room) {
        try {
            if (!LocationUtils.isInDungeons()) {
                return;
            }

            Main.currentRoom = room;
            SecretUtils.secrets = null;
            SecretUtils.secretLocations = new ArrayList<>();
            SecretUtils.resetValues();
        } catch (Exception e) {
            LogUtils.error(e);
        }
    }
}