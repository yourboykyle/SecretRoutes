package xyz.yourboykyle.secretroutes.utils;

import com.google.gson.*;
import net.minecraft.util.BlockPos;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Room.SECRET_TYPES;
import xyz.yourboykyle.secretroutes.utils.Room.WAYPOINT_TYPES;

import java.io.*;

public class RouteRecording {
    public static boolean recording = false;
    public String roomName = "Room-Name-Here";
    public JsonObject allSecretRoutes = new JsonObject();
    public JsonArray currentSecretRoute = new JsonArray();
    public JsonObject currentSecretWaypoints = new JsonObject();
    public int currentSecretIndex = 0;

    public RouteRecording() {
        // Create placeholders for the waypoints, as the coordinates are 2D arrays
        currentSecretWaypoints.add("locations", new JsonArray());
        currentSecretWaypoints.add("etherwarps", new JsonArray());
        currentSecretWaypoints.add("mines", new JsonArray());
        currentSecretWaypoints.add("interacts", new JsonArray());
        currentSecretWaypoints.add("tnts", new JsonArray());

        // Import all the current secret routes into the allSecretRoutes JsonObject
        importRoutes();

        // Put stuff for testing in here
        System.out.println("- Start RouteRecording Testing Data -");

        System.out.println("allSecretRoutes before adding: " + allSecretRoutes);

        addWaypoint(WAYPOINT_TYPES.LOCATIONS, new BlockPos(1, 2, 3));
        addWaypoint(WAYPOINT_TYPES.ETHERWARPS, new BlockPos(1, 2, 3));
        addWaypoint(WAYPOINT_TYPES.MINES, new BlockPos(1, 2, 3));
        addWaypoint(WAYPOINT_TYPES.INTERACTS, new BlockPos(1, 2, 3));
        addWaypoint(WAYPOINT_TYPES.TNTS, new BlockPos(1, 2, 3));
        addWaypoint(SECRET_TYPES.INTERACT, new BlockPos(1, 2, 3));
        System.out.println("Waypoints #1: " + currentSecretWaypoints);

        newSecret();

        addWaypoint(WAYPOINT_TYPES.LOCATIONS, new BlockPos(2, 3, 4));
        addWaypoint(WAYPOINT_TYPES.ETHERWARPS, new BlockPos(2, 3, 4));
        addWaypoint(WAYPOINT_TYPES.MINES, new BlockPos(2, 3, 4));
        addWaypoint(WAYPOINT_TYPES.INTERACTS, new BlockPos(2, 3, 4));
        addWaypoint(WAYPOINT_TYPES.TNTS, new BlockPos(2, 3, 4));
        addWaypoint(SECRET_TYPES.ITEM, new BlockPos(2, 3, 4));
        System.out.println("Waypoints #2: " + currentSecretWaypoints);

        newSecret();

        addWaypoint(WAYPOINT_TYPES.LOCATIONS, new BlockPos(3, 4, 5));
        addWaypoint(WAYPOINT_TYPES.ETHERWARPS, new BlockPos(3, 4, 5));
        addWaypoint(WAYPOINT_TYPES.MINES, new BlockPos(3, 4, 5));
        addWaypoint(WAYPOINT_TYPES.INTERACTS, new BlockPos(3, 4, 5));
        addWaypoint(WAYPOINT_TYPES.TNTS, new BlockPos(3, 4, 5));
        addWaypoint(SECRET_TYPES.BAT, new BlockPos(3, 4, 5));
        System.out.println("Waypoints #3: " + currentSecretWaypoints);

        newSecret();

        System.out.println(currentSecretRoute);

        newRoute();

        System.out.println("allSecretRoutes after adding: " + allSecretRoutes);

        System.out.println("Exporting all routes...");
        exportAllRoutes();
        System.out.println("Exported all routes!");

        allSecretRoutes = new JsonObject();
        String filePath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "routes.json";
        importRoutes(filePath);
        System.out.println("Imported routes: " + allSecretRoutes);

        System.out.println("- End RouteRecording Testing Data -");
    }

