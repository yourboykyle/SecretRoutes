package xyz.yourboykyle.secretroutes.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.quantizr.dungeonrooms.dungeons.catacombs.RoomDetection;
import io.github.quantizr.dungeonrooms.utils.MapUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import xyz.yourboykyle.secretroutes.Main;
import xyz.yourboykyle.secretroutes.utils.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RouteBuilder extends CommandBase {
    public static JsonObject data = new JsonObject();

    @Override
    public String getCommandName() {
        return "rb";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "rb <set|clear|save> <move|stonk|aotv|chest|item|superboom|bat>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if(Utils.getLastEntry(data) == null) {
            JsonObject json = new JsonObject();
            json.addProperty("type", "stonk");
            data.add("0", json);
        }

        if(args.length == 0) {
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Please include at least 1 argument!"));
            return;
        }

        EntityPlayer p = (EntityPlayer) sender;

        if(args[0].equals("save")) {
            if(args.length != 2) {
                p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Please include the name of the file! (Excluding .json - Example: /rb save FairyRoom)"));
                return;
            }
            String filePath = System.getProperty("user.home") + File.separator + "Downloads";
            String fileName = args[1] + ".json";

            File file = new File(filePath, fileName);

            try {
                // Create the FileWriter
                FileWriter writer = new FileWriter(file);

                // Write the JsonObject to the file
                writer.write(data.toString());
                writer.flush();
                writer.close();

                sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "JSON exported successfully to: " + file.getAbsolutePath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(args[0].equals("clear")) {
            data.entrySet().clear();
            sender.addChatMessage(new ChatComponentText(Main.chatPrefix + "Successfully cleared path builder data."));
        } else if(args[0].equals("set") && args.length == 2) {
            p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Added new waypoint."));
            if(args[1].equals("move")) {
                int lastEntryNum = Integer.parseInt(Utils.getLastEntry(data).getKey());

                BlockPos relativePos = MapUtils.actualToRelative(new BlockPos(Math.floor(p.posX), Math.floor(p.posY), Math.floor(p.posZ)), RoomDetection.roomDirection, RoomDetection.roomCorner);

                if(Utils.getLastEntry(data).getValue().getAsJsonObject().get("type").getAsString().equals("move")) {
                    JsonArray firstCoords = new JsonArray();
                    firstCoords.add(new JsonPrimitive(relativePos.getX()));
                    firstCoords.add(new JsonPrimitive(relativePos.getY()));
                    firstCoords.add(new JsonPrimitive(relativePos.getZ()));

                    Utils.getLastEntry(data).getValue().getAsJsonObject().get("coords").getAsJsonArray().add(firstCoords);
                } else {
                    JsonObject json = new JsonObject();
                    JsonArray coords = new JsonArray();
                    JsonArray nextCoords = new JsonArray();

                    nextCoords.add(new JsonPrimitive(relativePos.getX()));
                    nextCoords.add(new JsonPrimitive(relativePos.getY()));
                    nextCoords.add(new JsonPrimitive(relativePos.getZ()));

                    coords.add(nextCoords);
                    json.add("coords", coords);
                    json.addProperty("type", "move");

                    if(lastEntryNum == 0) {
                        data.remove("0");
                    }
                    data.add("" + (lastEntryNum + 1), json);
                }
            } else if(args[1].equals("stonk")) {
                addToJson("stonk");
            } else if(args[1].equals("aotv")) {
                addToJson("aotv");
            } else if(args[1].equals("chest")) {
                addToJson("chest");
            } else if(args[1].equals("item")) {
                addToJson("item");
            } else if(args[1].equals("superboom")) {
                addToJson("superboom");
            } else if(args[1].equals("bat")) {
                addToJson("bat");
            } else if(args[1].equals("lever")) {
                addToJson("lever");
            } else if(args[1].equals("wither")) {
                addToJson("wither");
            }
        } else if(args[0].equals("set") && args.length == 5) {
            BlockPos pos = new BlockPos(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
            p.addChatComponentMessage(new ChatComponentText(Main.chatPrefix + "Added new waypoint."));
            if(args[1].equals("move")) {
                int lastEntryNum = Integer.parseInt(Utils.getLastEntry(data).getKey());

                BlockPos relativePos = MapUtils.actualToRelative(new BlockPos(Math.floor(pos.getX()), Math.floor(pos.getY()), Math.floor(pos.getZ())), RoomDetection.roomDirection, RoomDetection.roomCorner);

                if(Utils.getLastEntry(data).getValue().getAsJsonObject().get("type").getAsString().equals("move")) {
                    JsonArray firstCoords = new JsonArray();
                    firstCoords.add(new JsonPrimitive(relativePos.getX()));
                    firstCoords.add(new JsonPrimitive(relativePos.getY()));
                    firstCoords.add(new JsonPrimitive(relativePos.getZ()));

                    Utils.getLastEntry(data).getValue().getAsJsonObject().get("coords").getAsJsonArray().add(firstCoords);
                } else {
                    JsonObject json = new JsonObject();
                    JsonArray coords = new JsonArray();
                    JsonArray nextCoords = new JsonArray();

                    nextCoords.add(new JsonPrimitive(relativePos.getX()));
                    nextCoords.add(new JsonPrimitive(relativePos.getY()));
                    nextCoords.add(new JsonPrimitive(relativePos.getZ()));

                    coords.add(nextCoords);
                    json.add("coords", coords);
                    json.addProperty("type", "move");

                    if(lastEntryNum == 0) {
                        data.remove("0");
                    }
                    data.add("" + (lastEntryNum + 1), json);
                }
            } else if(args[1].equals("stonk")) {
                addToJson("stonk", pos);
            } else if(args[1].equals("aotv")) {
                addToJson("aotv", pos);
            } else if(args[1].equals("chest")) {
                addToJson("chest", pos);
            } else if(args[1].equals("item")) {
                addToJson("item", pos);
            } else if(args[1].equals("superboom")) {
                addToJson("superboom", pos);
            } else if(args[1].equals("bat")) {
                addToJson("bat", pos);
            } else if(args[1].equals("lever")) {
                addToJson("lever", pos);
            } else if(args[1].equals("wither")) {
                addToJson("wither", pos);
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    private static void addToJson(String type) {
        EntityPlayer p = Minecraft.getMinecraft().thePlayer;

        int lastEntryNum = Integer.parseInt(Utils.getLastEntry(data).getKey());

        JsonObject json = new JsonObject();
        JsonArray coords = new JsonArray();

        BlockPos relativePos = MapUtils.actualToRelative(new BlockPos((int) Math.floor(p.posX), (int) Math.floor(p.posY), (int) Math.floor(p.posZ)), RoomDetection.roomDirection, RoomDetection.roomCorner);

        coords.add(new JsonPrimitive(Math.floor(relativePos.getX())));
        coords.add(new JsonPrimitive(Math.floor(relativePos.getY())));
        coords.add(new JsonPrimitive(Math.floor(relativePos.getZ())));

        json.add("coords", coords);
        json.addProperty("type", type);

        if(lastEntryNum == 0) {
            data.remove("0");
        }
        data.add("" + (lastEntryNum + 1), json);
    }
    private static void addToJson(String type, BlockPos pos) {
        int lastEntryNum = Integer.parseInt(Utils.getLastEntry(data).getKey());

        JsonObject json = new JsonObject();
        JsonArray coords = new JsonArray();

        BlockPos relativePos = MapUtils.actualToRelative(new BlockPos((int) Math.floor(pos.getX()), (int) Math.floor(pos.getY()), (int) Math.floor(pos.getZ())), RoomDetection.roomDirection, RoomDetection.roomCorner);

        coords.add(new JsonPrimitive(Math.floor(relativePos.getX())));
        coords.add(new JsonPrimitive(Math.floor(relativePos.getY())));
        coords.add(new JsonPrimitive(Math.floor(relativePos.getZ())));

        json.add("coords", coords);
        json.addProperty("type", type);

        if(lastEntryNum == 0) {
            data.remove("0");
        }
        data.add("" + (lastEntryNum + 1), json);
    }
}
