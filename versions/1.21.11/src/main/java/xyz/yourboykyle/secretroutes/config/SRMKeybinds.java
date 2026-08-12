package xyz.yourboykyle.secretroutes.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import xyz.yourboykyle.secretroutes.Main;

public class SRMKeybinds {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(Main.MODID, "general")
    );

    public static final KeyMapping NEXT_SECRET = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.secretroutes.next_secret",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_N,
                    CATEGORY
            )
    );

    public static final KeyMapping LAST_SECRET = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.secretroutes.last_secret",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_B,
                    CATEGORY
            )
    );

    public static final KeyMapping TOGGLE_MOD = KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                    "key.secretroutes.toggle_mod",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_UNKNOWN,
                    CATEGORY
            )
    );

    public static void init() {
    }
}