package xyz.yourboykyle.secretroutes.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import xyz.yourboykyle.secretroutes.Main;

//? if <=26.2
import org.lwjgl.glfw.GLFW;

public class SRMKeybinds {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(Main.MODID, "general")
    );

    public static final KeyMapping NEXT_SECRET = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.secretroutes.next_secret",
                    //? if<=26.2 {
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_N,
                    //? } elif >= 26.3 {
                    /*InputConstants.Type.KEYBOARD,
                    InputConstants.KEY_N,
                    *///?}
                    CATEGORY
            )
    );

    public static final KeyMapping LAST_SECRET = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.secretroutes.last_secret",
                    //? if<=26.2 {
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_B,
                    //? } elif >= 26.3 {
                    /*InputConstants.Type.KEYBOARD,
                    InputConstants.KEY_B,
                     *///?}
                    CATEGORY
            )
    );

    public static final KeyMapping CYCLE_ROUTE = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.secretroutes.cycle_route",
                    //? if<=26.2 {
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    //? } elif >= 26.3 {
                    /*InputConstants.Type.KEYBOARD,
                    InputConstants.UNKNOWN.getValue(),
                     *///?}
                    CATEGORY
            )
    );

    public static final KeyMapping TOGGLE_MOD = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.secretroutes.toggle_mod",
                    //? if<=26.2 {
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    //? } elif >= 26.3 {
                    /*InputConstants.Type.KEYBOARD,
                    InputConstants.UNKNOWN.getValue(),
                     *///?}
                    CATEGORY
            )
    );

    public static void init() {
    }
}
