package xyz.yourboykyle.secretroutes.utils;

import net.minecraft.util.BlockPos;

public class BlockUtils {

    public static String blockPos(BlockPos pos) {
        return pos.getX() + ":" + pos.getY() + ":" + pos.getZ();
    }
}