    public void importRoutes() {
        // Import all the current secret routes into the allSecretRoutes JsonObject
        allSecretRoutes = new JsonObject();

        Gson gson = new GsonBuilder().create();
        InputStream inputStream = Main.class.getResourceAsStream(Main.newRoomsDataPath);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        allSecretRoutes = gson.fromJson(reader, JsonObject.class);
    }

    public void importRoutes(String filePath) {
        // Import all the current secret routes into the allSecretRoutes JsonObject from a file
        allSecretRoutes = new JsonObject();

        try {
            Gson gson = new GsonBuilder().create();
            FileReader reader = new FileReader(filePath);

            allSecretRoutes = gson.fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addWaypoint(WAYPOINT_TYPES type, BlockPos pos) {
        // Add a non-secret waypoint to the current secret waypoints

        JsonArray posArray = new JsonArray();

        posArray.add(new JsonPrimitive(pos.getX()));
        posArray.add(new JsonPrimitive(pos.getY()));
        posArray.add(new JsonPrimitive(pos.getZ()));

        if(type == WAYPOINT_TYPES.LOCATIONS) {
            currentSecretWaypoints.get("locations").getAsJsonArray().add(posArray);
        } else if(type == WAYPOINT_TYPES.ETHERWARPS) {
            currentSecretWaypoints.get("etherwarps").getAsJsonArray().add(posArray);
        } else if(type == WAYPOINT_TYPES.MINES) {
            currentSecretWaypoints.get("mines").getAsJsonArray().add(posArray);
        } else if(type == WAYPOINT_TYPES.INTERACTS) {
            currentSecretWaypoints.get("interacts").getAsJsonArray().add(posArray);
        } else if(type == WAYPOINT_TYPES.TNTS) {
            currentSecretWaypoints.get("tnts").getAsJsonArray().add(posArray);
        }
    }

    public void addWaypoint(SECRET_TYPES type, BlockPos pos) {
        // Add a secret waypoint to the current secret waypoints

        JsonArray posArray = new JsonArray();

        posArray.add(new JsonPrimitive(pos.getX()));
        posArray.add(new JsonPrimitive(pos.getY()));
        posArray.add(new JsonPrimitive(pos.getZ()));

        JsonObject secret = new JsonObject();

        if(type == SECRET_TYPES.INTERACT) {
            secret.add("type", new JsonPrimitive("interact"));
        } else if(type == SECRET_TYPES.ITEM) {
            secret.add("type", new JsonPrimitive("item"));
        } else if(type == SECRET_TYPES.BAT) {
            secret.add("type", new JsonPrimitive("bat"));
        }

        secret.add("location", posArray);
        currentSecretWaypoints.add("secret", secret);
    }

    public void newSecret() {
        // Add current secret waypoints to the route and start a new secret waypoints list
        currentSecretRoute.add(currentSecretWaypoints);

        currentSecretWaypoints = new JsonObject();

        currentSecretWaypoints.add("locations", new JsonArray());
        currentSecretWaypoints.add("etherwarps", new JsonArray());
        currentSecretWaypoints.add("mines", new JsonArray());
        currentSecretWaypoints.add("interacts", new JsonArray());
        currentSecretWaypoints.add("tnts", new JsonArray());
    }

    public void newRoute() {
        // Start a new secret route recording
        allSecretRoutes.add(roomName, currentSecretRoute);
        currentSecretRoute = new JsonArray();

        currentSecretWaypoints = new JsonObject();

        currentSecretWaypoints.add("locations", new JsonArray());
        currentSecretWaypoints.add("etherwarps", new JsonArray());
        currentSecretWaypoints.add("mines", new JsonArray());
        currentSecretWaypoints.add("interacts", new JsonArray());
        currentSecretWaypoints.add("tnts", new JsonArray());
    }

    public void exportAllRoutes() {
        // Export the secret route to a file
        String filePath = System.getProperty("user.home") + File.separator + "Downloads";
        String fileName = "routes.json";

        File file = new File(filePath, fileName);
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(allSecretRoutes.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}