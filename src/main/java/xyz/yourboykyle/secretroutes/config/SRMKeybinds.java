package xyz.yourboykyle.secretroutes.config;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import xyz.yourboykyle.secretroutes.Main;

public class SRMKeybinds {

    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(Main.MODID, "general")
    );

    public static final KeyMapping NEXT_SECRET = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.secretroutes.next_secret",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_N,
                    CATEGORY
            )
    );

    public static final KeyMapping LAST_SECRET = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.secretroutes.last_secret",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_B,
                    CATEGORY
            )
    );

    public static final KeyMapping TOGGLE_MOD = KeyMappingHelper.registerKeyMapping(
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