package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.BlockPos;
import xyz.yourboykyle.secretroutes.Main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Queue;

public class Room {
    private Queue<Pair<BlockPos, String>> route; // Pairs will be BlockPos to the type of secret
    public String name;
    public JsonObject data;

    public Room(String roomName) {
        try {
            route = new LinkedList<>();
            name = roomName;

            if (roomName != null) {
                Gson gson = new GsonBuilder().create();
                InputStream inputStream = Main.class.getResourceAsStream(Main.roomsDataPath);

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                JsonObject myData = gson.fromJson(reader, JsonObject.class);

                if(myData != null) {
                    data = myData.get(name).getAsJsonObject();
                }
            } else {
                data = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Room(String dataPath, boolean placeHolderForNotDuplicateConstructors) {
        try {
            route = new LinkedList<>();

            Gson gson = new GsonBuilder().create();
            InputStream inputStream = new FileInputStream(dataPath);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            JsonObject myData = gson.fromJson(reader, JsonObject.class);
            inputStream.close();

            data = myData.getAsJsonObject();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Pair<BlockPos, String> getNext() {
        return route.peek();
    }

    public void add(BlockPos pos, String type) {
        if(getNext() == null || getNext().getKey() == null || getNext().getValue() == null) {
            removeNext();
        }
        route.add(new Pair(pos, type));
    }
    public void add(BlockPos pos, String type, boolean isInit) {
        route.add(new Pair(pos, type));
    }

    public void removeNext() {
        route.poll();
    }

    public Queue<Pair<BlockPos, String>> getRoute() {
        return route;
    }
}
